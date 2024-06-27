package com.example.shivambhardwaj.shitube.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shivambhardwaj.shitube.Models.MoviesModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.databinding.MoviesRecyclerviewBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SubscibedChannelAdapter extends RecyclerView.Adapter<SubscibedChannelAdapter.viewHolder> {
    ArrayList<MoviesModel> list;
    Context context;

    public SubscibedChannelAdapter(ArrayList<MoviesModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override

    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movies_recyclerview, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        MoviesModel model = list.get(position);
        holder.binding.textView3.setText(model.getTitle());
        Picasso.get().load(model.getPosterURL()).placeholder(R.drawable.gallery).into(holder.binding.moviePosterImageview);
        holder.binding.clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri=model.getImdbId().toString();
                String Link="https://www.imdb.com/title/"+uri;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Link));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        MoviesRecyclerviewBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MoviesRecyclerviewBinding.bind(itemView);
        }
    }
}