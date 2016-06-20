package ru.pa_resultant.resultanttest.Cities;

/**
 * Created by Anton on 20.06.2016.
 */
public class City {
    public String Name;
    public int Id;
    public int CountryId;
    public boolean IsStar;

    public City(String Name, int Id, int CountryId, boolean IsStar){
        this.Name=Name;
        this.Id=Id;
        this.CountryId=CountryId;
        this.IsStar=IsStar;
    }
}
