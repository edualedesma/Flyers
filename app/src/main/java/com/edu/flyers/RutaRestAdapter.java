package com.edu.flyers;

import retrofit.RestAdapter;

/**
 * Created by edualedesma on 9/6/16.
 */
public class RutaRestAdapter {
    private static RestAdapter restAdapter;

    public static RestAdapter getInstanced(){
        if(restAdapter == null){
            restAdapter = new RestAdapter
                    .Builder()
                    .setEndpoint("https://maps.googleapis.com")
                    .build();

        }
        return restAdapter;
    }
}
