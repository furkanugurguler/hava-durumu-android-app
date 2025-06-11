package com.example.havadurumu.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val main: WeatherMain,
    val name: String,
    val sys: Sys,
    val weather: List<Weather>,
    val coord: Coord,

    )

data class WeatherMain(
    val temp: Float,
    val humidity: Int
)

data class Sys(
    val country: String,
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset") val sunset: Long
)

data class Weather(
    @SerializedName("id")
    val id: Int,

    @SerializedName("main")
    val main: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("icon")
    val icon: String
)


data class Coord(
    val lat: Double,
    val lon: Double
)