package main.java.entity;

public class DbConfigGsonBean {
    /*{
        "dbconfig":{
             "url":"",
             "name":"",
             "pswd":"",
              "table":"",
              "ip":"",
              "mark":""
    }*/

    private DBConfig dbconfig;

    public DBConfig getDbconfig() {
        return dbconfig;
    }

    public void setDbconfig(DBConfig dbconfig) {
        this.dbconfig = dbconfig;
    }
}
