package me.gv7.woodpecker.plugin;

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
}
