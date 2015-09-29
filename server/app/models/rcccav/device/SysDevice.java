package models.rcccav.device;

import java.io.IOException;
import java.util.logging.Logger;

import org.json.simple.JSONObject;


public class SysDevice extends Device {

    private static final Logger LOG = Logger.getLogger(SysDevice.class.getName());

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
                runtime.exec(cmdStr);
            } catch (IOException ex) {
                ex.printStackTrace();
                LOG.severe(ex.getMessage());
            }
            if (cmd == "OFF") System.exit(0);
        }
    }

    @Override
    public void disconnect() {
    }

}