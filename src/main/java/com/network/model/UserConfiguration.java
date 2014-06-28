package com.network.model;

import java.io.*;
import java.util.Properties;

/**
 * UserConfiguration.
 *
 * @author ningzhangnj
 */
public class UserConfiguration {
    public static final String DEFAULT_LOCAL_PORT = "8081";
    public static final String DEFAULT_REMOTE_IP = "10.186.150.8";
    public static final String DEFAULT_REMOTE_PORT = "5001";

    private Properties props = new Properties();
    private File userConfFile;

    public UserConfiguration() {
        init();
    }

    private void init() {
        StringBuffer dir = new StringBuffer();
        dir.append(System.getProperty("user.home"));
        dir.append(File.separator);
        dir.append(".netcapplay");
        dir.append(File.separator);
        String applicationDataPath = dir.toString();
        File f = new File(applicationDataPath);
        f.mkdirs();
        userConfFile = new File(applicationDataPath + "userConfiguration.properties");
        if (!userConfFile.exists()) {
            try {
                userConfFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            props.load(new FileInputStream(userConfFile));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getLocalPort() {
        return props.getProperty("local_port", DEFAULT_LOCAL_PORT);
    }

    public void setLocalPort(String port) {
        props.setProperty("local_port", port);
    }

    public String getRemoteIp() {
        return props.getProperty("remote_ip", DEFAULT_REMOTE_IP);
    }

    public void setRemoteIp(String ip) {
        props.setProperty("remote_ip", ip);
    }

    public String getRemotePort() {
        return props.getProperty("remote_port", DEFAULT_REMOTE_PORT);
    }

    public void setRemotePort(String port) {
        props.setProperty("remote_port", port);
    }

    public boolean save() {
        try {
            props.store(new FileOutputStream(userConfFile), "");
            return true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}
