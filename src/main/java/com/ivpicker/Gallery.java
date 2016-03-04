package com.ivpicker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class Gallery extends Fragment implements GalleryAdapter.OnItemClickListener {

    public static final int TYPE_VIDEOS = 2;
    public static final int TYPE_IMAGES = 1;
    public static final int DEFAULT_GRID = 3;
    AppCompatActivity activity;
    boolean multiSelection = false;
    int selectionLimit = 10;

    public static Gallery newInstance(int type, int gridColumns, boolean multiSelection, int selectionLimit) {
        Gallery gallery = new Gallery();
        Bundle bundle = new Bundle();
        bundle.putInt("Type", type);
        bundle.putInt("GridColumns", gridColumns);
        bundle.putBoolean("MultiSelection", multiSelection);
        bundle.putInt("SelectionLimit", selectionLimit);
        gallery.setArguments(bundle);
        return gallery;
    }

    public static Gallery newVideosGallery(int gridColumns, boolean multiSelection, int selectionLimit) {
        return newInstance(Gallery.TYPE_VIDEOS, gridColumns, multiSelection, selectionLimit);
    }

    public static Gallery newImagesGallery(int gridColumns, boolean multiSelection, int selectionLimit) {
        return newInstance(Gallery.TYPE_IMAGES, gridColumns, multiSelection, selectionLimit);
    }

    int type = 1;
    int gridColumns = DEFAULT_GRID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        type = bundle.getInt("Type");
        gridColumns = bundle.getInt("GridColumns");
        multiSelection = bundle.getBoolean("MultiSelection");
        selectionLimit = bundle.getInt("SelectionLimit");
        if (gridColumns == 0) {
            gridColumns = DEFAULT_GRID;
        }
    }

    RecyclerView recyclerViewGalleryImages;
    GalleryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerViewGalleryImages = (RecyclerView) view.findViewById(R.id.recyclerViewGallery);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, 3);
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerViewGalleryImages.setLayoutManager(gridLayoutManager);
        adapter = new GalleryAdapter(type, activity);
        adapter.setOnItemClickListener(this);
        adapter.setMultiSelection(multiSelection);
        adapter.setselectionLimit(selectionLimit);
        recyclerViewGalleryImages.setAdapter(adapter);
        return view;
    }

    @Override
    public void onItemClick(String path) {
        if (activity instanceof OnFragmentResult) {
            if (multiSelection && adapter.isSelecting) {
                changeSelectionCount(adapter.getSelectionCount());
            } else {
                ArrayList<String> paths = new ArrayList<>();
                paths.add(path);
                ((OnFragmentResult) activity).setResult(paths);
            }
        }

    }

    public void changeSelectionCount(int selected) {
        if (mActionMode != null) {
            mActionMode.setTitle(selected + " selected");
            if (selected == 0) {
                adapter.isSelecting = false;
                mActionMode.finish();
                mActionMode = null;
            }
        }
    }

    ActionMode mActionMode;

    @Override
    public void onItemLongClick(String path) {
        if (mActionMode == null)
            mActionMode = activity.startSupportActionMode(new ActionModeCallback());
        changeSelectionCount(adapter.getSelectionCount());
        //activity.startActionMode(new ActionModeCallback(),ActionMode.TYPE_FLOATING);
    }

    class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.selection_options, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

            if (menuItem.getItemId() == R.id.itemOk) {
                ArrayList<String> selections = adapter.getSelection();
                ((IVPicker) activity).setResult(selections);
                actionMode.finish();
                mActionMode = null;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mActionMode = null;
            adapter.clearSelection();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (AppCompatActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentResult {
        public void setResult(ArrayList<String> path);
    }
}
