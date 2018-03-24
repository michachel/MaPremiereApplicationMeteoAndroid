package escroco.monapplicationmeteo;

import android.app.Application;

import escroco.monapplicationmeteo.repositories.WeatherRepository;

/**
 * Created by micka_000 on 16/03/2018.
 */

public class WeatherApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        WeatherRepository.init(this.getBaseContext());
    }
}
