package escroco.monapplicationmeteo.datasources;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import escroco.monapplicationmeteo.R;
import escroco.monapplicationmeteo.WeatherResponse;
import escroco.monapplicationmeteo.models.Weather;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by micka_000 on 16/03/2018.
 */

public class WeatherNetworkDataSource {

    private String _apiKey;
    private OkHttpClient _client;

    public WeatherNetworkDataSource(@NonNull Context context) {
        _apiKey = context.getResources().getString(R.string.api_key_weathermap);
        _client = new OkHttpClient();
    }

    @NonNull
    public Maybe<Weather> getWeather(final String city) {
        Log.i("récupération données","Network");
        return Maybe.create(new MaybeOnSubscribe<Weather>() {
            @Override
            public void subscribe(final MaybeEmitter<Weather> emitter) throws Exception {

                Request request = new Request.Builder()
                        .url("http://api.openweathermap.org/data/2.5/weather?q="+city+"&lang=fr&units=metric&APPID="+_apiKey)
                        .build();

                _client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, IOException e) {
                        Log.e("Error call", "Damn it", e);
                        emitter.onError(e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        ResponseBody responseBody = response.body();

                        if (!response.isSuccessful()) {
                            Log.e("Pas de réponse", response.message());
                            emitter.onError(new RuntimeException("Pas de réponse"));
                        }
                        else {
                            Log.i("récupération données","Network OK");
                            //responsebody.string ne peut être utilisé qu'une seule fois, donc il faut impérativement récupérer la valeur
                            String responseBodyString = responseBody.string();

                            Gson gson = new Gson();

                            Weather weather = gson.fromJson(responseBodyString, WeatherResponse.class).toWeather();

                            emitter.onSuccess(weather);
                        }

                    }
                });
            }
        });
    }
}
