package com.example.minitwitter.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.minitwitter.R;
import com.example.minitwitter.data.TweetViewModel;
import com.example.minitwitter.retrofit.response.Tweet;

import java.util.List;


public class TweetListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    RecyclerView recyclerView;
    MyTweetRecyclerViewAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Tweet> tweetList;
    TweetViewModel tweetViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TweetListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TweetListFragment newInstance(int columnCount) {
        TweetListFragment fragment = new TweetListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweetViewModel = new ViewModelProvider(getActivity()).get(TweetViewModel.class);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
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
                loadNewData();
            }
        });
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        adapter = new MyTweetRecyclerViewAdapter(
                getActivity(),
                tweetList
        );
        recyclerView.setAdapter(adapter);

        loadTweetData();
        return view;
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
