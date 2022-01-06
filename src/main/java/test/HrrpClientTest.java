package test;

import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lulu
 * @data 2021/12/28 5:39 下午
 */
public class HrrpClientTest {
    public static void main(String[] args) throws IOException {
        //创建线程池
        CloseableHttpClient httpclient = HttpClients.createDefault();

        //创建HttpGet请求
        HttpGet get = new HttpGet("url");
        HttpEntity httpEntity = null;

        try {
            try (CloseableHttpResponse response = httpclient.execute(get)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    httpEntity = response.getEntity();

                    System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
                }
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
    }

    //post请求处理
    public static String doPost(String url, Map<String, Object> parmes) {
        // 创建Httpclient对象

        CloseableHttpClient clients = HttpClients.createDefault();

        //创建HttpPost请求
        HttpEntity httpEntity = null;
        try {
            HttpPost post = new HttpPost(url);
            List<BasicNameValuePair> parameter = new ArrayList<BasicNameValuePair>();
            if (MapUtils.isNotEmpty(parmes)){
                List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
                parmes.forEach((k,v)-> parameters.add(new BasicNameValuePair(k,v.toString())));
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameter, "utf-8");
                post.setEntity(formEntity);
            }
//            //构造参数
//            parameter.add(new BasicNameValuePair("method", "value1"));
//            parameter.add(new BasicNameValuePair("loginname", "value2"));
//            parameter.add(new BasicNameValuePair("loginpass","abc"));
            CloseableHttpResponse response = clients.execute(post);

            if (response.getStatusLine().getStatusCode() == 200) {
                httpEntity = response.getEntity();

                System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
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
//    http://118.24.13.38:8080/goods/UserServlet?method=loginMobile&loginname=abc&loginpass=abc

}