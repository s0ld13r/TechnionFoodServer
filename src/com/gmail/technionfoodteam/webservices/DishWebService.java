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
import com.gmail.technionfoodteam.model.DishReview;

public class DishWebService extends ServerResource {
	@Get
	public JSONObject represent(){
		JSONObject obj = new JSONObject();
		Client serverDispatcher = getContext().getServerDispatcher();
		ServletContext servletContext = (ServletContext)serverDispatcher.getContext().getAttributes()
		                    .get("org.restlet.ext.servlet.ServletContext");
		TechnionFoodDb db = (TechnionFoodDb)servletContext.getAttribute("db");
		try{
			int dishId = Integer.parseInt((String) getRequest().getAttributes().get("dishId"));
			Dish dish = db.getDish(dishId);
			if(dish == null){
				return obj;
			}
			obj.put(Dish.JSON_OBJECT_NAME, dish.toJSON());
			JSONArray dishes = new JSONArray();
			LinkedList<DishReview> dishReviews = db.getAllDishReviews(dishId);
			for(int i=0; i<dishReviews.size();i++){
				dishes.put(dishReviews.get(i).toJSON());
			}
			obj.put("reviews", dishes);
			
		}catch(Exception ex){
			System.out.println("Error in Dish web service: " + ex.getMessage());
		} 
		return obj;
	}
}
