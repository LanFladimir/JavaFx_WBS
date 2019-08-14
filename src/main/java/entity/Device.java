package main.java.entity;

/**
 * 设备(IP) 参数(wardid)
 */
public class Device {

    String ip;
    String mark;
    boolean pingstatus;
    boolean select;
    boolean userinput;

    public Device(String ip, String mark, boolean pingstatus, boolean select, boolean userinput) {
        this.ip = ip;
        this.mark = mark;
        this.pingstatus = pingstatus;
        this.select = select;
        this.userinput = userinput;
    }

    @Override
    public String toString() {
        return "ip = " + ip +
                " mark = " + mark +
                " pingstatus = " + pingstatus +
                " select = " + select +
                " userinput = " + userinput;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isPingstatus() {
        return pingstatus;
    }

    public void setPingstatus(boolean pingstatus) {
        this.pingstatus = pingstatus;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public boolean isUserinput() {
        return userinput;
    }

    public void setUserinput(boolean userinput) {
        this.userinput = userinput;
    }
}
