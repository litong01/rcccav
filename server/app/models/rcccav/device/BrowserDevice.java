package models.rcccav.device;

import java.io.File;
import org.json.simple.JSONObject;

public class BrowserDevice extends Device {

    protected String fileDir = "";

    public BrowserDevice(JSONObject spec) {
        this.spec = spec;
        this.setting = new DeviceSetting(this.spec);
        this.fileDir = this.setting.sParams.get("fileDir");
    }

    @Override
    public void doCommand(String cmd) {
        if ("GET_FILE_LIST".equals(cmd)) {
            this.getFileList();
        }
        else {
            this.checkFile(cmd);
        }
    }

    private void getFileList() {
        File fileDirectory = new File(this.fileDir);
        File[] fileList = fileDirectory.listFiles();
        String filename;
        this.actionResult = "";
        if (fileList.length > 0) {
            for (File mp3File : fileList) {
                filename = mp3File.getName();
                if (mp3File.isFile()) {
                    this.actionResult += "/" + filename;
                }
            }
        }
    }

    private void checkFile(String filename) {
        File file = new File(this.fileDir + "/" + filename);
        if (file.exists()) {
            this.actionResult = this.fileDir + "/" + filename;
        }
        else {
            this.actionResult = "";
        }
    }

    @Override
    public void disconnect() {
    }
}
