package com.salesmanager.rest.service;


import javax.ws.rs.*;

@Path(("/actuator"))
public interface ActuatorRestService {

    @Path("/shutdown")
    @POST
    @Consumes("application/json")
    // FIXME 暂时不支持
    ShutdownMessage shutdown(@BeanParam ShutdownMessage message);

    @Path("/shutdown")
    @POST
    @Consumes("application/json")
    ShutdownMessage shutdown();

}
