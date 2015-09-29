package models.rcccav;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import play.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import models.rcccav.device.Device;

public class Configuration {

    private JSONObject config = null;
    private HashMap<Integer, ArrayList<Device>> devices = null;
    private HashMap<String, Device> deviceByName = new HashMap<String, Device>();

    public Configuration(String config_file) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(config_file));
            this.config = (JSONObject) obj;

            this.devices = new HashMap<Integer, ArrayList<Device>>(3);

            JSONObject device_list = (JSONObject) this.config.get("devices");
            for (Object key: device_list.keySet()) {
                JSONObject item = (JSONObject) device_list.get(key);
                Boolean enabled = (Boolean) item.get("enabled");
                if (!enabled) continue;
                Integer seq = ((Long) item.get("powerSequence")).intValue();
                if (this.devices.get(seq) == null) {
                    this.devices.put(seq, new ArrayList<Device>());
                }
                String deviceType = (String) item.get("deviceType");
                Class<?> deviceClass = Class.forName("models.rcccav.device." + deviceType);
                Constructor<?> ctor = deviceClass.getConstructor(JSONObject.class);
                Device device = (Device) ctor.newInstance(item);
                this.devices.get(seq).add(device);
                this.deviceByName.put((String) key, device);
            }
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
            Logger.error(ex.getMessage());
        }
        catch (IOException ex) {
            ex.printStackTrace();
            Logger.error(ex.getMessage());
        }
        catch (ParseException ex) {
            ex.printStackTrace();
            Logger.error(ex.getMessage());
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Logger.error(ex.getMessage());
        }
    }

    public JSONObject getConfig() {
        return this.config;
    }

    public ArrayList<Device> getDevicesBySequence(int sequence) {
        return this.devices.get(sequence);
    }

    public Device getDevicesByName(String name) {
        return this.deviceByName.get(name);
    }
    
    public Set<String> getDeviceList() {
        return this.deviceByName.keySet();
    }
}
