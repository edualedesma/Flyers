package com.edu.flyers;

import com.edu.flyers.model.Ruta;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by edualedesma on 9/6/16.
 */
public interface PeticionesRestAPI {

    /*@GET("/maps/api/directions/json")
    public void rutaAsync(@Query("origin") String origen,
                          @Query("destination") String destino,
                          @Query("waypoints") String waypoints,
                          @Query("key") String key,
                          Callback<Ruta> cb);*/

    @GET("/maps/api/directions/json")
    public void rutaAsync(@Query("origin") String origen,
                          @Query("destination") String destino,
                          @Query("departure_time") String departureTime,
                          @Query("mode") String mode,
                          Callback<Ruta> cb);
}
