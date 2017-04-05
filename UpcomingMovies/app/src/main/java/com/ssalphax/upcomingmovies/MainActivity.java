package com.ssalphax.upcomingmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.studioidan.httpagent.HttpAgent;
import com.studioidan.httpagent.JsonCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<UpcomingModel> list=new ArrayList<>();
    private MainAdapter adapter;
    private ProgressBar progressBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        recyclerView=(RecyclerView)findViewById(R.id.recycler_movie_list);

        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        textView=(TextView)findViewById(R.id.txt_internet);
        textView.setVisibility(View.GONE);


        if (checkInternet()){
            getMovieList();
        }else{
            textView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }



        recyclerView.setLayoutManager(new LinearLayoutManager(this));





    }

    private boolean checkInternet() {

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    private void getMovieList() {

        HttpAgent.post("https://api.themoviedb.org/3/movie/upcoming")
                .queryParams("api_key","b7cd3340a794e5a2f35e3abb820b497f")
                .goJson(new JsonCallback() {
                    @Override
                    protected void onDone(boolean success, JSONObject jsonResults) {

//                        Log.d("data Fron",jsonResults.toString());
                        if (success) {
                            try {
                                putData(jsonResults);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            textView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                        }

                    }

                });



    }

    private void putData(JSONObject jsonResults) throws JSONException {

        JSONArray jsonArray=jsonResults.getJSONArray("results");

        for (int i = 0; i < jsonArray.length(); i++) {

            UpcomingModel model=new UpcomingModel();

            JSONObject object=jsonArray.getJSONObject(i);

            String poster_path=object.getString("poster_path");
            model.setPoster_img(poster_path);
            boolean adult=object.getBoolean("adult");
            model.setAdult(adult);

            String release=object.getString("release_date");
            model.setRelease_date(release);

            int id=object.getInt("id");
            model.setId(id);

            String title=object.getString("title");
            model.setTitle(title);

            list.add(model);



        }

        adapter=new MainAdapter(MainActivity.this,list);




        recyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {

            Intent intent=new Intent(MainActivity.this,InfoActivity.class);
            startActivity(intent);



            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
