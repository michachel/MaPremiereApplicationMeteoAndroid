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
import io.reactivex.Scheduler;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
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

    private Maybe<Weather> GetWeatherNetwork(final String city){
        _weatherNetwork.getWeather(city).
    }

    public Maybe<Weather> getWeather(final String city){

        final Flowable<Weather> concat = Maybe.concat(_weatherCache.getWeather(city), _weatherNetwork.getWeather(city));


        return Maybe.create(new MaybeOnSubscribe<Weather>() {
            @Override
            public void subscribe(final MaybeEmitter<Weather> emitter) throws Exception {
                _weatherCache.getWeather(city)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(Schedulers.computation())
                        .subscribe(new Consumer<Weather>() {
                                       @Override
                                       public void accept(Weather weather) throws Exception {
                                           emitter.onSuccess(weather);
                                       }
                                   },
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        emitter.onError(throwable);
                                    }
                                }, new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        _weatherNetwork.getWeather(city)
                                                .subscribeOn(Schedulers.computation())
                                                .observeOn(Schedulers.computation())
                                                .subscribe(new Consumer<Weather>() {
                                                               @Override
                                                               public void accept(Weather weather) throws Exception {
                                                                   _weatherCache.insertWeather(weather);
                                                                   emitter.onSuccess(weather);
                                                               }
                                                           },
                                                        new Consumer<Throwable>() {
                                                            @Override
                                                            public void accept(Throwable throwable) throws Exception {
                                                                emitter.onError(throwable);
                                                            }
                                                        },
                                                        new Action() {
                                                            @Override
                                                            public void run() throws Exception {
                                                                emitter.onComplete();
                                                            }
                                                        });
                                    }
                                });
            }
        });
    }
}
