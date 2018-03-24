package escroco.monapplicationmeteo;

import com.google.gson.annotations.SerializedName;

import escroco.monapplicationmeteo.models.Weather;

/**
 * Created by micka_000 on 04/03/2018.
 */

public class WeatherResponse {


    @SerializedName("name")
    public String city;

    @SerializedName("main")
    public Main main;

    @SerializedName("weather")
    public Weather[] weather;


    public static class Main{
        @SerializedName("temp")
        public float temperature;

        @SerializedName("humidity")
        public int humidity;
    }

    public static class Weather {
        @SerializedName("description")
        public String description;

        @SerializedName("icon")
        public String icon;
    }

    public escroco.monapplicationmeteo.models.Weather toWeather(){

        return new escroco.monapplicationmeteo.models.Weather(this.city,this.main.temperature, this.main.humidity, this.weather[0].description);
    }
}




