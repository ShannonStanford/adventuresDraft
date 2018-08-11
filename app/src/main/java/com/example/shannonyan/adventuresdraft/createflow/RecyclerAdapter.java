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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter{

    public  ArrayList<String> allEvents;
    public DatabaseReference mDatabase;

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

            itemView.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View v) {
                    Snackbar.make(v, "Item Deleted",
                            Snackbar.LENGTH_LONG)
                            .setAction("UNDO", null).show();
                    return false;
                }
            });

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);

        mDatabase = FirebaseDatabase.getInstance().getReference().child(Database.ITINERARY_ARRAY_NAME);

//        mDatabaseItinerary = FirebaseDatabase.getInstance().getReference().child("itinerary");
//        mDatabaseItinerary.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                itinerary = (ArrayList<String>) dataSnapshot.getValue();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if(allEvents.get(i).equals(Database.EVENT_TYPE_NORM)){
            viewHolder.itemImage.setImageResource(R.drawable.sunset);
            viewHolder.itemTitle.setText(Database.CARD_TITLE_EVENT_NORM);
        }else{
            viewHolder.itemImage.setImageResource(R.drawable.menu);
            viewHolder.itemTitle.setText(Database.CARD_TITLE_EVENT_FOOD);
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
