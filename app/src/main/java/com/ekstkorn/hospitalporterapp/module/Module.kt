package com.ekstkorn.hospitalporterapp.module

import android.content.Context
import androidx.room.Room
import com.ekstkorn.hospitalporterapp.repository.DataStoreRepository
import com.ekstkorn.hospitalporterapp.room.AppDataBase
import com.ekstkorn.hospitalporterapp.room.BuildingDao
import com.ekstkorn.hospitalporterapp.view.JobViewModel
import com.ekstkorn.hospitalporterapp.view.LoginViewModel
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.readystatesoftware.chuck.ChuckInterceptor
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val remoteModule = module {

    single { createHttpClient(get()) }
    single { createChuckInterceptor(androidApplication()) }
    single { createWebService<WebServiceApi>(get()) }
}

private const val CONNECT_TIMEOUT = 30L
private const val READ_TIMEOUT = 30L
private const val WRITE_TIMEOUT = 30L

fun createHttpClient(chuckInterceptor: ChuckInterceptor): OkHttpClient {
    val clientBuilder = OkHttpClient.Builder()
    clientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
    clientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
    clientBuilder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
    clientBuilder.addInterceptor(chuckInterceptor)
    clientBuilder.addNetworkInterceptor(StethoInterceptor())
//    clientBuilder.addInterceptor(commonHeaderInterceptor)
//    if (BuildConfig.DEBUG) {
//        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        clientBuilder.addInterceptor(httpLoggingInterceptor)
//    }
    return clientBuilder.build()
}

fun createChuckInterceptor(context: Context) : ChuckInterceptor {
    return ChuckInterceptor(context)
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient): T {
    val retrofit = Retrofit.Builder()
            .baseUrl("http://118.175.30.68:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(okHttpClient)
            .build()
    return retrofit.create(T::class.java)
}

val viewModule = module {
    viewModel { jobViewModel(get()) }
    viewModel { loginViewModel(get()) }
    single { dataStoreRepository(get(), get()) }
    single { Room.databaseBuilder(androidApplication(), AppDataBase::class.java, "app_db")
            .build()}

    single { get<AppDataBase>().buildingDao() }
}

fun dataStoreRepository(api: WebServiceApi, buildingDao: BuildingDao) : DataStoreRepository {
    return DataStoreRepository(api, buildingDao)
}

fun jobViewModel(dataStoreRepository: DataStoreRepository) : JobViewModel {
    return JobViewModel(dataStoreRepository)
}

fun loginViewModel(dataStoreRepository: DataStoreRepository) : LoginViewModel {
    return LoginViewModel(dataStoreRepository)
}

val appModules = listOf(remoteModule, viewModule)

