package me.gv7;

import static org.junit.Assert.assertTrue;

import net.dongliu.requests.Requests;
import org.junit.Test;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        final String url = "http://118.31.122.159:9999/"+"actuator/env";
        String response = Requests.get(url).verify(false).send().readToText();
        if (response.contains("111")){
            System.out.println("OK");
        }
        System.out.println(response);

//        final String url = "http://47.115.79.250:8500/"+"actuator/env";
//        InputStream stream = Requests.get(url).verify(false).send().body();
//        Scanner scanner = new Scanner(stream);
//        StringBuilder builder = new StringBuilder();
//        while (scanner.hasNext()){
//            builder.append(scanner.nextLine()).append("\n");
//        }
//        System.out.println(builder.toString());
    }

    @Test
    public void shouldAnswerWithTrue2(){

    }
}
