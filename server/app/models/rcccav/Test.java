package models.rcccav;

import java.util.Set;
import java.util.logging.Logger;

import models.rcccav.DeviceController.SystemAction;

public class Test {
    private static final Logger LOG = Logger.getLogger(Test.class.getName());

    public static void main(String[] args) {
        LOG.info("Test started!");
        String config_file = args[0];

        // Get a device controller instance by passing the configuration file
        DeviceController controller = DeviceController.getInstance(config_file);

        Set<String> deviceNames = controller.getDeviceList();
        for (String name: deviceNames) {
            LOG.info(name);
        }

        // Execute "OFF" command on a device named "controller"
        controller.doCommand("projector_front_center", "OFF");

        // Get the response of the action on the device named "controller"
        String actionResponse = controller.getActionResponse("projector_front_center");
        LOG.info(actionResponse);

        //Turn entire system on
        //controller.powerSystem(SystemAction.ON);

        //Turn entire system off
        //controller.powerSystem(SystemAction.OFF);
        try { Thread.sleep(6000);} catch (InterruptedException ex) {}
    }

}
