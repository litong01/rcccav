package models.rcccav.device;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import play.Logger;

import org.json.simple.JSONObject;


public class RecorderDevice extends Device {

    protected class RCCCAVFileFilter implements FilenameFilter {

        protected String fileExtension = "";
        public RCCCAVFileFilter(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        public boolean accept(File directory, String fileName) {
            if (fileName.endsWith(this.fileExtension)) {
                return true;
            }
            return false;
        }
    }

    protected String wavDir = "";
    protected String mp3Dir = "";
    protected String wavBackupDir = "";
    protected String mp3BackupDir = "";
    protected String filename = "";
    protected String ftp_host = "";
    protected String ftp_user = "";
    protected String ftp_password = "";
    protected String ftp_dir = "";
    protected long delayBetweenUploadRetrys = 7;
    public long delayBetweenTasks = 7;
    protected RCCCAVFileFilter wavFilter = new RCCCAVFileFilter(".wav");
    protected RCCCAVFileFilter mp3Filter = new RCCCAVFileFilter(".mp3");

    public RecorderDevice(JSONObject spec) {
        this.spec = spec;
        this.setting = new DeviceSetting(this.spec);
        this.wavDir = this.setting.sParams.get("wavDir");
        this.mp3Dir = this.setting.sParams.get("mp3Dir");
        this.wavBackupDir = this.setting.sParams.get("wavBackupDir");
        this.mp3BackupDir = this.setting.sParams.get("mp3BackupDir");
        this.filename = this.setting.sParams.get("filename");
        this.ftp_host = this.setting.sParams.get("ftp_host");
        this.ftp_user = this.setting.sParams.get("ftp_user");
        this.ftp_password = this.setting.sParams.get("ftp_password");
        this.ftp_dir = this.setting.sParams.get("ftp_dir");
        this.delayBetweenUploadRetrys = this.setting.nParams.get("delayBetweenUploadRetrys");
        this.delayBetweenTasks = this.setting.nParams.get("delayBetweenTasks");
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject getStatus() {
        JSONObject ns = new JSONObject();
        String pid = this.getPid();
        ns.put("recording", pid.isEmpty()==false);
        JSONObject st = new JSONObject();
        st.put(this.setting.name, ns);
        return st;
    }

    @Override
    public void doCommand(String cmd) {
        String cmdStr = this.setting.actions.get(cmd);
        if (cmdStr != null && cmdStr.length() > 0) {
            String pid = this.getPid();
            if (pid.isEmpty() && cmd.equals("STOP")) {
                this.actionResult = "Recording is not in process!"; //not recording
                return;
            }
            else if (!pid.isEmpty() && (cmd.equals("START"))) {
                this.actionResult = "Recording is in process!"; //recording
                return;
            }
            //For INFO command
            else if (cmd.equals("INFO")) {
                if (pid.isEmpty()) {
                    this.actionResult = "Recording is not in process!"; //not recording
                }
                else {
                    this.actionResult = "Recording is in process!"; //recording
                }
                return;
            }
            this.actionResult = "";
            if (cmd.equals("START") || cmd.equals("STOP")) {
                cmdStr = cmdStr.replace("$wavDir", this.wavDir);
                cmdStr = cmdStr.replace("$mp3Dir", this.mp3Dir);
                cmdStr = cmdStr.replace("$filename", this.filename);
                cmdStr = cmdStr.replace("$pid", pid);
                this.executeCmdWithoutOutput(cmdStr);
            }
        }
    }

    private String getPid() {
        String pid = "";
        try {
            pid = new String(Files.readAllBytes(Paths.get(this.wavDir + "/pid")));
        } catch (IOException e) {}
        return pid;
    }

    private boolean doUploadMP3(String path, String filename) {
        boolean resultFlag = false;
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(this.ftp_host, 21);
            ftpClient.login(this.ftp_user, this.ftp_password);
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // uploads file using an InputStream
            File localFile = new File(path + "/" + filename);

            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            String yearInString = String.valueOf(year);
            String remoteFile = this.ftp_dir + yearInString + "/" + filename;
            InputStream inputStream = new FileInputStream(localFile);

            Logger.debug("Start uploading file " + filename + " to " + remoteFile);
            boolean done = ftpClient.storeFile(remoteFile, inputStream);
            inputStream.close();
            if (done) {
                Logger.debug(filename + " uploaded successfully!");
                resultFlag = true;
            }
            else {
                Logger.error(filename + " uploading failed!");
            }
        } catch (IOException ex) {
            Logger.error(ex.getMessage());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                Logger.error(ex.getMessage());
            }
        }
        return resultFlag;
    }

    public void doMP3Uploads() {
        String pid = this.getPid();
        if (!pid.isEmpty()) return;
        File mp3Dir = new File(this.mp3Dir);
        File[] fileList = mp3Dir.listFiles(this.mp3Filter);
        String filename = "";

        for (File mp3File : fileList) {
            //Make sure that we try 3 times if the upload fails
            filename = mp3File.getName();
            if (mp3File.isFile()) {
                if (this.doUploadMP3(this.mp3Dir, filename)) {
                    try {
                        Path sPath = Paths.get(this.mp3Dir + "/" + filename);
                        Path tPath = Paths.get(this.mp3BackupDir + "/" + filename);
                        Files.move(sPath, tPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        Logger.error(e.getMessage());
                    }
                }
                try {
                    //We need to wait for awhile before handle next file.
                    Thread.sleep(this.delayBetweenUploadRetrys * 1000);
                } catch (InterruptedException ex) {}
            }
        }
    }
    
    public void doWavToMP3(String cmd) {
        //If recording is in progress, we will not perform this task
        String pid = this.getPid();
        if (!pid.isEmpty()) return;

        File wavDir = new File(this.wavDir);
        File[] fileList = wavDir.listFiles(this.wavFilter);
        String filename = "";
        String newCmd = "";
        String cmdStr = this.setting.actions.get(cmd);
        for (File wavFile : fileList) {
            if (wavFile.isFile()) {
                //First convert the wav to mp3
                filename = wavFile.getName();
                filename = filename.substring(0, filename.length() - 4);
                newCmd = cmdStr.replace("$wavDir", this.wavDir);
                newCmd = newCmd.replace("$mp3Dir", this.mp3Dir);
                newCmd = newCmd.replace("$fileName", filename);

                // if conversion is OK move the file
                if (this.executeCmdWithOutput(newCmd)) {
                    Path sPath = Paths.get(this.wavDir + "/" + filename + ".wav");
                    Path tPath = Paths.get(this.wavBackupDir + "/" + filename + ".mp3");
                    try {
                        Files.move(sPath, tPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        Logger.error(e.getMessage());
                    }
                }
            }
        }
    }

    private void executeCmdWithoutOutput(String cmdStr) {
        try {
            Logger.debug("Ready to execute recorder command: " + cmdStr);
            ProcessBuilder pb = new ProcessBuilder(Arrays.asList(cmdStr.split("\\s+")));
            pb.start();
            this.actionResult = "Command Accepted";
        } catch (IOException ex) {
            this.actionResult = ex.getMessage();
            Logger.error(this.actionResult);
        }
    }

    private boolean executeCmdWithOutput(String cmdStr) {
        try {
            Logger.debug("Ready to execute recorder command: " + cmdStr);
            ProcessBuilder pb = new ProcessBuilder(Arrays.asList(cmdStr.split("\\s+")));
            File output = new File(this.wavDir + "/shellcmd.output.log");
            File errors = new File(this.wavDir + "/shellcmd.error.log");

            pb.redirectError(errors);
            pb.redirectOutput(output);

            pb.start().waitFor();
            Logger.debug("Shell command " + cmdStr + " executed successfully!");
            return true;
        } catch (IOException ex) {
            Logger.error(ex.getMessage());
            return false;
        } catch (InterruptedException e) {
            Logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public void disconnect() {
    }
}
