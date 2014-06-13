package com.gmail.technionfoodteam.webservices;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.gmail.technionfoodteam.database.TechnionFoodDb;
import com.gmail.technionfoodteam.model.DishReview;
import com.gmail.technionfoodteam.model.Review;


public class AddDishReviewWebService extends ServerResource{
	@Post("json")
	public Representation acceptAndReturnJson(JsonRepresentation entity) {
		Client serverDispatcher = getContext().getServerDispatcher();
		JSONObject obj = new JSONObject();
		ServletContext servletContext = (ServletContext)serverDispatcher.getContext().getAttributes()
		                    .get("org.restlet.ext.servlet.ServletContext");
		TechnionFoodDb db = (TechnionFoodDb)servletContext.getAttribute("db");
	    JSONObject json = null;
	    try {
	        json = entity.getJsonObject();
	        DishReview rev = (new DishReview()).fromJSON(json);
	        if(!db.addDishReview(rev.getDishId(), rev.getUsername(), rev.getRanking(), rev.getDescription())){
	        	setStatus(Status.SERVER_ERROR_INTERNAL);
	        }
	        obj.put(DishReview.JSON_RANKING, db.getDish(json.getInt(DishReview.JSON_DISH_ID)).getRanking());
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
	    return new JsonRepresentation(obj);
	}
}
