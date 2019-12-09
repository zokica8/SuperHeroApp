package com.nsweb.heroapp.data.retrofit.client;

import com.nsweb.heroapp.data.domain.SuperHero;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SuperHeroClient {

    @GET("/superheroes")
    Observable<List<SuperHero>> getAllSuperHeroes();

    @GET("/superheroes/{superhero_id}")
    Observable<SuperHero> getSuperHeroById(@Path("superhero_id") long id);

    @POST("/superheroes")
    Observable<SuperHero> insertSuperHero(@Body SuperHero superHero);

    @PUT("/superheroes/{id}")
    Observable<SuperHero> updateSuperHero(@Body SuperHero superHero, @Path("id") long id);

    @DELETE("/superheroes/{id}")
    Observable<SuperHero> deleteSuperHero(@Path("id") long id);
}
