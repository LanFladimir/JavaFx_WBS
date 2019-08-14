package main.java.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import main.java.entity.Device;
import main.java.impl.HttpCallBack;
import main.java.utils.FileUtils;
import main.java.utils.FtpUtils;
import main.java.utils.HttpUtils;
import main.java.utils.SystemUtil;

/**
 * 控制操作
 */
public class CtrlController implements HttpCallBack {
    private GridPane ctrlPane;
    private String operationTAG;
    private HttpUtils mOkHttp;
    private Text text_record, text_toast;
    private ArrayList<String> urls = new ArrayList<>();
    private int callBackNumber = 0;
    private boolean requesting = false;
    private TextField app_filepath;

    public CtrlController(GridPane ctrlPane) {
        this.ctrlPane = ctrlPane;
    }

    public void init() {
        Label label_app = new Label("程序");
        Label label_device = new Label("设备");
        Label label_record = new Label("操作记录");
        Label label_apps = new Label("常用app");
        Text label_apps_tip = new Text("(点击复制包名)");
        label_apps_tip.setFill(Color.FIREBRICK);
        label_apps_tip.setFont(Font.font("monaco", FontWeight.NORMAL, 12));
        label_app.setFont(Font.font("monaco", FontWeight.NORMAL, 20));
        label_device.setFont(Font.font("monaco", FontWeight.NORMAL, 20));
        label_apps.setFont(Font.font("monaco", FontWeight.NORMAL, 20));
        label_record.setFont(Font.font("monaco", FontWeight.NORMAL, 20));

        app_filepath = new TextField();
        app_filepath.setPromptText("apk路径");
        TextField app_uninstall = new TextField();
        app_uninstall.setPromptText("package name");
        TextField app_openclose = new TextField();
        app_openclose.setPromptText("package name");
        TextField app_auto = new TextField();
        app_auto.setPromptText("package name");
        text_record = new Text();
        text_record.setFill(Color.FIREBRICK);
        text_toast = new Text();
        text_toast.setFill(Color.FIREBRICK);

        Button bt_install = new Button("安装");
        Button bt_uninstall = new Button("卸载");
        Button bt_open = new Button("打开");
        Button bt_close = new Button("关闭");
        Button bt_auto_yes = new Button("自启");
        Button bt_auto_no = new Button("取消");
        Button bt_synctime = new Button("同步时间");
        Button bt_restart = new Button("重启");

        ctrlPane.add(label_app, 0, 0);
        ctrlPane.add(app_filepath, 1, 1);
        ctrlPane.add(app_uninstall, 1, 2);
        ctrlPane.add(app_openclose, 1, 3);
        ctrlPane.add(app_auto, 1, 4);
        ctrlPane.add(bt_install, 2, 1);
        ctrlPane.add(bt_uninstall, 2, 2);
        ctrlPane.add(bt_open, 2, 3);
        ctrlPane.add(bt_close, 3, 3);
        ctrlPane.add(bt_auto_yes, 2, 4);
        ctrlPane.add(bt_auto_no, 3, 4);
        ctrlPane.add(label_device, 0, 5);
        ctrlPane.add(bt_restart, 1, 5);
        ctrlPane.add(bt_synctime, 1, 6);
        ctrlPane.add(label_record, 4, 0);
        ctrlPane.add(text_record, 4, 1, 1, 10);
        ctrlPane.add(text_toast, 1, 0);
        ctrlPane.add(label_apps, 0, 7);
        ctrlPane.add(label_apps_tip, 1, 7);

        Text package_wbs = new Text("WBS(后台控制)");
        Text package_webbrowser = new Text("WebBrowser(网站显示)");
        Text package_constructer = new Text("SConstructer(构造程序)");
        Text package_bjpj = new Text("SConstructer(构造程序)");
        ctrlPane.add(package_wbs, 1, 8);
        ctrlPane.add(package_webbrowser, 1, 9);
        ctrlPane.add(package_constructer, 1, 10);

        //方便输入
        package_wbs.setOnMouseClicked(event -> SystemUtil.copyToClipBoare("com.scdz.wbs"));
        package_webbrowser.setOnMouseClicked(event -> SystemUtil.copyToClipBoare("com.scdz.webbrowser"));
        package_constructer.setOnMouseClicked(event -> SystemUtil.copyToClipBoare("com.scdz.constructer"));

        mOkHttp = new HttpUtils(this);
        bt_install.setOnAction(event -> {
            if (requesting)
                text_toast.setText("请等待队列完成!");
            else {
                String filePath = app_filepath.getText().replaceAll(" ", "");
                if (filePath.length() == 0) {
                    text_toast.setText("检查apk路径!");
                } else {
                    //ftp
                    ArrayList<Device> devices = FileUtils.readSelectDevices();
                    System.out.println("devices： " + devices.size());
                    new Thread(() -> {
                        FtpUtils ftpUtils;
                        urls = new ArrayList<>();
                        for (Device ignored : devices) {
                            urls.add("");
                        }
                        callBackNumber = 0;
                        requesting = true;
                        text_record.setText("");
                        text_toast.setText("");
                        operationTAG = "安装";
                        for (Device device : devices) {
                            try {
                                ftpUtils = new FtpUtils(device.getIp());
                                File file = new File(app_filepath.getText());
                                if (file.exists()) {
                                    if (file.isFile()) {
                                        if (ftpUtils.uploadFile("/ftp/", file.getName(), file.getAbsolutePath())) {
                                            updateRecord("IP：" + device.getIp() + "上传成功");
                                            mOkHttp.getRequest("install",
                                                    "http://" + device.getIp() + ":9090/install?file=" + "/mnt/sdcard/scdz/ftp/" + file.getName());
                                        }
                                    } else {
                                        updateRecord("当前仅支持单个文件传输!");
                                    }
                                } else {
                                    text_toast.setText("文件不存在!");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("FTP异常: " + e.getMessage());
                                text_toast.setText("FTP异常!");
                            }
                        }
                    }).start();
                }
            }
        });
        bt_uninstall.setOnAction(event -> {
            String packageName = app_uninstall.getText();
            request(packageName, ":9090/uninstall?file=" + packageName, "卸载");
        });
        bt_open.setOnAction(event -> {
            String packageName = app_openclose.getText();
            request(packageName, ":9090/runfile?file=" + packageName, "开启");
        });
        bt_close.setOnAction(event -> {
            String packageName = app_openclose.getText();
            request(packageName, ":9090/closefile?file=" + packageName, "关闭");
        });
        bt_auto_yes.setOnAction(event -> {
            String packageName = app_auto.getText();
            request(packageName, ":9090/setstart?file=" + packageName + "&flag=true", "设置自启");
        });
        bt_auto_no.setOnAction(event -> {
            String packageName = app_auto.getText();
            request(packageName, ":9090/setstart?file=" + packageName + "&flag=false", "取消自启");
        });
        bt_restart.setOnAction(event -> {
            String packageName = "restart device";
            request(packageName, ":9090/reset", "设备重启");
        });
        bt_synctime.setOnAction(event -> {
            //年月日.时分秒
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd.HHmmss");
            String packageName = "set time";
            System.out.println("time = " + format.format(System.currentTimeMillis()));
            request(packageName, ":9090/settime?datetime=" +
                    format.format(System.currentTimeMillis()), "时间同步");
        });
    }

    /**
     * 请求统一方法
     */
    private void request(String packageName, String urlPart, String tag) {
        if (!requesting) {
            callBackNumber = 0;
            text_record.setText("");
            text_toast.setText("");
            operationTAG = tag;
            if (!packageName.isEmpty()) {
                urls = new ArrayList<>();
                for (Device device : FileUtils.readSelectDevices()) {
                    urls.add("http://" + device.getIp() + urlPart);
                }
                if (urls.size() > 0) {
                    callBackNumber = 0;
                    text_record.setText("");
                    text_toast.setText("");

                    requesting = true;
                    for (String url : urls)
                        mOkHttp.getRequest(tag, url);
                } else {
                    text_toast.setText("请勾选至少一个设备!");
                }
            } else {
                text_toast.setText("包名不能为空!");
            }
        } else {
            text_toast.setText("请等待队列完成!");
        }
    }

    @Override
    public void onSuccess(String tag, String ip, String resp) {
        updateRecord("ip: " + ip + " | " + operationTAG + "成功!");
        callBackNumber++;
        if (callBackNumber >= urls.size())
            onComplete();
        System.out.println("Callback onSuccess");
    }

    @Override
    public void onFailed(String tag, String ip, String failedinfo) {
        updateRecord("ip: " + ip + " | " + operationTAG + "失败!");
        System.out.println("Callback onFailed");
        if (callBackNumber >= urls.size())
            onComplete();
    }

    @Override
    public void onComplete() {
        //todo 加入一个统计(成功/失败/具体是什么类型操作/用时)
        System.out.println("Callback onComplete " + callBackNumber + " | " + urls.size());
        updateRecord("操作完成!");
        requesting = false;
    }

    /**
     * 更新
     *
     * @param record 新消息
     */
    private void updateRecord(String record) {
        String oldRecord = text_record.getText();
        String newRecord = oldRecord + "\n" + record;
        text_record.setText(newRecord);
    }

}