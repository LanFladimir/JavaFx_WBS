package main.java.controller;

import com.sun.javafx.scene.control.skin.TableHeaderRow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;
import main.java.entity.DBConfig;
import main.java.entity.Device;
import main.java.impl.PingCallback;
import main.java.net.PingWorker;
import main.java.utils.FileUtils;
import main.java.utils.JdbcUtils;

/**
 * 设备
 */
public class DevicesController implements PingCallback {
    private GridPane devicesPane;
    private ArrayList<Device> devicesList = new ArrayList<>();
    private ObservableList<Device> mTableViewData;
    private DBConfig dbConfig;
    private PingWorker pingThread = new PingWorker(this);
    private TableView<Device> devicesTableView;

    public DevicesController(GridPane devicesPane) {
        this.devicesPane = devicesPane;
    }

    /**
     * 选中
     */
    public void onSelectCall() {
        devicesList = FileUtils.readDeicesFile();
        //mTableViewData.addAll(devicesList);
        mTableViewData.setAll(devicesList);
        devicesTableView.refresh();
    }

    public void init() {
        Label label_devices = new Label("设备列表");
        Label label_database = new Label("数据库配置");
        Label label_addnew = new Label("手动添加");
        label_devices.setFont(Font.font("monaco", FontWeight.NORMAL, 20));
        label_database.setFont(Font.font("monaco", FontWeight.NORMAL, 20));
        label_addnew.setFont(Font.font("monaco", FontWeight.NORMAL, 20));
        label_addnew.setPadding(new Insets(0, 10, 0, 0));

        Label empty = new Label("空空空空空空空");
        empty.setTextFill(Color.web("#adcdd8"));
        empty.setFont(Font.font("monaco", FontWeight.NORMAL, 20));

        Label database_url = new Label("数据库地址");
        Label database_name = new Label("用户名");
        Label database_pswd = new Label("密码");
        Label database_schema = new Label("库");
        Label database_table = new Label("表");
        Label database_ip = new Label("IP(字段)");
        Label database_mark = new Label("MARK(字段)");
        Label database_limit = new Label("Where(sql)");
        Button database_check = new Button("同步设备");
        Text database_check_response = new Text();

        TextField database_url_value = new TextField();
        database_url_value.setPromptText("192.168.2.1");
        TextField database_name_value = new TextField();
        TextField database_pswd_value = new TextField();
        TextField database_schema_value = new TextField();
        TextField database_table_value = new TextField();
        TextField database_ip_value = new TextField();
        TextField database_mark_value = new TextField();
        TextField database_limit_value = new TextField();
        TextField addnew_ip = new TextField();
        addnew_ip.setPromptText("ip|...");
        TextField addnew_mark = new TextField();
        addnew_mark.setPromptText("mark");
        Button addnew_add = new Button("ADD");
        //ListView<Device> devicesListView = new ListView<>();
        devicesTableView = new TableView<>();

        Button devices_select_all = new Button("全选");
        Button devices_select_null = new Button("不选");


        Text toast_text = new Text();
        toast_text.setFill(Color.FIREBRICK);


        devicesPane.add(label_devices, 0, 0);
        //devicesPane.add(empty, 1, 0);
        devicesPane.add(devices_select_all, 1, 0);
        devicesPane.add(devices_select_null, 2, 0);
        devicesPane.add(label_database, 3, 0);
        devicesPane.add(label_addnew, 3, 10);

        devicesPane.add(devicesTableView, 0, 1, 3, 14);
        devicesPane.add(database_url, 3, 1);
        devicesPane.add(database_name, 3, 2);
        devicesPane.add(database_pswd, 3, 3);
        devicesPane.add(database_schema, 3, 4);
        devicesPane.add(database_table, 3, 5);
        devicesPane.add(database_ip, 3, 6);
        devicesPane.add(database_mark, 3, 7);
        devicesPane.add(database_limit, 3, 8);
        devicesPane.add(database_check, 3, 9);
        devicesPane.add(database_check_response, 4, 9);
        devicesPane.add(database_url_value, 4, 1);
        devicesPane.add(database_name_value, 4, 2);
        devicesPane.add(database_pswd_value, 4, 3);
        devicesPane.add(database_schema_value, 4, 4);
        devicesPane.add(database_table_value, 4, 5);
        devicesPane.add(database_ip_value, 4, 6);
        devicesPane.add(database_mark_value, 4, 7);
        devicesPane.add(database_limit_value, 4, 8);
        devicesPane.add(addnew_mark, 3, 11);
        devicesPane.add(addnew_ip, 3, 12);
        devicesPane.add(addnew_add, 4, 12);
        devicesPane.add(toast_text, 3, 13);


        database_check.setOnAction(event -> new Thread(() -> {
            FileUtils.reWriteJdbcFile(new DBConfig(
                    database_url_value.getText(),
                    database_name_value.getText(),
                    database_pswd_value.getText(),
                    database_schema_value.getText(),
                    database_table_value.getText(),
                    database_ip_value.getText(),
                    database_mark_value.getText(),
                    database_limit_value.getText()
            ));

            database_check_response.setFill(Color.YELLOWGREEN);
            database_check_response.setText("验证中...");
            if (JdbcUtils.syneJDBC(new DBConfig(
                    database_url_value.getText(),
                    database_name_value.getText(),
                    database_pswd_value.getText(),
                    database_schema_value.getText(),
                    database_table_value.getText(),
                    database_ip_value.getText(),
                    database_mark_value.getText(),
                    database_limit_value.getText()))) {
                database_check_response.setFill(Color.YELLOWGREEN);
                database_check_response.setText("Success！");

                devicesList = FileUtils.readDeicesFile();
                mTableViewData.clear();
                mTableViewData.addAll(devicesList);
            } else {
                database_check_response.setFill(Color.FIREBRICK);
                database_check_response.setText("Fail！");
            }
        }).start());

        pingThread.startPing();
        mTableViewData = FXCollections.observableArrayList(devicesList);

        TableColumn ipCol = new TableColumn("Device Ip");
        ipCol.setCellValueFactory(new PropertyValueFactory<>("ip"));//关联数据参数
        TableColumn mkCol = new TableColumn("Mark");
        mkCol.setCellValueFactory(new PropertyValueFactory<>("mark"));
        TableColumn pingCol = new TableColumn("PING");
        //pingCol.setCellValueFactory(new PropertyValueFactory<>("pingstatus"));
        pingCol.setCellValueFactory(new PropertyValueFactory<Device, Circle>("pingstatus") {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures param) {
                Circle rect = new Circle(10);
                if (param != null) {
                    rect.setFill(((Device) param.getValue()).isPingstatus() ? Color.GREENYELLOW : Color.RED);
                    //setGraphic(rect);
                }
                return new SimpleObjectProperty<>(rect);
            }
        });
        TableColumn selectCol = new TableColumn("SELECT");
        selectCol.setCellValueFactory(new PropertyValueFactory<Device, Boolean>("select"));
        selectCol.setCellFactory((Callback<TableColumn<Device, Boolean>, TableCell<Device, Boolean>>)
                param -> {
                    //关联
                    DevicesTableCell_Check<Device, Boolean> cell = new DevicesTableCell_Check<>();
                    CheckBox checkbox = (CheckBox) cell.getGraphic();
                    checkbox.setOnAction(event -> {
                        int index = cell.getIndex();
                        Device device = mTableViewData.get(index);
                        if (device.isSelect()) {
                            device.setSelect(false);
                            devicesList.get(index).setSelect(false);
                        } else {
                            device.setSelect(true);
                            devicesList.get(index).setSelect(true);
                        }
                        FileUtils.reWriteDevicesFile(devicesList);
                    });
                    return cell;
                }
        );

        TableColumn deleteCol = new TableColumn("DELETE");
        //deleteCol.setCellValueFactory(new PropertyValueFactory<Device, Boolean>("ip"));
        deleteCol.setCellFactory((Callback<TableColumn<Device, Boolean>, TableCell<Device, Boolean>>)
                param -> {
                    DevicesTableCell_Button<Device, Boolean> cell = new DevicesTableCell_Button<>();
                    Button delBt = (Button) cell.getGraphic();
                    delBt.setOnAction(event -> {
                        int index = cell.getIndex();
                        Device device = mTableViewData.get(index);
                        Iterator<Device> it = devicesList.iterator();
                        while (it.hasNext()) {
                            if (it.next().getIp().equals(device.getIp())) {
                                it.remove();
                                mTableViewData.remove(index);
                            }
                        }
                        FileUtils.reWriteDevicesFile(devicesList);
                    });
                    return cell;
                }
        );

        devicesTableView.setItems(mTableViewData);
        devicesTableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) devicesTableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> {
                ObservableList columns = devicesTableView.getColumns();
                if (columns.indexOf(ipCol) != 0) {
                    columns.remove(ipCol);
                    columns.add(0, ipCol);
                }
                if (columns.indexOf(selectCol) != columns.size() - 1) {
                    columns.remove(selectCol);
                    columns.add(columns.size(), selectCol);
                }
            });
        });

        ipCol.setStyle("-fx-alignment: CENTER;");
        mkCol.setStyle("-fx-alignment: CENTER;");
        pingCol.setStyle("-fx-alignment: CENTER;");
        selectCol.setStyle("-fx-alignment: CENTER;");
        deleteCol.setStyle("-fx-alignment: CENTER;");
        devicesTableView.getColumns().addAll(ipCol, mkCol, pingCol, selectCol, deleteCol);

        addnew_add.setOnAction(event -> {
            String ip = addnew_ip.getText().replaceAll(" ","");
            String mark = addnew_mark.getText().replaceAll(" ","");

            if (checkIfExist(ip)) {
                toast_text.setText("IP为 " + ip + " 的设备已存在!");
            } else {
                toast_text.setText("");
                Device newdevice = new Device(ip, mark, false, false, true);
                mTableViewData.add(newdevice);
                devicesList.add(newdevice);
                addnew_ip.setText("");
                addnew_mark.setText("");
                FileUtils.reWriteDevicesFile(devicesList);
                pingThread.updatePingThread();
            }
        });

        devices_select_all.setOnAction(event -> {
            for (Device devices : devicesList) {
                devices.setSelect(true);
            }
            mTableViewData.clear();
            mTableViewData.addAll(devicesList);
            FileUtils.reWriteDevicesFile(devicesList);
        });
        devices_select_null.setOnAction(event -> {
            for (Device devices : devicesList) {
                devices.setSelect(false);
            }
            mTableViewData.clear();
            mTableViewData.addAll(devicesList);
            FileUtils.reWriteDevicesFile(devicesList);
        });

        //read file-->devices.json
        new Thread(() -> {
            devicesList = FileUtils.readDeicesFile();
            mTableViewData.addAll(devicesList);
        }).start();

        //read file-->jdbc.json
        new Thread(() -> {
            dbConfig = FileUtils.readJDBCFile();
            database_url_value.setText(dbConfig.getUrl());
            database_name_value.setText(dbConfig.getName());
            database_pswd_value.setText(dbConfig.getPswd());
            database_schema_value.setText(dbConfig.getSchema());
            database_table_value.setText(dbConfig.getTable());
            database_ip_value.setText(dbConfig.getIp());
            database_mark_value.setText(dbConfig.getMark());
            database_limit_value.setText(dbConfig.getLimit());
        }).start();


    }

    /**
     * add a new devices if exist @return true else @return false
     */
    private boolean checkIfExist(String newip) {
        for (Device devices : FileUtils.readDeicesFile()) {
            if (devices.getIp().equals(newip))
                return true;
        }
        return false;
    }

    @Override
    public void pingCallBack(String devicesIp, boolean pingStatus) {
        //System.out.println(devicesIp + " | " + pingStatus);
        for (Device device : devicesList) {
            if (device.getIp().equals(devicesIp)) {
                device.setPingstatus(pingStatus);
                //mTableViewData.clear();
                //mTableViewData.addAll(devicesList);
                devicesTableView.refresh();
                FileUtils.reWriteDevicesFile(devicesList);
            }
        }
    }

    class DevicesTableCell_Check<S, T> extends TableCell<S, T> {
        private final CheckBox chebox;

        DevicesTableCell_Check() {
            this.chebox = new CheckBox();
            setGraphic(chebox);
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                chebox.setSelected((Boolean) item);
                setGraphic(chebox);
            }
        }
    }

    class DevicesTableCell_Button<S, T> extends TableCell<S, T> {
        private final Button button;

        DevicesTableCell_Button() {
            this.button = new Button("删");
            setGraphic(button);
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setGraphic(button);
            }
        }
    }
}
