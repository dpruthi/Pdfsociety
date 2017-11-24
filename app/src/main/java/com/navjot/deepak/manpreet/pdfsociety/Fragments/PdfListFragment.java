package com.navjot.deepak.manpreet.pdfsociety.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.navjot.deepak.manpreet.pdfsociety.Models.Pdf;
import com.navjot.deepak.manpreet.pdfsociety.Adapters.PdfAdapter;
import com.navjot.deepak.manpreet.pdfsociety.R;
import java.util.ArrayList;
import java.util.Locale;

public abstract class PdfListFragment extends Fragment {

    protected DatabaseReference mDatabase;
    protected PdfAdapter mAdapter;
    protected RecyclerView mRecycler;
    protected LinearLayoutManager mManager;
    protected ProgressBar mProgressBar;
    TextView Nopdf;
    ArrayList<Pdf> pdfList;
    Pdf pdf;
    SearchView searchView;

    public PdfListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_pdfs, container, false);

        pdfList = new ArrayList<>();
        Nopdf = rootView.findViewById(R.id.Nopdf);
        mProgressBar = rootView.findViewById(R.id.mProgressBar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler =  rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);
        return rootView;


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        getListData(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (mAdapter != null) {
//            mAdapter.
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (mAdapter != null) {
//            mAdapter.stopListening();
//        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    protected abstract void getListData(Context context);

    protected void searchViewListener(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        mAdapter.getFilter().filter(newText);
                        return false;
                    }

                }
        );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home, menu);
        MenuItem item = menu.findItem(R.id.menusearch);
        searchView = (SearchView)item.getActionView();
        searchViewListener();
    }

}