package controllers;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import models.rcccav.DeviceController;


public class Application extends Controller {
    private DeviceController controller = DeviceController.getInstance("conf/avconfig.json");

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
        synchronized(this) {
            Logger.debug("in powerSystem... action = " + action);
            String results = this.controller.powerSystem(DeviceController.SystemAction.valueOf(action));
            Logger.debug("finished call DeviceController.");
            return ok(results);
        }
    }

    /**
     * Switch video signal
     * @param source The source of video signal 
     * @param output The destination of video signal
     * @return Action result.
     */
    public Result switchVideoSignal(String source, String output){
        synchronized(this) {
            this.controller.doCommand(source, output);
            String results = this.controller.getActionResponse(source);
            return ok("Perform video switch on device: " + source + ". Action: " +
                      output + ".<br/>Results: " + results);
        }
    }
}
