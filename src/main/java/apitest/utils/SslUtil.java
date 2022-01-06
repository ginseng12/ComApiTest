package apitest.utils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
/**
 * @author lulu
 * @data 2022/1/6 11:05 上午
 */
public class SslUtil {
    public static CloseableHttpClient SslHttpClientBuild() {
        return SslHttpClientBuild(false);
    }

    public static CloseableHttpClient SslHttpClientBuild(boolean openProxy) {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE).
                // trustAllHttpsCertificates 处理https
                        register("https", trustAllHttpsCertificates()).build();
        // 创建ConnectionManager，添加Connection配置信息
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
        CloseableHttpClient httpClient = null;
        if (openProxy) {
            HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
            httpClient = HttpClients.custom().setConnectionManager(connectionManager).setProxy(proxy).build();
        } else {
            httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
        }
        return httpClient;
    }

    private static SSLConnectionSocketFactory trustAllHttpsCertificates() {
        SSLConnectionSocketFactory socketFactory = null;
        // 证书
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");// sc = SSLContext.getInstance("TLS")
            sc.init(null, trustAllCerts, null);
            socketFactory = new SSLConnectionSocketFactory(sc, NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return socketFactory;
    }

    static class miTM implements TrustManager, X509TrustManager {

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
            // don't check
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
            // don't check
        }
    }

    public static void main(String[] args) {
        //CloseableHttpClient clintClient = SslUtil.SslHttpClientBuild();
        //CloseableHttpClient clintClient = SslUtil.SslHttpClientBuild(false);
        CloseableHttpClient clintClient = HttpClients.createDefault();
        //https 加密
//		HttpGet get = new HttpGet(
//				"https://1.15.86.29:443/goods/UserServlet?method=loginMobile&loginname=test1&loginpass=test1");
        HttpGet get = new HttpGet("https://www.baidu.com");
        try {
            CloseableHttpResponse response = clintClient.execute(get);
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
