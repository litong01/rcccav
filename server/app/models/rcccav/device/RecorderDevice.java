package models.rcccav.device;

import java.io.IOException;

import play.Logger;

import org.json.simple.JSONObject;


public class RecorderDevice extends Device {

    protected String wavDir = "";
    protected String mp3Dir = "";
    protected String filename = "";

    public RecorderDevice(JSONObject spec) {
        this.spec = spec;
        this.setting = new DeviceSetting(this.spec);
        this.wavDir = this.setting.sParams.get("wavDir");
        this.mp3Dir = this.setting.sParams.get("mp3Dir");
        this.filename = this.setting.sParams.get("filename");
    }

    @Override
    public void doCommand(String cmd) {
        String cmdStr = this.setting.actions.get(cmd);
        if (cmdStr != null && cmdStr.length() > 0) {
            Runtime runtime = Runtime.getRuntime();
            try {
                cmdStr = cmdStr.replace("$wavDir", this.wavDir);
                cmdStr = cmdStr.replace("$mp3Dir", this.mp3Dir);
                cmdStr = cmdStr.replace("$filename", this.filename);
                Logger.info("Ready to execute recorder command: " + cmdStr);
                runtime.exec(cmdStr);
                //proc.waitFor();
                Logger.info("Shell command " + cmdStr + " executed successfully!");
            } catch (IOException ex) {
                ex.printStackTrace();
                Logger.error(ex.getMessage());
            }
        }
    }

    @Override
    public void disconnect() {
    }

}
