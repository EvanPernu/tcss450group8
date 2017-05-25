package edu.uw.tcss450.group8.frolicker.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.uw.tcss450.group8.frolicker.R;

/**
 * Created by Tim on 5/25/2017.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventAdapterHolder> {

    private List<EventCard> objectList;
    private LayoutInflater inflater;
    private Context context;
    private int mExpandedPosition = -1;
    private RecyclerView recyclerView;
    private EventCard current;


    public EventAdapter(Context context, List<EventCard> objectList, RecyclerView recyclerView) {
        inflater = LayoutInflater.from(context);
        this.objectList = objectList;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    class EventAdapterHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView date;
        TextView address;
        ImageView moreInfo;
        WebView description;
        ImageView image;
        ProgressBar progressBar;
        LinearLayout llExpandArea;

        public EventAdapterHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_eventName);
            date = (TextView) itemView.findViewById(R.id.event_date);
            address = (TextView) itemView.findViewById(R.id.event_address);
            image = (ImageView) itemView.findViewById(R.id.eventCardImage);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            llExpandArea = (LinearLayout) itemView.findViewById(R.id.llExpandArea);
            moreInfo = (ImageView) itemView.findViewById(R.id.imageButton2);
            description = (WebView) itemView.findViewById(R.id.event_description);

        }
    }

    @Override
    public EventAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_event_card, parent, false);
        EventAdapterHolder holder = new EventAdapterHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final EventAdapterHolder holder, int position) {

        // current card
        current = objectList.get(position);


        holder.title.setText(current.getTitle());
        holder.date.setText(current.getEventStart());
        holder.address.setText(current.getFullAddress());

        Typeface myCustomFont = Typeface.createFromAsset(context.getAssets(), "fonts/grotesk.otf");
        holder.title.setTypeface(myCustomFont);

        //sets up the image loader library
        setupImageLoader();
        executeImageLoader(holder, current.getEventImgURL());


        // Card expander
        final boolean isExpanded = holder.getAdapterPosition() == mExpandedPosition;
        holder.llExpandArea.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.description.loadDataWithBaseURL(null, current.getEventDescription(), "text/html", "UTF-8", null);
        holder.moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : holder.getAdapterPosition();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    TransitionManager.beginDelayedTransition(recyclerView);
                }
                notifyDataSetChanged();
            }
        });
    }

    public void executeImageLoader(final EventAdapterHolder holder, String imageUrl) {

        //create the imageloader object
        ImageLoader imageLoader = ImageLoader.getInstance();

        int defaultImage = context.getResources().getIdentifier("@drawable/noimage", null, context.getPackageName());

        //create display options
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .imageScaleType(ImageScaleType.EXACTLY).build();
        //.showImageOnLoading(defaultImage).build();

        //download and display image from url, add setup progress bar
        imageLoader.displayImage(imageUrl, holder.image, options, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.progressBar.setVisibility(View.GONE);
            }

        });

        // press and hold image to add event to calender
        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Add New Event")
                        .setMessage("Add this event to your calendar?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                launchCalendar(v);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return false;
            }
        });
    }

    public void launchCalendar(View v) {

        String eventStartRaw = current.getEventStart2();
        String eventEnd = current.getEventEnd();
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();


        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date;
        try {
            date = df.parse(eventStartRaw);
            beginTime.setTime(date);
            date = df.parse(eventEnd);
            endTime.setTime(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
//
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, current.getEventTitle())
                //.putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, current.getEventStreetAddress())
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        //.putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");
        //startActivity(intent);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Required for setting up the Universal Image loader Library
     */
    private void setupImageLoader(){
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
    }


}
