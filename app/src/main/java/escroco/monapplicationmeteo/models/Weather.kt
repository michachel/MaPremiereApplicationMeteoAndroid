package escroco.monapplicationmeteo.models

/**
 * Created by micka_000 on 16/03/2018.
 */

data class Weather(
    val city: String,
    val temperature: Float,
    val humidity: Float,
    val description: String
)
