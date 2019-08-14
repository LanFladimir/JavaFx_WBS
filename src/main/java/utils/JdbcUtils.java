package main.java.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import main.java.entity.DBConfig;
import main.java.entity.Device;

public class JdbcUtils {
    private static final String JDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public static boolean syneJDBC(DBConfig dbConfig) {
        try {
            System.out.println(dbConfig.toString());
            Class.forName(JDriver);
            //SQLServerDriver.register();
            String url = "jdbc:sqlserver://" + dbConfig.getUrl() + ":1433;DatabaseName=" + dbConfig.getSchema();
            System.out.println(url);
            Connection conn = DriverManager.getConnection(url, dbConfig.getName(), dbConfig.getPswd());

            Statement stmt = conn.createStatement();

            ResultSet set = stmt.executeQuery("select " + dbConfig.getIp() +
                    " , " + dbConfig.getMark() + " from " + dbConfig.getTable()
                    + (dbConfig.getLimit().length() > 0 ? " where " + dbConfig.getLimit() : ""));

            //移除非手输
            ArrayList<Device> userInpurtDevices = FileUtils.removeUnInputDevices();
            ArrayList<Device> sqlDevices = new ArrayList<>();
            Device device;
            while (set.next()) {
                device = new Device(
                        set.getString(1),
                        set.getString(2),
                        false,
                        false,
                        false
                );
                sqlDevices.add(device);
            }

            sqlDevices.addAll(userInpurtDevices);
            System.out.println("sqlDevices = " + sqlDevices.size());
            //写入文件
            FileUtils.reWriteDevicesFile(sqlDevices);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.append("JDBC连接异常: " + dbConfig.toString(), e.getMessage());
            return false;
        }
    }
}
