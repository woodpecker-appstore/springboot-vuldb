package me.gv7.woodpecker.plugin.Bean;

public class PropertiesBean {

    String jvmName;
    String serverPort;
    String javaVersion;
    String userName;
    Boolean haveInfo;

    public Boolean getHaveInfo() {
        return haveInfo;
    }

    public void setHaveInfo(Boolean haveInfo) {
        this.haveInfo = haveInfo;
    }

    public String getJvmName() {
        return jvmName;
    }

    public void setJvmName(String jvmName) {
        this.jvmName = jvmName;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
