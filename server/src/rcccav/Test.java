package rcccav;

import java.util.logging.Logger;

import rcccav.DeviceController.SystemAction;

public class Test {
    private static final Logger LOG = Logger.getLogger(Test.class.getName());

    public static void main(String[] args) {
        LOG.info("Test started!");
        String config_file = args[0];
        
        // Get a device controller instance by passing the configuration file
        DeviceController controller = DeviceController.getInstance(config_file);

        String[] deviceNames = DeviceController.getDeviceList();
        for (String name: deviceNames) {
            LOG.info(name);
        }

        // Call a specific command off of specific controller
        controller.doCommand("controller", "OFF");
        
        //Turn entire system on
        controller.powerSystem(SystemAction.ON);

        //Turn entire system off
        controller.powerSystem(SystemAction.OFF);
    }

}
