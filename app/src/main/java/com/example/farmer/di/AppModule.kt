package com.example.farmer.di

import com.example.farmer.Network.Crop_Api
import com.example.farmer.repository.CropRepository
import com.example.farmer.utils.constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesCropDataRepository(api:Crop_Api) = CropRepository(api)


    @Singleton
    @Provides
    fun providesCrop():Crop_Api{
       return Retrofit.Builder()
           .baseUrl(constants.BASE_URL)
           .addConverterFactory(GsonConverterFactory.create())
           .build()
           .create(Crop_Api::class.java)
    }

}