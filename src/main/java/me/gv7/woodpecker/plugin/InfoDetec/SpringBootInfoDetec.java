package me.gv7.woodpecker.plugin.InfoDetec;

import me.gv7.woodpecker.plugin.*;
import me.gv7.woodpecker.plugin.Bean.PropertiesBean;
import me.gv7.woodpecker.requests.RawResponse;
import me.gv7.woodpecker.requests.Requests;

import java.util.*;

public class SpringBootInfoDetec implements InfoDetector {

    List<String> pointListV1 = new ArrayList<>();
    List<String> pointListV2 = new ArrayList<>();
    Map<String,String> h2Headers = new HashMap<>();
    boolean SpringbootVersionV1 = false;
    String[] basicPoint = new String[]{"cloudfoundryapplication","hystrix.stream" };
    PropertiesBean properties;

    public SpringBootInfoDetec(){
        h2Headers.put("Cache-Control", "max-age=0");

        pointListV1.add("autoconfig");
        pointListV1.add("heapdump");
        pointListV1.add("dump");
        pointListV1.add("mappings");
        pointListV1.add("auditevents");
        pointListV1.add("beans");
        pointListV1.add("health");
        pointListV1.add("configprops");
        pointListV1.add("info");
        pointListV1.add("loggers");
        pointListV1.add("threaddump");
        pointListV1.add("metrics");
        pointListV1.add("trace");
        pointListV1.add("env/spring.jmx.enabled");


        pointListV2.add("actuator/auditevents");
        pointListV2.add("actuator/beans");
        pointListV2.add("actuator/health");
        pointListV2.add("actuator/conditions");
        pointListV2.add("actuator/configprops");
        pointListV2.add("actuator/info");
        pointListV2.add("actuator/loggers");
        pointListV2.add("actuator/threaddump");
        pointListV2.add("actuator/metrics");
        pointListV2.add("actuator/httptrace");
        pointListV2.add("actuator/mappings");
        pointListV2.add("actuator/jolokia");
        pointListV2.add("actuator/hystrix.stream");
        pointListV2.add("actuator/env/spring.jmx.enabled");

        pointListV2.add("monitor/auditevents");
        pointListV2.add("monitor/beans");
        pointListV2.add("monitor/conditions");
        pointListV2.add("monitor/configprops");
        pointListV2.add("monitor/env");
        pointListV2.add("monitor/info");
        pointListV2.add("monitor/loggers");
        pointListV2.add("monitor/heapdump");
        pointListV2.add("monitor/threaddump");
        pointListV2.add("monitor/metrics");
        pointListV2.add("monitor/scheduledtasks");
        pointListV2.add("monitor/httptrace");
        pointListV2.add("monitor/mappings");
        pointListV2.add("monitor/jolokia");
        pointListV2.add("monitor/hystrix.stream");
    }

    @Override
    public String getInfoDetectorTabCaption() {
        return "SpringBoot Actuators Info探测";
    }

    @Override
    public IArgsUsageBinder getInfoDetectorCustomArgs() {
        return null;
    }

    @Override
    public LinkedHashMap<String, String> doDetect(ITarget target, Map<String, Object> customArgs, IResultOutput result) throws Throwable {
        LinkedHashMap<String,String> infos = new LinkedHashMap<String, String>();

        String address = target.getRootAddress();
        try {
            if (SpringbootUtils.SpringbootCheck(address)){
                result.successPrintln("检测到springboot 404特征！");
            }

            if (SpringbootUtils.check404(address)){
                result.errorPrintln("默认404页面返回200，无法准确爆破！");
            }
        }catch (Exception e){
            result.errorPrintln(e.toString());
        }
        checkActuatorPointV1(address, result, infos);
        checkEnvPointV1(address, result, infos);
        if (!SpringbootVersionV1){
            checkActuatorPointV2(address, result, infos);
            checkEnvPointV2(address, result, infos);
        }
        checkJolokiaActuatorPoint(address, result, infos);
        checkJolokiaListPoint(address, result, infos);
        checkBasicPoint(address, result, infos);
        checkH2(address, result, infos);
        checkJenkins(address, result, infos);
        result.infoPrintln("检测流程结束");

        return infos;
    }

    // Spring Boot env端点存在环境属性覆盖和XStream反序列化漏洞
    private void checkEnvPointV1(String addr,IResultOutput result,LinkedHashMap<String,String> infos){
        final String url = addr+"env";
        RawResponse response = getResponse(url);
        String resp = SpringbootUtils.scannerOutput(new Scanner(response.body()));
        if (response.statusCode() ==200){
            SpringbootVersionV1 = true;
            result.successPrintln("检测到env端点，Springboot 1.x: "+ url);
            infos.put(url, "Success");
            properties = SpringbootUtils.EnvParser(resp);
            parseProperties(properties, result);
            if (resp.contains("spring.cloud.bootstrap.location")){
                result.successPrintln(" [*]检测到spring.cloud.bootstrap.location属性,可进行环境属性覆盖RCE!");
            }else if(resp.contains("eureka.client.serviceUrl.defaultZone")){
                result.successPrintln(" [*]检测到eureka.client.serviceUrl.defaultZone属性,可进行XStream反序列化RCE!");
            }else if (resp.contains("spring.h2.console.enabled")){
                result.successPrintln(" [*]检测到配置了H2 console属性,可能可以进行h2反序列化RCE!");
            }
        }
    }

    // Spring Boot 2.x版本存在H2配置不当导致的RCE，目前非正则判断，测试阶段
    private void checkEnvPointV2(String addr,IResultOutput result,LinkedHashMap<String,String> infos){
        final String url = addr+"actuator/env";
        RawResponse response = getResponse(url);
        String resp = SpringbootUtils.scannerOutput(new Scanner(response.body()));
        //String resp = response.readToText();
        if (response.statusCode() ==200){
            infos.put(url, "Success");
            result.successPrintln("检测到 env端点，Springboot 2.x: "+ url);
            properties = SpringbootUtils.EnvParser(resp);
            parseProperties(properties, result);
            if (resp.contains("spring.cloud.bootstrap.location")){
                result.successPrintln(" [*]检测到spring.cloud.bootstrap.location属性,可进行环境属性覆盖RCE!");
            }else if(resp.contains("eureka.client.serviceUrl.defaultZone")){
                result.successPrintln(" [*]检测到eureka.client.serviceUrl.defaultZone属性,可进行XStream反序列化RCE!");
            }else if (resp.contains("spring.h2.console.enabled")){
                result.successPrintln(" [*]检测到配置了H2 console属性,可能可以进行h2反序列化RCE!");
            }
        }
        RawResponse h2Response = Requests.post(addr + "actuator/restart")
                .verify(false)
                .headers(h2Headers)
                .send();
        if (h2Response.statusCode() == 200){
            infos.put(addr + "actuator/restart", "Success");
            result.successPrintln(" [*]检测到env restart端点,可进行H2 RCE!");
        }
    }

    // Spring Boot 1.x版本端点在根URL下注册。
    private void checkActuatorPointV1(String addr,IResultOutput result,LinkedHashMap<String,String> infos){
        for (String point: pointListV1){
            if (checkPoint(addr, point, result, infos)){
                SpringbootVersionV1 = true;
            }
        }
    }

    // Spring Boot 2.x版本端点移动到/actuator/路径
    private void checkActuatorPointV2(String addr,IResultOutput result,LinkedHashMap<String,String> infos){
        for (String point: pointListV1){
            checkPoint(addr, point, result, infos);
        }
    }

    private void checkBasicPoint(String addr,IResultOutput result,LinkedHashMap<String,String> infos){
        for (String point: basicPoint){
            checkPoint(addr, point, result, infos);
        }
    }

    private void checkJolokiaListPoint(String addr,IResultOutput result,LinkedHashMap<String,String> infos){
        final String url = addr+"jolokia/list";
        RawResponse response = getResponse(url);
        checkJolokiaPoint(response, url, result, infos);
    }

    private void checkJolokiaActuatorPoint(String addr, IResultOutput result,LinkedHashMap<String,String> infos){
        final String url = addr+"actuator/jolokia/list";
        RawResponse response = getResponse(url);
        checkJolokiaPoint(response, url, result, infos);
    }

    private void checkH2(String addr, IResultOutput result,LinkedHashMap<String,String> infos){
        final String h2Url1 = addr+"h2";
        final String h2Url2 = addr+"h2-console";
        if(getResponse(h2Url1).statusCode()==200){
            infos.put(h2Url1, "Success");
            result.successPrintln("[*]检测到存在h2 console!");
            result.successPrintln(h2Url1);
        }else if(getResponse(h2Url2).statusCode()==200){
            infos.put(h2Url2, "Success");
            result.successPrintln("[*]检测到存在h2 console!");
            result.successPrintln(h2Url2);
        }
    }

    private void checkJenkins(String addr, IResultOutput result,LinkedHashMap<String,String> infos) {
        final String jenkinsUrl = addr+"jenkins";
        if (getResponse(jenkinsUrl).statusCode()==200){
            infos.put(jenkinsUrl, "Success");
            result.successPrintln("[*]检测到存在Jenkins!");
            result.successPrintln(jenkinsUrl);
        }
    }

    private void checkJolokiaPoint(RawResponse response,String url,IResultOutput result, LinkedHashMap<String,String> infos){
        if (response.statusCode() ==200){
            String resp = SpringbootUtils.scannerOutput(new Scanner(response.body()));
            result.successPrintln("检测到 jolokia端点: "+ url);
            infos.put(url, "success");
            if (resp.contains("reloadByURL")){
                result.successPrintln(" [*]检测到reloadByURL方法,可进行XXE/RCE!");
            }else if(resp.contains("createJNDIRealm")){
                result.successPrintln(" [*]检测到createJNDIRealm方法,可进行JNDI注入!");
            }
        }
    }

    private boolean checkPoint(String addr,String point,IResultOutput result, LinkedHashMap<String,String> infos){
        final String url = addr+point;
        RawResponse response = getResponse(url);
        if (response.statusCode()==200){
            if (SpringbootUtils.checkPoint(url, response)){
                result.successPrintln("检测到 "+ point +"端点,已做验证: "+ url);
                infos.put(url, "success");
                return true;
            }else {
                infos.put(url, "maybe");
                result.infoPrintln("检测到 "+ point +"端点，请手工验证: "+ url);
            }

        }
        return false;
    }

    private RawResponse getResponse(String url){
        return Requests.get(url).verify(false).timeout(10000).send();
    }
    private void parseProperties(PropertiesBean properties, IResultOutput result){
        if (properties.getHaveInfo()){
            result.infoPrintln("\tJVM信息:\t\t"+properties.getJvmName());
            result.infoPrintln("\t端口信息:\t\t"+properties.getServerPort());
            result.infoPrintln("\tJava版本:\t\t"+properties.getJavaVersion());
            result.infoPrintln("\t用户名:\t\t"+properties.getUserName());
        }
    }

}