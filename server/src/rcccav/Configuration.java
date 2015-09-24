package rcccav;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Configuration {

    private static final Logger LOG = Logger.getLogger(Configuration.class.getName());
    private JSONObject config = null;
    private HashMap<Integer, ArrayList<AVDevice>> devices = null;
    private HashMap<String, AVDevice> deviceByName = new HashMap<String, AVDevice>();

    public Configuration(String config_file) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(config_file));
            this.config = (JSONObject) obj;

            this.devices = new HashMap<Integer, ArrayList<AVDevice>>(3);

            JSONObject device_list = (JSONObject) this.config.get("devices");
            for (Object key: device_list.keySet()) {
                JSONObject item = (JSONObject) device_list.get(key);
                Integer seq = ((Long) item.get("powerSequence")).intValue();
                if (this.devices.get(seq) == null) {
                    this.devices.put(seq, new ArrayList<AVDevice>());
                }
                AVDevice device = new AVDevice(item);
                this.devices.get(seq).add(device);
                this.deviceByName.put((String) key, device);
            }
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
            LOG.log(Level.SEVERE, ex.getMessage());
        }
        catch (IOException ex) {
            ex.printStackTrace();
            LOG.log(Level.SEVERE, ex.getMessage());
        }
        catch (ParseException ex) {
            ex.printStackTrace();
            LOG.log(Level.SEVERE, ex.getMessage());
        }
    }

    public JSONObject getConfig() {
        return this.config;
    }

    public ArrayList<AVDevice> getDevicesBySequence(int sequence) {
        return this.devices.get(sequence);
    }

    public AVDevice getDevicesByName(String name) {
        return this.deviceByName.get(name);
    }
}
