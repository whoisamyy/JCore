package ru.whoisamyy.core;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.File;


@Getter
@Configuration
@PropertySource("file:settings.yml")
public class ServerConfig {
    @Value("${server_url}")
    public String serverURL;
    @Value("${db_url}")
    public static String url;
    @Value("${db_username}")
    public static String username;
    @Value("${db_password}")
    public static String password;
    @Value("${salt}")
    public static String SALT;
}
