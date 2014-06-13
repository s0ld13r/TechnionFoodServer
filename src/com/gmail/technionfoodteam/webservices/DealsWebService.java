package com.gmail.technionfoodteam.webservices;

import java.util.LinkedList;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.gmail.technionfoodteam.database.TechnionFoodDb;
import com.gmail.technionfoodteam.model.Dish;
import com.gmail.technionfoodteam.model.Restaurant;

public class DealsWebService extends ServerResource {
	@Get
	public JsonRepresentation represent(){
		JSONArray dishes = new JSONArray();
		Client serverDispatcher = getContext().getServerDispatcher();
		ServletContext servletContext = (ServletContext)serverDispatcher.getContext().getAttributes()
		                    .get("org.restlet.ext.servlet.ServletContext");
		TechnionFoodDb db = (TechnionFoodDb)servletContext.getAttribute("db");
		try{
			LinkedList<Dish> listOfDishes = db.getAllDeals();
			for(int i=0; i<listOfDishes.size();i++){
				dishes.put(listOfDishes.get(i).toJSON());
			}			
		}catch(Exception ex){
			System.out.println("Error in Deals web service: " + ex.getMessage());
		} 
		return new JsonRepresentation(dishes);
	}
	
}
