package com.zyc.db.service.impl;

import com.zyc.db.service.DbService;
import com.zyc.db.util.DataBaseSqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: zyc
 * Date: 2020/8/15
 * Time: 21:13
 * Description:
 */
@Service
public class DbServiceImpl implements DbService {
    @Value("${spring.datasource.driver-class-name}")
    private String driverClass;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public String createDB(String dbName) {
        String str = "";
        try {
            ClassPathResource classPathResource = new ClassPathResource("sql/db.sql");
            InputStream inputStream = classPathResource.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String s = "";
            try {
                while ((s = br.readLine()) != null) {
                    str = str + s;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Json信息查询出错");
        }
        String tableSql = str.replaceAll("AAAA", dbName);
        DataBaseSqlUtils.createDatabase(dbName, driverClass, url, username, password);
        DataBaseSqlUtils.createTable(tableSql, driverClass, url, username, password);
        return tableSql;
    }
}
