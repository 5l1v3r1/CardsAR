package com.wear.cardsar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class MappingsListAdapter extends RecyclerView.Adapter<MappingsListAdapter.MappingViewHolder> {

    class MappingViewHolder extends RecyclerView.ViewHolder {
        private final TextView mappingItemView;

        private MappingViewHolder(View itemView) {
            super(itemView);
            mappingItemView = itemView.findViewById(R.id.mappingTextView);
        }
    }

    private final LayoutInflater mInflater;
    private List<CardMapping> mMappings; // Cached copy of words

    MappingsListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public MappingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_mapping_item, parent, false);
        return new MappingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MappingViewHolder holder, int position) {
        if (mMappings != null) {
            CardMapping current = mMappings.get(position);
            holder.mappingItemView.setText(current.getMappingName());
        } else {
            // Covers the case of data not being ready yet.
            holder.mappingItemView.setText("No Mapping");
        }
    }

    void setmMappings(List<CardMapping> mappings){
        mMappings = mappings;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mMappings != null)
            return mMappings.size();
        else return 0;
    }

}
