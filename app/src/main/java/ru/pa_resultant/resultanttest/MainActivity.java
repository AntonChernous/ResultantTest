package ru.pa_resultant.resultanttest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

/*import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;*/
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import ru.pa_resultant.resultanttest.Cities.AllCities;
import ru.pa_resultant.resultanttest.Cities.City;
import ru.pa_resultant.resultanttest.Cities.Country;
import ru.pa_resultant.resultanttest.Cities.FavoriteAdapter;
import ru.pa_resultant.resultanttest.Cities.StarAdapter;

public class MainActivity extends AppCompatActivity {

    //Настройки
    private String app_settings = "mysettings";
    private String app_settings_SelectedCity = "SelectedCity";
    private int SelectedCity = 0;
    private SharedPreferences mSettings;

    //страны, города
    private AllCities mAllCities = new AllCities();
    private GetCitiesAsyncTask mGetCitiesAsyncTask;
    private ArrayAdapter CountriesArrayAdapter=null;
    private StarAdapter starAdapter=null;
    private FavoriteAdapter favoriteAdapter=null;


    private TabHost tabHost;
    private ListView listViewCountries;
    private ListView listViewCities;
    private ListView listViewCitiesFavorite;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-06-20 14:22:23 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        tabHost = (TabHost)findViewById( R.id.tabHost );
        listViewCountries = (ListView)findViewById( R.id.listViewCountries );
        listViewCities = (ListView)findViewById( R.id.listViewCities );
        listViewCitiesFavorite = (ListView)findViewById( R.id.listViewCitiesFavorite );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        //Настройки
        mSettings = getSharedPreferences(app_settings, Context.MODE_PRIVATE);
        if (mSettings.contains(app_settings_SelectedCity)) {
            // Получаем число из настроек
            SelectedCity = mSettings.getInt(app_settings_SelectedCity, 0);
        }


        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("Страны");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("Избранное");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("Настройки");
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);

        //Tab select
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String sTag) {
                if (sTag.equals("tag2")){
                    //Избранное
                    favoriteAdapter = new FavoriteAdapter(mAllCities, MainActivity.this);
                    listViewCitiesFavorite.setAdapter(favoriteAdapter);
                }else if (sTag.equals("tag1")){
                    //Страны
                    if (starAdapter != null)starAdapter.notifyDataSetChanged();
                }
            }
        });


        //Загрузка стран, городов через интернет
        mGetCitiesAsyncTask = new GetCitiesAsyncTask();
        mGetCitiesAsyncTask.execute();





    }

    private class GetCitiesAsyncTask extends AsyncTask<Void, Integer, Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            String url = "https://atw-backend.azurewebsites.net/api/countries";

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet hget = new HttpGet(url);

                //Response
                HttpResponse response = client.execute(hget);
                HttpEntity httpEntity = response.getEntity();

                //String result = EntityUtils.toString(httpEntity, "UTF-8");
                String result = EntityUtils.toString(httpEntity);

                if (response.getStatusLine().getStatusCode() == 200){
                    JSONObject jObject = new JSONObject(result);

                    //страны
                    JSONArray jCountries = jObject.getJSONArray("Result");
                    for (int i=0; i < jCountries.length(); i++){
                        JSONObject jCountry = jCountries.getJSONObject(i);
                        Country c = new Country(jCountry.getString("Name"), jCountry.getInt("Id"));

                        //города
                        JSONArray jCities = jCountry.getJSONArray("Cities");
                        for (int j=0; j < jCities.length(); j++){
                            JSONObject jCity = jCities.getJSONObject(j);
                            City city = new City(jCity.getString("Name"), jCity.getInt("Id"), jCity.getInt("CountryId"), false);
                            c.Cities.add(city);
                        }

                        mAllCities.Countries.add(c);
                    }

                    return true;
                }

            } catch (UnsupportedEncodingException | ClientProtocolException e) {
                e.printStackTrace();
                Log.e("UPLOAD", e.getMessage());
                //this.exception = e;
            } catch (IOException e) {
                if (e!=null) e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean uploaded) {
            super.onPostExecute(uploaded);

            if(uploaded){
                //Закачали

                //Заполняем страны, города
                ArrayList<String> cList = new ArrayList<String>();
                for (int i=0; i < mAllCities.Countries.size(); i++){
                    cList.add(mAllCities.Countries.get(i).Name);
                }
                CountriesArrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, cList);
                listViewCountries.setAdapter(CountriesArrayAdapter);

                listViewCountries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        starAdapter = new StarAdapter(mAllCities, position, MainActivity.this);
                        listViewCities.setAdapter(starAdapter);

                        //SelectedCity
                        for (int i=0; i<mAllCities.Countries.get(position).Cities.size(); i++){
                            if (mAllCities.Countries.get(position).Cities.get(i).Id == SelectedCity) {
                                listViewCities.setSelection(i);
                                break;
                            }
                        }

                        listViewCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                SelectedCity = (int) id;

                                //Запоминаем данные
                                SharedPreferences.Editor editor = mSettings.edit();
                                editor.putInt(app_settings_SelectedCity, SelectedCity);
                                editor.apply();
                            }
                        });

                    }
                });

            }else{
                //Не закачали
                Toast.makeText(getApplicationContext(), "Ошибка загрузки стран, городов через интернет", Toast.LENGTH_LONG).show();
            }
        }



    }



}
