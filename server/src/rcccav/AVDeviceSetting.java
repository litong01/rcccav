package rcccav;

import java.util.HashMap;
import org.json.simple.JSONObject;

/**
 * @author tongli
 * 
 * This class represents a device setting. its member should mirror the
 * configuration file item. Each device setting should be comply with
 * the following:
 * 
 *  "title": "VGA matrix switch",
    "deviceId": "/dev/tty1",
    "powerSequence": 1,
    "settings": {"BAUDRATE": 4800, "DATABITS": 8,
                 "STOPBITS": 1, "PARITY": 0,
                 "FLOWCONTROL": 0},
    "actions": {"ON": "",
                "OFF": ""
               },
 *
 */
public class AVDeviceSetting {

    public String title = null;
    public String deviceId = null;
    public short powerSequence = 1;
    public HashMap<String, Integer> settings = new HashMap<String, Integer>();
    public HashMap<String, byte[]> actions = new HashMap<String, byte[]>();

    public AVDeviceSetting(JSONObject setting) {

        this.title = (String) setting.get("title");
        this.deviceId = (String) setting.get("deviceId");
        this.powerSequence = ((Long) setting.get("powerSequence")).shortValue();

        JSONObject port_setting = (JSONObject) setting.get("settings");
        for (Object key: port_setting.keySet()) {
            Long lValue = (Long) port_setting.get(key);
            Integer value = new Integer(lValue.intValue());
            this.settings.put((String)key, value);
        }

        JSONObject port_actions = (JSONObject) setting.get("actions");
        for (Object key: port_actions.keySet()) {
            String cmdStr = (String) port_actions.get(key);
            byte[] cmd = null;
            String[] numbers = cmdStr.replace(" ", "").split(",");
            String charStr = new String();
            for (String number: numbers) {
                charStr += (char) Integer.decode(number).byteValue();
            }
            cmd = charStr.getBytes();
            this.actions.put((String) key, cmd);
        }
    }
}
