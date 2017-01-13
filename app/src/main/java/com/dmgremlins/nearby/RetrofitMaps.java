package com.dmgremlins.nearby;

import com.dmgremlins.nearby.POJO.Example;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/*
    interface that is needed
    for the code that takes care of
    getting locations from a specific category
 */
public interface RetrofitMaps {

    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyDN7RJFmImYAca96elyZlE5s_fhX-MMuhk")
    Call<Example> getNearbyPlaces(@Query("type") String type, @Query("location") String location, @Query("radius") int radius);

}
