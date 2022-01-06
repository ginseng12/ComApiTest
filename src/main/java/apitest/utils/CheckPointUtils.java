package apitest.utils;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.googlecode.aviator.AviatorEvaluator;
/**
 * @author lulu
 * @data 2022/1/6 10:44 上午
 *
 * 核心思想是 jsonpath 和Aviator 组合
 *
 *
 */
public class CheckPointUtils {
    public static boolean openLog = false;

    public static JsonCheckResult check(String jsonString, String check) {
        if (check == null || jsonString == null || "null".equals(jsonString)) {
            return JsonCheckResult.SKIP;
        }
        if (!JSON.isValid(jsonString)) {
            return JsonCheckResult.ISVALID;
        }
        String[] check_array = check.split("&&|\\|\\|"); //&&|\\|\\|
        int keycount = 0;
        Map<String, Object> env = new LinkedHashMap<String, Object>();
        for (String curentcheck : check_array) {
            String oldcheck = curentcheck;
            String[] values = curentcheck.split("=|>|<|>=|<=|==");
            String key = values[0];
            String value = values[values.length - 1];
            Object checkValue = JSONPath.read(jsonString, key);
            //如果提取值没有，搜所有相关节点
            if(checkValue==null) {
                checkValue = JSONPath.read(jsonString, ".."+key);
            }
            // 表达式替换
            String newKey = "data" + keycount++;
            curentcheck = curentcheck.replace(key, newKey);
            if (checkValue != null) {
                // 如果传入的表达式不规范，返回的是list，默认取第一个
                if (checkValue instanceof List) {
                    List checkValueList = (List) checkValue;
                    if (!checkValueList.isEmpty()) {
                        checkValue =checkValueList.get(0);
                        //数组类型
                        env.put(newKey, checkValue);
                    }
                }else {
                    //普通类型
                    env.put(newKey, checkValue);
                }
            }

            //如果value类型是字符串要特殊处理  追加单引号
            if (checkValue instanceof String) {
                String newValue = covertToAviatorString(value.toString());
                curentcheck = replaceLast(curentcheck, value, newValue);
            }
            // =替换
            if (curentcheck.contains("=") && !curentcheck.contains(">=")&& !curentcheck.contains("<=") &&!curentcheck.contains("==")) {
                curentcheck = curentcheck.replace("=", "==");
            }
            check = check.replace(oldcheck, curentcheck);
            if (openLog) {
                System.out.println(curentcheck + " map " + env);
            }
        }

        if (openLog) {
            System.out.println("check  " + check + " map " + env);
        }

        Boolean result = true;
        if (env.isEmpty()) {
            return JsonCheckResult.EMPTY;
        } else {
            result = (Boolean) AviatorEvaluator.execute(check, env);
            env.clear();
        }
        // 最终检查结果
        if (result) {
            return JsonCheckResult.SUCCESS;
        } else {
            return JsonCheckResult.FAIL;
        }
    }

    private static String covertToAviatorString(String str) {
        return "'" + str + "'";
    }

    private static String replaceLast(String text, String strToReplace, String replaceWithThis) {
        return text.replaceFirst("(?s)" + strToReplace + "(?!.*?" + strToReplace + ")", replaceWithThis);
    }

    public static void main(String[] args) {
        // String json
        // ="{\"code\":300,\"message\":\"SUCCESS\",\"totalCount\":0,\"totalPage\":0,\"pageSize\":0,\"page\":0,\"data\":null,\"json\":null,\"list\":null}";
        // System.out.println(check(json,"$.code==300"));
        //
        openLog = true;
        // String test
        // ="{\"msg\":\"登录成功\",\"uid\":\"DAD3483647A94DBDB174C4C036CA8A80\",\"code\":\"1\",\"code2\":\"2\",\"code3\":\"3\"}";
        // String expression="code=1&&code2>=4&&code3>=2";
        //// //String expression="$.code=1";
        // System.out.println(check(test,expression));

        String jsontest3 = "[{\"uid\":\"e3eeb0a6-b413-4b73-9a19-f5eacc3398a10\"}]";
        System.out.println(CheckPointUtils.check(jsontest3, "uid=e3eeb0a6-b413-4b73-9a19-f5eacc3398a10").getMsg());

        // String test2 ="{\"uid\":\"test\"}";
        // String expression="uid=test";
        // System.out.println(check(test2,expression).getMsg());
    }
}
