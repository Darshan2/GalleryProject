package com.darshan.android.fileexplorer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by Darshan B.S on 15-09-2018.
 */

public class DirListAdapter extends RecyclerView.Adapter<DirListAdapter.DirecListViewHolder> {
    private static final String TAG = "DirListAdapter";
    private Context mContext;
    private ArrayList<Image> mDirecList;
    private DirClickListener mDirClickListener;
    private String mRequiredMediaType;

    public DirListAdapter(Context mContext, ArrayList<Image> subDirList, DirClickListener mDirClickListener, String mRequiredMediaType) {
        this.mContext = mContext;
        this.mDirClickListener = mDirClickListener;
        this.mDirecList = subDirList;
        this.mRequiredMediaType = mRequiredMediaType;

    }

    interface DirClickListener {
        void onDirClick(String dirName);
    }

    @NonNull
    @Override
    public DirecListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_gallery_direc, null);

        return new DirecListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DirecListViewHolder holder, final int position) {
        Image image = mDirecList.get(position);

        String filePath = image.getImageUri();

        String subDirPath = filePath.substring(0, filePath.lastIndexOf("/"));
        final String dirName = new File(subDirPath).getName();

        String thumbUri = image.getThumbUri();
        if(thumbUri != null ) {
            if(!thumbUri.equals(mContext.getResources().getString(R.string.No_first_frame_video_thumb))) {
                Glide.with(mContext)
                        .load(image.getThumbUri())
                        .into(holder.ivFolderThumb);
            } else {
                Glide.with(mContext)
                        .load(R.drawable.blank_video_screen)
                        .into(holder.ivFolderThumb);
            }

        }

        holder.tvDirName.setText(dirName);
        holder.tvDirItems.setText(getFolderItemCount(dirName));
        holder.rvFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDirClickListener.onDirClick(dirName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDirecList.size();
    }


    class DirecListViewHolder extends RecyclerView.ViewHolder {
        TextView tvDirName, tvDirItems;
        ImageView ivFolderThumb;
        RelativeLayout rvFolder;

        private DirecListViewHolder(View itemView) {
            super(itemView);

            tvDirName = itemView.findViewById(R.id.dirName_TV);
            tvDirItems = itemView.findViewById(R.id.dirItems_TV);
            ivFolderThumb = itemView.findViewById(R.id.folderThumb_IV);
            rvFolder = itemView.findViewById(R.id.folder_RV);
        }
    }

    private String getFolderItemCount(String folderName) {
        String itemNum = "";
        switch (mRequiredMediaType) {
            case GalleryConsts.IMAGE_TYPE :
               itemNum = getNumImages(folderName) + "";
               break;

            case GalleryConsts.VIDEO_TYPE :
                itemNum = getNumVideos(folderName) + "";
                break;

            case GalleryConsts.IMAGE_VIDEO_TYPE :
                int total = getNumImages(folderName) + getNumVideos(folderName);
                itemNum = total + "";
                break;

            default:break;
        }

        return itemNum;
    }

    private int getNumImages(String folderName) {
        Cursor cursor  = ((GalleryActivity)mContext).getFolderCursor(folderName,GalleryConsts.IMAGE_TYPE);
        return getNumItems(cursor);
    }

    private int getNumVideos(String folderName) {
        Cursor cursor  = ((GalleryActivity)mContext).getFolderCursor(folderName,GalleryConsts.VIDEO_TYPE);
        return getNumItems(cursor);
    }

    /**
     * @param cursor cursor pointing to particular folder
     * @return count of valid files(i.e readable file(file size > 0)) in folder pointed by a cursor
     */
    private int getNumItems(Cursor cursor) {
        if(cursor != null) {
            int itemCount = 0;
            //Check to see if the file exist(i.e has size more than 0kb)
            for(int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String filePath = cursor.getString(dataColumnIndex);

                File file = new File(filePath);
                if (file.length() > 0) {
                    itemCount ++;
                }
            }
            return itemCount;
        }
        return 0;
    }


}
