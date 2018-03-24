package escroco.monapplicationmeteo;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import escroco.monapplicationmeteo.enums.WeatherError;
import escroco.monapplicationmeteo.viewmodels.WeatherViewModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MeteoActivity extends AppCompatActivity {

    /**
     * l'application va s'occuper d'initialiser le viewmodel et gèrer ses cycles de vie
     */
    private WeatherViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        setContentView(R.layout.activity_meteo);

        Spinner spinner = findViewById(R.id.spn_cities);

        /**
         * évènement sur la sélection de la ville
         * on indique au viewmodel que la ville a changé
         */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                viewModel.getMutableCity().setValue(getCity(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //ne rien faire
            }
        });

        //On met à jour la température dès qu'une nouvelle donnée arrive
        viewModel.getWeather().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                TextView temperature = findViewById(R.id.txt_temperature);

                temperature.setText(s);
            }
        });

        /**
         * si une erreur suvient dans la récupération de la température alors on affiche un toast sur l'écran
         */
        viewModel.getError().observe(this, new Observer<WeatherError>() {
            @Override
            public void onChanged(@Nullable WeatherError weatherError) {
                if (weatherError.equals(WeatherError.ERROR))
                {
                    Toast.makeText(MeteoActivity.this, "une erreur incongrue est survenue", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Récupère en fonction de la position dans la liste la ville voulue
     * @param position position de l'élément dans la liste
     * @return ville
     */
    private String getCity(int position){

        final String[] lstCity = getResources().getStringArray(R.array.array_cities);

        if (lstCity.length > position)
        {
            return lstCity[position];
        }

        return "";
    }

}
