package com.example.minitwitter.ui.tweets;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.data.TweetViewModel;
import com.example.minitwitter.retrofit.response.Tweet;

import java.util.List;


public class TweetListFragment extends Fragment {

    private int tweetListType = 1;
    RecyclerView recyclerView;
    MyTweetRecyclerViewAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Tweet> tweetList;
    TweetViewModel tweetViewModel;

    public TweetListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TweetListFragment newInstance(int tweetListType) {
        TweetListFragment fragment = new TweetListFragment();
        Bundle args = new Bundle();
        args.putInt(Constantes.TWEET_LIST_TYPE, tweetListType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweetViewModel = new ViewModelProvider(getActivity()).get(TweetViewModel.class);

        if (getArguments() != null) {
            tweetListType = getArguments().getInt(Constantes.TWEET_LIST_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);

        // Set the adapter
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAzul));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if (tweetListType == Constantes.TWEET_LIST_ALL) {
                    loadNewData();
                } else if (tweetListType == Constantes.TWEET_LIST_FAVS) {
                    loadNewFavData();
                }
            }
        });

        adapter = new MyTweetRecyclerViewAdapter(
                getActivity(),
                tweetList
        );
        recyclerView.setAdapter(adapter);

        if (tweetListType == Constantes.TWEET_LIST_ALL) {
            loadTweetData();
        } else if (tweetListType == Constantes.TWEET_LIST_FAVS) {
            loadFavTweetData();
        }


        return view;
    }

    private void loadNewFavData() {
        tweetViewModel.getNewFavTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) {
                tweetList = tweets;
                swipeRefreshLayout.setRefreshing(false);
                adapter.setData(tweetList);
                tweetViewModel.getNewFavTweets().removeObserver(this);
            }
        });
    }

    private void loadFavTweetData() {
        tweetViewModel.getFavTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) {
                tweetList = tweets;
                adapter.setData(tweetList);
            }
        });
    }

    private void loadTweetData() {
        tweetViewModel.getTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) {
                tweetList = tweets;
                adapter.setData(tweetList);
            }
        });
    }

    private void loadNewData() {
        tweetViewModel.getNewTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) {
                tweetList = tweets;
                swipeRefreshLayout.setRefreshing(false);
                adapter.setData(tweetList);
                tweetViewModel.getNewTweets().removeObserver(this);
            }
        });
    }
}