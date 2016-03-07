package com.example.erick.listviews;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    ArrayAdapter<String> arrAdapter;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ventana = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayList<String> Fechas = new ArrayList<String>();

        Fechas.add("Lunes 01 de febrero del 2016");
        Fechas.add("martes 02 de febrero del 2016");
        Fechas.add("miercoles 03 de febrero del 2016");
        Fechas.add("viernes 04 de febrero del 2016");
        Fechas.add("sabado 05 de febrero del 2016");
        Fechas.add("domingo 06 de febrero del 2016");
        Fechas.add("Lunes 07 de febrero del 2016");
        Fechas.add("martes 08 de febrero del 2016");
        Fechas.add("miesrcoles 09 de febrero del 2016");
        Fechas.add("jueves 10 de febrero del 2016");

        arrAdapter = new ArrayAdapter <String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textView,
                Fechas
        );
        ListView lista = (ListView)ventana.findViewById(R.id.listView_forecast);
        lista.setAdapter(arrAdapter);
//***********************************************************************************************************************************
        new cargarDatosJson().execute();
        return ventana;
    }
//*************************************************************************************************************************
private class cargarDatosJson extends AsyncTask<Void,Void,String[]>{

    @Override
    protected String[] doInBackground(Void... params) {
        String[] arrString = cargarDatos();
        return arrString;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);

        List<String> listaDatos = Arrays.asList(strings);
        arrAdapter.clear();
        arrAdapter.addAll(listaDatos);
    }
}

public String[] cargarDatos(){
    // These two need to be declared outside the try/catch
    // so that they can be closed in the finally block.
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String [] arregloLibros = new String[0];
    String nombreLibro;
    // Will contain the raw JSON response as a string.
    String forecastJsonStr = null;
    try {
        // Construct the URL for the OpenWeatherMap query
        // Possible parameters are avaiable at OWM's forecast API page, at
        // http://openweathermap.org/API#forecast
        // String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
        String baseUrl = "http://192.168.1.50/libro/JsonLibros";
        //String apiKey = "&APPID="+"166be3a65dd492ddbcacf2139facfbfc";
        //URL url = new URL(baseUrl.concat(apiKey));
        URL url = new URL(baseUrl);

        // Create the request to OpenWeatherMap, and open the connection
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        // Read the input stream into a String
        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            // Nothing to do.
            return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
            // But it does make debugging a *lot* easier if you print out the completed
            // buffer for debugging.
            buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
            // Stream was empty.  No point in parsing.
            return null;
        }
        //json en string que biene de la api

        forecastJsonStr = buffer.toString();
        ExtractorDeDatosJson v = new ExtractorDeDatosJson();

        try {
            arregloLibros = v.getLibroDataFromJson(forecastJsonStr);
            //nombreLibro = ExtractorDeDatosJson.getName(forecastJsonStr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Libros","El JSON Recibido fue: "+forecastJsonStr);

    } catch (IOException e) {
        Log.e("PlaceholderFragment", "Error ", e);
        // If the code didn't successfully get the weather data, there's no point in attemping
        // to parse it.
        return null;
    } finally{
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (final IOException e) {
                Log.e("PlaceholderFragment", "Error closing stream", e);
            }
        }
    }
    return arregloLibros;
}

    //**************************************************************************************************************************



     static class ExtractorDeDatosJson {

         public String[] getLibroDataFromJson(String forecastJsonStr)
                 throws JSONException {




             JSONArray ArrayLibro = new JSONArray(forecastJsonStr);



             String[] resultStrs = new String[ArrayLibro.length()];
             for(int i = 0; i < ArrayLibro.length(); i++) {
                 String nombre;
                 String autor;
                 int ano;

                 JSONObject libroObject = ArrayLibro.getJSONObject(i);

                 nombre=libroObject.getString("nombre");
                 autor=libroObject.getString("autor");
                 ano=libroObject.getInt("año");

                 resultStrs[i] = nombre + " - " + autor + " - " + ano;
             }


             return resultStrs;

         }

        }
}
