package models.rcccav;


import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Set;

import play.Logger;
import jssc.SerialPortList;
import models.rcccav.device.Device;
import models.rcccav.device.RecorderDevice;

public class DeviceController {

    private static final int LEVELS_SUPPORTED = 10;
    private static DeviceController instance = null;
    private RecorderDevice recorder = null;
    private Configuration config = null;
    private ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();


    public enum SystemAction {
        ON, OFF, REBOOT, FORCE_OFF
    }

    public class TaskRunner implements Runnable {
        private DeviceController controller = null;
        public TaskRunner(DeviceController controller) {
            this.controller = controller;
        }
        public void run() {
            this.controller.processAudio();
        }
    }

    protected DeviceController(String config_file) {
        this.config = new Configuration(config_file);
        TaskRunner runner = new TaskRunner(this);
        this.recorder = (RecorderDevice) this.config.getDevicesByName("recorder");
        this.scheduler.scheduleWithFixedDelay(
                runner, this.recorder.delayBetweenTasks,
                this.recorder.delayBetweenTasks, TimeUnit.SECONDS);
    }

    /**
     * @param config_file The full path to the configuration file
     * @return DeviceController instance. This method allows DeviceController
     * to be a singleton in the system.
     */
    public static DeviceController getInstance(String config_file) {
        Logger.info("Getting instance based on configuration file " + config_file);
        if (instance == null) {
            instance = new DeviceController(config_file);
            Logger.info("DeviceController instance was created successfully!");
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
            Logger.info("Device " + deviceName + " was found in the configuration");
            device.doCommand(command);
            // Wait for the command to complete
            try { Thread.sleep(100);} catch (InterruptedException ex) {}
            device.disconnect();
        }
        else {
            Logger.info("Device " + deviceName + " was not found!");
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
            Logger.info("Device " + deviceName + " was found in the configuration");
            return device.getActionResult();
        }
        else {
            Logger.info("Device " + deviceName + " was not found or may not be enabled!");
            return "Device " + deviceName + " was not found or may not be enabled!";
        }
    }

    /**
     * @param action the action to be performed by the controller, the valid
     * actions are ON, OFF, REBOOT and FORCE_OFF
     * @return the result of the action
     */
    public String powerSystem(SystemAction action) {
        String results = "";
        switch (action) {
        case ON:
            results = this.doOnOff("ON");
            break;
        case OFF:
            results = this.doOnOff("OFF");
            break;
        case REBOOT:
            break;
        case FORCE_OFF:
            break;
        }
        return results;
    }

    private String doOnOff(String cmd) {
        int start, increase;
        if (cmd == "ON") {
            start = 1; increase = 1;
        }
        else {
            start = LEVELS_SUPPORTED; increase = -1;
        }

        //We only support up to LEVELS_SUPPORTED levels of delays
        String results = "";
        for (int index=0; index<=LEVELS_SUPPORTED; index++) {
            ArrayList<Device> devices = this.config.getDevicesBySequence(start);
            if (devices != null) {
                for (Device device: devices) {
                    device.doCommand(cmd);
                    results += device.getActionResult() + "<br/>";
                }
                try {
                    int sleeptime = ((Long)this.config.getConfig().get("delay")).intValue();
                    Thread.sleep(sleeptime * 1000);
                } catch (InterruptedException ex) {
                    Logger.error(ex.getMessage());
                }
            }
            start += increase;
        }
        return results;
    }

    /**
     * @return The list of the serial ports available in the system
     */
    public static String[] getSerialDeviceList() {
        return SerialPortList.getPortNames();
    }

    public void processAudio() {
        Logger.debug("Audio convertion and uploading process started!");
        try {
            this.recorder.doWavToMP3("2MP3");
            this.recorder.doMP3Uploads();
        }
        catch (Exception e) {
            Logger.error(e.getMessage());
        }
        Logger.debug("Audio convertion and uploading process finised!");
    }
}
