package com.example.minitwitter.data;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.MyApp;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.retrofit.AuthTwitterClient;
import com.example.minitwitter.retrofit.AuthTwitterService;
import com.example.minitwitter.retrofit.request.RequestCreateTweet;
import com.example.minitwitter.retrofit.response.Like;
import com.example.minitwitter.retrofit.response.Tweet;
import com.example.minitwitter.retrofit.response.TweetDeleted;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TweetRepository {
    AuthTwitterService authTwitterService;
    AuthTwitterClient authTwitterClient;
    MutableLiveData<List<Tweet>> allTweets;
    MutableLiveData<List<Tweet>> favTweets;
    String userName;

    TweetRepository() {
        authTwitterClient = AuthTwitterClient.getInstance();
        authTwitterService = authTwitterClient.getAuthTwitterService();
        allTweets = getAllTweets();
        userName = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_USERNAME);
    }

    public MutableLiveData<List<Tweet>> getFavTweets() {
        if (favTweets == null) {
            favTweets = new MutableLiveData<>();
        }

        List<Tweet> newFavList = new ArrayList<>();

        Iterator itTweets = allTweets.getValue().iterator();

        while (itTweets.hasNext()) {
            Tweet currentTweet = (Tweet) itTweets.next();
            Iterator itLikes = currentTweet.getLikes().iterator();
            boolean enc = false;
            while (itLikes.hasNext() && !enc){
                Like like = (Like) itLikes.next();
                if (like.getUsername().equals(userName)) {
                    enc = true;
                    newFavList.add(currentTweet);
                }
            }
        }

        favTweets.setValue(newFavList);

        return favTweets;
    }

    public MutableLiveData<List<Tweet>> getAllTweets() {
        if (allTweets == null) {
            allTweets = new MutableLiveData<>();
        }

        Call<List<Tweet>> call = authTwitterService.getAllTweets();
        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
                if (response.isSuccessful()) {
                    allTweets.setValue(response.body());
                } else {
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
        return allTweets;
    }

    public void createTweet(String message) {
        RequestCreateTweet requestCreateTweet = new RequestCreateTweet(message);
        Call<Tweet> call = authTwitterService.createTweet(requestCreateTweet);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                if (response.isSuccessful()) {
                    List<Tweet> clonedList = new ArrayList<>();
                    //Añadimos en primer lugar el nuevo tweet
                    clonedList.add(response.body());
                    for (int i = 0; i < allTweets.getValue().size(); i++) {
                        clonedList.add(new Tweet(allTweets.getValue().get(i)));
                    }
                    allTweets.setValue(clonedList);
                } else {
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void deleteTweet(final int idTweet) {
        Call<TweetDeleted> call = authTwitterService.deleteTweet(idTweet);

        call.enqueue(new Callback<TweetDeleted>() {
            @Override
            public void onResponse(Call<TweetDeleted> call, Response<TweetDeleted> response) {
                if (response.isSuccessful()) {
                    List<Tweet> clonedTweets = new ArrayList<>();
                    for (int i = 0; i < allTweets.getValue().size(); i++) {
                        if (allTweets.getValue().get(i).getId() != idTweet) {
                            clonedTweets.add(new Tweet(allTweets.getValue().get(i)));
                        }
                    }

                    allTweets.setValue(clonedTweets);
                    getFavTweets();
                } else {
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TweetDeleted> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void likeTweet(final int idTweet) {
        Call<Tweet> call = authTwitterService.likeTweet(idTweet);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                if (response.isSuccessful()) {
                    List<Tweet> clonedList = new ArrayList<>();
                    for (int i = 0; i < allTweets.getValue().size(); i++) {
                        if (allTweets.getValue().get(i).getId() == idTweet) {
                            //Si encontramos en la lista original el elemento donde hemos hecho like
                            //Introducimos el elemento que nos ha llegado del servidor
                            clonedList.add(response.body());
                        } else {
                            clonedList.add(new Tweet(allTweets.getValue().get(i)));
                        }
                    }
                    allTweets.setValue(clonedList);
                    getFavTweets();
                } else {
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
