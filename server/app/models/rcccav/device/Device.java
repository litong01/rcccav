package models.rcccav.device;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import play.Logger;


public abstract class Device {

    protected JSONObject spec = null;
    protected DeviceSetting setting = null;
    protected String actionResult = "";
    protected JSONObject status = null;

    abstract public void doCommand(String cmd);
    abstract public void disconnect();

    public String getActionResult() {
        return this.actionResult;
    }

    public JSONObject getStatus() {
        return this.status;
    }

    public void doInitActions() {
        JSONArray actions = (JSONArray) this.spec.get("initActions");
        if (actions != null) {
            Logger.info(this.setting.title + " is ready to execute initAction");
            for (Object cmd: actions) {
                this.doCommand((String) cmd);
            }
        }
    }

    public DeviceSetting getSetting() {
        return this.setting;
    }

    public JSONObject getSpec() {
        return this.spec;
    }
}
