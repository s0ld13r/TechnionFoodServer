package com.gmail.technionfoodteam.webservices;

import java.util.LinkedList;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.gmail.technionfoodteam.database.TechnionFoodDb;
import com.gmail.technionfoodteam.model.Dish;
import com.gmail.technionfoodteam.model.Restaurant;


public class RestaurantWebService extends ServerResource {
	@Get
	public JSONObject represent(){
		JSONObject obj = new JSONObject();
		Client serverDispatcher = getContext().getServerDispatcher();
		ServletContext servletContext = (ServletContext)serverDispatcher.getContext().getAttributes()
		                    .get("org.restlet.ext.servlet.ServletContext");
		TechnionFoodDb db = (TechnionFoodDb)servletContext.getAttribute("db");
		try{
			int restId = Integer.parseInt((String) getRequest().getAttributes().get("restId"));
			Restaurant rest = db.getRestaurant(restId);
			if(rest == null){
				return obj;
			}
			obj.put(Restaurant.JSON_OBJECT_NAME, rest.toJSON());
			JSONArray dishes = new JSONArray();
			LinkedList<Dish> listOfDishes = db.getAllRestaurantDishes(restId);
			for(int i=0; i<listOfDishes.size();i++){
				dishes.put(listOfDishes.get(i).toJSON());
			}
			obj.put("dishes", dishes);
			
		}catch(Exception ex){
			System.out.println("Error in Restaurant web service: " + ex.getMessage());
		} 
		return obj;
	}
}
