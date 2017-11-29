package com.navjot.deepak.manpreet.pdfsociety.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.navjot.deepak.manpreet.pdfsociety.Activities.PdfDetailActivity;
import com.navjot.deepak.manpreet.pdfsociety.Models.Pdf;
import com.navjot.deepak.manpreet.pdfsociety.R;
import com.navjot.deepak.manpreet.pdfsociety.Viewholders.PdfViewHolder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PdfAdapter extends RecyclerView.Adapter<PdfViewHolder> implements Filterable {
    ProgressBar mProgressBar;
    TextView Nopdf;
    ArrayList<Pdf> pdfList;
    ArrayList<Pdf> allDataList;
    Pdf pdf;
    Context context;

    public PdfAdapter( ProgressBar mprogressBar, TextView nopdf, ArrayList<Pdf> pdfarraylist, Context conText) {
        super();
        Log.d("Pdfsociety", "PdfAdapter constructor ");
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
            Nopdf.setText("No PDF Uploaded");
            Nopdf.setVisibility(View.VISIBLE);
        }
        Log.d("Pdfsociety", "checkNoPdf");
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
    public PdfViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mProgressBar.setVisibility(View.GONE);
        Nopdf.setVisibility(View.GONE);
        Log.d("Pdfsociety", "PdfAdapter onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new PdfViewHolder(inflater.inflate(R.layout.item_pdf, viewGroup, false));
    }

    @Override
    public int getItemCount() {
        if(pdfList == null){
            Log.d("Pdfsociety", "pdflist null");
            return 0;
        }
        else{return pdfList.size();}
    }

    @Override
    public void onBindViewHolder(PdfViewHolder viewHolder, int position) {
        pdf = pdfList.get(position);
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

        viewHolder.bindToPdf(pdf);
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
