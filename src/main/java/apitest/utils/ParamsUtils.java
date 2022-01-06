package apitest.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lulu
 * @data 2022/1/6 10:59 上午
 */
public class ParamsUtils {
    //在这 全局map
//	public static Map<String, Object> params =new LinkedHashMap<String, Object>();


    //每个线程绑定一个自己的变量map, 线程之间不相互干扰
    public static ThreadLocal<Map<String, Object>> params =new ThreadLocal<Map<String,Object>>(){

        @Override
        protected Map<String, Object> initialValue() {
            return new LinkedHashMap<String, Object>();
        }

    };




    public static void addMap(String key,Object value) {
        params.get().put(key, value);
    }

    public static void addMap(Map<String, Object> map) {
        params.get().putAll(map);
    }


    //{"msg":"登录成功","uid":"9CC972DFA2D4481F89841A46FD1B3E7B","code":"1"}
    //{"code":"1","data":[{"name":"testfan0","pwd":"pwd0"},
    //{"name":"testfan1","pwd":"pwd1"},{"name":"testfan2","pwd":"pwd2"}]}
    //id=uid;mycode=code
    public static void addFromJson(String json,String regx) {
        if (JSON.isValid(json)&&StringUtils.isNotBlank(regx)) {
            Map<String, Object> jsonMap = StringToMapUtils.covert1(regx);
            final String finaljson = json;
            jsonMap.forEach((k, v) -> {
                Object jsonObject= JSONPath.read(finaljson, v.toString());
                //如果直接提取不到，全局搜索
                if(jsonObject==null) {
                    jsonObject=JSONPath.read(json, ".."+v.toString());
                }
                //提取結果多个
                int i=1;
                if(jsonObject instanceof List) {
                    List<Object> jsonList=(List)jsonObject;
                    for (Object object : jsonList) {
                        System.out.println("k"+k +" object"+object);
                        addMap(k+"_"+i++,object);
                    }
                    //补充一个默认的
                    if(!jsonList.isEmpty()) {
                        addMap(k,jsonList.get(0));
                    }
                }else {
                    //提取结果1个
                    addMap(k,jsonObject);
                }

            });

        }
    }


    public static void addFromRegx(String regxResult, String regx) {
        if (StringUtils.isNotBlank(regx)&&StringUtils.isNotBlank(regxResult)) {
            Map<String, Object> regxMap = StringToMapUtils.covert1(regx);
            //final String finalregx = regx;
            System.out.println(regxMap);
            regxMap.forEach((k, v) -> {
                Pattern r = Pattern.compile(v.toString());
                Matcher m = r.matcher(regxResult);
                int count=0;
                String value1="";
                while (m.find()) {
                    System.out.println(m.group(0));
                    System.out.println(m.group(1));
                    count++;
                    if(count==1) {
                        value1 = m.group(1);
                    }else {
                        //k_2 value
                        ParamsUtils.addMap(k+"_"+count,m.group(1));
                    }
                }
                //循环结束
                if(count==1) {
                    ParamsUtils.addMap(k,value1);
                }else if(count>1) {
                    //k_1 value
                    ParamsUtils.addMap(k+"_"+1,value1);
                    //补充一个默认的
                    ParamsUtils.addMap(k,value1);
                }
            });

        }

    }

    static final String pattern = "\\$\\{(.*?)\\}";
    static final  Pattern r = Pattern.compile(pattern);

    public static String replace(String str) {
        if (StringUtils.isNotBlank(str)) {
            Matcher m = r.matcher(str);
            //非贪婪 多个
            while (m.find()) {
//				System.out.println(m.group(0));
//				System.out.println(m.group(1));
                str = str.replace(m.group(0), MapUtils.getString(get(), m.group(1), ""));
            }
        }
        return FunctionUtils.function(str);
    }


    public static Map<String, Object> get() {
        return params.get();
    }

    public static void clear() {
        params.get().clear();
    }


    public static void main(String[] args) {
        String jsonString="{\"code\":\"1\",\"data\":[{\"name\":\"testfan0\",\"pwd\":\"pwd0\"},{\"name\":\"testfan1\",\"pwd\":\"pwd1\"},{\"name\":\"testfan2\",\"pwd\":\"pwd2\"}]}";
        String regx="mycode=code;names=name;mytest=test";
        ParamsUtils.addFromJson(jsonString, regx);
        System.out.println(ParamsUtils.get());

//		ParamsUtils.addMap("hello", "testhello");

        String testString="http://baidu.com?test=${names}&test1=${mycode}";
//
        System.out.println(ParamsUtils.replace(testString));

    }
}
