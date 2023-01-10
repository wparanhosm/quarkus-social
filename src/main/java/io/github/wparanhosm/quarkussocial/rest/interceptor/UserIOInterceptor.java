package io.github.wparanhosm.quarkussocial.rest.interceptor;

import io.github.wparanhosm.quarkussocial.domain.model.User;

import io.vertx.core.http.HttpServerRequest;

import javax.ws.rs.container.*;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import javax.ws.rs.ext.Provider;

import java.io.IOException;

import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.logging.Logger;

@ValidateUserIdEntity
@Provider
public class UserIOInterceptor implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger LOG = Logger.getLogger(UserIOInterceptor.class.getName());

    @Context
    UriInfo info;
    @Context
    HttpServerRequest request;
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final String message = String.format("Request %s %s from IP %s", requestContext.getMethod(), info.getPath(),  request.remoteAddress().toString());
        LOG.info(message);

        Method method = resourceInfo.getResourceMethod();

        if (method != null) {
            ValidateUserIdEntity entity = method.getAnnotation(ValidateUserIdEntity.class);
            Arrays.stream(entity.names()).forEach(e -> {

                Long userId = Long.decode(requestContext.getUriInfo().getPathParameters().getFirst(e));
                User user = User.findById(userId);

                if(user == null){
                    requestContext.abortWith(Response.status(Response.Status.NOT_FOUND).build());
                }
            });
        }
    }




    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        final String message = String.format("Response from  %s %s from IP %s - Status Code: %s", requestContext.getMethod(), info.getPath(),  request.remoteAddress().toString(), responseContext.getStatus());
        LOG.info(message);
    }
}
