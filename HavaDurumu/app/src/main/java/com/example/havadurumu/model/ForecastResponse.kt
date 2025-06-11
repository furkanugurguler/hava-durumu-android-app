package com.example.havadurumu.model

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt: Long,
    @SerializedName("dt_txt") val dtTxt: String,
    val main: ForecastMain,
    val weather: List<Weather>
)

data class ForecastMain(
    val temp: Float
)
