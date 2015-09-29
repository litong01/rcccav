package controllers;

import org.json.simple.JSONObject;

import play.*;
import play.mvc.*;
import views.html.*;
import models.rcccav.*;


public class Application extends Controller {
    private DeviceController controller = DeviceController.getInstance("conf/avconfig.json");

    public Result index() {
        return ok("Welcome to RCCC AV!");
    }

    /**
     * Whole system actions
     * @param action The system action. Valid values are ON, OFF, REBOOT and FORCE_OFF
     * @return Action result.
     */
    public Result powerSystem(String action){
        Logger.debug("in powerSystem... action = " + action);
        this.controller.powerSystem(DeviceController.SystemAction.valueOf(action));
        Logger.debug("finished call DeviceController.");
        return ok("System " + action + " succeeds");

    }

    /**
     * Switch video signal
     * @param source The source of video signal 
     * @param output The destination of video signal
     * @return Action result.
     */
    public Result switchVideoSignal(String source, String output){
        this.controller.doCommand(source, output);
        String results = this.controller.getActionResponse(source);
        return ok("Switching video signal from " + source + " to " + output +
                  ". Results are " + results);
    }
}
