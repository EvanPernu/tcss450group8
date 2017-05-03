package model;
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
    private Map<String, Integer> keywords;

    /**
     * Default constructor
     */
    public PrefList(){
        keywords = new HashMap<String, Integer>();
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
     * @param value The keyword's corresponding value
     */
    public void addKey(String keyword, int value){
        keywords.put(keyword, value);
    }

    /**
     * Getter for keywords
     *
     * @return a deep copy of keywords
     */
    public Map<String, Integer> getKeywords(){
        Map<String, Integer> result = new HashMap<String, Integer>();
        for(String key : keywords.keySet()){
            result.put(key, keywords.get(key));
        }
        return result;
    }
}
