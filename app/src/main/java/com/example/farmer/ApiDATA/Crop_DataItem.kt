package com.example.farmer.ApiDATA

data class Crop_DataItem(
    val Crop: String,
    val maximum: Int,
    val minimum: Int,
    val nutrient: String,
    val unit: String
)