package models.rcccav.device;

import java.io.IOException;

import play.Logger;

import org.json.simple.JSONObject;


public class SysDevice extends Device {

    public SysDevice(JSONObject spec) {
        this.spec = spec;
        this.setting = new DeviceSetting(this.spec);
    }

    @Override
    public void doCommand(String cmd) {
        String cmdStr = this.setting.actions.get(cmd);
        if (cmdStr != null && cmdStr.length() > 0) {
            Runtime runtime = Runtime.getRuntime();
            try {
                Logger.info("Ready to execute shell command " + cmdStr);
                Process proc = runtime.exec(cmdStr);
                proc.waitFor();
                Logger.info("Shell command " + cmdStr + " executed successfully!");
            } catch (IOException ex) {
                ex.printStackTrace();
                Logger.error(ex.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
                Logger.error(e.getMessage());
            }
        }
    }

    @Override
    public void disconnect() {
    }

}
