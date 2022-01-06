package apitest.utils;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author lulu
 * @data 2022/1/6 11:04 上午
 */
public class PropertiesUtils {
    private static Configuration config;

    static {
        if(null == config){
            try {
                config = new PropertiesConfiguration("config.properties");
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    //工具类构造方法私有
    private PropertiesUtils(){}

    public static String getString(String key){
        return config.getString(key,"");
    }

    public static String[] getStringArray(String key){
        return config.getStringArray(key);
    }

    public static void main(String[] args) {
        System.out.println(getString("test"));
        String[] strs = getStringArray("mail.touser");
        for (String string : strs) {
            System.out.println(string);
        }
    }
}
