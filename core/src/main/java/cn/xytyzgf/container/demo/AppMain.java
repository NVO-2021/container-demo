package cn.xytyzgf.container.demo;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by xiazhen on 18/6/5.
 */
@Controller
@ComponentScan
@Configuration
@EnableScheduling
@EnableWebMvc
@EnableAutoConfiguration
@ImportResource(locations = {"classpath*:app.xml"})
public class AppMain implements ApplicationContextAware {

    private final static Logger log = LoggerFactory.getLogger(AppMain.class);

    private final static int retention = 86400 * 1000 * 3;

    private final static List<Runnable> preHaltTasks = Lists.newArrayList();

    private static ApplicationContext context;

    public static ApplicationContext context() {
        return context;
    }

    private static boolean halt = false;

    @Autowired
    Environment environment;

    @RequestMapping("/ok.htm")
    @ResponseBody
    String ok(@RequestParam(defaultValue = "false") String down, final HttpServletResponse response) {
        if (halt) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            return "halting";
        }
        if (Boolean.parseBoolean(down) && !halt) {
            log.warn("prehalt initiated and further /ok.htm request will return with status 503");
            halt = true;
            for (final Runnable r : preHaltTasks) {
                try {
                    r.run();
                } catch (Exception e) {
                    log.error("prehalt task failed", e);
                }
            }
        }
        return "ok";
    }

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "ok";
    }


    public static void main(String[] args) throws Exception {
        log.warn("container.demo started 😘");
        try {
            SpringApplication.run(AppMain.class, args);
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (AppMain.context == null) {
            AppMain.context = applicationContext;
        }
    }


}

