package apitest.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author lulu
 * @data 2022/1/6 10:56 上午
 */
public class JdbcUtils {
    public static final ComboPooledDataSource ds_deault = new ComboPooledDataSource(); //统一数据连接关联

    public static final  ComboPooledDataSource ds_mysql = new ComboPooledDataSource("mysql1"); //统一数据连接关联

    public static final  ComboPooledDataSource ds_mysql2 = new ComboPooledDataSource("mysql2"); //统一数据连接关联


    public static DataSource getDataSource(String type) {
        if("mysql".equals(type)) {
            return ds_mysql;
        }else if ("mysql2".equals(type)) {
            return ds_mysql2;
        }

        return ds_deault;

    }

    public static DataSource getDataSource() {
        return getDataSource("");
    }

    public static Connection getConnection(String type) throws SQLException {
        return getDataSource(type).getConnection();
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource("").getConnection();
    }
}
