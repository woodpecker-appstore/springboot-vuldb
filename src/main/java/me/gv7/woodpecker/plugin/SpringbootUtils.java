package me.gv7.woodpecker.plugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import me.gv7.woodpecker.plugin.Bean.PropertiesBean;
import net.dongliu.requests.RawResponse;
import net.dongliu.requests.Requests;

import java.util.Scanner;
import java.util.UUID;

public class SpringbootUtils {

    public static boolean SpringbootCheck(String addr){
        final String url = addr+"404";
        RawResponse response = Requests.get(url).verify(false).send();
        final int statusCode = response.statusCode();
        final String respText = response.readToText();
        if (statusCode == 404 || statusCode == 403){
            return respText.contains("Whitelabel Error Page") || respText.contains("There was an unexpected error");
        }
        return false;
    }

    public static boolean check404(String addr){
        final String url = addr + UUID.randomUUID();
        RawResponse response = Requests.get(url).verify(false).send();
        final int statusCode = response.statusCode();
        return statusCode == 200;
    }

    public static String scannerOutput(Scanner scanner){
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNext()){
            builder.append(scanner.nextLine()).append("\n");
        }
        return builder.toString();
    }

    public static PropertiesBean EnvParser(String result){
        JSONObject jsonObject = JSON.parseObject(result);
        JSONArray propertySources = jsonObject.getJSONArray("propertySources");
        JSONObject propertySource;
        PropertiesBean propertiesBean = new PropertiesBean();
        if (propertySources.size()>0){
            for (int i=0;i<propertySources.size();i++){
                propertySource = propertySources.getJSONObject(i);
                String name = (String)propertySource.get("name");
                if ("systemProperties".equals(name)) {
                    JSONObject properties = propertySource.getJSONObject("properties");
                    String jvmName      = properties.getJSONObject("java.vm.name").getString("value");
                    String javaVersion  = properties.getJSONObject("java.runtime.version").getString("value");
                    String userName     = properties.getJSONObject("user.name").getString("value");
                    propertiesBean.setHaveInfo(true);
                    propertiesBean.setJvmName(jvmName);
                    propertiesBean.setJavaVersion(javaVersion);
                    propertiesBean.setUserName(userName);
                }else if ("server.ports".equals(name)){
                    propertiesBean.setHaveInfo(true);
                    JSONObject properties = propertySource.getJSONObject("properties");
                    String serverPort   = properties.getJSONObject("local.server.port").getString("value");
                    propertiesBean.setServerPort(serverPort);
                }

            }
        }
        return propertiesBean;
    }
}
