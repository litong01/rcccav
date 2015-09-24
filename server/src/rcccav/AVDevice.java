package rcccav;

import java.util.HashMap;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;


public class AVDevice implements SerialPortEventListener {

    private static final Logger LOG = Logger.getLogger(AVDevice.class.getName());
    private JSONObject spec = null;
    private SerialPort serialPort = null;
    private AVDeviceSetting setting = null;
    private String actionResult = "";

    public AVDevice(JSONObject spec) {
        this.spec = spec;
        this.setting = new AVDeviceSetting(this.spec);
    }

    public String getActionResult() {
        return this.actionResult;
    }

    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR()) {
            try {
                byte buffer[] = this.serialPort.readBytes(8);
                for (int i=0; i < buffer.length; i++) {
                    this.actionResult += String.format("%02x", buffer[i]);
                }
            }
            catch (SerialPortException ex) {
                ex.printStackTrace();
                LOG.severe(ex.getMessage());
            }
        }
        else if (event.isCTS()) {
            if(event.getEventValue() == 1){//If line is ON
                LOG.info("CTS - ON");
            }
            else {
                LOG.info("CTS - OFF");
            }
        }
        else if (event.isDSR()) {
            if(event.getEventValue() == 1){//If line is ON
                LOG.info("DSR - ON");
            }
            else {
                LOG.info("DSR - OFF");
            }
        }
    }

    public void doCommand(String command)
    {
        try {
            if (this.setting.actions.containsKey(command)) {
                this.actionResult = "";
                this.initDevice();
                byte[] action = this.setting.actions.get(command);
                this.serialPort.writeBytes(action);
            }
        }
        catch (Exception ex) {
            LOG.severe(ex.getMessage());
        }
    }

    private void initDevice()
    {
        try {
            this.serialPort = new SerialPort(this.setting.deviceId);
            this.serialPort.openPort();
            HashMap<String, Integer> settings = this.setting.settings;
            this.serialPort.setParams(settings.get("BAUDRATE"),
                                      settings.get("DATABITS"),
                                      settings.get("STOPBITS"),
                                      settings.get("PARITY"));
            this.serialPort.setFlowControlMode(settings.get("FLOWCONTROL"));
        }
        catch (SerialPortException ex) {
            ex.printStackTrace();
            LOG.severe(ex.getMessage());
        }
        this.initListener();
    }

    private void initListener()
    {
        try {
            this.serialPort.addEventListener(this);
            LOG.info("Port listener added successfully!");
        }
        catch (SerialPortException ex) {
            ex.printStackTrace();
            LOG.severe(ex.getMessage());
        }
    }

    public void disconnect() {
        if (this.serialPort.isOpened()) {
            try {
                this.serialPort.closePort();
            }
            catch (SerialPortException ex) {
                ex.printStackTrace();
                LOG.severe(ex.getMessage());
            }
        }
    }
}
