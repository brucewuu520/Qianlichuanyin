package org.brucewuu.http;

import android.text.TextUtils;

import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class AppClient {

    public static final int TIMEOUT_CONNECTION = 10000;
    public static final int TIMEOUT_SOCKET = 30000;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType TEXT = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final MediaType FILE = MediaType.parse("multipart/form-data; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient();

    static {
        client.setConnectTimeout(TIMEOUT_CONNECTION, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(TIMEOUT_CONNECTION, TimeUnit.MILLISECONDS);
        client.setReadTimeout(TIMEOUT_SOCKET, TimeUnit.MILLISECONDS);
    }

    /**
     * 发送GET请求（不带参数）
     * 有缓存
     *
     * @param url
     * @return
     */
    public static String get(String url) throws IOException {
        Request request = new Request.Builder().cacheControl(CacheControl.FORCE_CACHE).url(url).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful())
            return response.body().string();
        else
            throw new IOException("Unexpected code " + response);
    }

    /**
     * 发送GET请求（带参数）
     *
     * @param url
     * @param params
     * @return
     */
    public static String get(String url, Map<String, Object> params) throws IOException {
        String urlParams = makeUrl(url, params);
        Request request = new Request.Builder().url(urlParams).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful())
            return response.body().string();
        else
            throw new IOException("Unexpected code " + response);
    }

    public static InputStream getStream(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful())
            return response.body().byteStream();
        else
            throw new IOException("Unexpected code " + response);
    }

    /**
     * @param url
     * @param data
     * @return
     */
    public static String postData(String url, String data) throws IOException {
        RequestBody body = RequestBody.create(TEXT, data);
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful())
            return response.body().string();
        else
            throw new IOException("Unexpected code " + response);
    }

    /**
     * 发送POST请求
     *
     * @param url
     * @param json
     * @return
     */
    public static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful())
            return response.body().string();
        else
            throw new IOException("Unexpected code " + response);
    }

    /**
     * 发送POST请求
     *
     * @param url
     * @param formEncodingBuilder
     * @return
     */
    public static String post(String url, FormEncodingBuilder formEncodingBuilder) throws IOException {
        Request request = new Request.Builder().url(url).post(formEncodingBuilder.build()).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful())
            return response.body().string();
        else
            throw new IOException("Unexpected code " + response);
    }

    /**
     * 上传文件
     *
     * @param url
     * @param file
     * @return
     */
    public static String upLoadFile(String url, File file, Map<String, Object> params) throws IOException {
        String urlParams = makeUrl(url, params);
        RequestBody body = RequestBody.create(FILE, file);
        Request request = new Request.Builder().url(urlParams).post(body).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful())
            return response.body().string();
        else
            throw new IOException("Unexpected code " + response);
    }

    /**
     * 封装带参数的URL
     *
     * @param url
     * @param params
     * @return
     */
    private static String makeUrl(String url, Map<String, Object> params) throws UnsupportedEncodingException {
        if (params.size() == 0)
            return url;

        StringBuilder urlParams = new StringBuilder(url);
        if (urlParams.indexOf("?") < 0)
            urlParams.append('?');

        for (String name : params.keySet()) {
            String value = String.valueOf(params.get(name));
            if (!TextUtils.isEmpty(value)) {
                urlParams.append('&')
                        .append(name)
                        .append('=');
                // URLEncoder处理
                urlParams.append(URLEncoder.encode(value, "UTF-8"));
            }
        }

        return urlParams.toString().replace("?&", "?");
    }

    /**
     * 封装POST请求参数
     *
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String makeParams(Map<String, Object> params) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (String name : params.keySet()) {
            String value = String.valueOf(params.get(name));
            if (!TextUtils.isEmpty(value)) {
                sb.append(name)
                        .append("=")
                        .append(URLEncoder.encode(String.valueOf(params.get(name)), "UTF-8"))
                        .append("&");
            }
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    /**
     * 下载文件
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static ResponseBody downLoadFile(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful())
            return response.body();
        else
            throw new IOException("Unexpected code " + response);
    }

    /**
     * 获取服务器时间
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static long getSerDateTime(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful())
            return Long.parseLong(response.body().string());
        else
            return new Date().getTime();
    }

    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param callback
     */
    public static void enqueue(Request request, Callback callback) {
        client.newCall(request).enqueue(callback);
    }
}
