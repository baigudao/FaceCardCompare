package com.taisau.facecardcompare.http;

import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.util.NetworkUtil;
import com.taisau.facecardcompare.util.Preference;
import com.taisau.facecardcompare.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Cookie;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/3/7 0007.
 */

public class NetClient {
    private static String mToken = "";
    public static String getToken() {
        return mToken;
    }

    public static void setToken(String mToken) {
        NetClient.mToken = mToken;
    }

    private static NetClient mNetworks;

    public static NetClient getInstance() {
        if (mNetworks == null) {
            mNetworks = new NetClient();
        }
        return mNetworks;
    }

    private static DeviceAPI deviceAPI;

    public DeviceAPI getDeviceAPI() {
        return deviceAPI== null ?configRetrofit(DeviceAPI.class, false):deviceAPI;
    }

    private static PersonListAPI personListAPI;

    public PersonListAPI getPersonListAPI() {
        return personListAPI== null ?configRetrofit(PersonListAPI.class, false):personListAPI;
    }

    public UpdateInfoAPI updateInfoAPI;

    public UpdateInfoAPI getUpdateInfoAPI() {
        return updateInfoAPI== null ?configRetrofit(UpdateInfoAPI.class, false):updateInfoAPI;
    }

    private <T> T configRetrofit(Class<T> service, boolean isGetToken) throws IllegalArgumentException {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Preference.getServerUrl())
                .client(configClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(service);

    }

    final static HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();

    private OkHttpClient configClient(/*final boolean isGetToken*/) {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();

   /*     //为所有请求添加头部 Header 配置的拦截器
        Interceptor headerIntercept = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                L.i("ds>>>"," 配置的拦截器 ");
                Request.Builder builder = chain.request().newBuilder();
                builder.addHeader("X-Client-Platform", "Android");
                builder.addHeader("X-Client-Version", BuildConfig.VERSION_NAME);
                builder.addHeader("X-Client-Build", String.valueOf(BuildConfig.VERSION_CODE));

                builder.removeHeader("Accept");
                if (isGetToken) {
                    builder.addHeader("Accept", "application/vnd.PHPHub.v1+json");
                } else {
                    builder.addHeader("Accept", "application/vnd.OralMaster.v1+json");
                }

                if (!TextUtils.isEmpty(mToken)) {
                    builder.addHeader("Authorization", "Bearer " + mToken);
                }

                Request request = builder.build();

                return chain.proceed(request);
            }
        };*/

        // Log信息拦截器
       /* if (true) {
            Interceptor loggingIntercept = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Response response = chain.proceed(request);
                    ResponseBody responseBody = response.body();
                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.buffer();
                    Charset UTF8 = Charset.forName("UTF-8");
                    return response;
                }
            };
            okHttpClient.addInterceptor(loggingIntercept);
        }*/
        //设置缓存路径
        File httpCacheDirectory = new File(StringUtil.getExternalCacheDir(FaceCardApplication.getApplication()), "http-cache");
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        okHttpClient.addInterceptor(getInterceptor());//离线*/
        okHttpClient.cache(cache);
        okHttpClient.addNetworkInterceptor(getNetWorkInterceptor());//在线

        okHttpClient.connectTimeout(20L, TimeUnit.SECONDS);
        okHttpClient.readTimeout(50L, TimeUnit.SECONDS);
        okHttpClient.writeTimeout(20L, TimeUnit.SECONDS);
//        okHttpClient.addNetworkInterceptor(headerIntercept);
//        okHttpClient.cookieJar(new CookiesManager());
        return okHttpClient.build();
    }

    /**
     * 设置返回数据的  Interceptor  判断网络   没网读取缓存
     * 1.Cache-control 是由服务器返回的 Response 中添加的头信息，它的目的是告诉客户端是要从本地读取缓存还是直接从服务器摘取消息。它有不同的值，每一个值有不同的作用。
     * 2.max-age：这个参数告诉浏览器将页面缓存多长时间，超过这个时间后才再次向服务器发起请求检查页面是否有更新。
     * 3.s-maxage：这个参数告诉缓存服务器(proxy，如Squid)的缓存页面的时间。如果不单独指定，缓存服务器将使用max-age。
     */
    //设置返回数据的  Interceptor  判断网络   没网读取缓存
    public Interceptor getInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!NetworkUtil.isConnected(FaceCardApplication.getApplication())) {
                    request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                }return chain.proceed(request);
            }};}
    //   设置连接器  设置缓存
    public Interceptor getNetWorkInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                if (NetworkUtil.isConnected(FaceCardApplication.getApplication())) {
                    int maxAge = 0 * 60; // 有网络时 设置缓存超时时间0个小时
                    response.newBuilder().header("Cache-Control", "public, max-age=" + maxAge).removeHeader("Pragma").build();
                } else {
                    int maxStale = 60 * 60 * 24 * 7;  // 无网络时，设置超时为1周
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("Pragma")
                            .build();
                }return response;
            }};}

   /* private class CookiesManager implements CookieJar {
        private final PersistentCookieStore cookieStore = new PersistentCookieStore(App.getApplication());

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            L.i("ds>>>", " aaaaaaaaaaaaaaa 1111");
            if (cookies != null && cookies.size() > 0) {
                for (Cookie item : cookies) {
                    cookieStore.add(url, item);
                    if (cookieStore.getCookies().size() != 0)
                        L.i("ds>>>", " aaaaaaaaaaaaaaa cookieStore1=" + cookieStore.getCookies().get(0).toString());
                }
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            L.i("ds>>>", " aaaaaaaaaaaaaaa 2222");
            List<Cookie> cookies = cookieStore.get(url);
            if (cookieStore.getCookies().size() != 0)
                L.i("ds>>>", "aaaaaaaaaaaaaaa cookieStore2=" + cookieStore.getCookies().get(0).toString());
            return cookies;
        }
    }*/

}
