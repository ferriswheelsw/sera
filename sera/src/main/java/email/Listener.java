package email;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import sera.sera.Testing;
import sera.sera.User;

import javax.mail.internet.AddressException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static sera.sera.Testing.email;

@WebListener
public class Listener implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {
    private ScheduledExecutorService scheduler;
    public Listener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        System.out.println("uhuhuh");
        System.out.println(sc.getContextPath());
        System.out.println(sc.getRealPath("src/main/webapp/img/seralogo.png"));
        System.out.println(sc.getResourcePaths("/src/main/webapp/img"));

        scheduler = Executors.newSingleThreadScheduledExecutor();
        // scheduler.scheduleAtFixedRate(new DailyJob(), 0, 1, TimeUnit.DAYS);
        try {
//            scheduler.schedule(new Email(), 1, TimeUnit.MINUTES);
            scheduler.scheduleAtFixedRate(new Email(), 0, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //scheduler.scheduleAtFixedRate(new MinJob(), 0, 1, TimeUnit.MINUTES);
        // scheduler.scheduleAtFixedRate(new SecJob(), 0, 15, TimeUnit.SECONDS);
        /* This method is called when the servlet context is initialized(when the Web application is deployed). */
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        /* This method is called when the servlet Context is undeployed or Application Server shuts down. */
        scheduler.shutdownNow();
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        /* Session is created. */
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        /* Session is destroyed. */
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is added to a session. */
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is removed from a session. */
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is replaced in a session. */
    }
}
