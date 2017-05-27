package edu.uw.tcss450.group8.frolicker.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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

    private List<EventCard> eventCardList;
    private LayoutInflater inflater;
    private Context context;
    private int mExpandedPosition = -1;
    private RecyclerView recyclerView;
    private EventCard currentEventCard;


    public EventAdapter(Context context, List<EventCard> eventCardList, RecyclerView recyclerView) {
        inflater = LayoutInflater.from(context);
        this.eventCardList = eventCardList;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    class EventAdapterHolder extends RecyclerView.ViewHolder {

        TextView eventName;
        TextView eventDate;
        TextView eventAddress;
        ImageView moreInfoButton;
        WebView eventDescription;
        ImageView eventImage;
        ProgressBar progressBar;
        LinearLayout llExpandArea;

        public EventAdapterHolder(View itemView) {
            super(itemView);

            eventName = (TextView) itemView.findViewById(R.id.tv_event_name);
            eventDate = (TextView) itemView.findViewById(R.id.tv_event_date);
            eventAddress = (TextView) itemView.findViewById(R.id.event_address);
            eventImage = (ImageView) itemView.findViewById(R.id.event_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
            llExpandArea = (LinearLayout) itemView.findViewById(R.id.llExpandArea);
            moreInfoButton = (ImageView) itemView.findViewById(R.id.more_info_button);
            eventDescription = (WebView) itemView.findViewById(R.id.event_description);
        }
    }

    @Override
    public EventAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view;

        View view = inflater.inflate(R.layout.fragment_event_card, parent, false);
        EventAdapterHolder holder = new EventAdapterHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(final EventAdapterHolder holder, int position) {

        currentEventCard = eventCardList.get(position);


        holder.eventName.setText(currentEventCard.getEventName());
        holder.eventDate.setText(currentEventCard.getEventStart());
        holder.eventAddress.setText(currentEventCard.getFullAddress());

//        Typeface myCustomFont = Typeface.createFromAsset(context.getAssets(), "fonts/grotesk.otf");
//        holder.eventName.setTypeface(myCustomFont);

        setupImageLoader();
        runImageLoader(holder, currentEventCard.getEventImgURL());
        setupCalendarLoader(holder);
        setupCardExpander(holder);
    }

    private void setupCardExpander(final EventAdapterHolder holder) {

        final boolean isExpanded = holder.getAdapterPosition() == mExpandedPosition;
        holder.llExpandArea.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.eventDescription.loadDataWithBaseURL(null, currentEventCard.getEventDescription()
                , "text/html", "UTF-8", null);
        holder.moreInfoButton.setOnClickListener(new View.OnClickListener() {
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

    private void runImageLoader(final EventAdapterHolder holder, String imageUrl) {

        ImageLoader imageLoader = ImageLoader.getInstance();
        int defaultImage = context.getResources().getIdentifier("@drawable/noimage", null,
                context.getPackageName());

        //display options
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .imageScaleType(ImageScaleType.EXACTLY).build();
        //.showImageOnLoading(defaultImage).build();

        //download and display image from url, add setup progress bar
        imageLoader.displayImage(imageUrl, holder.eventImage, options,
                new SimpleImageLoadingListener() {

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
    }

    private void setupCalendarLoader(final EventAdapterHolder holder) {

        // press and hold image to add event to calender
        holder.eventImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Add New Event")
                        .setMessage("Add this event to your calendar?")
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                launchCalendar();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return false;
            }
        });

    }

    private void launchCalendar() {

        String eventStart = currentEventCard.getUnformattedEventStart();
        String eventEnd = currentEventCard.getEventEnd();
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();

        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date;
        try {
            date = df.parse(eventStart);
            beginTime.setTime(date);
            date = df.parse(eventEnd);
            endTime.setTime(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, currentEventCard.getEventName())
                .putExtra(CalendarContract.Events.EVENT_LOCATION,
                        currentEventCard.getEventStreetAddress())
                .putExtra(CalendarContract.Events.AVAILABILITY,
                        CalendarContract.Events.AVAILABILITY_BUSY);
        context.startActivity(intent);
    }

    /**
     * Required for setting up the Universal Image loader Library
     */
    private void setupImageLoader() {
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


    @Override
    public int getItemCount() {
        return eventCardList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
