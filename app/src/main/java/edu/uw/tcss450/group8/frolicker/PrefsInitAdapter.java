package edu.uw.tcss450.group8.frolicker;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * This is the adapter class used to display initial preferences as a RecyclerView
 *
 * @author Evan Pernu
 * @version 5/2/2017
 */
public class PrefsInitAdapter extends RecyclerView.Adapter<PrefsInitAdapter.Holder>{

    /** This map is equivalent to keywords in PrefList.java
     *
     * Key: a list of event categories
     * Value: how interested the user is in the category
     *
     *     -1 = not interested
     *      0 = impartial
     *      1 = interested
     *
     *This scale may later be changed to become more complicated.
     */
    private Map<String, Integer> mMap;

    /** Holds the contents of mMap's keyset. Used to track position/selections.*/
    private List<String> mKeyList;

    /**A reference to the Fragment this adapter is being used in (for communication to the Fragment)*/
    private PrefsInitFragment mParent;

    /**
     * Default constructor
     * @param theMap The map of event categories to be displayed
     */
    public PrefsInitAdapter(Map<String, Integer> theMap, PrefsInitFragment parent){
        mMap = theMap;
        mParent = parent;

        //initialize mKeyList
        mKeyList = new ArrayList<String>();
        for(String key : mMap.keySet()){
            mKeyList.add(key);
        }
    }

    @Override
    public PrefsInitAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pref_init_card, parent, false);
        return new Holder(v, this);
}

    @Override
    public void onBindViewHolder(PrefsInitAdapter.Holder holder, int position) {
        if(position < mKeyList.size()) {
            holder.mName.setText(mKeyList.get(position));
        }
    }

    @Override
    public int getItemCount() {
       return mKeyList.size();
    }

     /** Removes an item from the recyclerview
     * and updates its rating.
     *
     * @param position the position to be removed
     * @param val the rating to be assigned
     */
    public void removeItem(int position, int val, PrefsInitAdapter.Holder holder) throws JsonProcessingException {
        String name = mKeyList.get(position);

        mKeyList.remove(position);
        Log.d("removeItem", "mKeyList.size() = "+mKeyList.size());
        mMap.remove(name);
        mMap.put(name, val);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mKeyList.size());
        //notifyDataSetChanged();

        //the user has provided their opinion on each category. pass mMap to the parent Fragment.
        if(mKeyList.isEmpty()){
            mParent.notifyDone(mMap);
        }
    }

    /**
     * Internal class that represents one item in the RecyclerView
     *
     * @author Evan Pernu
     */
    public static class Holder extends RecyclerView.ViewHolder{
        private TextView mName;
        private Button mBtnLike;
        private Button mBtnDislike;
        private Button mBtnMaybe;
        private  PrefsInitAdapter mParent;

        /**
         * Default constructor, initializes all listeners.
         * Each listener calls remove() with a different rating value.
         *
         * @param theView the active view
         */
        public Holder(View theView, PrefsInitAdapter theAdapter){
            super(theView);
            mName = (TextView) theView.findViewById(R.id.pref_init_name);
            mParent = theAdapter;
            mBtnLike = (Button) theView.findViewById(R.id.pref_init_btnLike);
            mBtnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("recycler", "Like "+mName.getText());
                    try {
                        remove(1);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            });

            mBtnMaybe = (Button) theView.findViewById(R.id.pref_init_btnMaybe);
            mBtnMaybe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("recycler", "Maybe "+mName.getText());
                    try {
                        remove(0);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            });

            mBtnDislike = (Button) theView.findViewById(R.id.pref_init_btnDislike);
            mBtnDislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("recycler", "Dislike "+mName.getText());
                    try {
                        remove(-1);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            });


        }


        /**
         * Removes this holder from the list of holders being viewed.
         * Passes the selected rating to the adapter.
         *
         * @param rating the selected rating
         * @throws JsonProcessingException
         */
        private void remove(int rating) throws JsonProcessingException {
            mParent.removeItem(this.getAdapterPosition(), rating, this);
        }

    }
}
