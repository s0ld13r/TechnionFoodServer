package com.gmail.technionfoodteam.webservices;

import java.util.LinkedList;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.gmail.technionfoodteam.database.TechnionFoodDb;
import com.gmail.technionfoodteam.model.Restaurant;

public class RestaurantsWebService extends ServerResource {
	@Get
	public JsonRepresentation represent(){
		Client serverDispatcher = getContext().getServerDispatcher();
		ServletContext servletContext = (ServletContext)serverDispatcher.getContext().getAttributes()
		                    .get("org.restlet.ext.servlet.ServletContext");
		TechnionFoodDb db = (TechnionFoodDb)servletContext.getAttribute("db");
		LinkedList<Restaurant> listOfRestaurants =db.getAllRestaurants();
		JSONArray array = new JSONArray();
		try {
			for(int i=0; i<listOfRestaurants.size();i++){
				JSONObject obj = new JSONObject();
				Restaurant restaurant = listOfRestaurants.get(i);
				if(restaurant!=null){
					obj = restaurant.toJSON();
				}
				array.put(obj);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new JsonRepresentation(array);
	}
}
