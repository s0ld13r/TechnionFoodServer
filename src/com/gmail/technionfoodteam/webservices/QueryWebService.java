package com.gmail.technionfoodteam.webservices;



import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.gmail.technionfoodteam.database.TechnionFoodDb;
import com.gmail.technionfoodteam.model.DayOpeningHours;
import com.gmail.technionfoodteam.model.Dish;
import com.gmail.technionfoodteam.model.Restaurant;

public class QueryWebService extends ServerResource{
	public static final String JSON_DISTANCE = "max_distance";
	public static final String JSON_TIME = "max_time";
	public static final String JSON_PRICE = "max_price";	
	public static final String JSON_TYPES = "types";
	public static final String JSON_LAT = "current_lat";
	public static final String JSON_LNG = "current_lng";
	@Post("json")
	public Representation acceptAndReturnJson(JsonRepresentation entity) {
		Client serverDispatcher = getContext().getServerDispatcher();
		JSONObject obj = new JSONObject();
		JSONArray resArr = new JSONArray();
		ServletContext servletContext = (ServletContext)serverDispatcher.getContext().getAttributes()
		                    .get("org.restlet.ext.servlet.ServletContext");
		TechnionFoodDb db = (TechnionFoodDb)servletContext.getAttribute("db");
	    JSONObject json = null;
	    try {
	    	LinkedList<Dish> listOfProperDishes = new LinkedList<Dish>();
	        json = entity.getJsonObject();
	        // first of all have to get list of all appropriate Restaurants
	        LinkedList<Restaurant> listOfAllRestaurants = db.getAllRestaurants();
	        LinkedList<Restaurant> listOfProperRestaurants = new LinkedList<Restaurant>();
	        int maxDistance = json.getInt(JSON_DISTANCE);
	        long maxTime = json.getLong(JSON_TIME);
	        /*if there no restaurants limit*/
	        listOfProperRestaurants = listOfAllRestaurants;
	        if((maxDistance == -1) && (maxTime == -1)){
	        	listOfProperRestaurants = listOfAllRestaurants;
	        }else{
	        	LinkedList<Restaurant> temp = new LinkedList<Restaurant>();
	        	if(maxDistance > -1){
	        		/*we have to add to temp only those which distance is less than maxDist*/
	        		double lat = json.getDouble(JSON_LAT);
	        		double lng = json.getDouble(JSON_LNG);
	        		for(Restaurant rest : listOfAllRestaurants){
	        			double dist = distFrom(lat, lng, rest.getLat(), rest.getLng());
	        			if(dist < maxDistance){
	        				temp.add(rest);
	        			}
	        		}
	        		listOfProperRestaurants = temp;
	        		temp = new LinkedList<Restaurant>();
	        	}
	        	/*time filtering*/
	        	if(maxTime > -1){
	        		Time time = new Time(maxTime);
	        		int hoursToAdd = time.getHours();
	        		int minutesToAdd = time.getMinutes();
	        		Calendar now = Calendar.getInstance();
	        		now.setTime(new Date());
	        		Calendar requestedTime = Calendar.getInstance();    
	        		requestedTime.setTime(new Date());
	        		requestedTime.add(Calendar.HOUR_OF_DAY, hoursToAdd);
	        		requestedTime.add(Calendar.MINUTE, minutesToAdd);
	        		
	        		for(Restaurant rest :listOfProperRestaurants){
	        			DayOpeningHours dayOpeningHours = db.getRestautantsOpeningHoursAtDay(rest.getId(),now.get(Calendar.DAY_OF_WEEK));
	        			Calendar startTodayTime = Calendar.getInstance();    
		        		startTodayTime.setTime(new Date());
		        		startTodayTime.set(Calendar.HOUR_OF_DAY,dayOpeningHours.getStartTime().getHours());
		        		startTodayTime.set(Calendar.MINUTE,dayOpeningHours.getStartTime().getMinutes());
		        		
		        		Calendar endTodayTime = Calendar.getInstance();    
		        		endTodayTime.setTime(new Date());
		        		endTodayTime.set(Calendar.HOUR_OF_DAY,dayOpeningHours.getEndTime().getHours());
		        		endTodayTime.set(Calendar.MINUTE,dayOpeningHours.getEndTime().getMinutes());
		        		
	        			if((requestedTime.before(endTodayTime)) && (requestedTime.after(startTodayTime))){
	        				temp.add(rest);
	        			}
	        		}
	        		listOfProperRestaurants = temp;
	        		temp = new LinkedList<Restaurant>();
	        	}
	        }
	    	double maxPrice = json.getDouble(JSON_PRICE);
	    	JSONArray arr = json.getJSONArray(JSON_TYPES);
	    	
	    	if(arr.getInt(0) == 0){
	    		listOfProperDishes = db.getAllQueryDishes(listOfProperRestaurants, null, maxPrice);
	    	}else{
	    		LinkedList<Integer> dishTypesList = new LinkedList<Integer>();
		    	for(int i =0; i<arr.length();i++){
		    		dishTypesList.add(arr.getInt(i));
		    	}
		    	listOfProperDishes = db.getAllQueryDishes(listOfProperRestaurants, dishTypesList, maxPrice);
	    	}
	    	
	    	for(Dish dish : listOfProperDishes){
	    		
	    		int restId = dish.getRestaurantId();
	    		for(Restaurant rest : listOfAllRestaurants){
	    			if(rest.getId() == restId){
	    				dish.setRestLat(rest.getLat());
	    				dish.setRestLng(rest.getLng());
	    				break;
	    			}
	    		}
	    		JSONObject dishObj = dish.toJSON();
	    		resArr.put(dishObj);
	    	}
	        
	        //have to get all dishes from appropriate resturants according max price and type
	        
	    } catch (Exception e) {
	        setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        try {
				obj.put("error", e.getMessage());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        return new JsonRepresentation(obj);
	    } 
	    return new JsonRepresentation(resArr);
	}
	public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
	    double earthRadius = 6371000;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);
	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    return dist;
	    }
}