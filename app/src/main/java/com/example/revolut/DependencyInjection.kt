package com.example.revolut

import com.example.revolut.http.ADDRESS
import com.example.revolut.http.RatesApi
import com.example.revolut.http.RatesRepository
import com.example.revolut.http.RatesRepositoryImpl
import com.example.revolut.http.ResponseHandler
import com.example.revolut.list.ListViewModel
import com.example.revolut.list.delegate.RateDelegate
import com.example.revolut.list.delegate.RateDelegateImpl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val myModule = module {
    viewModel {
        ListViewModel(
            rateDelegate = get()
        )
    }

    single<RateDelegate> {
        RateDelegateImpl(
            ratesRepository = get()
        )
    }

    single<RatesRepository> {
        RatesRepositoryImpl(
            api = get<Retrofit>().create(RatesApi::class.java),
            responseHandler = get()
        )
    }

    single {
        createRetrofit(
            baseUrl = ADDRESS,
            client = get()
        )
    }

    single {
        createOkHttpClient()
    }

    single { ResponseHandler() }


}

fun createRetrofit(
    baseUrl: String,
    client: OkHttpClient
): Retrofit {
    return Retrofit.Builder()
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()
}

fun createOkHttpClient(
    loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor(),
    authInterceptor: Interceptor = createInterceptor()
): OkHttpClient {
    loggingInterceptor.level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.NONE
    }
    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()
}

fun createInterceptor(): Interceptor {
    return Interceptor { chain ->
        var request: Request = chain.request()
        request = request.newBuilder().build()
        chain.proceed(request)
    }
}
