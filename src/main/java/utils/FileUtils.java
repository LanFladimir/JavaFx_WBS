package main.java.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import main.java.entity.DBConfig;
import main.java.entity.DbConfigGsonBean;
import main.java.entity.Device;
import main.java.entity.DevicesGsonBean;

/**
 * 配置文件/本地化存储
 */
public class FileUtils {
    //todo 文件存储于：文档/wbs/目录
    private static String ROOT_PATH = System.getProperty("user.home")
            + File.separator + "Documents" + File.separator + "scdz" + File.separator + "wbs";
    //private static String ROOT_PATH = "D:\\scdz\\";
    private static String DEVICES_FILE_PATH = ROOT_PATH + File.separator + "devices.json";
    private static String JDBC_FILE_PATH = ROOT_PATH + File.separator + "jdbc.json";


    /**
     * 确保配置文件存在
     */
    public static void checkFiles() {
        new Thread(() -> {
            //dir
            File dir = new File(ROOT_PATH);
            if (!dir.exists() || dir.isFile())
                dir.mkdirs();

            //ip
            File ipFile = new File(DEVICES_FILE_PATH);
            if (!ipFile.exists())
                try {
                    System.out.println("Devices文件不存在，creatNew");
                    ipFile.createNewFile();
                    reWriteDevicesFile(new ArrayList<>());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            //sql
            File jdbcFIle = new File(JDBC_FILE_PATH);
            if (!jdbcFIle.exists())
                try {
                    System.out.println("JDBC文件不存在，creatNew");
                    jdbcFIle.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }).start();
    }

    /**
     * 写入新的IP文件(iplist.json)
     */
    public static void reWriteDevicesFile(ArrayList<Device> devices) {
        try {
            JsonObject deviceList = new JsonObject();
            JsonArray devicearray = new JsonArray();
            JsonObject deviceObj;
            for (Device device : devices) {
                deviceObj = new JsonObject();
                deviceObj.addProperty("ip", device.getIp());
                deviceObj.addProperty("mark", device.getMark());
                deviceObj.addProperty("pingstatus", false);
                deviceObj.addProperty("select", device.isSelect());
                deviceObj.addProperty("userinput", device.isUserinput());
                devicearray.add(deviceObj);
            }
            deviceList.add("devices", devicearray);

            File deviceFile = new File(DEVICES_FILE_PATH);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(deviceFile),StandardCharsets.UTF_8
            ));
            writer.write(deviceList.toString());
            writer.close();
            /*FileOutputStream outputStream = new FileOutputStream(deviceFile);
            outputStream.write(deviceList.toString().getBytes());
            outputStream.close();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取IP配置文件
     *
     * @return arraylist
     */
    public static ArrayList<Device> readDeicesFile() {
        try {
            StringBuilder jsonString = new StringBuilder();
            BufferedReader mReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(DEVICES_FILE_PATH), StandardCharsets.UTF_8)
            );
            String line;
            while ((line = mReader.readLine()) != null) {
                jsonString.append(line);
            }
            mReader.close();
            DevicesGsonBean devicesBean = new Gson().fromJson(jsonString.toString(), DevicesGsonBean.class);
            if (devicesBean == null) {
                return new ArrayList<>();
            } else
                return devicesBean.getDevices();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * read selected devices
     *
     * @return selected
     */
    public static ArrayList<Device> readSelectDevices() {
        ArrayList<Device> devices = new ArrayList<>();
        for (Device device : readDeicesFile()) {
            if (device.isSelect()) {
                devices.add(device);
            }
        }
        return devices;
    }

    /**
     * 移除非手输设备
     *
     * @return 返回手输设备
     */
    public static ArrayList<Device> removeUnInputDevices() {
        ArrayList<Device> allDevices = readDeicesFile();
        allDevices.removeIf(device -> !device.isUserinput());
        reWriteDevicesFile(allDevices);
        return allDevices;
    }

    public static DBConfig readJDBCFile() {
        try {
            StringBuilder jsonString = new StringBuilder();
            BufferedReader mReader = new BufferedReader(new FileReader(JDBC_FILE_PATH));
            String line;
            while ((line = mReader.readLine()) != null) {
                jsonString.append(line);
            }
            mReader.close();
            DbConfigGsonBean dbConfigGsonBean = new Gson().fromJson(
                    jsonString.toString(), DbConfigGsonBean.class);
            if (dbConfigGsonBean == null)
                return new DBConfig("", "", "", "", "", "", "", "");
            else
                return dbConfigGsonBean.getDbconfig();
        } catch (IOException e) {
            e.printStackTrace();
            return new DBConfig("", "", "", "", "", "", "", "");
        }
    }

    public static void reWriteJdbcFile(DBConfig dbConfig) {
        try {
            JsonObject rootObject = new JsonObject();
            JsonObject dbObject = new JsonObject();
            dbObject.addProperty("url", dbConfig.getUrl());
            dbObject.addProperty("name", dbConfig.getName());
            dbObject.addProperty("pswd", dbConfig.getPswd());
            dbObject.addProperty("schema", dbConfig.getSchema());
            dbObject.addProperty("table", dbConfig.getTable());
            dbObject.addProperty("ip", dbConfig.getIp());
            dbObject.addProperty("mark", dbConfig.getMark());
            dbObject.addProperty("limit", dbConfig.getLimit());
            rootObject.add("dbconfig", dbObject);

            File deviceFile = new File(JDBC_FILE_PATH);
            FileOutputStream outputStream = new FileOutputStream(deviceFile);
            outputStream.write(rootObject.toString().getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
