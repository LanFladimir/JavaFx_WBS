package main.java.controller;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.entity.Device;
import main.java.entity.UrlPart;
import main.java.impl.HttpCallBack;
import main.java.utils.FileUtils;
import main.java.utils.HttpUtils;

public class InfoController implements HttpCallBack {
    private GridPane infoPane;
    private ArrayList<UrlPart> urlparts = new ArrayList<>();
    private HttpUtils mHttpUtils;

    private final String Request_Tag_HD = "re_t_hardware";
    private final String Request_Tag_IP = "re_t_ip";
    private final String Request_Tag_V = "re_t_volume";

    private Text system_cpu;
    private Text system_broad;
    private Text system_memory;
    private Text system_imei;
    private Text net_ip;
    private Text net_gateway;
    private Text net_dns;
    private Text net_mac;
    private TextField other_volumn_value;

    private ArrayList<Button> buttons = new ArrayList<>();
    private ArrayList<GridPane> gridPanes = new ArrayList<>();
    private ArrayList<Device> devices = new ArrayList<>();

    private String currentIp = "";

    public InfoController(GridPane infoPane) {
        this.infoPane = infoPane;
    }

    /**
     * 选中页面 处置选项卡
     */
    public void onSelectCall() {
        infoPane.getChildren().clear();

        buttons = new ArrayList<>();
        gridPanes = new ArrayList<>();
        HBox devicesBox = new HBox();
        devicesBox.setPadding(new Insets(15, 10, 15, 10));
        devicesBox.setSpacing(10);

        devices = FileUtils.readSelectDevices();
        for (int i = 0; i < devices.size(); i++) {
            Device device = devices.get(i);
            GridPane pane = new GridPane();
            Button bt = new Button(device.getIp());
            bt.setStyle("-fx-start-margin: 20px;-fx-end-margain:20px;-fx-background-color: aliceblue");

            //添加菜单
            devicesBox.getChildren().add(bt);
            //显示对应内容
            int finalI = i;
            bt.setOnAction(event -> showSelectPanes(finalI));

            //保存至列表
            buttons.add(bt);
            gridPanes.add(pane);

            infoPane.add(pane, 0, 1);
            pane.setVisible(false);
        }
        infoPane.add(devicesBox, 0, 0);

        if (devices.size() > 0) {
            showSelectPanes(0);
            currentIp = devices.get(0).getIp();
        } else {
            infoPane.add(new Text("No Devices Selected"), 0, 0);
            currentIp = "";
        }
    }


    /**
     * 显示选项卡
     *
     * @param i index
     */
    private void showSelectPanes(int i) {
        currentIp = devices.get(i).getIp();
        for (GridPane gridPane : gridPanes) {
            gridPane.setVisible(false);
            gridPane.getChildren().clear();
        }
        for (Button button : buttons) {
            button.setStyle("-fx-background-color: aliceblue");
        }

        //填充内容
        addViewsInGridPane(gridPanes.get(i));
        gridPanes.get(i).setVisible(true);
        buttons.get(i).setStyle("-fx-background-color: cornflowerblue");
    }

    /**
     * 选项卡填充控件 读取信息
     */
    private void addViewsInGridPane(GridPane pane) {
        Text LabelSystem = new Text("系统");
        LabelSystem.setFont(Font.font("monaco", FontWeight.NORMAL, 20));
        Label LabelNet = new Label("网络");
        LabelNet.setFont(Font.font("monaco", FontWeight.NORMAL, 20));
        Label LabelOther = new Label("其他");
        LabelOther.setFont(Font.font("monaco", FontWeight.NORMAL, 20));

        system_cpu = new Text("内核：");
        system_broad = new Text("主板：");
        system_memory = new Text("内存：");
        system_imei = new Text("IMEI：");
        net_ip = new Text("IP：");
        net_gateway = new Text("GateWay：");
        net_dns = new Text("DNS：");
        net_mac = new Text("MAC：");
        Label other_volumn = new Label("音量：");
        other_volumn_value = new TextField("");
        Button other_volumn_set = new Button("设置");
        Label other_ps = new Label("截图：");
        Button other_showps = new Button("查看截图");

        pane.add(LabelSystem, 0, 0);
        pane.add(LabelNet, 0, 3);
        pane.add(LabelOther, 0, 6);

        pane.add(system_cpu, 1, 1);
        pane.add(system_broad, 3, 1);
        pane.add(system_memory, 1, 2);
        pane.add(system_imei, 3, 2);
        pane.add(net_ip, 1, 4);
        pane.add(net_gateway, 3, 4);
        pane.add(net_dns, 3, 5);
        pane.add(net_mac, 1, 5);
        pane.add(other_volumn, 1, 7);
        pane.add(other_volumn_value, 2, 7);//
        pane.add(other_volumn_set, 3, 7);
        pane.add(other_ps, 1, 8);
        pane.add(other_showps, 2, 8);

        urlparts.clear();
        urlparts.add(new UrlPart(Request_Tag_HD, "gethardware"));//硬件数据
        urlparts.add(new UrlPart(Request_Tag_IP, "getip"));//网络数据
        urlparts.add(new UrlPart(Request_Tag_V, "getvolume"));//音量

        other_volumn_set.setOnAction(event -> {
            String url = "http://" + currentIp + ":9090/setvolume?volume=" + other_volumn_value.getText();
            mHttpUtils.getRequest("setvolume", url);
        });

        other_showps.setOnAction(event -> {
            Stage window = new Stage();
            window.setTitle("设备(" + currentIp + ")截图 加载中...");


            ImageView scImageView = new ImageView();
            //scImageView.

            HBox hBox = new HBox(10);
            hBox.getChildren().addAll(scImageView);
            Scene scene = new Scene(hBox, 400, 400);
            window.setScene(scene);
            window.setResizable(true);
            window.show();

            HttpUtils mHttpUtils = new HttpUtils(new HttpCallBack() {
                @Override
                public void onSuccess(String tag, String ip, String resp) {
                    //resp   CaptureScreen/1565679851572.png|540|960
                    //imgUrl http://192.168.2.191/CaptureScreen/1565679851572.png

                    Platform.runLater(() -> {
                        String[] resps = resp.split("\\|");
                        String imgUrl = "http://" + currentIp + "/" + resps[0];
                        //显示
                        Image image = new Image(imgUrl);
                        scImageView.setImage(image);
                        scImageView.setFitWidth(Double.parseDouble(resps[1]));
                        scImageView.setFitHeight(Double.parseDouble(resps[2]));

                        window.setTitle("设备(" + currentIp + ")截图");
                    });
                }

                @Override
                public void onFailed(String tag, String ip, String failedinfo) {

                }

                @Override
                public void onComplete() {

                }
            });

            String url = "http://" + currentIp + ":9090/getscreen";
            mHttpUtils.getRequest("getscreen", url);
        });

        if (mHttpUtils == null)
            mHttpUtils = new HttpUtils(this);
        readInfo(currentIp);
    }

    /**
     * 读取数据
     */
    private void readInfo(String ip) {
        for (UrlPart urlpart : urlparts) {
            String url = "http://" + ip + ":9090/" + urlpart.getUrl();
            mHttpUtils.getRequest(urlpart.getTag(), url);
        }
    }

    @Override
    public void onSuccess(String tag, String ip, String resp) {
        System.out.println("onSuccess:" + tag + " : " + resp);
        switch (tag) {
            case Request_Tag_HD://cpu + "|" + broad + "|" + memall + "|" + memused + "|" + imei;
                String[] hdInfos = resp.split("\\|");
                System.out.println("Request_Tag_HD:" + hdInfos.length);
                if (hdInfos.length == 5) {
                    System.out.println(hdInfos[0]);
                    System.out.println(hdInfos[1]);
                    System.out.println(hdInfos[2]);
                    System.out.println(hdInfos[3]);
                    System.out.println(hdInfos[4]);
                    system_cpu.setText("内核：" + hdInfos[0]);
                    system_broad.setText("主板：" + hdInfos[1]);
                    system_memory.setText("内存：" + hdInfos[2] + "(已用" + hdInfos[3] + ")");
                    system_imei.setText("IMEI：" + hdInfos[4]);
                } else {
                    System.out.println("WBS请升级至1.2.11或以上版本!");
                }
                break;
            case Request_Tag_IP:// ip + ";" + mask + ";" + gateway + ";" + dns + ";" + mac
                String[] ipInfos = resp.split(";");
                if (ipInfos.length == 5) {
                    net_ip.setText("IP：" + ipInfos[0]);
                    net_gateway.setText("GateWay：" + ipInfos[2]);
                    net_dns.setText("DNS：" + ipInfos[3]);
                    net_mac.setText("MAC：" + ipInfos[4]);
                }
                break;
            case Request_Tag_V://volume
                other_volumn_value.setText(resp);
                break;
        }
    }

    @Override
    public void onFailed(String tag, String ip, String failedinfo) {

    }

    @Override
    public void onComplete() {

    }

}
