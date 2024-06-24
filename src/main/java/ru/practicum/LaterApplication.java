package ru.practicum;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class LaterApplication {

    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setSilent(true);
        tomcat.getConnector().setPort(8080);

        Context context = tomcat.addContext("", null);

        Wrapper wrapper = Tomcat.addServlet(context, "test", new TestServlet());
        wrapper.addMapping("/test");


        AnnotationConfigWebApplicationContext annotationConfigWebApplicationContext = new
                AnnotationConfigWebApplicationContext();

        annotationConfigWebApplicationContext.scan("ru.practicum");
        annotationConfigWebApplicationContext.setServletContext(context.getServletContext());
        annotationConfigWebApplicationContext.refresh();

        DispatcherServlet dispatcherServlet = new DispatcherServlet(annotationConfigWebApplicationContext);
        Wrapper testServletWrapper = Tomcat.addServlet(context, "dispatcherservlet", dispatcherServlet);


        testServletWrapper.addMapping("/");
        testServletWrapper.setLoadOnStartup(1);

        tomcat.start();

    }
}