package com.example.farmer.screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmer.ApiDATA.Crop_DataItem
import com.example.farmer.data.DataOrException
import com.example.farmer.repository.CropRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CropViewModel @Inject constructor( private val repository: CropRepository) : ViewModel() {
    val data: MutableState<DataOrException<ArrayList<Crop_DataItem>,
           Boolean,Exception >> = mutableStateOf(
               DataOrException(null , true , Exception("")))

    init {
        getAllCropData()
    }

    private fun getAllCropData(){
       viewModelScope.launch {
           data.value.loading = true
           data.value = repository.getAllCropData()
           if (data.value.data.toString().isNotEmpty()){
               data.value.loading = false
           }
       }
    }




}