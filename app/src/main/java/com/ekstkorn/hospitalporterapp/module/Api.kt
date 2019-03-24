package com.ekstkorn.hospitalporterapp.module

import com.ekstkorn.hospitalporterapp.model.AuthenRequest
import com.ekstkorn.hospitalporterapp.model.Building
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WebServiceApi {
//    @POST("auth/")
//    fun login(@Body request: ): Single<ResponseBody>

    @GET("buildings")
    fun getAllBuilding() : Single<List<Building>>

    @POST("auth")
    fun authenUser(@Body request: AuthenRequest) : Single<ResponseBody>

}

