package com.navjot.deepak.manpreet.pdfsociety.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.navjot.deepak.manpreet.pdfsociety.Activities.PdfDetailActivity;
import com.navjot.deepak.manpreet.pdfsociety.Adapters.MyPdfAdapter;
import com.navjot.deepak.manpreet.pdfsociety.Adapters.PdfAdapter;
import com.navjot.deepak.manpreet.pdfsociety.Models.Pdf;
import com.navjot.deepak.manpreet.pdfsociety.R;
import com.navjot.deepak.manpreet.pdfsociety.Viewholders.MyPdfViewHolder;

import java.util.ArrayList;

public class MyPdfsFragment extends PdfListFragment {

    MyPdfAdapter mAdapter;

    public MyPdfsFragment() {}

    protected void searchViewListener(){
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
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
    public void getListData(final Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {

            FirebaseDatabase.getInstance().getReference().child(getString(R.string.DB_user_pdfs)).child(getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<Pdf> pdfList = new ArrayList<>();
                            for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                                pdf = postSnapShot.getValue(Pdf.class);
                                pdfList.add(pdf);
                            }

                            mAdapter = new MyPdfAdapter(mProgressBar, Nopdf, pdfList, context);
                            mRecycler.setAdapter(mAdapter);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("Pdfsociety", "getData Error: " + databaseError.getMessage());
                        }
                    });
        }else {
            mProgressBar.setVisibility(View.GONE);
            Nopdf.setVisibility(View.VISIBLE);
            Nopdf.setText("Check your internet connection");
        }
    }
}