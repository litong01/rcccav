package models.rcccav.device;

import java.util.HashMap;

import play.Logger;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import org.json.simple.JSONObject;


public class RS232Device extends Device implements SerialPortEventListener{

    private SerialPort serialPort = null;

    public RS232Device(JSONObject spec) {
        this.spec = spec;
        this.setting = new DeviceSetting(this.spec);
    }

    private byte[] getCmdByte(String cmd) {
        String cmdStr = this.setting.actions.get(cmd);
        String[] numbers = cmdStr.replace(" ", "").split(",");
        if (numbers.length > 0) {
            String charStr = new String();
            for (String number: numbers) {
                charStr += (char) Integer.decode(number).byteValue();
            }
            return charStr.getBytes();
        }
        else return null;
    }

    @Override
    public void doCommand(String cmd) {
        try {
            if (this.setting.actions.containsKey(cmd)) {
                Logger.info("Ready to execute command: " + cmd);
                byte[] action = this.getCmdByte(cmd);
                this.initDevice();
                this.serialPort.writeBytes(action);
                Logger.info(this.setting.title + " executed " + cmd + " request successfully!");
            }
            else {
                Logger.info(this.setting.title + " does not support command " + cmd);
            }
        }
        catch (Exception ex) {
            this.actionResult = this.setting.title + " " + ex.getMessage();
            Logger.error(this.setting.title + " " + ex.getMessage());
        }
    }

    @Override
    public void disconnect() {
        if (this.serialPort.isOpened()) {
            try {
                this.serialPort.removeEventListener();
                this.serialPort.closePort();
                Logger.info("Device " + this.setting.title + " is now disconnected!");
            }
            catch (SerialPortException ex) {
                Logger.error(this.setting.title + " " + ex.getMessage());
            }
        }
    }

    public void serialEvent(SerialPortEvent event) {
        try {
            byte buffer[] = this.serialPort.readBytes();
            for (int i=0; i < buffer.length; i++) {
                this.actionResult += String.format("%02x", buffer[i]);
            }
        }
        catch (SerialPortException ex) {
            this.actionResult = "ERROR:" + ex.getMessage();
            Logger.error(this.setting.title + " " + this.actionResult);
        }
    }

    private void initDevice()
    {
        try {
            Logger.info("Start initialize device " + this.setting.title);
            this.actionResult = "";
            this.serialPort = new SerialPort(this.setting.deviceId);
            this.serialPort.openPort();
            HashMap<String, Integer> settings = this.setting.nParams;
            this.serialPort.setParams(settings.get("BAUDRATE"),
                                      settings.get("DATABITS"),
                                      settings.get("STOPBITS"),
                                      settings.get("PARITY"));
            this.serialPort.setFlowControlMode(settings.get("FLOWCONTROL"));
            Logger.info(this.setting.title + " is now initialized!");
        }
        catch (SerialPortException ex) {
            this.actionResult = "ERROR:" + ex.getMessage();
            Logger.error(this.setting.title + " " + this.actionResult);
            return;
        }
        this.initListener();
    }

    private void initListener()
    {
        try {
            this.serialPort.addEventListener(this);
            Logger.info("Port listener added successfully!");
        }
        catch (SerialPortException ex) {
            this.actionResult = "ERROR:" + ex.getMessage();
            Logger.error(this.setting.title + " " + this.actionResult);
        }
    }
}
