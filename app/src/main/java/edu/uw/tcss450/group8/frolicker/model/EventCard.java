package edu.uw.tcss450.group8.frolicker.model;

import java.util.Date;

/**
 * Created by Tim on 5/25/2017.
 */

public class EventCard {

    private String eventImageUrl;
    private String eventTitle;
    private String eventStart;
    private String eventEnd;
    private String eventDescription;
    private String eventCity;
    private String eventLongitude;
    private String eventLatitude;
    private String eventStreetAddress;
    private String monthDay;
    private String eventPrice;

    public EventCard() {

    }


    public String getEventImgURL() {
        return eventImageUrl;
    }

    public String getTitle() {
        return eventTitle;
    }


    public String getEventEnd() {
        return eventEnd;
    }

    public String getEventDescription() {
        return eventDescription;
    }


    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventStart() {
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
    public String getEventStart2() {
        return eventStart;
    }

    public String getEventCity() {
        return eventCity;
    }

    public String getEventLongitude() {
        return eventLongitude;
    }

    public String getEventLatitude() {
        return eventLatitude;
    }

    public String getEventStreetAddress() {
        return eventStreetAddress;
    }

    public String getFullAddress() {
        return eventStreetAddress + ", " + eventCity;

    }

    public void setEventImgURL(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
    }

    public void setEventEnd(String eventEnd) {
        this.eventEnd = eventEnd;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public void setEventCity(String eventCity) {
        this.eventCity = eventCity;
    }

    public void setEventLongitude(String eventLongitude) {
        this.eventLongitude = eventLongitude;
    }

    public void setEventLatitude(String eventLatitude) {
        this.eventLatitude = eventLatitude;
    }

    public void setEventStreetAddress(String eventStreetAddress) {
        this.eventStreetAddress = eventStreetAddress;
    }
}
