package edu.uw.tcss450.group8.frolicker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    /**
     * Default constructor
     * @param theMap The map of event categories to be displayed
     */
    public PrefsInitAdapter(Map<String, Integer> theMap){
        mMap = theMap;

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
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(PrefsInitAdapter.Holder holder, int position) {
        holder.mName.setText(mKeyList.get(position));
    }

    @Override
    public int getItemCount() {
       return mMap.size();
    }

    /**
     * Internal class that represents one item in the RecyclerView
     */
    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mName;

        /**
         * Default constructor
         * @param theView the active view
         */
        public Holder(View theView){
            super(theView);
            mName = (TextView) theView.findViewById(R.id.pref_init_name);
        }

        @Override
        public void onClick(View v) {
            //TODO
        }
    }
}
