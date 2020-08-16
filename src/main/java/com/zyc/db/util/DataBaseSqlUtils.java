package com.zyc.db.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zyc
 * Date: 2020/8/15
 * Time: 21:27
 * Description:
 */
public class DataBaseSqlUtils {
    private static final Logger log = LoggerFactory.getLogger(DataBaseSqlUtils.class);
    //配置文件 读取jdbc的配置文件
    private static Connection conn;
    private static PreparedStatement ps;

    /**
     * 创建数据库
     *
     * @param dbName
     * @param driverClass
     * @param url
     * @param username
     * @param password
     */
    public static void createDatabase(String dbName, String driverClass, String url, String username, String password) {
        conn = getConnection(driverClass, url, username, password);  // 首先要获取连接，即连接到数据库
        try {
            String sql = "CREATE DATABASE " + dbName + ";";
            ps = conn.prepareStatement(sql);
            ps.executeUpdate(sql);
            ps.close();
            conn.close();  //关闭数据库连接
        } catch (SQLException e) {
            log.info("建库失败：{}", e.getMessage());
        }
    }

    /**
     * 创建表
     *
     * @param tableSql    SQL脚本语句
     * @param driverClass 驱动
     * @param url         请求地址
     * @param username    用户名
     * @param password    密码
     */
    public static void createTable(String tableSql, String driverClass, String url, String username, String password) {
        conn = getConnection(driverClass, url, username, password);  // 首先要获取连接，即连接到数据库
        try {
            log.info("建表语句是：{}", tableSql);
            ps = conn.prepareStatement(tableSql);
            List<String> sql = getSql(tableSql);
            for (String s : sql) {
                ps.addBatch(s);
            }
            ps.executeBatch();
            ps.close();
            conn.close();  //关闭数据库连接
        } catch (SQLException e) {
            log.info("建表失败：{}", e.getMessage());
        }
    }

    /*
     * getSqlArray方法
     * 从文件的sql字符串中分析出能够独立执行的sql语句并返回
     */
    public static List<String> getSql(String sql) {
        String s = sql;
        s = s.replaceAll("\r\n", "\r");
        s = s.replaceAll("\r", "\n");
        List<String> ret = new ArrayList<String>();
        String[] sqlarry = s.split(";");  //用;把所有的语句都分开成一个个单独的句子
        sqlarry = filter(sqlarry);
        ret = Arrays.asList(sqlarry);
        return ret;
    }

    public static String[] filter(String[] ss) {
        List<String> strs = new ArrayList<String>();
        for (String s : ss) {
            if (s != null && !s.equals("")) {
                strs.add(s);
            }
        }
        String[] result = new String[strs.size()];
        for (int i = 0; i < strs.size(); i++) {
            result[i] = strs.get(i).toString();
        }
        return result;
    }

    /**
     * 添加数据
     *
     * @param tabName 表名
     * @param fields  参数字段
     * @param data    参数字段数据
     */
    public static void insert(String tabName, String[] fields, String[] data, String driverClass, String url, String username, String password) {
        conn = getConnection(driverClass, url, username, password);  // 首先要获取连接，即连接到数据库
        try {
            String sql = "insert into " + tabName + "(";
            int length = fields.length;
            for (int i = 0; i < length; i++) {
                sql += fields[i];
                //防止最后一个,
                if (i < length - 1) {
                    sql += ",";
                }
            }
            sql += ") values(";
            for (int i = 0; i < length; i++) {
                sql += "?";
                //防止最后一个,
                if (i < length - 1) {
                    sql += ",";
                }
            }
            sql += ");";
            System.out.println("添加数据的sql:" + sql);
            //预处理SQL 防止注入
            excutePs(sql, length, data);
            //执行
            ps.executeUpdate();
            //关闭流
            ps.close();
            conn.close();  //关闭数据库连接
        } catch (SQLException e) {
            System.out.println("添加数据失败" + e.getMessage());
        }
    }

    /**
     * 查询表 【查询结果的顺序要和数据库字段的顺序一致】
     *
     * @param tabName    表名
     * @param fields     参数字段
     * @param data       参数字段数据
     * @param tab_fields 数据库的字段
     */
    public static String[] query(String tabName, String[] fields, String[] data, String[] tab_fields, String driverClass, String url, String username, String password) {
        conn = getConnection(driverClass, url, username, password);  // 首先要获取连接，即连接到数据库
        String[] result = null;
        try {
            String sql = "select * from " + tabName + " where ";
            int length = fields.length;
            for (int i = 0; i < length; i++) {
                sql += fields[i] + " = ? ";
                //防止最后一个,
                if (i < length - 1) {
                    sql += " and ";
                }
            }
            sql += ";";
            System.out.println("查询sql:" + sql);
            //预处理SQL 防止注入
            excutePs(sql, length, data);
            //查询结果集
            ResultSet rs = ps.executeQuery();
            //存放结果集
            result = new String[tab_fields.length];
            while (rs.next()) {
                for (int i = 0; i < tab_fields.length; i++) {
                    result[i] = rs.getString(tab_fields[i]);
                }
            }
            //关闭流
            rs.close();
            ps.close();
            conn.close();  //关闭数据库连接
        } catch (SQLException e) {
            System.out.println("查询失败" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取某张表总数
     *
     * @param tabName
     * @return
     */
    public static Integer getCount(String tabName, String driverClass, String url, String username, String password) {
        int count = 0;
        conn = getConnection(driverClass, url, username, password);  // 首先要获取连接，即连接到数据库
        try {
            String sql = "select count(*) from " + tabName + " ;";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            ps.close();
            conn.close();  //关闭数据库连接
        } catch (SQLException e) {
            System.out.println("获取总数失败" + e.getMessage());
        }
        return count;
    }

    /**
     * 后台分页显示
     *
     * @param tabName
     * @param pageNo
     * @param pageSize
     * @param tab_fields
     * @return
     */
    public static List<String[]> queryForPage(String tabName, int pageNo, int pageSize, String[] tab_fields, String driverClass, String url, String username, String password) {
        conn = getConnection(driverClass, url, username, password);  // 首先要获取连接，即连接到数据库
        List<String[]> list = new ArrayList<String[]>();
        try {
            String sql = "select * from " + tabName + " LIMIT ?,? ; ";
            System.out.println("查询sql:" + sql);
            //预处理SQL 防止注入
            ps = conn.prepareStatement(sql);
            //注入参数
            ps.setInt(1, pageNo);
            ps.setInt(2, pageSize);
            //查询结果集
            ResultSet rs = ps.executeQuery();
            //存放结果集
            while (rs.next()) {
                String[] result = new String[tab_fields.length];
                for (int i = 0; i < tab_fields.length; i++) {
                    result[i] = rs.getString(tab_fields[i]);
                }
                list.add(result);
            }
            //关闭流
            rs.close();
            ps.close();
            conn.close();  //关闭数据库连接
        } catch (SQLException e) {
            System.out.println("查询失败" + e.getMessage());
        }
        return list;
    }

    /**
     * 清空表数据
     *
     * @param tabName 表名称
     */
    public static void delete(String tabName, String driverClass, String url, String username, String password) {
        conn = getConnection(driverClass, url, username, password);  // 首先要获取连接，即连接到数据库
        try {
            String sql = "delete from " + tabName + ";";
            System.out.println("删除数据的sql:" + sql);
            //预处理SQL 防止注入
            ps = conn.prepareStatement(sql);
            //执行
            ps.executeUpdate();
            //关闭流
            ps.close();
            conn.close();  //关闭数据库连接
        } catch (SQLException e) {
            System.out.println("删除数据失败" + e.getMessage());
        }
    }

    /**
     * 用于注入参数
     *
     * @param ps
     * @param data
     * @throws SQLException
     */
    private static void excutePs(String sql, int length, String[] data) throws SQLException {
        //预处理SQL 防止注入
        ps = conn.prepareStatement(sql);
        //注入参数
        for (int i = 0; i < length; i++) {
            ps.setString(i + 1, data[i]);
        }
    }

    /* 获取数据库连接的函数*/
    private static Connection getConnection(String driverClass, String url, String username, String password) {
        Connection con = null;  //创建用于连接数据库的Connection对象
        try {
            Class.forName(driverClass);// 加载Mysql数据驱动
            con = DriverManager.getConnection(url, username, password);// 创建数据连接
        } catch (Exception e) {
            System.out.println("数据库连接失败" + e.getMessage());
        }
        return con;  //返回所建立的数据库连接
    }

    /**
     * 判断表是否存在
     *
     * @param tabName
     * @return
     */
    public static boolean exitTable(String tabName, String driverClass, String url, String username, String password) {
        boolean flag = false;
        conn = getConnection(driverClass, url, username, password);  // 首先要获取连接，即连接到数据库
        try {
            String sql = "select id from " + tabName + ";";
            //预处理SQL 防止注入
            ps = conn.prepareStatement(sql);
            //执行
            flag = ps.execute();
            //关闭流
            ps.close();
            conn.close();  //关闭数据库连接
        } catch (SQLException e) {
            System.out.println("删除数据失败" + e.getMessage());
        }
        return flag;
    }

    /**
     * 删除数据表
     * 如果执行成功则返回false
     *
     * @param tabName
     * @return
     */
    public static boolean dropTable(String tabName, String driverClass, String url, String username, String password) {
        boolean flag = true;
        conn = getConnection(driverClass, url, username, password);  // 首先要获取连接，即连接到数据库
        try {
            String sql = "drop table " + tabName + ";";
            //预处理SQL 防止注入
            ps = conn.prepareStatement(sql);
            //执行
            flag = ps.execute();
            //关闭流
            ps.close();
            conn.close();  //关闭数据库连接
        } catch (SQLException e) {
            System.out.println("删除数据失败" + e.getMessage());
        }
        return flag;
    }

    /**
     * 测试方法
     *
     * @param args
     */
    public static void main(String[] args) {
        //建表===========================================
        //表名
//    String tabName = "mytable";
        //表字段
//    String[] tab_fields = {"name","password","sex","age"};
        //创建表
//    createTable(tabName, tab_fields);
        //添加===========================================
        //模拟数据
//    String[] data1 = {"jack","123456","男","25"};
//    String[] data2 = {"tom","456789","女","20"};
//    String[] data3 = {"mark","aaa","哈哈","21"};
        //插入数据
//    insert(tabName, tab_fields, data1);
//    insert(tabName, tab_fields, data2);
//    insert(tabName, tab_fields, data3);
        //查询=============================================
//    String[] q_fileds ={"name","sex"};
//    String[] data4 = {"jack","男"};
//
//    String[] result = query(tabName, q_fileds, data4, tab_fields);
//    for (String string : result) {
//      System.out.println("结果：\t"+string);
//    }
        //删除 清空=============================================
//    delete(tabName);
        //是否存在
//    System.out.println(exitTable("mytable"));
        //删除表
//    System.out.println(dropTable("mytable"));
    }
}

