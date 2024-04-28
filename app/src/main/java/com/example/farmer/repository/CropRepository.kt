package com.example.farmer.repository

import android.util.Log
import com.example.farmer.ApiDATA.Crop_DataItem
import com.example.farmer.Network.Crop_Api
import com.example.farmer.data.DataOrException
import javax.inject.Inject

class CropRepository @Inject constructor(
    private val api: Crop_Api){


          private val dataOrException =
              DataOrException<ArrayList<Crop_DataItem>,
                  Boolean ,
                  Exception>()

    suspend fun getAllCropData(): DataOrException<ArrayList<Crop_DataItem> , Boolean , java.lang.Exception>{
        try {
           dataOrException.loading = true
            dataOrException.data = api.getAllCropData()
            if (dataOrException.data.toString().isNotEmpty()) dataOrException.loading = false
        }catch (exception : Exception){
           dataOrException.e = exception
            Log.d("Exc","getAllCropData:${dataOrException.e!!.localizedMessage}")
        }
        return dataOrException
    }



}