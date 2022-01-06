package apitest.utils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
/**
 * @author lulu
 * @data 2022/1/6 10:46 上午
 */
public class DbCheckUtils {
    public  static boolean dbCheck(String dbCheck) throws SQLException {
        if(StringUtils.isNotBlank(dbCheck)) {
            String[] dbcheck_array=dbCheck.split(",");
            String sql=dbcheck_array[0];
            sql=ParamsUtils.replace(sql);
            QueryRunner runner=null;
            if(dbcheck_array.length<=2) {
                runner = new QueryRunner(JdbcUtils.getDataSource());
            }else {
                runner = new QueryRunner(JdbcUtils.getDataSource(dbcheck_array[2]));
            }

            System.out.println("sql "+sql);
            List<Map<String, Object>> list =  runner.query(sql, new MapListHandler());
            System.out.println("结果"+list.size());
            JsonCheckResult jsonrResult= CheckPointUtils.check(JSON.toJSONString(list), dbcheck_array[1]);
            System.out.println("检查结果"+jsonrResult.getMsg());
            return jsonrResult.isResult();
        }
        return false;
    }

    public static void main(String[] args) {
        //ParamsUtils.addMap(key, value);
    }

}
