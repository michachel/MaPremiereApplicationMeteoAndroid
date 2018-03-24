package escroco.monapplicationmeteo.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import escroco.monapplicationmeteo.enums.WeatherError;
import escroco.monapplicationmeteo.models.Weather;
import escroco.monapplicationmeteo.repositories.WeatherRepository;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by micka_000 on 16/03/2018.
 */

public class WeatherViewModel extends AndroidViewModel {

    private MutableLiveData<String> mutableWeather = new MutableLiveData<>();
    private MutableLiveData<String> mutableCity = new MutableLiveData<>();
    private MutableLiveData<WeatherError> mutableWeatherError = new MutableLiveData<>();

    private Disposable disposable;

    /**
     * Constructeur qui lance l'abonnement sur la valeur de la ville choisie
     * Si la valeur change alors on récupère la température
     * @param application
     */
    public WeatherViewModel(@NonNull Application application) {
        super(application);
        mutableCity.observeForever(new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                refresh();
            }
        });
    }

    /**
     * Getter
     */
    public LiveData<String> getWeather(){
        return mutableWeather;
    }

    public MutableLiveData<String> getMutableCity() {
        return mutableCity;
    }

    public LiveData<WeatherError> getError() {
        return mutableWeatherError;
    }

    /**
     * Récupère la température de la ville sélectionnée
     */
    public void refresh() {
        if (mutableCity.getValue() != null) {

            //si l'instance pour récupérer la température existe déjà alors il faut la disposer
            if(disposable != null) {
                disposable.dispose();
            }

            disposable = WeatherRepository.getInstance().getWeather(mutableCity.getValue())
                    .subscribeOn(Schedulers.io())//autre thread
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Weather>() {
                                   @Override
                                   public void accept(Weather weather) throws Exception {
                                       displayWeather(weather);
                                       mutableWeatherError.setValue(WeatherError.NO_ERROR);
                                   }
                               },
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Log.e("Récupération en erreur", "voilà", throwable);
                                    mutableWeatherError.setValue(WeatherError.ERROR);
                                }
                            });
        }
    }

    /**
     * fourni aux observables les informations de température récupérées
     * @param weather objet récupéré contenant la température de la ville sélectionnée
     */
    private void displayWeather(@NonNull Weather weather){

        mutableWeather.postValue(weather.getTemperature() + "°C");
        //mutableWeather.setValue(weatherResponse.main.humidity + "% d'humidité");
        //mutableWeather.setValue(weatherResponse.weather[0].description);

    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if(disposable != null) {
            disposable.dispose();
        }
    }

}
