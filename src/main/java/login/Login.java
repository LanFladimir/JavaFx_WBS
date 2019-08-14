package main.java.login;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.java.controller.CtrlController;
import main.java.controller.DevicesController;
import main.java.controller.FTPController;
import main.java.controller.InfoController;
import main.java.net.PingWorker;
import main.java.utils.FileUtils;
import main.java.utils.GlobalConfig;
import main.java.utils.Logger;


/**
 * Main
 */
public class Login extends Application {
    private TextField userTextField;
    private PasswordField pwBox;
    private Text actiontarget;

    private static GridPane contentPane_Devices;
    private static GridPane contentPane_FTP;
    private static GridPane contentPane_Infos;
    private static GridPane contentPane_Ctrl;

    private static InfoController infoController;
    private static DevicesController devicesController;
    private static Button b1, b2, b3, b4;

    public static void main(String[] args) {
        Logger.append("程序启动!");
        FileUtils.checkFiles();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 25, 25, 25));
        // 绑定Scene/root
        Scene scene = new Scene(grid, 600, 600 * GlobalConfig.goldRate);
        // TODO: 2019-06-03 add css(1:css 文件位置 2:css 样式)
        // https://docs.oracle.com/javase/8/javafx/get-started-tutorial/css.htm
        // scene.getStylesheets().add(getClass().getResource("/Login.css").toExternalForm());
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        //登录界面
        Text scenetitle = new Text("Log In");
        scenetitle.setFont(Font.font("monaco", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 1, 1);
        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);
        userTextField = new TextField();
        grid.add(userTextField, 1, 1);
        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);
        pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);
        pwBox.setOnAction(event -> doLogin(primaryStage));

        Button btn_connect_us = new Button("Connect us");
        btn_connect_us.setFont(Font.font("monaco", FontWeight.NORMAL, 10));
        HBox hbBtn_connect_us = new HBox(10);
        hbBtn_connect_us.setAlignment(Pos.BOTTOM_LEFT);
        hbBtn_connect_us.getChildren().add(btn_connect_us);
        grid.add(hbBtn_connect_us, 0, 4);

        Button btn_sign_in = new Button("Sign in");
        btn_sign_in.setFont(Font.font("monaco", FontWeight.NORMAL, 10));
        HBox hbBtn_sign_in = new HBox(10);
        hbBtn_sign_in.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn_sign_in.getChildren().add(btn_sign_in);
        grid.add(hbBtn_sign_in, 1, 4);

        //点击反馈
        actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        btn_connect_us.setOnAction(event -> {
            //FileUtils.checkFiles();
            actiontarget.setFill(Color.CADETBLUE);
            actiontarget.setText("Telnet: 021-56986041");
        });
        btn_sign_in.setOnAction(event -> doLogin(primaryStage));

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("程序关闭!");
            Logger.append("程序关闭!");
            PingWorker.ping = false;
            System.exit(1);
        });
    }

    private void doLogin(Stage primaryStage) {
        String user = userTextField.getCharacters().toString();
        String psw = pwBox.getCharacters().toString();

        //if (user.equals("scdz") && psw.equals("56986041")) {
        actiontarget.setFill(Color.YELLOWGREEN);
        actiontarget.setText("Login success");
        //登陆成功,跳转
        primaryStage.close();
        GridPane rootPane = new GridPane();
        Scene scene = new Scene(rootPane, 1200, 1200 * GlobalConfig.goldRate);
        rootPane.add(getMainMenu(), 0, 0, 1, 1);
        rootPane.minWidth(1200);
        rootPane.setStyle("-fx-background-color: #adcdd8;");
        rootPane.minHeight(1200 * GlobalConfig.goldRate);

        GridPane contentPane = new GridPane();
        rootPane.add(contentPane, 1, 0, 1, 1);

        contentPane_Devices = getDevicesContentPane();
        contentPane_FTP = getFTPContentPane();
        contentPane_Infos = getInfosContentPane();
        contentPane_Ctrl = getCtrlContentPane();
        contentPane.add(contentPane_Devices, 0, 1, 1, 1);
        contentPane.add(contentPane_FTP, 0, 1, 1, 1);
        contentPane.add(contentPane_Infos, 0, 1, 1, 1);
        contentPane.add(contentPane_Ctrl, 0, 1, 1, 1);
        replaceContent(0);

        primaryStage.setScene(scene);
        primaryStage.setResizable(true);//todo false
        primaryStage.setTitle("ScApp Control System");
        primaryStage.show();
        //} else {
        //    actiontarget.setFill(Color.FIREBRICK);
        //    actiontarget.setText("Login failed");
        //}
    }

    /**
     * 主菜单
     */
    private static VBox getMainMenu() {
        VBox vBox = new VBox();
        vBox.setPrefHeight(500);
        vBox.setPrefWidth(120);
        vBox.setPadding(new Insets(15, 10, 15, 10));
        vBox.setSpacing(10);
        vBox.setStyle("-fx-background-color: #336699;");

        b1 = new Button("设备");
        b1.setPrefSize(100, 30);
        b2 = new Button("FTP");
        b2.setPrefSize(100, 30);
        b3 = new Button("查看");
        b3.setPrefSize(100, 30);
        b4 = new Button("控制");
        b4.setPrefSize(100, 30);

        b1.setOnAction(event -> replaceContent(0));
        b2.setOnAction(event -> replaceContent(1));
        b3.setOnAction(event -> replaceContent(2));
        b4.setOnAction(event -> replaceContent(3));

        vBox.getChildren().addAll(b1, b2, b3, b4);
        return vBox;
    }

    /*设备Pane*/
    private GridPane getDevicesContentPane() {
        GridPane devicesPane = new GridPane();
        setContentBordStyle(devicesPane);
        devicesController = new DevicesController(devicesPane);
        devicesController.init();
        return devicesPane;
    }

    /*FTPPane*/
    private GridPane getFTPContentPane() {
        GridPane ftpPane = new GridPane();
        setContentBordStyle(ftpPane);
        FTPController ftpController = new FTPController(ftpPane);
        ftpController.init();
        return ftpPane;
    }

    /*查看信息Pane*/
    private GridPane getInfosContentPane() {
        GridPane infoPane = new GridPane();
        setContentBordStyle(infoPane);
        infoController = new InfoController(infoPane);
        return infoPane;
    }

    /*控制Pane*/
    private GridPane getCtrlContentPane() {
        GridPane ctrlPane = new GridPane();
        setContentBordStyle(ctrlPane);
        CtrlController ctrlController = new CtrlController(ctrlPane);
        ctrlController.init();
        return ctrlPane;
    }

    /*PANE STYLE*/
    private void setContentBordStyle(GridPane pane) {
        pane.setPrefHeight(1200 * GlobalConfig.goldRate);
        pane.setPrefWidth(1200 - 120);
        pane.setStyle("-fx-background-color: #adcdd8;");
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setVgap(8);
        pane.setHgap(15);
    }

    /**
     * scdz 替换主内容部分
     */
    private static void replaceContent(int index) {
        b1.setStyle("-fx-background-color: aliceblue;-fx-text-fill: black");
        b2.setStyle("-fx-background-color: aliceblue;-fx-text-fill: black");
        b3.setStyle("-fx-background-color: aliceblue;-fx-text-fill: black");
        b4.setStyle("-fx-background-color: aliceblue;-fx-text-fill: black");

        contentPane_Devices.setVisible(false);
        contentPane_FTP.setVisible(false);
        contentPane_Infos.setVisible(false);
        contentPane_Ctrl.setVisible(false);
        switch (index) {
            case 0:
                b1.setStyle("-fx-background-color: cornflowerblue;-fx-text-fill: white");
                contentPane_Devices.setVisible(true);
                devicesController.onSelectCall();
                break;
            case 1:
                b2.setStyle("-fx-background-color: cornflowerblue;-fx-text-fill: white");
                contentPane_FTP.setVisible(true);
                break;
            case 2:
                b3.setStyle("-fx-background-color: cornflowerblue;-fx-text-fill: white");
                contentPane_Infos.setVisible(true);
                infoController.onSelectCall();
                break;
            case 3:
                b4.setStyle("-fx-background-color: cornflowerblue;-fx-text-fill: white");
                contentPane_Ctrl.setVisible(true);
                break;
        }
    }
}
