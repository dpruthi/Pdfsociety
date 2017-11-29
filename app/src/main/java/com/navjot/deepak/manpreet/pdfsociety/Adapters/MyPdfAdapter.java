package com.navjot.deepak.manpreet.pdfsociety.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.navjot.deepak.manpreet.pdfsociety.Activities.PdfDetailActivity;
import com.navjot.deepak.manpreet.pdfsociety.Models.Pdf;
import com.navjot.deepak.manpreet.pdfsociety.R;
import com.navjot.deepak.manpreet.pdfsociety.Viewholders.MyPdfViewHolder;
import com.navjot.deepak.manpreet.pdfsociety.Viewholders.PdfViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MyPdfAdapter extends RecyclerView.Adapter<MyPdfViewHolder> implements Filterable {

    ProgressBar mProgressBar;
    TextView Nopdf;
    List<Pdf> pdfList;
    List<Pdf> allDataList;
    Context context;
    ProgressDialog mprogress;
    protected DatabaseReference mDatabase;

    public MyPdfAdapter( ProgressBar mprogressBar, TextView nopdf, ArrayList<Pdf> pdfarraylist, Context conText) {
        super();
        mProgressBar = mprogressBar;
        Nopdf = nopdf;
        pdfList = pdfarraylist;
        allDataList = pdfarraylist;
        context = conText;
        checkNoPdf();
    }

    private void checkNoPdf() {
        if(allDataList.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            Nopdf.setText("You haven't uploaded any Pdf yet !!");
            Nopdf.setVisibility(View.VISIBLE);
        }
        Log.d("Pdfsociety", "checkNoPdf");
    }

    @Override
    public MyPdfViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mProgressBar.setVisibility(View.GONE);
        Nopdf.setVisibility(View.GONE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("Pdfsociety", "MyPdfAdapter onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new MyPdfViewHolder(inflater.inflate(R.layout.item_mypdf, viewGroup, false));
    }

    @Override
    public int getItemCount() {
//        Log.d("Pdfsociety", "MyPdfAdapter getItemCount: "+pdfList.size());
        return pdfList.size();
    }

    private void checkNoResultFound(){
        if(pdfList.isEmpty()){
            Nopdf.setText("No Results Found");
            Nopdf.setVisibility(View.VISIBLE);
        }
        else {
            Nopdf.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(MyPdfViewHolder viewHolder, int position) {
      final Pdf pdf = pdfList.get(position);
      final String PdfKey = pdf.getPdfKey();

//         Set click listener for the whole Pdf view
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Pdfsociety", "PostListFragment onBindViewHolder viewHolder onClick");
                // Launch PdfDetailActivity
                Intent intent = new Intent(v.getContext(), PdfDetailActivity.class);
                intent.putExtra("pdfkey", PdfKey);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                v.getContext().startActivity(intent);
            }
        });

        // Bind Pdf to ViewHolder, setting OnClickListener for the deletepdf button
        viewHolder.bindToPdf(pdf, new View.OnClickListener() {
            @Override
            public void onClick(View starView) {
//                pdf = model;
                Log.d("Pdfsociety", "PostListFragment viewholder bindToPost onDeleteClicked");
                askForDeletion(PdfKey, pdf);
            }
        });
    }

    public void askForDeletion(final String pdfkey, final Pdf pdf){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete: "+pdf.getPdfname());
        builder.setMessage("Are you Sure ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showCaptionProgressDialog("Deleting...");
                deletePdfOnStorage(pdfkey, pdf);
            }
        });
        builder.setNegativeButton("Cancel",null);
        builder.create().show();
    }

    public void showCaptionProgressDialog(String caption) {
        if (mprogress == null) {
            mprogress = new ProgressDialog(context);
            mprogress.setMessage(caption);
            mprogress.setIndeterminate(true);
        }

        mprogress.show();
    }
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void deletePdfOnStorage(final String PdfKey, final Pdf pdf){
        Log.d(context.getString(R.string.tag), "pdf.getPdfname(): "+pdf.getPdfname());
        Log.d(context.getString(R.string.tag), "getUid(): "+getUid());
        Log.d(context.getString(R.string.tag), "PdfKey: "+PdfKey);
        FirebaseStorage.getInstance().getReference()
                .child(getUid())
                .child(PdfKey)
                .child(pdf.getPdfname())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, pdf.getPdfname()+" deleted", Toast.LENGTH_SHORT).show();
                        DatabaseReference globalPdfRef = mDatabase.child(context.getString(R.string.DB_Pdfs)).child(PdfKey);
                        DatabaseReference userPdfRef = mDatabase.child(context.getString(R.string.DB_user_pdfs)).child(getUid()).child(PdfKey);
                        deletePdfOnFirebaseDB(globalPdfRef);
                        deletePdfOnFirebaseDB(userPdfRef);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        hideProgressDialog();
                        Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void hideProgressDialog() {
        if (mprogress != null && mprogress.isShowing()) {
            mprogress.dismiss();
        }
    }

    public void deletePdfOnFirebaseDB(DatabaseReference pdfRef){
        pdfRef.removeValue();
//        notifyDataSetChanged();
        if(pdfRef.getParent().toString().contains(context.getString(R.string.DB_user_pdfs))){
            hideProgressDialog();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString().trim();

                if (charString.isEmpty()) {

                    pdfList = allDataList;
                } else {

                    ArrayList<Pdf> filteredList = new ArrayList<>();

                    for (Pdf pdf : allDataList) {

                        if (pdf.getPdfname().toLowerCase().contains(charString)){
                            filteredList.add(pdf);
                        }
                    }

                    pdfList = filteredList;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = pdfList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                pdfList = (ArrayList<Pdf>) filterResults.values;
                checkNoResultFound();
                notifyDataSetChanged();
            }
        };
    }
}
