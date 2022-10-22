/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.backend.config;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * @author hm
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {
  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext response) {
    response.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
    response.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST");
    response.getHeaders().putSingle("Access-Control-Allow-Headers", "content-type,Authorization");
    response.getHeaders().putSingle("Access-Control-Allow-Credentials", true);
  }
}
