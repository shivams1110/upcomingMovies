package com.ssalphax.upcomingmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.studioidan.httpagent.HttpAgent;
import com.studioidan.httpagent.JsonCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieDetailActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager intro_images;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;
    private ArrayList<String> mImageResources = new ArrayList<>();
    private TextView txtOverView, txtTitle;
    private RatingBar ratingBar;
    private ProgressBar progressBar;
    private int id;
    private CardView cardView;
    private Toolbar toolbar;
    private TextView txt_internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        txt_internet = (TextView) findViewById(R.id.txt_internet_detail);
        txt_internet.setVisibility(View.GONE);


        id = this.getIntent().getIntExtra("_id", 0);

        intro_images = (ViewPager) findViewById(R.id.pager);
//        btnNext = (ImageButton) view.findViewById(R.id.btn_next);
//        btnFinish = (ImageButton) view.findViewById(R.id.btn_finish);

        pager_indicator = (LinearLayout) findViewById(R.id.linear_dots);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_detail);
        txtOverView = (TextView) findViewById(R.id.txt_overview);
        txtTitle = (TextView) findViewById(R.id.txt_title_detail);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        cardView = (CardView) findViewById(R.id.card_view);
        cardView.setVisibility(View.INVISIBLE);

//        btnNext.setOnClickListener(this);
//        btnFinish.setOnClickListener(this);


        if (checkInternet()) {
            getImage();

            getMovieDetail();
        } else {

            txt_internet.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

        }
        //intro_images.setAdapter(mAdapter);


    }

    private boolean checkInternet() {

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    private void getMovieDetail() {


        HttpAgent.get("http://api.themoviedb.org/3/movie/" + id + "?api_key=b7cd3340a794e5a2f35e3abb820b497f")
                .goJson(new JsonCallback() {
                    @Override
                    protected void onDone(boolean success, JSONObject jsonResults) {

                        if (success) {
                            try {
                                putDetail(jsonResults);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            txt_internet.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                        }

                    }
                });

        // http://api.themoviedb.org/3/movie/<movie-id>?api_key=b7cd3340a794e5a2f35e3abb820b497f
    }

    private void putDetail(JSONObject jsonResults) throws JSONException {

        String overView = jsonResults.getString("overview");
        String title = jsonResults.getString("title");

        double popularity = jsonResults.getDouble("popularity");


        txtTitle.setText(title);
        txtOverView.setText(overView);
        ratingBar.setIsIndicator(true);
        ratingBar.setRating(((float) popularity / 20));

        toolbar.setTitle(title);

        progressBar.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);

    }

    private void getImage() {


        HttpAgent.get("https://api.themoviedb.org/3/movie/" + id + "/images?api_key=b7cd3340a794e5a2f35e3abb820b497f")

                .goJson(new JsonCallback() {
                    @Override
                    protected void onDone(boolean success, JSONObject jsonResults) {

                        Log.d("data Fron", jsonResults.toString());
                        try {
                            putImage(jsonResults);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });


    }

    private void putImage(JSONObject jsonResults) throws JSONException {

        JSONArray array = jsonResults.getJSONArray("backdrops");

        for (int i = 0; i < array.length(); i++) {


            JSONObject object = array.getJSONObject(i);
            String file_path = object.getString("file_path");

            mImageResources.add(file_path);


        }


        mAdapter = new ViewPagerAdapter(this, mImageResources);
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.setOnPageChangeListener(this);


        setUiPageViewController();


    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
