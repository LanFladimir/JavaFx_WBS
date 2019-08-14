package main.java.entity;

/**
 * 数据库配置
 */
public class DBConfig {
    private String url;
    private String name;
    private String pswd;
    private String table;
    private String schema;
    private String ip;
    private String mark;
    private String limit;

    public DBConfig(String url, String name, String pswd, String schema, String table, String ip, String mark, String limit) {
        this.url = url;
        this.name = name;
        this.pswd = pswd;
        this.schema = schema;
        this.table = table;
        this.ip = ip;
        this.mark = mark;
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "url = " + url +
                " name = " + name +
                " pswd = " + pswd +
                " schema = " + schema +
                " table = " + table +
                " ip = " + ip +
                " mark = " + mark +
                " limit = " + limit;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTable() {
        return table;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }
}
