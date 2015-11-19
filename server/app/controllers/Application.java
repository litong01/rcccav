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
     * Perform action on a give device
     * @param device The AV device which the action will be performed 
     * @param action The action the device will perform.
     * @return Action result.
     */
    public Result switchVideoSignal(String device, String action){
        synchronized(this) {
            this.controller.doCommand(device, action);
            String results = this.controller.getActionResponse(device);
            return ok("Perform video switch on device: " + device + ". Action: " +
                      action + ".<br/>Results: " + results);
        }
    }
}
