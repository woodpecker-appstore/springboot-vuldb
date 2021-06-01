package me.gv7.woodpecker.plugin;

import me.gv7.woodpecker.plugin.InfoDetec.SpringBootInfoDetec;

import java.util.ArrayList;
import java.util.List;

public class SpringBootInfoDetecPlugin implements InfoDetectorPlugin{

    IPluginHelper pluginHelper;

    @Override
    public void InfoDetectorPluginMain(InfoDetectorPluginCallbacks callbacks) {
        pluginHelper = callbacks.getPluginHelper();
        callbacks.setInfoDetectorPluginAuthor("Ppsoft1991");
        callbacks.setInfoDetectorPluginName("SpringBoot漏洞检测");
        callbacks.setInfoDetectorPluginDescription("通过检测路径来判断Springboot漏洞");
        callbacks.setInfoDetectorPluginVersion("woodpecker-2020-1021");
        List<InfoDetector> infoDetecList = new ArrayList<>();

        infoDetecList.add(new SpringBootInfoDetec());
        callbacks.registerInfoDetector(infoDetecList);
    }
}
