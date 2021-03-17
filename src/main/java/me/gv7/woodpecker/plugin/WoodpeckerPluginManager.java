package me.gv7.woodpecker.plugin;

public class WoodpeckerPluginManager implements IPluginManager {
    public void registerPluginManagerCallbacks(IPluginManagerCallbacks pluginManagerCallbacks) {
        pluginManagerCallbacks.registerVulPlugin(new SpringBootActuatorsInfoPlugin());
    }
}
