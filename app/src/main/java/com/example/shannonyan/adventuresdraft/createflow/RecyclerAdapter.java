package com.example.shannonyan.adventuresdraft.createflow;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.constants.Database;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public  ArrayList<String> allEvents;

    public RecyclerAdapter(ArrayList<String> events){
        allEvents = events;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView itemImage;
        public TextView itemTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImage = (ImageView)itemView.findViewById(R.id.item_image);
            itemTitle = (TextView)itemView.findViewById(R.id.item_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {

                    Snackbar.make(v, "Item Deleted",
                            Snackbar.LENGTH_LONG)
                            .setAction("UNDO", null).show();

                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if(allEvents.get(i).equals(Database.EVENT_TYPE_NORM)){
            viewHolder.itemImage.setImageResource(R.drawable.spaceship_dark);
            viewHolder.itemTitle.setText(Database.CARD_TITLE_EVENT_NORM);
        }else{
            viewHolder.itemImage.setImageResource(R.drawable.fork);
            viewHolder.itemTitle.setText(Database.CARD_TITLE_EVENT_FOOD);
        }
    }

    @Override
    public int getItemCount() {
        return allEvents.size();
    }
}
