package me.gv7.woodpecker.plugin.pocs;

import me.gv7.woodpecker.plugin.*;
import me.gv7.woodpecker.plugin.exploits.SpringBootActuatorsInfoExploit;

public class SpringBootPocs implements IPoc {
    @Override
    public IScanResult doCheck(ITarget target, IResultOutput iResultOutput) {
        IScanResult result = SpringBootActuatorsInfoPlugin.pluginHelper.createScanResult();
        iResultOutput.infoPrintln("[+] "+target.getAddress()+"攻击成功!\n");
        result.setExists(true);
        return result;
    }
}
