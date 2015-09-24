package rcccav;

import java.util.logging.Logger;

import rcccav.DeviceController.SystemAction;

public class Test {
    private static final Logger LOG = Logger.getLogger(Test.class.getName());

    public static void main(String[] args) {
        LOG.info("Test started!");
        String config_file = args[0];
        DeviceController controller = DeviceController.getInstance(config_file);

        String[] deviceNames = DeviceController.getDeviceList();
        for (String name: deviceNames) {
            LOG.info(name);
        }

        //Call to act
        controller.powerSystem(SystemAction.ON);
    }

}
