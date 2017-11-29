package com.navjot.deepak.manpreet.pdfsociety.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navjot.deepak.manpreet.pdfsociety.Models.Pdf;
import com.navjot.deepak.manpreet.pdfsociety.Adapters.PdfAdapter;
import com.navjot.deepak.manpreet.pdfsociety.R;

import java.util.ArrayList;

public class RecentPdfsFragment extends PdfListFragment {

    public RecentPdfsFragment() {}

    public void getListData(final Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {

            FirebaseDatabase.getInstance().getReference().child(getString(R.string.DB_Pdfs))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<Pdf> pdfList = new ArrayList<>();
                            for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                                pdf = postSnapShot.getValue(Pdf.class);
                                pdfList.add(pdf);
                            }

                            mAdapter = new PdfAdapter(mProgressBar, Nopdf, pdfList, context);
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
