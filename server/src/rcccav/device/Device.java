package rcccav.device;

import org.json.simple.JSONObject;


public abstract class Device {

    protected JSONObject spec = null;
    protected DeviceSetting setting = null;
    protected String actionResult = "";

    abstract public void doCommand(String cmd);
    abstract public void disconnect();

    public String getActionResult() {
        return this.actionResult;
    }
    
    public DeviceSetting getSetting() {
        return this.setting;
    }

    public JSONObject getSpec() {
        return this.spec;
    }
}
