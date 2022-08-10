package works.weave.socks.cart.middleware;

import io.prometheus.client.Histogram;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.webmvc.RepositoryRestHandlerMapping;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class HTTPMonitoringInterceptor implements HandlerInterceptor {
  private static final Histogram REQUEST_LATENCY =
      Histogram.build()
          .name("request_duration_seconds")
          .help("Request duration in seconds.")
          .labelNames("service", "method", "route", "status_code")
          .register();

  private static final String START_TIME_KEY = "startTime";

  @Value("${spring.application.name}")
  private String serviceName;

  @Autowired private ResourceMappings mappings;
  @Autowired private RepositoryRestConfiguration repositoryConfiguration;
  @Autowired private ApplicationContext applicationContext;
  @Autowired private RequestMappingHandlerMapping requestMappingHandlerMapping;

  private Set<PatternsRequestCondition> urlPatterns;

  @Override
  public boolean preHandle(
      HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
      throws Exception {
    httpServletRequest.setAttribute(START_TIME_KEY, System.nanoTime());
    return true;
  }

  @Override
  public void postHandle(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      Object o,
      ModelAndView modelAndView)
      throws Exception {
    var start = (long) httpServletRequest.getAttribute(START_TIME_KEY);
    var elapsed = System.nanoTime() - start;
    var seconds = (double) elapsed / 1000000000.0;
    var matchedUrl = getMatchingURLPattern(httpServletRequest);
    if (!matchedUrl.equals("")) {
      REQUEST_LATENCY
          .labels(
              serviceName,
              httpServletRequest.getMethod(),
              matchedUrl,
              Integer.toString(httpServletResponse.getStatus()))
          .observe(seconds);
    }
  }

  @Override
  public void afterCompletion(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      Object o,
      Exception e)
      throws Exception {}

  private String getMatchingURLPattern(HttpServletRequest httpServletRequest) {
    var res = "";
    for (var pattern : getUrlPatterns()) {
      if (pattern.getMatchingCondition(httpServletRequest) != null
          && !httpServletRequest.getServletPath().equals("/error")) {
        res = pattern.getMatchingCondition(httpServletRequest).getPatterns().iterator().next();
        break;
      }
    }
    return res;
  }

  private Set<PatternsRequestCondition> getUrlPatterns() {
    if (urlPatterns == null) {
      urlPatterns = new HashSet<>();
      requestMappingHandlerMapping
          .getHandlerMethods()
          .forEach((mapping, handlerMethod) -> urlPatterns.add(mapping.getPatternsCondition()));
      var repositoryRestHandlerMapping =
          new RepositoryRestHandlerMapping(mappings, repositoryConfiguration);
      repositoryRestHandlerMapping.setApplicationContext(applicationContext);
      repositoryRestHandlerMapping.afterPropertiesSet();
      repositoryRestHandlerMapping
          .getHandlerMethods()
          .forEach((mapping, handlerMethod) -> urlPatterns.add(mapping.getPatternsCondition()));
    }
    return urlPatterns;
  }
}
