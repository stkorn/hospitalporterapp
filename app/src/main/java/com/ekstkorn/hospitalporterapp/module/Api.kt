package com.ekstkorn.hospitalporterapp.module

import com.ekstkorn.hospitalporterapp.model.Building
import io.reactivex.Single
import retrofit2.http.GET

interface WebServiceApi {
//    @POST("auth/")
//    fun login(@Body request: ): Single<ResponseBody>

    @GET("buildings")
    fun getAllBuilding() : Single<List<Building>>

}

