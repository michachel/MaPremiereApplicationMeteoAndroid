package escroco.monapplicationmeteo.repositories;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import escroco.monapplicationmeteo.datasources.WeatherCacheDataSource;
import escroco.monapplicationmeteo.datasources.WeatherNetworkDataSource;
import escroco.monapplicationmeteo.models.Weather;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.MaybeSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by micka_000 on 16/03/2018.
 */

public class WeatherRepository {

    private static WeatherRepository _instance;

    private static WeatherNetworkDataSource _weatherNetwork;

    private static WeatherCacheDataSource _weatherCache;

    /**
     * constructeur par défaut privé pour ne pas qu'elle soit instanciée autrement que par
     * l'initialisation de l'application
     */
    private WeatherRepository()
    {
    }

    /**
     * Getter de l'instance de cette classe
     * @return
     */
    public static WeatherRepository getInstance(){
        if(_instance == null)
        {
            _instance = new WeatherRepository();
        }

        return _instance;
    }

    /**
     * Initiatlisation du repository en fonction du contexte
     * @param context
     */
    public static void init(@NonNull Context context){
        _weatherNetwork = new WeatherNetworkDataSource(context);
        _weatherCache = new WeatherCacheDataSource();
    }

    public Maybe<Weather> getWeather(final String city) {
        return _weatherCache.getWeather(city)
                .switchIfEmpty(_weatherNetwork.getWeather(city)
                        .flatMap(new Function<Weather, MaybeSource<Weather>>() {
                            @Override
                            public MaybeSource<Weather> apply(Weather weather) throws Exception {
                                if (weather != null) {
                                    _weatherCache.insertWeather(weather);
                                    return Maybe.just(weather);
                                } else {
                                    return Maybe.empty();
                                }
                            }
                        }));
    }
}
