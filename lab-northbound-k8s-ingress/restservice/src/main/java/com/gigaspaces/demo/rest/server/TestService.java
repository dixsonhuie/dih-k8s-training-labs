package com.gigaspaces.demo.rest.server;

import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.SpaceProxyConfigurer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
@Path("/restful-example")
public class TestService {
    GigaSpace gigaSpace;

    private void init() {
        SpaceProxyConfigurer configurer = new SpaceProxyConfigurer("demo");
        gigaSpace = new GigaSpaceConfigurer(configurer).gigaSpace();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getTestService(
        @QueryParam("id") String id) {
        String s = null;
        try {
            if (gigaSpace == null) {
                init();
            }

            SQLQuery<java.lang.Object> query = new SQLQuery<java.lang.Object>(java.lang.Object.class, "");
            int count = gigaSpace.count(query);
            s = String.format("Count of objects is: %d", count);
        } catch
        (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return s;
    }
}



