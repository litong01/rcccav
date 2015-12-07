package models.rcccav.device;

import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class VGASwitch extends RS232Device {

    private boolean onStatus = true;
    private String[] mappings = new String[]{"S1_A", "S1_B", "S1_C",
                                             "S1_D"};

    public VGASwitch(JSONObject spec) {
        super(spec);
    }

    @Override
    public void doCommand(String cmd) {
        super.doCommand(cmd);
        if (cmd.equals("ON") || cmd.equals("OFF"))
            this.onStatus = cmd.equals("ON");
        else if (cmd.startsWith("S")) {
            String[] parts = cmd.substring(1).split("_");
            this.mappings[(int) (parts[1].charAt(0)) - 65] = cmd;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject getStatus() {
        JSONObject newStatus = new JSONObject();
        newStatus.put("On", this.onStatus);
        JSONArray pos = new JSONArray();
        for (String mapping: this.mappings)
            pos.add(mapping);
        newStatus.put("Position", pos);
        JSONObject ns = new JSONObject();
        ns.put(this.setting.name, newStatus);
        return ns;
    }

}
