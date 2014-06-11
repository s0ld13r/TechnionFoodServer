package com.gmail.technionfoodteam.webservices;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class SearchWebservice extends Application {
	 @Override
	 public synchronized Restlet createInboundRoot() {
		 Router router = new Router(getContext());
		 router.attach("/restaurants",RestaurantsWebService.class);
		 router.attach("/restaurant/{restId}", RestaurantWebService.class);
		 router.attach("/dish/{dishId}", DishWebService.class);
		 router.attach("/dishTypes", DishTypesWebService.class);
		 router.attach("/addDishReview", AddDishReviewWebService.class);
		 router.attach("/addRestaurantReview", AddRestaurantReviewWebService.class);
		 return router;
	 }
}
