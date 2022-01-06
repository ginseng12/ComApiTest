package apitest.utils;

import org.apache.commons.lang3.StringUtils;
import java.util.UUID;
import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.digest.DigestUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lulu
 * @data 2022/1/6 10:50 上午
 */

public class FunctionUtils {

    public static void main(String[] args) {
        
    }

    //函数定义规则 #{__time(,)}
    static final String pattern = "#\\{(.*?)\\}";
    static final Pattern r = Pattern.compile(pattern);

    public static String function(String str) {
        if (StringUtils.isNotBlank(str)) {
            Matcher m = r.matcher(str);
            // 非贪婪 多个 提取表达式
            while (m.find()) {
                System.out.println("group m0  " + m.group());
                System.out.println("group m1 " + m.group(1));
                // __md5(abc,test)
                try {
                    String value = getFunctionValue(m.group(1));
                    str = str.replace(m.group(), value);
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        }

        return str;
    }
}
