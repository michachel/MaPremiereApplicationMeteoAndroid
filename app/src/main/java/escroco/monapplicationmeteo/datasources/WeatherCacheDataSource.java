package escroco.monapplicationmeteo.datasources;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import java.util.HashMap;

import escroco.monapplicationmeteo.models.Weather;
import io.reactivex.Maybe;

/**
 * Created by micka_000 on 16/03/2018.
 */

public class WeatherCacheDataSource {

    private HashMap<String, Pair<Weather, Long>> hashMap = new HashMap<>();

    public Maybe<Weather> getWeather(String city){
        Pair<Weather, Long> weather = hashMap.get(city);

        if (weather != null && (System.currentTimeMillis() - weather.second)/(60 * 1000) < 60 )
        {
            Log.i("récupération données","Cache OK");
            return Maybe.just(weather.first);
        }
        else
        {

            Log.i("récupération données","Cache Non trouvé");
            hashMap.remove(city);

            return Maybe.empty();
        }
    }

    public void insertWeather(@NonNull Weather weather){
        Log.i("récupération données","Insertion en cache");
        hashMap.put(weather.getCity(), new Pair<>(weather, System.currentTimeMillis()));
    }
}
