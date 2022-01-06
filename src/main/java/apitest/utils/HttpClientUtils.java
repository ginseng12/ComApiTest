package apitest.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;



/**
 * @author lulu
 * @data 2021/12/28 4:49 下午
 */

public class HttpClientUtils {
    public static boolean openPoxy = false;

    private static CloseableHttpClient init() {
//		if (openPoxy) {
////			RequestConfig defaultRequestConfig = RequestConfig.custom()
////					.setProxy(new HttpHost("127.0.0.1", 8888, "http")).build(); // 添加代理
////			return HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
//			return SslUtil.SslHttpClientBuild(true);
//		} else {
//			return SslUtil.SslHttpClientBuild();
//			//return HttpClients.createDefault();
//		}

        return SslUtil.SslHttpClientBuild(openPoxy);

    }

    public static String doGet(String url) {
        return doGet(url, "");
    }

    public static String doGet(String url, String headers) {

        return doGet(url, StringToMapUtils.covert1(headers));
    }

    public static String doGet(String url, Map<String, Object> header) {
        // 创建http统一链接管理
        CloseableHttpClient client = init();

        // 测试地址在url
        HttpGet get = new HttpGet(url);
        HttpEntity httpEntity = null;
        try {
            if (MapUtils.isNotEmpty(header)) {
                header.forEach((k, v) -> get.setHeader(k, v.toString()));
            }
            CloseableHttpResponse reponse = client.execute(get);
            System.out.println("status ---" + reponse.getStatusLine());
            if (reponse.getStatusLine().getStatusCode() == 200) {
                httpEntity = reponse.getEntity();
                return EntityUtils.toString(reponse.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "error";
    }

    public static String doPost(String url, Map<String, Object> params, Map<String, Object> header) {
        // 设置代理
        CloseableHttpClient client = init();

        // 测试地址在url
        HttpEntity httpEntity = null;
        try {
            HttpPost post = new HttpPost(url);
            if (MapUtils.isNotEmpty(params)) {
                List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
                params.forEach((k, v) -> parameters.add(new BasicNameValuePair(k, v.toString())));
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "utf-8");
                post.setEntity(formEntity);
            }

            if (MapUtils.isNotEmpty(header)) {
                header.forEach((k, v) -> post.setHeader(k, v.toString()));
            }
//					List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
//					parameters.add(new BasicNameValuePair("method", "loginMobile"));
//					parameters.add(new BasicNameValuePair("loginname", "test1"));
//					parameters.add(new BasicNameValuePair("loginpass", "test1"));
            CloseableHttpResponse reponse = client.execute(post);
            System.out.println("status ---" + reponse.getStatusLine());
            if (reponse.getStatusLine().getStatusCode() == 200) {
                httpEntity = reponse.getEntity();
                return EntityUtils.toString(reponse.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            params.clear();
        }
        return "error";
    }

    // method=loginMobile&loginname=test1&loginpass=test1 string->map
    public static String doPost(String url, String parmas, String headers) {
        // method=loginMobile&loginname=abc&loginpass=abc
//		Map<String, Object> map = new HashMap<String, Object>();
//		if (StringUtils.isNotBlank(parmas)) {
//			String[] parmas_array = parmas.split("&");
//			for (int i = 0; i < parmas_array.length; i++) {
//				String key = parmas_array[i];
//				String[] key_array = key.split("=");
//				map.put(key_array[0], key_array[1]);
//			}
//
//		}
        // token=61b3590090982a0185dda9d3bd793b46;userId=123

        return doPost(url, StringToMapUtils.covert2(parmas), StringToMapUtils.covert1(headers));
    }

    public static String doPostJson(String url, String json, String headers) {

        return doPostJson(url, json, StringToMapUtils.covert1(headers));
    }

    public static String doPostJson(String url, String json, Map<String, Object> headers) {
        if(!JSON.isValid(json)) {
            return "error";
        }
        // 设置代理
        CloseableHttpClient client = init();
        HttpPost post = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(json, "utf-8");
        post.setEntity(stringEntity);
        // 工具，后台能自动识别是json格式
        post.addHeader("Content-type", "application/json;charset=utf-8");
        HttpEntity httpEntity = null;

        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach((k, v) -> post.addHeader(k, v.toString()));
        }
        try {
            CloseableHttpResponse reponse = client.execute(post);
            System.out.println("status ---" + reponse.getStatusLine());
            if (reponse.getStatusLine().getStatusCode() == 200) {
                httpEntity = reponse.getEntity();
                return EntityUtils.toString(reponse.getEntity(), "utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "error";
    }

    public static String doPost(String url, String parmas) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(parmas)) {
            String[] parmas_array = parmas.split("&");
            for (int i = 0; i < parmas_array.length; i++) {
                String key = parmas_array[i];
                String[] key_array = key.split("=");
                map.put(key_array[0], key_array[1]);
            }

        }
        return doPost(url, map, null);
    }

    public static void main(String[] args) {

        openPoxy = true;
        // System.out.println(doGet("http://www.baidu.com/"));
        System.out.println(doGet(
                "http://1.15.86.29:8080/goods/UserServlet?method=loginMobile&loginname=test1&loginpass=test1&a=123",
                "token=61b3590090982a0185dda9d3bd793b46;userId=123"));

//		Map<String, Object> params =new HashMap<String, Object>();
//		params.put("method", "loginMobile");
//		params.put("loginname", "test1");
//		params.put("loginpass", "test1");
//		System.out.println(doGet(u"http://1.15.86.29:8080/goods/UserServlet",
//				"method=loginMobile&loginname=test1&loginpass=test1"));

//		 params =new HashMap<String, Object>();
//		params.put("method", "loginMobile");
//		params.put("loginname", "abc");
//		params.put("loginpass", "abc");
        System.out.println(doPost("http://1.15.86.29:8080/goods/UserServlet",
                "method=loginMobile&loginname=abc&loginpass=abc&a=123",
                "token=61b3590090982a0185dda9d3bd793b46;userId=123"));

        doPostJson("http://1.15.86.29:8080/goods/json2", "{\"count\":3}",
                "token=61b3590090982a0185dda9d3bd793b46;userId=123");
    }
}
