package models.rcccav.device;

import java.util.HashMap;
import play.Logger;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

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
                this.actionResult = "";
                this.initDevice();
                byte[] action = this.getCmdByte(cmd);
                this.serialPort.writeBytes(action);
                Logger.info(this.setting.title + " is now " + cmd);
            }
            else {
                Logger.info(this.setting.title + " does not support command " + cmd);
            }
        }
        catch (Exception ex) {
            Logger.error(ex.getMessage());
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
                ex.printStackTrace();
                Logger.error(ex.getMessage());
            }
        }
    }

    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR()) {
            try {
                byte buffer[] = this.serialPort.readBytes(1, 80);
                for (int i=0; i < buffer.length; i++) {
                    this.actionResult += String.format("%02x", buffer[i]);
                }
            }
            catch (SerialPortException ex) {
                ex.printStackTrace();
                Logger.error(ex.getMessage());
            } catch (SerialPortTimeoutException ex) {
                Logger.info("Not more data to read!");
            }
        }
        else if (event.isCTS()) {
            if(event.getEventValue() == 1){//If line is ON
                Logger.info("CTS - ON");
            }
            else {
                Logger.info("CTS - OFF");
            }
        }
        else if (event.isDSR()) {
            if(event.getEventValue() == 1){//If line is ON
                Logger.info("DSR - ON");
            }
            else {
                Logger.info("DSR - OFF");
            }
        }
    }

    private void initDevice()
    {
        try {
            Logger.info("Start initialize device " + this.setting.title);
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
            ex.printStackTrace();
            Logger.error(ex.getMessage());
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
            ex.printStackTrace();
            Logger.error(ex.getMessage());
        }
    }
}
