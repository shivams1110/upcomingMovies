package com.ssalphax.upcomingmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;

import java.util.List;

/**
 * Created by ssalphax on 4/4/2017.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private Context context;
    private List<UpcomingModel> list;

    public MainAdapter(Context context, List<UpcomingModel> list) {

        this.context=context;
        this.list=list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.mian_adapter,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);



        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        UpcomingModel uM=list.get(position);

        holder.txtDate.setText(uM.getRelease_date());
        holder.txtTitle.setText(uM.getTitle());

        boolean type= uM.isAdult();

        if (type) holder.txtAdult.setText("(A)");
        else holder.txtAdult.setText("(U/A)");


        Glide.with(context).load("https://image.tmdb.org/t/p/w500"+uM.getPoster_img()).into(holder.imgMain);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imgMain;
        public TextView txtTitle,txtDate,txtAdult;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            imgMain=(ImageView)itemView.findViewById(R.id.img_main);
            txtAdult=(TextView)itemView.findViewById(R.id.txt_type);
            txtTitle=(TextView)itemView.findViewById(R.id.txt_title);
            txtDate=(TextView)itemView.findViewById(R.id.txt_date);





        }

        @Override
        public void onClick(View v) {


            int position=getPosition();

            UpcomingModel model=list.get(position);

            Intent intent=new Intent(context,MovieDetailActivity.class);
            intent.putExtra("_id",model.getId());
            context.startActivity(intent);




        }
    }
}
