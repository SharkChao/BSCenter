package first.test.com.bscenter.inject.module;



import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import first.test.com.bscenter.inject.StringConverterFactory;
import first.test.com.bscenter.network.ApiService;
import first.test.com.bscenter.network.ResponseErrorProxy;
import first.test.com.bscenter.presenter.MainPresenter;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络相关module
 */
@Module
public class ApiServiceModule {
    private static final String BASE_URL = "http://www.baidu.com";
    private static final int DEFAULT_TIMEOUT = 5;
    private static final int READ_TIMEOUT = 3;

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClientBuilder(){
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClientBuilder = new OkHttpClient.Builder()
//                .addInterceptor(interceptor)
//                .addNetworkInterceptor(new StethoInterceptor())  //添加拦截器
//                .sslSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory(), SSLSocketFactoryUtils.createTrustAllManager())
//                .hostnameVerifier(new SSLSocketFactoryUtils.TrustAllHostnameVerifier())
                .readTimeout(READ_TIMEOUT,TimeUnit.SECONDS)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true).build();
//        OkHttpClient httpClientBuilder = new OkHttpClient();
//        httpClientBuilder.newBuilder()
//                .addInterceptor(interceptor)
//                .sslSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory(), SSLSocketFactoryUtils.createTrustAllManager())
//                .hostnameVerifier(new SSLSocketFactoryUtils.TrustAllHostnameVerifier())
//                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
//                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true);
        return httpClientBuilder;
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient OkHttpClientBuilder){
        return  new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(OkHttpClientBuilder)
                .build();
    }

    @Provides
    @Singleton
    ApiService provideApiService(Retrofit retrofit){
        return retrofit.create(ApiService.class);
    }

    ApiService getByProxy(Class<? extends ApiService> apiService, Retrofit retrofit){
        ApiService api = retrofit.create(apiService);
        return (ApiService) Proxy.newProxyInstance(apiService.getClassLoader(),new Class<?>[] { apiService },new ResponseErrorProxy(api));
    }
    @Provides
    @Singleton
    MainPresenter provideMainPresenter(ApiService apiService){
        return MainPresenter.getInstance(apiService);
    }
}
