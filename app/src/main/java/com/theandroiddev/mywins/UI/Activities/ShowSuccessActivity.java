package com.theandroiddev.mywins.UI.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.theandroiddev.mywins.R;
import com.theandroiddev.mywins.Storage.DBAdapter;
import com.theandroiddev.mywins.UI.Adapters.SuccessImageAdapter;
import com.theandroiddev.mywins.UI.Helpers.DrawableSelector;
import com.theandroiddev.mywins.UI.Models.Success;
import com.theandroiddev.mywins.UI.Models.SuccessImage;

import java.util.ArrayList;

import static com.theandroiddev.mywins.UI.Helpers.Constants.EXTRA_EDIT_SUCCESS_ITEM;
import static com.theandroiddev.mywins.UI.Helpers.Constants.EXTRA_SHOW_SUCCESS_IMAGES;
import static com.theandroiddev.mywins.UI.Helpers.Constants.EXTRA_SHOW_SUCCESS_ITEM;
import static com.theandroiddev.mywins.UI.Helpers.Constants.EXTRA_SUCCESS_ITEM;
import static com.theandroiddev.mywins.UI.Helpers.Constants.REQUEST_CODE_INSERT;
import static com.theandroiddev.mywins.UI.Helpers.Constants.SNACK_SAVED;

public class ShowSuccessActivity extends AppCompatActivity implements SuccessImageAdapter.OnSuccessImageClickListener {
    private static final String TAG = "ShowSuccessActivity";

    int id;
    int color;
    Success s;
    TextView showTitle, showCategory, showDescription, showDateStarted, showDateEnded;
    ImageView showCategoryIv, showImportanceIv;
    DrawableSelector drawableSelector;
    ConstraintLayout showConstraintLayout;
    DBAdapter dbAdapter;
    private RecyclerView recyclerView;
    private ArrayList<SuccessImage> successImages;
    private SuccessImageAdapter successImageAdapter;

    public ShowSuccessActivity() {
        this.drawableSelector = new DrawableSelector(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_success);
        Toolbar toolbar = (Toolbar) findViewById(R.id.show_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.show_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editSuccess();
            }
        });

        initFab(fab);
        initRecycler();
        initViews();
    }

    private void initImages(int successId) {
        getSuccessImages(successId);
    }

    private void initRecycler() {

        recyclerView = (RecyclerView) findViewById(R.id.show_image_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

    }

    private void getSuccessImages(int successId) {

        dbAdapter = new DBAdapter(this);



        successImages = new ArrayList<>();
        successImages.clear();
        dbAdapter.openDB();
        successImages.addAll(dbAdapter.retrieveSuccessImages(successId));
        dbAdapter.closeDB();
        successImageAdapter = new SuccessImageAdapter(successImages, this, R.layout.success_image_layout, this);
        recyclerView.setAdapter(successImageAdapter);
        successImageAdapter.notifyDataSetChanged();
    }

    private void initFab(FloatingActionButton fab) {

        id = R.drawable.ic_create_black_24dp;
        color = R.color.white;
        Drawable myDrawable = ResourcesCompat.getDrawable(getResources(), id, null);

        fab.setImageDrawable(myDrawable);
        fab.setColorFilter(ContextCompat.getColor(this, color));

    }

    private void editSuccess() {

        Intent editSuccessIntent = new Intent(ShowSuccessActivity.this, EditSuccessActivity.class);

        Success showSuccess = new Success(showTitle.getText().toString(), showCategory.getText().toString(),
                (int) showImportanceIv.getTag(), showDescription.getText().toString(), showDateStarted.getText().toString(), showDateEnded.getText().toString());
        showSuccess.setId((Integer) showTitle.getTag());

        editSuccessIntent.putExtra(EXTRA_SHOW_SUCCESS_ITEM, showSuccess);
        editSuccessIntent.putParcelableArrayListExtra(EXTRA_SHOW_SUCCESS_IMAGES, successImages);

        startActivityForResult(editSuccessIntent, REQUEST_CODE_INSERT);
    }

    private void initViews() {

        showConstraintLayout = (ConstraintLayout) findViewById(R.id.show_constraint_layout);
        showTitle = (TextView) findViewById(R.id.item_title);
        showCategory = (TextView) findViewById(R.id.item_category);
        showCategoryIv = (ImageView) findViewById(R.id.item_category_iv);
        showImportanceIv = (ImageView) findViewById(R.id.item_importance_iv);
        showDescription = (TextView) findViewById(R.id.show_description);
        showDateStarted = (TextView) findViewById(R.id.item_date_started);
        showDateEnded = (TextView) findViewById(R.id.item_date_ended);

        s = getIntent().getParcelableExtra(EXTRA_SUCCESS_ITEM);

        showTitle.setTag(s.getId());
        showTitle.setText(s.getTitle());
        showCategory.setText(s.getCategory());
        showDescription.setText(s.getDescription());
        showDateStarted.setText(s.getDateStarted());
        showDateEnded.setText(s.getDateEnded());
        showImportanceIv.setTag(s.getImportance());

        drawableSelector.selectCategoryImage(showCategoryIv, s.getCategory(), showCategory);
        drawableSelector.selectImportanceImage(showImportanceIv, s.getImportance());

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        showDescription.startAnimation(fadeIn);
        fadeIn.setDuration(375);
        fadeIn.setFillAfter(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initImages(s.getId());
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        showDescription.startAnimation(fadeOut);
        fadeOut.setDuration(375);
        fadeOut.setFillAfter(true);
    }

    private void editSuccess(Success showSuccess) {

        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.openDB();
        dbAdapter.editSuccess(showSuccess);
        dbAdapter.closeDB();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_INSERT) {
            if (resultCode == Activity.RESULT_OK) {

                Success s = data.getExtras().getParcelable(EXTRA_EDIT_SUCCESS_ITEM);

                editSuccess(s);

                if (s != null) {
                    showTitle.setText(s.getTitle());
                    showCategory.setText(s.getCategory());
                    showDateStarted.setText(s.getDateStarted());
                    showDateEnded.setText(s.getDateEnded());
                    showDescription.setText(s.getDescription());
                    showImportanceIv.setTag(s.getImportance());
                    drawableSelector.selectCategoryImage(showCategoryIv, s.getCategory(), showCategory);
                    drawableSelector.selectImportanceImage(showImportanceIv, s.getImportance());

                }

                makeSnackbar(SNACK_SAVED);
            }
        }

    }

    public void makeSnackbar(String s) {
        Snackbar.make(showConstraintLayout, s, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessImageClick(SuccessImage successImage, ImageView successImageIv, int position, ConstraintLayout constraintLayout, CardView cardView) {

        Intent intent = new Intent(this, ImageActivity.class);
        ArrayList<String> imagePaths = new ArrayList<>();

        for (int i = 0; i < successImages.size(); i++) {
            imagePaths.add(successImages.get(i).getImagePath());
        }

        intent.putStringArrayListExtra("imagePaths", imagePaths);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public void onSuccessImageLongClick(SuccessImage successImage, ImageView successImageIv, int position, ConstraintLayout constraintLayout, CardView cardView) {

    }

}

