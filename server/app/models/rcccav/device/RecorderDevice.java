package models.rcccav.device;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


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
    protected String filename = "";
    protected RCCCAVFileFilter fileFilter = new RCCCAVFileFilter(".wav");

    public RecorderDevice(JSONObject spec) {
        this.spec = spec;
        this.setting = new DeviceSetting(this.spec);
        this.wavDir = this.setting.sParams.get("wavDir");
        this.mp3Dir = this.setting.sParams.get("mp3Dir");
        this.wavBackupDir = this.setting.sParams.get("wavBackupDir");
        this.filename = this.setting.sParams.get("filename");
    }

    
    @Override
    public void doCommand(String cmd) {
        String cmdStr = this.setting.actions.get(cmd);
        if (cmdStr != null && cmdStr.length() > 0) {
            String pid = "";
            try {
                pid = new String(Files.readAllBytes(Paths.get(this.wavDir + "/pid")));
            } catch (IOException e) {}

            if (pid.isEmpty() && cmd.equals("STOP")) {
                this.actionResult = "Recording is not in progress!";
                return;
            }
            else if (!pid.isEmpty() && cmd.equals("START")) {
                this.actionResult = "Recording is already in progress!";
                return;
            }
            this.actionResult = "";
            if (cmd.equals("START") || cmd.equals("STOP")) {
                cmdStr = cmdStr.replace("$wavDir", this.wavDir);
                cmdStr = cmdStr.replace("$mp3Dir", this.mp3Dir);
                cmdStr = cmdStr.replace("$filename", this.filename);
                cmdStr = cmdStr.replace("$pid", pid);
                this.executeCmd(cmdStr);
            }
            else if (cmd.equals("2MP3")) {
                this.doWavToMP3(cmdStr);;
            }
        }
    }

    private void doWavToMP3(String cmdStr) {
        File wavDir = new File(this.wavDir);
        File[] fileList = wavDir.listFiles(this.fileFilter);
        String filename = "";
        String newCmd = "";
        for (File wavFile : fileList) {
            if (wavFile.isFile() && wavFile.canWrite()) {
                filename = wavFile.getName();
                Path sPath = Paths.get(this.wavDir + "/" + filename);
                Path tPath = Paths.get(this.wavBackupDir + "/" + filename);
                try {
                    Files.move(sPath, tPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.error(e.getMessage());
                    continue;
                }
                filename = filename.substring(0, filename.length() - 4);
                newCmd = cmdStr.replace("$wavBackupDir", this.wavBackupDir);
                newCmd = newCmd.replace("$mp3Dir", this.mp3Dir);
                newCmd = newCmd.replace("$fileName", filename);
                Logger.debug("The command to be executed is " + newCmd);
                this.executeCmd(newCmd);
            }
        }
    }

    private void executeCmd(String cmdStr) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Logger.info("Ready to execute recorder command: " + cmdStr);
            runtime.exec(cmdStr);
            Logger.info("Shell command " + cmdStr + " executed successfully!");
            this.actionResult = "Command Accepted";
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.error(ex.getMessage());
            this.actionResult = ex.getMessage();
        }
    }

    @Override
    public void disconnect() {
    }

}
