package com.navjot.deepak.manpreet.pdfsociety.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.navjot.deepak.manpreet.pdfsociety.Activities.PdfDetailActivity;
import com.navjot.deepak.manpreet.pdfsociety.Models.Pdf;
import com.navjot.deepak.manpreet.pdfsociety.R;
import com.navjot.deepak.manpreet.pdfsociety.Viewholders.MyPdfViewHolder;

public class MyPdfsFragment extends PdfListFragment {

    private FirebaseRecyclerAdapter<Pdf, MyPdfViewHolder> mAdapter;
    Pdf pdf;
    ProgressDialog mprogress;


    public MyPdfsFragment() {}

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query PdfsQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Pdf>()
                .setQuery(PdfsQuery, Pdf.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Pdf, MyPdfViewHolder>(options) {

            public void onDataChanged() {
                if(i == 0){
                    mProgressBar.setVisibility(View.GONE);
                    Nopdf.setText("No Pdfs Uploaded Yet !!");
                    Nopdf.setVisibility(View.VISIBLE);
                    i = 1;
                }
                Log.d(getString(R.string.tag), "onDataChanged");
            }

            @Override
            public MyPdfViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                mProgressBar.setVisibility(View.GONE);
                Nopdf.setVisibility(View.GONE);
                Log.d(getString(R.string.tag), "PostListFragment PostViewHolder onCreateViewHolder");
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MyPdfViewHolder(inflater.inflate(R.layout.item_mypdf, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(MyPdfViewHolder viewHolder, int position, final Pdf model) {
              final DatabaseReference PdfRef = getRef(position);
                Log.d(getString(R.string.tag), "PostListFragment onBindViewHolder position: "+position);
                // Set click listener for the whole Pdf view
                final String PdfKey = PdfRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(getString(R.string.tag), "PostListFragment onBindViewHolder viewHolder onClick");
                        // Launch PdfDetailActivity
                        Intent intent = new Intent(getActivity(), PdfDetailActivity.class);
                        intent.putExtra("pdfkey", PdfKey);
                        startActivity(intent);
                    }
                });
                // Bind Pdf to ViewHolder, setting OnClickListener for the deletepdf button
                viewHolder.bindToPdf(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        pdf = model;
                        Log.d(getString(R.string.tag), "PostListFragment viewholder bindToPost onDeleteClicked");
                        askForDeletion(PdfKey);
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    public void askForDeletion(final String pdfkey){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete: "+pdf.getPdfname());
        builder.setMessage("Are you Sure ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showCaptionProgressDialog("Deleting...");
                deletePdfOnStorage(pdfkey);
            }
        });
        builder.setNegativeButton("Cancel",null);
        builder.create().show();
    }

    public void deletePdfOnStorage(final String PdfKey){
        Log.d(getString(R.string.tag), "pdf.getPdfname(): "+pdf.getPdfname());
        Log.d(getString(R.string.tag), "getUid(): "+getUid());
        Log.d(getString(R.string.tag), "PdfKey: "+PdfKey);
        FirebaseStorage.getInstance().getReference()
                .child(getUid())
                .child(PdfKey)
                .child(pdf.getPdfname())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), pdf.getPdfname()+" deleted", Toast.LENGTH_SHORT).show();
                        DatabaseReference globalPdfRef = mDatabase.child(getString(R.string.DB_Pdfs)).child(PdfKey);
                        DatabaseReference userPdfRef = mDatabase.child(getString(R.string.DB_user_pdfs)).child(pdf.getUid()).child(PdfKey);
                        deletePdfOnFirebaseDB(globalPdfRef);
                        deletePdfOnFirebaseDB(userPdfRef);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        hideProgressDialog();
                        Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deletePdfOnFirebaseDB(DatabaseReference pdfRef){
        pdfRef.removeValue();
        mAdapter.notifyDataSetChanged();
        hideProgressDialog();
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START my_top_pdfs_query]
        // My top pdfs by number of stars
        String myUserId = getUid();
        Query myToppdfsQuery = databaseReference.child(getString(R.string.DB_user_pdfs)).child(getUid());
        // [END my_top_pdfs_query]

        return myToppdfsQuery;
    }

    public void showCaptionProgressDialog(String caption) {
        if (mprogress == null) {
            mprogress = new ProgressDialog(getActivity());
            mprogress.setMessage(caption);
            mprogress.setIndeterminate(true);
        }

        mprogress.show();
    }

    public void hideProgressDialog() {
        if (mprogress != null && mprogress.isShowing()) {
            mprogress.dismiss();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
}