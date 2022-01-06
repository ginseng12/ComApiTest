package apitest.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author lulu
 * @data 2022/1/6 11:06 上午
 */
public class StringToMapUtils {
    public static Map<String, Object> covert1(String str) {
        return covert(str,";");
    }


    public static Map<String, Object> covert2(String str) {
        return covert(str,"&");
    }


    public static Map<String, Object> covert(String str, String regx) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        if (StringUtils.isNoneBlank(str)) {
            String[] header_array = str.split(regx);
            for (int i = 0; i < header_array.length; i++) {
                String header = header_array[i];
                System.out.println(header);
                String[] keys = header.split("=");
                map.put(keys[0], keys[1]);
            }
        }
        return map;
    }
}
