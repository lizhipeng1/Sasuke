package com.rpc.monitor.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

public class HttpUtil {


	public static String doGet(String url) {
		CloseableHttpClient httpClient=createSSLClientDefault();
		HttpGet httpGet=new HttpGet(url);
		CloseableHttpResponse response=null;
		String res=null;
		try {
			response=httpClient.execute(httpGet);
			HttpEntity entity=response.getEntity();
			if(entity!=null)
			{
				 res=EntityUtils.toString(entity,"UTF-8");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(response!=null)
			{
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return res;
	}

	public static String doPost(String url, List<NameValuePair> nvps) {
		String responseBody=null;
		HttpClient httpclient = createSSLClientDefault();
        try {
            HttpPost httppost = new HttpPost(url);
            //将POST参数以UTF-8编码并包装成表单实体对象
            httppost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));  
            //创建响应处理器处理服务器响应内容  
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            //执行请求并获取结果  
            responseBody = httpclient.execute(httppost, responseHandler);  
        } 
        catch(Exception e){
        	return "json error";
        }finally {
            // 当不再需要HttpClient实例时,关闭连接管理器以确保释放所有占用的系统资源
            httpclient.getConnectionManager().shutdown();
        }
		return responseBody;
	
	}

	public String doJSONPost(String url, StringEntity entity) {
		String responseBody=null;
		HttpClient httpclient = createSSLClientDefault();  
        try {  
            HttpPost httppost = new HttpPost(url);  
            //将POST参数以UTF-8编码并包装成表单实体对象  
            httppost.setEntity(entity);  
            //创建响应处理器处理服务器响应内容  
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            //执行请求并获取结果  
            responseBody = httpclient.execute(httppost, responseHandler);  
        } 
        catch(Exception e){
        	return "json error";
        }finally {
            // 当不再需要HttpClient实例时,关闭连接管理器以确保释放所有占用的系统资源  
            httpclient.getConnectionManager().shutdown();  
        }
		return responseBody;
	
	}
	
	
	public static CloseableHttpClient createSSLClientDefault(){
		 
        try {
 
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                //信任所有
                public boolean isTrusted(X509Certificate[] chain,
 
                        String authType) throws CertificateException {
 
                    return true;
                }
 
            }).build();
 
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
 
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
 
        } catch (KeyManagementException e) {
 
            e.printStackTrace();
 
        } catch (NoSuchAlgorithmException e) {
 
            e.printStackTrace();
 
        } catch (KeyStoreException e) {
 
            e.printStackTrace();
        }
 
        return  HttpClients.createDefault();
 
    }
	
	
	public static void main(String[] args)
	{
		HttpUtil service=new HttpUtil();
		System.out.println(service.doGet("http://baidu.com"));
		
	}

}
