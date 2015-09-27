package models.rcccav.device;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class DeviceSetting {
    public String title = null;
    public String deviceId = null;
    public short powerSequence = 1;
    public HashMap<String, Integer> nParams = new HashMap<String, Integer>();
    public HashMap<String, String> sParams = new HashMap<String, String>();
    public HashMap<String, String> actions = new HashMap<String, String>();

    public DeviceSetting(JSONObject setting) {

        this.title = (String) setting.get("title");
        this.deviceId = (String) setting.get("deviceId");
        this.powerSequence = ((Long) setting.get("powerSequence")).shortValue();

        JSONObject settings = (JSONObject) setting.get("nParams");
        if (settings != null) {
            for (Object key: settings.keySet()) {
                Long lValue = (Long) settings.get(key);
                Integer value = new Integer(lValue.intValue());
                this.nParams.put((String)key, value);
            }
        }

        settings = (JSONObject) setting.get("sParams");
        if (settings != null) {
            for (Object key: settings.keySet()) {
                this.sParams.put((String)key, (String) settings.get(key));
            }
        }

        settings = (JSONObject) setting.get("actions");
        if (settings != null) {
            for (Object key: settings.keySet()) {
                this.actions.put((String)key, (String) settings.get(key));
            }
        }
    }
}
