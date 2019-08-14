package main.java.controller;

import java.io.File;
import java.util.ArrayList;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.entity.Device;
import main.java.utils.FileUtils;
import main.java.utils.FtpUtils;

/**
 * FTP
 */
public class FTPController {
    private GridPane ftpPane;
    private Text text_record;
    private String fileDirRm = "";

    public FTPController(GridPane ftpPane) {
        this.ftpPane = ftpPane;
    }

    public void init() {
        Label dir_pc = new Label("本地目录");
        Label dir_target = new Label("发送目录");
        dir_pc.setFont(Font.font("monaco", FontWeight.NORMAL, 20));
        dir_target.setFont(Font.font("monaco", FontWeight.NORMAL, 20));
        TextField dir_pc_value = new TextField();
        dir_pc_value.setPromptText("directory path");
        TextField dir_target_value = new TextField();
        dir_target_value.setPromptText("default:/ftp/");
        dir_target_value.setText("/ftp/");
        Button choiceDir_bt = new Button("选择文件夹");
        Button choiceFile_bt = new Button("选择文件");
        Button default_bt = new Button("恢复默认");
        Button startFtp = new Button("发送");
        Text ftpToast = new Text();
        ftpToast.setFill(Color.FIREBRICK);
        text_record = new Text();
        text_record.setFill(Color.FIREBRICK);

        ftpPane.add(dir_pc, 0, 0);
        ftpPane.add(dir_pc_value, 1, 0);
        ftpPane.add(dir_target, 0, 1);
        ftpPane.add(dir_target_value, 1, 1);
        ftpPane.add(default_bt, 2, 1);
        //ftpPane.add(choiceDir_bt, 2, 0);
        ftpPane.add(choiceFile_bt, 2, 0);
        ftpPane.add(startFtp, 1, 2);
        ftpPane.add(ftpToast, 1, 3);
        ftpPane.add(text_record, 1, 4);

        choiceDir_bt.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("选择文件夹");
            File file = directoryChooser.showDialog(new Stage());
            if (file != null) {
                System.out.println("选择文件夹：" + file.getAbsolutePath());
                dir_pc_value.setText(file.getAbsolutePath());
            }
        });
        choiceFile_bt.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择文件");
            if (!fileDirRm.isEmpty())
                fileChooser.setInitialDirectory(new File(fileDirRm).getParentFile());
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                fileDirRm = file.getAbsolutePath();
                System.out.println("选择文件：" + file.getAbsolutePath());
                dir_pc_value.setText(file.getAbsolutePath());
            }
        });
        default_bt.setOnAction(event -> dir_target_value.setText("/ftp/"));

        startFtp.setOnAction(event -> {
            ArrayList<Device> devicesList = FileUtils.readSelectDevices();
            if (devicesList.size() == 0) {
                ftpToast.setFill(Color.FIREBRICK);
                ftpToast.setText("目标设备未确定!");
            } else
                for (Device devices : devicesList) {
                    new FTPThread(devices.getIp(), dir_pc_value.getText()).start();
                }
        });
    }

    /**
     * FTP发送线程(IP) <br> todo 测试：FTPutil是否支持多线程调用
     */
    private class FTPThread extends Thread {
        String ip;
        String path;

        FTPThread(String ip, String path) {
            setName("ftp send thread -" + ip);
            this.ip = ip;
            this.path = path;
        }

        @Override
        public void run() {
            super.run();
            FtpUtils ftpUtils = null;
            try {
                ftpUtils = new FtpUtils(ip);
                File file = new File(path);
                if (file.exists()) {
                    if (ftpUtils.uploadFile("/ftp/", file.getName(), path)) {
                        appendRecord(ip+"传输成功!");
                    }else {
                        appendRecord(ip+"传输成功!");
                    }
                } else {
                    appendRecord("FTP文件目录不存在!");
                }
            } finally {
                if (ftpUtils != null)
                    ftpUtils.disConnect();
            }
        }
    }

    private void appendRecord(String s) {
        String old = text_record.getText();
        text_record.setText(old + "\n" + s);
    }

}
