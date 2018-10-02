package com.darshan.android.fileexplorer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Darshan B.S on 13-09-2018.
 */

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    private static final String TAG = "FileListAdapter";

    private Context mContext;
    private ArrayList<Image> mImageFilesList;
    private HashSet<Image> mSelectedImageSet;
    private boolean selectEnabled = false;

    public FileListAdapter(Context mContext, ArrayList<Image> mImageFilesList) {
        this.mContext = mContext;
        this.mImageFilesList = mImageFilesList;

        mSelectedImageSet = new HashSet<>();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_gallery_file, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Image image = mImageFilesList.get(position);

        String thumbUri = image.getThumbUri();
        if(thumbUri != null ) {
            RequestOptions placeHolderOption = new RequestOptions().placeholder(R.drawable.blank_video_screen);
            Glide.with(mContext)
                    .load(image.getThumbUri())
                    .apply(placeHolderOption)
                    .into(holder.ivFileImage);
        }

        if(!image.isSelected()) {
            hideSelectedMark(holder.ivSelectedLogo, holder.ivFileImage);
        } else {
            showSelectedMark(holder.ivSelectedLogo, holder.ivFileImage);
        }

        if(image.isVideo()) {
            holder.ivVideoIcon.setVisibility(View.VISIBLE);
        } else {
            holder.ivVideoIcon.setVisibility(View.GONE);
        }

        holder.ivFileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEnabled = true;
                if(mSelectedImageSet.contains(image)) {
                    image.setSelected(false);
                    holder.ivFileImage.clearColorFilter();
                    mSelectedImageSet.remove(image);
                    hideSelectedMark(holder.ivSelectedLogo, holder.ivFileImage);
                } else {
                    image.setSelected(true);
                    mSelectedImageSet.add(image);
                    showSelectedMark(holder.ivSelectedLogo, holder.ivFileImage);
//                    holder.ivFileImage.setColorFilter(Color.argb(100, 0, 0, 0));
//                    highlightView(holder.ivSelectedLogo);
                }
            }

        });


    }


    @Override
    public int getItemCount() {
        int size = mImageFilesList.size();
//        Log.d(TAG, "getItemCount: " + size);
        return size;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
         ImageView ivFileImage;
         ImageView ivSelectedLogo, ivVideoIcon;

         ViewHolder(View itemView) {
            super(itemView);

            ivFileImage = itemView.findViewById(R.id.fileImage_IV);
            ivSelectedLogo = itemView.findViewById(R.id.selectedIcon);
            ivVideoIcon = itemView.findViewById(R.id.videoIcon);
        }
    }


    private void showSelectedMark(ImageView selectLogo, ImageView filterImageView) {
        ((GalleryActivity) mContext).showFileSelectBar(mSelectedImageSet.size());
        selectLogo.setVisibility(View.VISIBLE);
        filterImageView.setColorFilter(Color.argb(100, 0, 0, 0));
    }

    private void hideSelectedMark(ImageView selectLogo, ImageView filterImageView) {
        if(mSelectedImageSet.size() == 0) {
            ((GalleryActivity)mContext).showFolderSelectBar();
        } else {
            ((GalleryActivity) mContext).showFileSelectBar(mSelectedImageSet.size());
        }
        selectLogo.setVisibility(View.GONE);
        filterImageView.clearColorFilter();
    }

    public HashSet<Image> getSelectedItems() {
        return mSelectedImageSet;
    }

    public void clearSelectedList() {
        selectEnabled = false;
        for(Image image : mImageFilesList) {
            image.setSelected(false);
        }
        mSelectedImageSet.clear();
    }


}
