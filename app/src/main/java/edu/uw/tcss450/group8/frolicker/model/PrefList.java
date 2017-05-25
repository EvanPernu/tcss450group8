package edu.uw.tcss450.group8.frolicker.model;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class stores the user's preferences, and information on the user.
 * It has the utilites to be constructed from an accordingly formatted JSONObject, and turned into one.
 *
 * @author Evan Pernu
 * @version 5/2/2017
 */
public class PrefList implements Serializable{

    /**
     * Key: a list of event categories
     * Value: how interested the user is in the category
     *
     *     -1 = not interested
     *      0 = impartial
     *      1 = interested
     *
     *This scale may later be changed to become more complicated.
     */
    private Map<String, Integer> mKeywords;

    /**
     * Default constructor
     */
    public PrefList(){
        mKeywords = new HashMap<String, Integer>();
    }

    /**
     * Constructor that takes a predefined keyword map.
     *
     * @param theKeywords a predefined keyword map following the formal of mKeywords
     */
    public PrefList(Map<String, Integer> theKeywords){
        mKeywords = theKeywords;
    }


    /**
     * Converts a JSONObject of PrefList format into an object of type PrefList
     *
     * @param other a JSONObject of PrefList format
     * @return an object of type PrefList
     * @throws JSONException
     */
    public PrefList JSONFactory(JSONObject other) throws JSONException {
        PrefList result = new PrefList();

        //add each key pair to result's internal map
        Iterator<String> keys = other.keys();
        while(keys.hasNext()){
            String key = keys.next();
            result.addKey(key, other.getInt(key));
        }

        return result;
    }

    /**
     * Adds the given String/int pair to the internal map
     *
     * @param keyword The desired keyword
     * @param value The mKeyword's corresponding value
     */
    public void addKey(String keyword, int value){
        mKeywords.put(keyword, value);
    }

    /**
     * Getter for mKeywords
     *
     * @return a deep copy of mKeywords
     */
    public Map<String, Integer> getKeywords(){
        Map<String, Integer> result = new HashMap<String, Integer>();
        for(String key : mKeywords.keySet()){
            result.put(key, mKeywords.get(key));
        }
        return result;
    }
}
