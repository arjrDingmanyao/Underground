package com.creditcloud.newsservice;

/**
 * Hello world!
 *
 */
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author sobranie
 */
@ApplicationPath("/")
public class NewsserviceApplication extends ResourceConfig {

    public NewsserviceApplication() {
        packages("com.creditcloud.newsservice.resource");
    }
}
