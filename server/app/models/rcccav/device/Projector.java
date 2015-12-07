package models.rcccav.device;

import org.json.simple.JSONObject;

public class Projector extends RS232Device {

    private boolean onStatus = true;
    private boolean unFreezed = true;

    public Projector(JSONObject spec) {
        super(spec);
    }

    @Override
    public void doCommand(String cmd) {
        super.doCommand(cmd);
        if (cmd.equals("ON") || cmd.equals("OFF"))
            this.onStatus = cmd.equals("ON");
        else if (cmd.startsWith("FREEZE_O"))
            this.unFreezed = cmd.equals("FREEZE_OFF");
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject getStatus() {
        JSONObject newStatus = new JSONObject();
        newStatus.put("On", this.onStatus);
        newStatus.put("Unfreezed", this.unFreezed);
        return newStatus;
    }

}
