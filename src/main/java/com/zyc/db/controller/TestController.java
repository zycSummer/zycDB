package com.zyc.db.controller;

import com.zyc.db.repository.ZycAllRepo;
import com.zyc.db.service.DbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * User: zyc
 * Date: 2020/8/15
 * Time: 20:35
 * Description:
 */
@RestController
public class TestController {
    @Autowired
    private ZycAllRepo zycAllRepo;
    @Autowired
    private DbService dbService;

    @GetMapping("/db/{dbName}")
    public String queryLeftAlarmById(@PathVariable String dbName) {
        return dbService.createDB(dbName);
    }
}
