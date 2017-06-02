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
 * The EventAdapter takes event data from Event objects and puts it into a View
 *
 * @author Tim Weaver
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<EventCard> eventCardList;
    private LayoutInflater inflater;
    private Context context;
    private int mExpandedPosition = -1;
    private RecyclerView recyclerView;
    private EventCard currentEventCard;

    /**
     * Instantiates a new Event adapter.
     *
     * @param context       the context
     * @param eventCardList the list of events
     * @param recyclerView  the recycler view
     */
    public EventAdapter(Context context, List<EventCard> eventCardList, RecyclerView recyclerView) {
        inflater = LayoutInflater.from(context);
        this.eventCardList = eventCardList;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    /**
     *  A custom ViewHolder that stores event data for binding
     *  the contents of views.
     */
    class EventViewHolder extends RecyclerView.ViewHolder {

        private TextView eventName;
        private TextView eventDate;
        private TextView eventAddress;
        private ImageView moreInfoButton;
        private WebView eventDescription;
        private ImageView eventImage;
        private ProgressBar progressBar;
        private LinearLayout llExpandArea;

        /**
         * Instantiates a new Event view holder.
         *
         * @param itemView the item view
         */
        public EventViewHolder(View itemView) {
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

    /**
     *  Creates a new ViewHolder to hold Event items
     *
     * @param parent
     * @param viewType
     * @return a ViewHolder
     */
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.fragment_event_card, parent, false);
        EventViewHolder holder = new EventViewHolder(view);

        return holder;
    }

    /**
     *  Displays event data at specified position
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position) {

        currentEventCard = eventCardList.get(position);

        holder.eventName.setText(currentEventCard.getEventName());
        holder.eventDate.setText(currentEventCard.getEventStart());
        holder.eventAddress.setText(currentEventCard.getFullAddress());

        setupImageLoader();
        runImageLoader(holder, currentEventCard.getEventImgURL());
        setupCalendarLoader(holder);
        setupCardExpander(holder);
    }

    /**
     * Initializes card view expansion.
     *
     * @param holder the Event ViewHolder
     */
    private void setupCardExpander(final EventViewHolder holder) {

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

    /**
     * Starts the Universal Image Loader for loading and displaying
     * event images.
     *
     * @param holder the Event ViewHolder
     * @param imageUrl the URL of the image
     */
    private void runImageLoader(final EventViewHolder holder, String imageUrl) {

        ImageLoader imageLoader = ImageLoader.getInstance();
        int defaultImage = context.getResources().getIdentifier("@drawable/noimage", null,
                context.getPackageName());

        //display options
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .imageScaleType(ImageScaleType.EXACTLY).build();

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

    /**
     * Creates a listener for accessing the calendar.
     *
     * @param holder the Event ViewHolder
     */
    private void setupCalendarLoader(final EventViewHolder holder) {

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

    /**
     *  Helper method for displaying the Calendar.
     */
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
                .putExtra(CalendarContract.Events.CALENDAR_ACCESS_LEVEL, CalendarContract.Events.CAL_ACCESS_OWNER)
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
     * Helper required for setting up the Universal Image loader Library.
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

    /**
     * Gets the number of Events in the data set.
     *
     * @return the number of stored events
     */
    @Override
    public int getItemCount() {
        if(eventCardList != null) {
            return eventCardList.size();
        }else{
            //fixes a bug where the app would crash upon a search returning nothing
            return 0;
        }
    }

    /**
     * Gets the row id associated with the Event.
     *
     * @param position the position of the Event in the adapter
     * @return the id of the Event at the specified position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }
}
