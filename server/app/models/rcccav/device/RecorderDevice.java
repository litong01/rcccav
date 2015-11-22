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
    public void doCommand(String cmd) {
        String cmdStr = this.setting.actions.get(cmd);
        if (cmdStr != null && cmdStr.length() > 0) {
            String pid = this.getPid();
            if (pid.isEmpty() && cmd.equals("STOP")) {
                this.actionResult = "Recording is not in progress!";
                return;
            }
            else if (!pid.isEmpty() && (cmd.equals("START"))) {
                this.actionResult = "Recording is in progress!";
                return;
            }
            this.actionResult = "";
            if (cmd.equals("START") || cmd.equals("STOP")) {
                cmdStr = cmdStr.replace("$wavDir", this.wavDir);
                cmdStr = cmdStr.replace("$mp3Dir", this.mp3Dir);
                cmdStr = cmdStr.replace("$filename", this.filename);
                cmdStr = cmdStr.replace("$pid", pid);
                this.executeCmd(cmdStr, false);
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
                filename = wavFile.getName();
                Path sPath = Paths.get(this.wavDir + "/" + filename);
                Path tPath = Paths.get(this.wavBackupDir + "/" + filename);
                try {
                    Files.move(sPath, tPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    Logger.error(e.getMessage());
                    continue;
                }
                filename = filename.substring(0, filename.length() - 4);
                newCmd = cmdStr.replace("$wavBackupDir", this.wavBackupDir);
                newCmd = newCmd.replace("$mp3Dir", this.mp3Dir);
                newCmd = newCmd.replace("$fileName", filename);
                this.executeCmd(newCmd, true);
            }
        }
    }

    private void executeCmd(String cmdStr, boolean waitFor) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Logger.debug("Ready to execute recorder command: " + cmdStr);
            if (waitFor) {
                Process proc = runtime.exec(cmdStr);
                proc.waitFor();
            }
            else runtime.exec(cmdStr);
            Logger.debug("Shell command " + cmdStr + " executed successfully!");
            if (!waitFor) this.actionResult = "Command Accepted";
        } catch (IOException ex) {
            this.actionResult = ex.getMessage();
            Logger.error(this.actionResult);
        } catch (InterruptedException e) {
            this.actionResult = e.getMessage();
            Logger.error(this.actionResult);
        }
    }

    @Override
    public void disconnect() {
    }
}
