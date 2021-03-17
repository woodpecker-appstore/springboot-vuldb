package me.gv7.woodpecker.plugin.pocs;

import me.gv7.woodpecker.plugin.*;
import me.gv7.woodpecker.plugin.exploits.SpringBootActuatorsInfoExploit;
import net.dongliu.requests.Requests;

public class SpringBootPocs implements IPoc {
    @Override
    public IScanResult doVerify(ITarget target, IResultOutput resultOutput) {
        IScanResult result = SpringBootActuatorsInfoPlugin.pluginHelper.createScanResult();
        final String[] actuator = {"actuator/env", "env"};
        if (SpringbootUtils.SpringbootCheck(target.getAddress())){
            for (String a:actuator){
                String url = target.getAddress()+a;
                if (Requests.post(url).verify(false).send().statusCode()==200){
                    resultOutput.infoPrintln("[+] "+url+"存在env端点!\n");
                }
            }
            result.setExists(true);
        }
        return result;
    }
}
