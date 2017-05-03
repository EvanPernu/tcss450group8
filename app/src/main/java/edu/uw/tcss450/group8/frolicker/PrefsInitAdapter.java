package edu.uw.tcss450.group8.frolicker;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PrefsInitAdapter extends RecyclerView.Adapter<PrefsInitAdapter.Holder>{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Map<String, Integer> mMap;
    private List<String> mKeyList;
    public PrefsInitAdapter(Map<String, Integer> theMap){

        mMap = theMap;
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

    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mName;

        public Holder(View theView){
            super(theView);
            mName = (TextView) theView.findViewById(R.id.pref_init_name);
        }

        @Override
        public void onClick(View v) {
            Log.d("RecyclerView", "CLICK!");
        }
    }
}
