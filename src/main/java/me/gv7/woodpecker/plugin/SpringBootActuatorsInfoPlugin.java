package me.gv7.woodpecker.plugin;

import me.gv7.woodpecker.plugin.exploits.SpringBootActuatorsInfoExploit;
import me.gv7.woodpecker.plugin.pocs.SpringBootPocs;

import java.util.ArrayList;
import java.util.List;

public class SpringBootActuatorsInfoPlugin implements IVulPlugin {
    public static IVulPluginCallbacks callbacks;
    public static IPluginHelper pluginHelper;

    @Override
    public void VulPluginMain(IVulPluginCallbacks vulPluginCallbacks) {
        SpringBootActuatorsInfoPlugin.callbacks = vulPluginCallbacks;
        SpringBootActuatorsInfoPlugin.pluginHelper = callbacks.getPluginHelper();

        callbacks.setVulPluginName("Springboot接口探测");
        callbacks.setVulPluginVersion("0.1.0");
        callbacks.setVulPluginAuthor("notyeat");
        callbacks.setVulCVSS(9.5);
        callbacks.setVulName("Springboot接口探测");
        callbacks.setVulDescription("接口信息泄露，某些敏感接口会造成RCE");
        callbacks.setVulCategory("RCE");
        callbacks.setVulProduct("SpringBoot");
        callbacks.setVulId("woodpecker-2020-1021");
        callbacks.setVulSeverity("high");

        List<IExploit> exploitList = new ArrayList<>();
        exploitList.add(new SpringBootActuatorsInfoExploit());
        callbacks.registerExploit(exploitList);

        final List<IPayloadGenerator> payloadGeneratorList = new ArrayList<IPayloadGenerator>();
        callbacks.registerPayloadGenerator(payloadGeneratorList);
        callbacks.registerPoc(new SpringBootPocs());
    }
}
