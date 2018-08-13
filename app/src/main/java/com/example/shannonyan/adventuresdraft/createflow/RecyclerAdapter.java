package com.example.shannonyan.adventuresdraft.createflow;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.example.shannonyan.adventuresdraft.constants.TextViewStrings;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    public  ArrayList<String> allEvents;
    public DatabaseReference mDatabase;

    public RecyclerAdapter(ArrayList<String> events){
        allEvents = events;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder{

        public ImageView itemImage;
        public TextView itemTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImage = (ImageView)itemView.findViewById(R.id.item_image);
            itemTitle = (TextView)itemView.findViewById(R.id.item_title);
        }

        @Override
        public void onItemSelected() {
            itemView.setScaleX((float)1.1);
            itemView.setScaleY((float)1.1);
        }

        @Override
        public void onItemClear() {
            itemView.setScaleX((float)1);
            itemView.setScaleY((float)1);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);

        mDatabase = FirebaseDatabase.getInstance().getReference().child(Database.ITINERARY_ARRAY_NAME);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if(allEvents.get(i).equals(Database.EVENT_TYPE_NORM)){
            viewHolder.itemImage.setImageResource(R.drawable.sunset);
            viewHolder.itemTitle.setText(TextViewStrings.CARD_TITLE_EVENT_NORM);
        }else{
            viewHolder.itemImage.setImageResource(R.drawable.menu);
            viewHolder.itemTitle.setText(TextViewStrings.CARD_TITLE_EVENT_FOOD);
        }
    }

    @Override
    public int getItemCount() {
        return allEvents.size();
    }

    @Override
    public void onItemDismiss(int position) {
        allEvents.remove(position);
        notifyItemRemoved(position);
        updateItinerary(allEvents);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(allEvents, i, i + 1);
            }
            updateItinerary(allEvents);
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(allEvents, i, i - 1);
            }
            updateItinerary(allEvents);
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public void updateItinerary(ArrayList<String> itinerary){
        mDatabase.setValue(itinerary);
    }
}
