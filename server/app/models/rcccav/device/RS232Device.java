package models.rcccav.device;

import java.util.HashMap;
import java.util.logging.Logger;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

import org.json.simple.JSONObject;


public class RS232Device extends Device implements SerialPortEventListener{

    private static final Logger LOG = Logger.getLogger(RS232Device.class.getName());
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
                this.actionResult = "";
                this.initDevice();
                byte[] action = this.getCmdByte(cmd);
                this.serialPort.writeBytes(action);
            }
        }
        catch (Exception ex) {
            LOG.severe(ex.getMessage());
        }
    }

    @Override
    public void disconnect() {
        if (this.serialPort.isOpened()) {
            try {
                this.serialPort.removeEventListener();
                this.serialPort.closePort();
            }
            catch (SerialPortException ex) {
                ex.printStackTrace();
                LOG.severe(ex.getMessage());
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
                LOG.severe(ex.getMessage());
            } catch (SerialPortTimeoutException ex) {
                LOG.info("Not more data to read!");
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

    private void initDevice()
    {
        try {
            this.serialPort = new SerialPort(this.setting.deviceId);
            this.serialPort.openPort();
            HashMap<String, Integer> settings = this.setting.nParams;
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
}
