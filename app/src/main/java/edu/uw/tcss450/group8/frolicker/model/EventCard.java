package edu.uw.tcss450.group8.frolicker.model;

import java.util.Date;

/**
 * Created by Tim on 5/25/2017.
 */
public class EventCard {

    private String eventImageUrl;
    private String eventName;
    private String eventStart;
    private String eventEnd;
    private String eventDescription;
    private String eventCity;
    private String eventLongitude;
    private String eventLatitude;
    private String eventStreetAddress;
    private String monthDay;
    private String eventVenue;
    private String eventCategoryId;

    /**
     * Instantiates a new Event card.
     */
    public EventCard() {

    }


    /**
     * Gets event img url.
     *
     * @return the event img url
     */
    public String getEventImgURL() {
        return eventImageUrl;
    }

    /**
     * Gets event end.
     *
     * @return the event end
     */
    public String getEventEnd() {
        return eventEnd;
    }

    /**
     * Gets event description.
     *
     * @return the event description
     */
    public String getEventDescription() {
        return eventDescription;
    }


    /**
     * Gets event name.
     *
     * @return the event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Gets event category id.
     *
     * @return the event category id
     */
    public String getEventCategoryId() {
        return eventCategoryId;
    }

    /**
     * Gets event start.
     *
     * @return the event start
     */
    public String getEventStart() {

        return eventStart = formatEventDate(eventStart);
    }

    /**
     * Gets unformatted event start.
     *
     * @return the unformatted event start
     */
    public String getUnformattedEventStart() {
        return eventStart;
    }

    /**
     * Gets event city.
     *
     * @return the event city
     */
    public String getEventCity() {
        return eventCity;
    }

    /**
     * Gets event longitude.
     *
     * @return the event longitude
     */
    public String getEventLongitude() {
        return eventLongitude;
    }

    /**
     * Gets event latitude.
     *
     * @return the event latitude
     */
    public String getEventLatitude() {
        return eventLatitude;
    }

    /**
     * Gets event street address.
     *
     * @return the event street address
     */
    public String getEventStreetAddress() {
        return eventStreetAddress;
    }

    /**
     * Gets full address.
     *
     * @return the full address
     */
    public String getFullAddress() {
        return eventStreetAddress + ", " + eventCity;

    }

    /**
     * Gets event venue.
     *
     * @return the event venue
     */
    public String getEventVenue() {
        return eventVenue;
    }

    /**
     * Sets event venue.
     *
     * @param eventVenue the event venue
     */
    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    /**
     * Sets event category id.
     *
     * @param eventCategoryId the event category id
     */
    public void setEventCategoryId(String eventCategoryId) {
        this.eventCategoryId = eventCategoryId;
    }

    /**
     * Sets event img url.
     *
     * @param eventImageUrl the event image url
     */
    public void setEventImgURL(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    /**
     * Sets event name.
     *
     * @param eventName the event name
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Sets event start.
     *
     * @param eventStart the event start
     */
    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
    }

    /**
     * Sets event end.
     *
     * @param eventEnd the event end
     */
    public void setEventEnd(String eventEnd) {
        this.eventEnd = eventEnd;
    }

    /**
     * Sets event description.
     *
     * @param eventDescription the event description
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    /**
     * Sets event city.
     *
     * @param eventCity the event city
     */
    public void setEventCity(String eventCity) {
        this.eventCity = eventCity;
    }

    /**
     * Sets event longitude.
     *
     * @param eventLongitude the event longitude
     */
    public void setEventLongitude(String eventLongitude) {
        this.eventLongitude = eventLongitude;
    }

    /**
     * Sets event latitude.
     *
     * @param eventLatitude the event latitude
     */
    public void setEventLatitude(String eventLatitude) {
        this.eventLatitude = eventLatitude;
    }

    /**
     * Sets event street address.
     *
     * @param eventStreetAddress the event street address
     */
    public void setEventStreetAddress(String eventStreetAddress) {
        this.eventStreetAddress = eventStreetAddress;
    }

    /**
     *
     * @param eventStart
     * @return
     */
    private String formatEventDate(String eventStart) {
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date;
        try {
            date = df.parse(eventStart);
            java.text.SimpleDateFormat sdfmonth = new java.text.SimpleDateFormat("MM/dd");
            monthDay = sdfmonth.format(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return monthDay;
    }
}
