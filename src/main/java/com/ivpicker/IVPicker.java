package com.ivpicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class IVPicker extends AppCompatActivity implements Gallery.OnFragmentResult {


    /**
     * Please make sure to set   <item name="windowNoTitle">true</item>
     * <item name="windowActionBar">false</item> with your theme or make sure your theme should have parent="Theme.AppCompat.Light.NoActionBar" as parent theme
     */
    public static final String THEME = "Theme";
    public static final String IV_TYPE = "IVType";
    /**
     * if passed #IVType is either Images or Videos then passed title will be set as action bar title
     **/
    public static final String TITLE = "Title";
    /***/
    public static final String TITLE_ID = "TitleId";
    public static final String GRID_COL = "GridCol";
    public static final String MULTI_SELECTION = "MultiSelection";
    public static final String SELECTION_LIMIT = "SelectionLimit";
    /**
     * Used to setResult as intent data
     */
    public static final String SELECTION = "Selection";
    /**
     * Key declarations End
     */
    private static final int DEFAULT_THEME = R.style.IVPickerTheme;
    private int theme = 0;
    // private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FrameLayout frameLayoutContent;
    private IVType ivType;
    private int gridColumns = 0;
    private boolean multiSelection = false;
    private int selectionLimit = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        theme = intent.getIntExtra(THEME, 0);
        if (theme == 0) {
            theme = DEFAULT_THEME;
        }
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iv_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);


        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

        frameLayoutContent = (FrameLayout) findViewById(R.id.frameLayoutContent);

        selectionLimit = intent.getIntExtra(SELECTION_LIMIT, 10);
        multiSelection = intent.getBooleanExtra(MULTI_SELECTION, false);
        gridColumns = intent.getIntExtra(GRID_COL, 0);

        int titleId = intent.getIntExtra(TITLE_ID, 0);
        if (titleId > 0) {
            getSupportActionBar().setTitle(titleId);
        } else {
            String title = intent.getStringExtra(TITLE);
            if (!isValid(title)) {
                title = "Select";
            }
            getSupportActionBar().setTitle(title);
        }
        //default type is IVType#IMAGES
        ivType = IVType.toIVType(intent.getIntExtra(IV_TYPE, 0));
        switch (ivType) {
            case IMAGES:
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutContent, Gallery.newImagesGallery(gridColumns, multiSelection, selectionLimit)).commit();
                break;
            case VIDEOS:
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutContent, Gallery.newVideosGallery(gridColumns, multiSelection, selectionLimit)).commit();
                break;
            case BOTH:
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
                viewPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                frameLayoutContent.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void setResult(ArrayList<String> paths) {
        Intent data = getIntent();
        data.putStringArrayListExtra(SELECTION, paths);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean isValid(String str) {
        if (str != null) {
            return !str.equals("");
        }
        return false;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(Gallery.newImagesGallery(gridColumns, multiSelection, selectionLimit), "Images");
        adapter.addFragment(Gallery.newVideosGallery(gridColumns, multiSelection, selectionLimit), "Videos");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
