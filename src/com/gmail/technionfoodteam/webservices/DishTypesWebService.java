package com.gmail.technionfoodteam.webservices;

import java.util.LinkedList;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.restlet.Client;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.gmail.technionfoodteam.database.TechnionFoodDb;
import com.gmail.technionfoodteam.model.IntStringPair;

public class DishTypesWebService extends ServerResource {
	@Get
	public JsonRepresentation represent(){
		Client serverDispatcher = getContext().getServerDispatcher();
		ServletContext servletContext = (ServletContext)serverDispatcher.getContext().getAttributes()
		                    .get("org.restlet.ext.servlet.ServletContext");
		TechnionFoodDb db = (TechnionFoodDb)servletContext.getAttribute("db");
		LinkedList<IntStringPair> listOfTypes =db.getTypesValues();
		JSONArray array = new JSONArray();
		try {
			for(int i=0; i<listOfTypes.size();i++){
				IntStringPair pair = listOfTypes.get(i);
				if(pair != null){
					array.put(pair.toJSON());
				}
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new JsonRepresentation(array);
	}
}
