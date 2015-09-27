package rcccav;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;

import jssc.SerialPortList;

import rcccav.device.Device;

public class DeviceController {

    private static final Logger LOG = Logger.getLogger(DeviceController.class.getName());
    private static final int LEVELS_SUPPORTED = 10;
    private static DeviceController instance = null;
    private Configuration config = null;

    public enum SystemAction {
        ON, OFF, REBOOT, FORCE_OFF
    }

    protected DeviceController(String config_file) {
        this.config = new Configuration(config_file);
    }

    /**
     * @param config_file The full path to the configuration file
     * @return DeviceController instance. This method allows DeviceController
     * to be a singleton in the system.
     */
    public static DeviceController getInstance(String config_file) {
        if (instance == null) {
            instance = new DeviceController(config_file);
        }
        return instance;
    }

    /**
     * @param deviceName the device name which is specified in the avconfig.json file
     * @param command the command to be executed.
     * @return
     * 
     * This method executes the specified command on the given device. The
     * method returns nothing on purpose. To get the command response, call
     * getActionResponse method.
     */
    public void doCommand(String deviceName, String command) {
        Device device = this.config.getDevicesByName(deviceName);
        if (device != null) {
            device.doCommand(command);
            // Wait for the command to complete
            try { Thread.sleep(100);} catch (InterruptedException ex) {}
            device.disconnect();
        }
    }

    public Set<String> getDeviceList() {
        return this.config.getDeviceList();
    }

    /**
     * @param deviceName The device name.
     * @return String returns the string which is the result of the command
     * execution.
     */
    public String getActionResponse(String deviceName) {
        Device device = this.config.getDevicesByName(deviceName);
        if (device != null) {
            return device.getActionResult();
        }
        return "";
    }

    /**
     * @param action the action to be performed by the controller, the valid
     * actions are ON, OFF, REBOOT and FORCE_OFF
     * @return the result of the action
     */
    public String powerSystem(SystemAction action) {
        switch (action) {
        case ON:
            this.doOnOff("ON");
            break;
        case OFF:
            this.doOnOff("OFF");
            break;
        case REBOOT:
            break;
        case FORCE_OFF:
            break;
        }
        return "";
    }

    private void doOnOff(String cmd) {
        int start, increase;
        if (cmd == "ON") {
            start = 1; increase = 1;
        }
        else {
            start = LEVELS_SUPPORTED; increase = -1;
        }

        //We only support up to LEVELS_SUPPORTED levels of delays
        for (int index=0; index<=LEVELS_SUPPORTED; index++) {
            ArrayList<Device> devices = this.config.getDevicesBySequence(start);
            if (devices != null) {
                for (Device device: devices) {
                    device.doCommand(cmd);
                }
                try {
                    int sleeptime = ((Long)this.config.getConfig().get("delay")).intValue();
                    Thread.sleep(sleeptime * 1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    LOG.severe(ex.getMessage());
                }
            }
            start += increase;
        }
    }

    /**
     * @return The list of the serial ports available in the system
     */
    public static String[] getSerialDeviceList() {
        return SerialPortList.getPortNames();
    }
}
