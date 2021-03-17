package me.gv7.woodpecker.plugin;

import me.gv7.woodpecker.plugin.exploits.SpringBootJolokiaExploit;

import java.util.ArrayList;
import java.util.List;

public class SpringBootJolokiaPlugin implements IVulPlugin {
    public static IVulPluginCallbacks callbacks;
    public static IPluginHelper pluginHelper;

    @Override
    public void VulPluginMain(IVulPluginCallbacks vulPluginCallbacks) {
        SpringBootJolokiaPlugin.callbacks = vulPluginCallbacks;
        SpringBootJolokiaPlugin.pluginHelper = callbacks.getPluginHelper();
        callbacks.setVulPluginName("SpringBoot Jolokia 反序列化");
        callbacks.setVulPluginVersion("0.1.0");
        callbacks.setVulPluginAuthor("Frost Blue");
        callbacks.setVulCVSS(9.5);
        callbacks.setVulName("SpringBoot Jolokia 反序列化");
        callbacks.setVulDescription("反序列化可直接造成RCE");
        callbacks.setVulCategory("RCE");
        callbacks.setVulProduct("SpringBoot");
        callbacks.setVulSeverity("high");

        List<IExploit> exploitList = new ArrayList<IExploit>();
        exploitList.add(new SpringBootJolokiaExploit());
        callbacks.registerExploit(exploitList);

        final List<IPayloadGenerator> payloadGeneratorList = new ArrayList<IPayloadGenerator>();
        //payloadGeneratorList.add(new SeeyonHOSUploadPayloadGenerate());
        //callbacks.registerPoc(new SeeyonPoc());
        callbacks.registerPayloadGenerator(payloadGeneratorList);
    }
}
