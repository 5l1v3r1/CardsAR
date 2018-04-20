package com.wear.cardsar;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

// MappingListAdapter will use a recyclable xml to display all the items in a list of game mappings
public class MappingsListAdapter extends RecyclerView.Adapter<MappingsListAdapter.MappingViewHolder> {

    // class used as the holder for one mapping
    class MappingViewHolder extends RecyclerView.ViewHolder {
        private final TextView mappingItemView;
        private final ImageView mappingImage;
        private final TextView quantityView;
        private CardMapping mMapping;

        private MappingViewHolder(final View itemView) {
            super(itemView);

            mappingItemView = itemView.findViewById(R.id.mappingTextView);
            mappingImage = itemView.findViewById(R.id.iv);
            quantityView = itemView.findViewById(R.id.intView);

            Button deleteButton = itemView.findViewById(R.id.delete_button);


            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mModel.delete(mMapping);
                }
            });
        }

        public void setMapping(CardMapping mapping){
            mMapping = mapping;
        }
    }

    private final LayoutInflater mInflater;
    private List<CardMapping> mMappings; // Cached copy of words
    private MappingViewModel mModel;

    MappingsListAdapter(Context context, MappingViewModel model) {
        mInflater = LayoutInflater.from(context);
        mModel = model;
    }

    //sets up object internals when the app starts
    @Override
    public MappingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_mapping_item, parent, false);
        return new MappingViewHolder(itemView);
    }

    // funcition assigns text, buttons and images correct image per item listed
    @Override
    public void onBindViewHolder(MappingViewHolder holder, int position) {
        if (mMappings != null) {
            CardMapping current = mMappings.get(position);
            holder.mappingItemView.setText(current.getMappingName());
            Bitmap bitmap;
            String mUri = current.getMappingUri();
            try {
                if(mUri != null) {
                    bitmap = MediaStore.Images.Media.getBitmap(mModel.getApplication().getContentResolver(), Uri.parse(mUri));
                    holder.mappingImage.setImageBitmap(bitmap);
                }else{ holder.mappingImage.setImageBitmap(null); }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            holder.quantityView.setText("(x" + current.getQuantity() + ")");
            holder.setMapping(current);
        } else {
            // Covers the case of data not being ready yet.
            holder.mappingItemView.setText("No Mapping");
        }
    }

    //function updated the mappings displays and notifies UI of changes
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
