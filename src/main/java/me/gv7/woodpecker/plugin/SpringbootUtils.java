package me.gv7.woodpecker.plugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import me.gv7.woodpecker.plugin.Bean.PropertiesBean;
import me.gv7.woodpecker.requests.RawResponse;
import me.gv7.woodpecker.requests.Requests;

import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class SpringbootUtils {

    public static boolean SpringbootCheck(String addr){
        final String url = addr+"404";
        RawResponse response = Requests.get(url).verify(false).timeout(10000).send();
        final int statusCode = response.statusCode();
        final String respText = response.readToText();
        if (statusCode == 404 || statusCode == 403){
            return respText.contains("Whitelabel Error Page") || respText.contains("There was an unexpected error");
        }
        return false;
    }

    public static boolean check404(String addr){
        final String url = addr + UUID.randomUUID();
        RawResponse response = Requests.get(url).verify(false).timeout(10000).send();
        final int statusCode = response.statusCode();
        return statusCode == 200;
    }

    // https://blog.csdn.net/testcs_dn/article/details/79033009/
    public static boolean checkPoint(String url, RawResponse resp){
        String s = resp.readToText();
        // hystrix.stream
        if (url.contains("hystrix.stream")){
            return "ping:".contains(s)||"data:".contains(s);
        // health
        }else if (url.contains("health")){
            try {
                Map res = (Map)JSON.parse(s);
                return res.containsKey("status")||res.containsKey("diskSpace");
            }catch (Exception e){
                return false;
            }
        // beans
        }else if (url.contains("beans")){
            Map res;
            try {
                JSONArray objects = JSON.parseArray(s);
                for (Object o:objects){
                    res = (Map)o;
                    return res.containsKey("bean")||res.containsKey("scope")||res.containsKey("dependencies");
                }
            }catch (Exception e){
                return false;
            }
        // configprops
        }else if (url.contains("configprops")){
            try {
                Map res = (Map)JSON.parse(s);
                return res.containsKey("configurationPropertiesReportEndpoint");
            }catch (Exception e){
                return false;
            }
        }else if (url.contains("mappings")){
            return s.contains("bean")||s.contains("method");
        }else if (url.contains("metrics")){
            return s.contains("threads")||s.contains("heap");
        }
        return false;
    }

    public static String scannerOutput(Scanner scanner){
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNext()){
            builder.append(scanner.nextLine()).append("\n");
        }
        return builder.toString();
    }

    public static PropertiesBean EnvParser(String result){
        PropertiesBean propertiesBean = new PropertiesBean();
        try {
            JSONObject jsonObject = JSON.parseObject(result);
            JSONArray propertySources = jsonObject.getJSONArray("propertySources");
            JSONObject propertySource;
            if (propertySources.size() > 0) {
                for (int i = 0; i < propertySources.size(); i++) {
                    propertySource = propertySources.getJSONObject(i);
                    String name = (String) propertySource.get("name");
                    if ("systemProperties".equals(name)) {
                        JSONObject properties = propertySource.getJSONObject("properties");
                        String jvmName = properties.getJSONObject("java.vm.name").getString("value");
                        String javaVersion = properties.getJSONObject("java.runtime.version").getString("value");
                        String userName = properties.getJSONObject("user.name").getString("value");
                        propertiesBean.setHaveInfo(true);
                        propertiesBean.setJvmName(jvmName);
                        propertiesBean.setJavaVersion(javaVersion);
                        propertiesBean.setUserName(userName);
                    } else if ("server.ports".equals(name)) {
                        propertiesBean.setHaveInfo(true);
                        JSONObject properties = propertySource.getJSONObject("properties");
                        String serverPort = properties.getJSONObject("local.server.port").getString("value");
                        propertiesBean.setServerPort(serverPort);
                    }

                }

            }
        }catch (Exception e){
            propertiesBean.setHaveInfo(false);
        }
        return propertiesBean;
    }
}
