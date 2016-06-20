package ru.pa_resultant.resultanttest.Cities;

import java.util.ArrayList;

/**
 * Created by Anton on 20.06.2016.
 */
public class AllCities {
    //Страны с городами
    public ArrayList<Country> Countries = new ArrayList<Country>();

    //Избранное
    public ArrayList<City> FavoriteCities = new ArrayList<City>();
    //Выделяем избранное
    public void CalculateFavoriteCities(){
        FavoriteCities.clear();

        for (int i=0; i<Countries.size(); i++){
            for (int j=0; j<Countries.get(i).Cities.size(); j++){
                if(Countries.get(i).Cities.get(j).IsStar){
                    FavoriteCities.add(Countries.get(i).Cities.get(j));
                }
             }
        }
    }
}
