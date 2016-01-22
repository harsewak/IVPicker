package com.ivpicker;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by harsewaksingh on 20/01/16.
 */
public class GalleryViewHolder extends RecyclerView.ViewHolder{
    View itemView;
    ImageView imageViewThumbnail;
    FrameLayout frameLayoutSelected;
    public GalleryViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        imageViewThumbnail = (ImageView)itemView.findViewById(R.id.imageViewThumbnail);
        frameLayoutSelected = (FrameLayout)itemView.findViewById(R.id.frameLayoutSelected);
        frameLayoutSelected.setVisibility(View.GONE);
    }
}
