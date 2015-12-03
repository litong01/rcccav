package models.rcccav.device;

import org.json.simple.JSONObject;


public abstract class Device {

    protected JSONObject spec = null;
    protected DeviceSetting setting = null;
    protected String actionResult = "";
    protected String actionCode = "";

    abstract public void doCommand(String cmd);
    abstract public void disconnect();

    public String getActionResult() {
        return this.actionResult;
    }

    public String getActionCode() {
        return this.actionCode;
    }

    public DeviceSetting getSetting() {
        return this.setting;
    }

    public JSONObject getSpec() {
        return this.spec;
    }
}
