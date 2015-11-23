package ehealth;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

public class App
{
    public static void main(String[] args)
    {
        try {
            String protocol = "http://";
            String port_value = "3100";
            if (String.valueOf(System.getenv("PORT")) != "null") {
                port_value = String.valueOf(System.getenv("PORT"));
            }
            String port = ":" + port_value + "/";
            String hostname = InetAddress.getLocalHost().getHostAddress();
            if (hostname.equals("127.0.0.1")) {
                hostname = "localhost";
            }
            URI BASE_URI = new URI(protocol + hostname + "ehealth/");
            System.out.println("*********************************");
            System.out.println(BASE_URI);

            System.out.println("Starting sdelab standalone HTTP server...");
            JdkHttpServerFactory.createHttpServer(BASE_URI, createApp());

            System.out.println("Server started on " + BASE_URI + "\n[kill the process to exit]");
        }catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (URISyntaxException uri) {
            uri.printStackTrace();
        }
    }

    public static ResourceConfig createApp() {
        System.out.println("Starting sdelab REST services...");
        return new MyApplicationConfig();
    }
}