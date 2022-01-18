package org.practice;

import org.practice.dif.ApplicationContext;
import org.practice.service.UserService;

public class Application {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ApplicationContext(Config.class);

        UserService service = context.getBean(UserService.class);
        System.out.println(service.getPerson("4654664"));

    }
}
