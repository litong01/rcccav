package models.rcccav.device;

import java.util.logging.Logger;

import org.json.simple.JSONObject;


public class TestDevice extends Device {

    private static final Logger LOG = Logger.getLogger(TestDevice.class.getName());

    public TestDevice(JSONObject spec) {
        this.spec = spec;
        this.setting = new DeviceSetting(this.spec);
    }

    @Override
    public void doCommand(String cmd) {
        String cmdStr = this.setting.actions.get(cmd);
        if (cmdStr != null && cmdStr.length() > 0) {
            this.actionResult += String.format("Command %s excuted on device %s",
                    cmdStr, this.setting.title);
            LOG.info(this.actionResult);
        }
    }

    @Override
    public void disconnect() {
        String s = String.format("Device %s has been now disconnected.",
               this.setting.title);
        LOG.info(s);
    }

}
