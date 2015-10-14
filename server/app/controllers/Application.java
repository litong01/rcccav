package controllers;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import models.rcccav.DeviceController;


public class Application extends Controller {
    private DeviceController controller = DeviceController.getInstance("conf/avconfig.json");
    private boolean command_done = true;

    public Result index() {
        return ok(views.html.index.render("Something"));
    }

    /**
     * Whole system actions
     * @param action The system action. Valid values are ON, OFF, REBOOT and FORCE_OFF
     * @return Action result.
     */
    public Result powerSystem(String action){
        Logger.info(this.getClass().getSuperclass().getName());
        if (this.command_done) {
            this.command_done = false;
            Logger.debug("in powerSystem... action = " + action);
            String results = this.controller.powerSystem(DeviceController.SystemAction.valueOf(action));
            Logger.debug("finished call DeviceController.");
            this.command_done = true;
            return ok(results);
        }
        else {
            return ok("Previous command is still going! Try again in few seconds.");
        }
    }

    /**
     * Switch video signal
     * @param source The source of video signal 
     * @param output The destination of video signal
     * @return Action result.
     */
    public Result switchVideoSignal(String source, String output){
        if (this.command_done) {
            this.command_done = false;
            this.controller.doCommand(source, output);
            String results = this.controller.getActionResponse(source);
            this.command_done = true;
            return ok("Switching video signal from " + source + " to " +
                      output + ". Results are " + results);
        }
        else {
            return ok("Previous command is still going! Try again in few seconds.");
        }
    }
}
