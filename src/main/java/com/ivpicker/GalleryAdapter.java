package com.ivpicker;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by harsewaksingh on 20/01/16.
 */
public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Cursor cursor;
    Context context;
    int type;
    OnItemClickListener onItemClickListener;
    int SIZE = 250;
    Set<String> selections;
    boolean multiSelection;

    GalleryAdapter(int type, Context context) {
        selections = new HashSet<>();
        this.context = context;
        this.type = type;
        switch (type) {
            case Gallery.TYPE_IMAGES:
                String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
                String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";//MediaStore.Images.Media._ID;
                cursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                        null, orderBy);
                break;
            case Gallery.TYPE_VIDEOS:
                String[] vColumns = {MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID};
                String vOrderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";//MediaStore.Video.Media._ID;
                cursor = context.getContentResolver().query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, vColumns, null,
                        null, vOrderBy);
                break;
        }
    }

    public void setMultiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(context)
                .inflate(R.layout.gallery_item, parent, false);
        return new GalleryViewHolder(itemView);
    }

    public void clearSelection() {
        selections.clear();
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelection() {
        ArrayList<String> selectedPaths = new ArrayList<>();
        selectedPaths.addAll(selections);
        return selectedPaths;
    }

    interface OnItemClickListener {
        public void onItemClick(String path);

        public void onItemLongClick(String path);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GalleryViewHolder galleryViewHolder = (GalleryViewHolder) holder;
        cursor.moveToPosition(position);
        switch (type) {
            case Gallery.TYPE_IMAGES: {
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                int id = cursor.getInt(columnIndex);
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                final String path = cursor.getString(dataColumnIndex);
                handleClickEvents(path, galleryViewHolder);
                galleryViewHolder.imageViewThumbnail.setTag(id);
                loadImageThumbnail(id, path, galleryViewHolder.imageViewThumbnail);
                break;
            }
            case Gallery.TYPE_VIDEOS: {
                int columnIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                int id = cursor.getInt(columnIndex);
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                final String path = cursor.getString(dataColumnIndex);
                handleClickEvents(path, galleryViewHolder);
                galleryViewHolder.imageViewThumbnail.setTag(id);
                loadVideoThumbnail(id, path, galleryViewHolder.imageViewThumbnail);
                break;
            }
        }
    }

    public int getSelectionCount() {
        return selections.size();
    }

    private void handleClickEvents(final String path, GalleryViewHolder galleryViewHolder) {
        if (selections.contains(path)) {
            galleryViewHolder.frameLayoutSelected.setVisibility(View.VISIBLE);
        } else {
            galleryViewHolder.frameLayoutSelected.setVisibility(View.GONE);
        }
        galleryViewHolder.imageViewThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    if (multiSelection) {
                        if (selections.size() != 0) {
                            if (!selections.contains(path))
                                selections.add(path);
                            else
                                selections.remove(path);
                            notifyDataSetChanged();
                        }
                    }
                    onItemClickListener.onItemClick(path);
                }
            }
        });
        if (multiSelection) {
            galleryViewHolder.imageViewThumbnail.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onItemClickListener != null) {
                        if (selections.size() == 0) {
                            selections.add(path);
                            notifyDataSetChanged();
                            onItemClickListener.onItemLongClick(path);
                        }
                    }
                    return true;
                }
            });
        }
    }


    public static Bitmap generateBitmap(String path, int size) {
        Bitmap thumbnailVideo = ThumbnailUtils.createVideoThumbnail(path,
                MediaStore.Video.Thumbnails.MINI_KIND);
        if (size > 0) {
            if (thumbnailVideo != null) {
                return Bitmap.createScaledBitmap(thumbnailVideo, size, size, false);
            }
        }
        return thumbnailVideo;
    }


    private void loadImageThumbnail(final int id, final String path, final ImageView imageView) {
        imageView.setImageBitmap(null);
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                        context.getContentResolver(), id,
                        MediaStore.Images.Thumbnails.MICRO_KIND, null);
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                try {
                    Object tag = imageView.getTag();
                    if (Integer.parseInt(tag.toString()) == id) {
                        imageView.setImageBitmap(bitmap);
                    }
                } catch (Exception e) {

                }
            }
        }.execute();
    }

    private void loadVideoThumbnail(final int id, final String path, final ImageView imageView) {
        imageView.setImageBitmap(null);
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                        context.getContentResolver(), id,
                        MediaStore.Video.Thumbnails.MICRO_KIND, null);
                if (bitmap == null) {
                    bitmap = generateBitmap(path, SIZE);
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                try {
                    Object tag = imageView.getTag();
                    if (Integer.parseInt(tag.toString()) == id) {
                        imageView.setImageBitmap(bitmap);
                    }
                } catch (Exception e) {

                }
            }
        }.execute();
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }
}
