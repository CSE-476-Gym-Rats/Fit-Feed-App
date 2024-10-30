package com.example.fitfeed.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fitfeed.viewAdapters.PostsRecyclerViewAdapter;
import com.example.fitfeed.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private PostsRecyclerViewAdapter postsRecyclerViewAdapter;
    private Parcelable stateList;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO implement actual posts instead of placeholder values
        ArrayList<String> postText = new ArrayList<>();
        postText.add("Your friend just hit a PB in a set!");
        postText.add("You became friends with Josh!");
        postText.add("Josh shared his new workout plan with you");

        ArrayList<Drawable> postDrawable = new ArrayList<>();
        postDrawable.add(ResourcesCompat.getDrawable(getResources(), R.drawable.placeholder1, null));
        postDrawable.add(ResourcesCompat.getDrawable(getResources(), R.drawable.placeholder2, null));
        postDrawable.add(ResourcesCompat.getDrawable(getResources(), R.drawable.placeholder3, null));

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPosts);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        postsRecyclerViewAdapter = new PostsRecyclerViewAdapter(view.getContext(), postText, postDrawable);
        recyclerView.setAdapter(postsRecyclerViewAdapter);
        recyclerView.setSaveEnabled(true);

        // set up the RecyclerView
//        if (savedInstanceState == null) {
//            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
//            postsRecyclerViewAdapter = new PostsRecyclerViewAdapter(view.getContext(), postText);
//            recyclerView.setAdapter(postsRecyclerViewAdapter);
//            recyclerView.setSaveEnabled(true);
////        } else {
////            stateList = savedInstanceState.getParcelable("socialPostsList");
////            if (stateList != null) {
////                recyclerView.getLayoutManager().onRestoreInstanceState(stateList);
////                stateList = null;
////            }
//        }
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        RecyclerView recyclerView = getView().findViewById(R.id.recyclerViewPosts);
//        if (recyclerView != null) {
//            recyclerView.getLayoutManager();
//        }
//    }

    //    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        RecyclerView recyclerView = getView().findViewById(R.id.recyclerViewPosts);
//        outState.putParcelable("socialPostsList", recyclerView.getLayoutManager().onSaveInstanceState());
//        super.onSaveInstanceState(outState);
//    }

//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//        if (savedInstanceState != null) {
//            stateList = savedInstanceState.getParcelable("socialPostsList");
//        }
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        RecyclerView recyclerView = getView().findViewById(R.id.recyclerViewPosts);
//
//        if (stateList != null && recyclerView != null) {
//            recyclerView.getLayoutManager().onRestoreInstanceState(stateList);
//        }
//    }
}