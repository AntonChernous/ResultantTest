package ru.pa_resultant.resultanttest.Cities;

import java.util.ArrayList;

/**
 * Created by Anton on 20.06.2016.
 */
public class Country {
    public String Name;
    public int Id;

    public ArrayList<City> Cities = new ArrayList<City>();

    public Country(String Name, int Id){
        this.Name=Name;
        this.Id=Id;
    }
}
