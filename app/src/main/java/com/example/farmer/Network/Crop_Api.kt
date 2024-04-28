package com.example.farmer.Network

import com.example.farmer.ApiDATA.Crop_Data
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface Crop_Api {
    @GET("b/65f6ebca266cfc3fde99ac4f?meta=false")
    suspend fun getAllCropData(): Crop_Data

}