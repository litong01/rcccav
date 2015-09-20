
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortList;

import java.util.*;
import java.util.TooManyListenersException;

public class JSSCCmd implements SerialPortEventListener
{
    //static String TURNON    = "~0000 1\r";
    //static String TURNOFF   = "~0000 0\r";
    //static String GETINFO   = "~00150 1\r";

    /*
    static byte[] TURNON    = new byte[] {0x7E, 0x30, 0x31, 0x30, 0x30, 0x20, 0x31, 0x0D};
    static byte[] TURNOFF   = new byte[] {0x7E, 0x30, 0x31, 0x30, 0x30, 0x20, 0x30, 0x0D};
    */
    static byte[] TURNON    = new byte[] {0x79, 0x78, 0x0D};
    static byte[] TURNOFF   = new byte[] {0x79, 0x70, 0x70, 0x0D};
    static byte[] S1_TO_C    = new byte[] {0x67, 0x49, 0x0D};
    static byte[] S2_TO_C    = new byte[] {0x67, 0x50, 0x0D};
    static byte[] S3_TO_C    = new byte[] {0x67, 0x51, 0x0D};
    static byte[] S4_TO_C    = new byte[] {0x67, 0x52, 0x0D};
    
    
    static byte[] S1_ON     = "relay on 0\r".getBytes();
    static byte[] S1_OFF    = "relay off 0\r".getBytes();
    static byte[] S1_READ   = "relay read 0\r".getBytes();
    static byte[] S2_ON     = "relay on 1\r".getBytes();
    static byte[] S2_OFF    = "relay off 1\r".getBytes();
    static byte[] S2_READ   = "relay read 1\r".getBytes();
    static byte[] S_GETINFO = "id get\r".getBytes();

    
    static Map<String, byte[]> commands = new HashMap<String, byte[]>();
    static {
        commands.put("TURNON", TURNON);
        commands.put("TURNOFF", TURNOFF);
        commands.put("1_TO_C", S1_TO_C);
        commands.put("2_TO_C", S2_TO_C);
        commands.put("3_TO_C", S3_TO_C);
        commands.put("4_TO_C", S4_TO_C);

        commands.put("S1_ON", S1_ON);
        commands.put("S1_OFF", S1_OFF);
        commands.put("S1_READ", S1_READ);
        commands.put("S2_ON", S2_ON);
        commands.put("S2_OFF", S2_OFF);
        commands.put("S2_READ", S2_READ);
        commands.put("S_GETINFO", S_GETINFO);
    }


    private SerialPort serialPort;

    public JSSCCmd(String portName)
    {
        try {
            this.serialPort = new SerialPort(portName);
            this.serialPort.openPort();
            this.serialPort.setParams( //SerialPort.BAUDRATE_9600,
                                      SerialPort.BAUDRATE_4800,
                                      SerialPort.DATABITS_8,
                                      SerialPort.STOPBITS_1,
                                      SerialPort.PARITY_NONE);
            this.serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            
            System.out.println("Port opened OK");
        }
        catch (SerialPortException e) {
            System.out.println(e.getMessage());
        }
        
        this.initListener();
    }
    
    public void initListener()
    {
        try
        {
            this.serialPort.addEventListener(this);
            System.out.println("Port listener added successfully!");
        }
        catch (SerialPortException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    public void disconnect() {
        if (this.serialPort.isOpened()) {
            try {
                this.serialPort.closePort();
                System.out.println("port closed ok!");
            }
            catch (SerialPortException spe) {
                System.out.println(spe.getMessage());
            }
        }
    }

    public void writeData(byte[] data)
    {
        try {
            System.out.println("Ready to send the following command:");
            for (int i=0; i < data.length; i++) {
                System.out.print(String.format("%02x", data[i]) + " ");
            }
            System.out.println("");
            this.serialPort.writeBytes(data);
            System.out.println("write is OK!");
        }
        catch (Exception ee) {
            System.out.println(ee.getMessage());
        }
    }

    public void serialEvent(SerialPortEvent event) {
        System.out.println("Got called!");
        if (event.isRXCHAR()) {
            try {
                byte buffer[] = this.serialPort.readBytes(8);
                for (int i=0; i < buffer.length; i++) {
                    System.out.println(String.format("%02x", buffer[i]));
                }
            }
            catch (SerialPortException ex) {
                System.out.println(ex);
            }
        }
        else if (event.isCTS()) {
            if(event.getEventValue() == 1){//If line is ON
                System.out.println("CTS - ON");
            }
            else {
                System.out.println("CTS - OFF");
            }
        }
        else if (event.isDSR()) {
            if(event.getEventValue() == 1){//If line is ON
                System.out.println("DSR - ON");
            }
            else {
                System.out.println("DSR - OFF");
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Started! " + args[0]);

        String[] portNames = portNames = SerialPortList.getPortNames();

        for (int i = 0; i < portNames.length; i++) {
            System.out.println(portNames[i]);
        }
        
        JSSCCmd myport = new JSSCCmd(args[0]);
        myport.writeData(commands.get(args[1]));

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                myport.disconnect();
            }
        });
    }
}
