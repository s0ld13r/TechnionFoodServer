package com.gmail.technionfoodteam;

import java.sql.SQLException;
import java.sql.Time;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.gmail.technionfoodteam.database.TechnionFoodDb;
public class AppInitContextListener implements ServletContextListener {
	public static final String basePathToPictures = "http://ec2-54-72-218-202.eu-west-1.compute.amazonaws.com:8080/t/images/"; 
	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
    	TechnionFoodDb db = TechnionFoodDb.getInstance();
    	context.setAttribute("db", db);
    	try {
    		initDishTypesTable(db);
    		initRestaurants(db);
    	} catch (SQLException e) {
		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }
    public void contextDestroyed(ServletContextEvent event) {
    	ServletContext context = event.getServletContext();
    	TechnionFoodDb db = (TechnionFoodDb)context.getAttribute("db");
    	if(db != null){
    		db.releaseDB();
    	}
    }
    private void initDishTypesTable(TechnionFoodDb db) throws SQLException{
    	db.addDishType("Breakfast");		//dish type #1
		db.addDishType("Dairy dishes");		//dish type #2
    	db.addDishType("Meat dishes");		//dish type #3
    	db.addDishType("Drinks");			//dish type #4
		db.addDishType("Sweets");			//dish type #5
    	db.addDishType("Sales");			//dish type #6
    }
    @SuppressWarnings("deprecation")
	private void initRestaurants(TechnionFoodDb db) throws SQLException{
    	//-----Cafe Cafe----- restaurant #1
		db.addRestaurant("cafe-cafe", "Technion, Student House 3rd floor", 32.776397, 35.022832, 0, "cafecafe.jpg", "0543136555");
		db.addOpeningTime(1, 1, new Time(8, 0, 0), new Time(21, 0, 0));
		db.addOpeningTime(1, 2, new Time(8, 0, 0), new Time(21, 0, 0));
		db.addOpeningTime(1, 3, new Time(8, 0, 0), new Time(21, 0, 0));
		db.addOpeningTime(1, 4, new Time(8, 0, 0), new Time(21, 0, 0));
		db.addOpeningTime(1, 5, new Time(8, 0, 0), new Time(21, 0, 0));
		db.addOpeningTime(1, 6, new Time(9, 0, 0), new Time(13, 30, 0));
		db.addOpeningTime(1, 7, new Time(0, 0, 0), new Time(0, 0, 0));
		
		db.addDish("Muesli", 30, "Granola and friuts with yogurt", 1, 1, "muesli.jpg");		//dish #1
		db.addDishReview(1, "Dana", 3, "good");
		db.addDishReview(1, "Moshe", 4, "very good");
		
		db.addDish("Sandwitch with omelette", 15, "Omelette, cream cheese, tomato and cucumber", 1, 2, "sandwich.jpg");		//dish #2
		db.addDishReview(2, "Dani", 5, "A great sandwich!");
		db.addDishReview(2, "Shay", 3, "Tasty, but too small");
		
		db.addDish("Small coffee", 5, "Small coffee", 1, 4, "coffee.jpg");			//dish #3
		db.addDishReview(3, "Shay", 3, "nice coffee");
		db.addDish("Medium coffee", 8, "Medium coffee", 1, 4, "coffee.jpg");		//dish #4
		db.addDish("Large coffee", 10, "Large coffee", 1, 4, "coffee.jpg");			//dish #5
		db.addDish("Tea", 5, "Variety of flavors", 1, 4, "tea.jpg");				//dish #6
		
		db.addDish("Chocolate balls", 10, "3 Chocolate balls", 1, 5, "chocolate_balls.jpg");	//dish #7
		db.addDishReview(7, "Erez", 5, "Very chocolaty :)");
		
		db.addDish("Israeli salad", 20, "Fresh vegetables, olive oil, tehini and grain bread", 1, 1, "salad.jpg");	//dish #8
		db.addDishReview(8, "Omri", 5, "Very fresh");
		db.addDishReview(8, "Shani", 4, "Tasty");
		
		db.addDish("Shakshuka", 20, "Two eggs, tomato and peppers sauce, grain bread", 1, 1, "shakshuka.jpg");	//dish #9
		db.addDishReview(9, "Erez", 2, "Not as expected");
		db.addDishReview(9, "Shula", 3, "Mine is better");
		
		db.addDish("Pasta roza", 22, "Fresh tomato sauce, basil, parmesan cheese", 1, 2, "pasta_tomatos.jpg");	//dish #10
		db.addDishReview(10, "Shahar", 4, "Great tomato sauce");
		
		db.addDish("Pasta with mushroom", 22, "Cream sauce, maushrooms, basil", 1, 2, "pasta_cream.jpg");	//dish #11
		db.addDishReview(11, "Sergey", 3, "Not enough sauce");
		db.addDishReview(11, "Erez", 2, "Pasta is over cooked");
		
		db.addRestaurantReview(1, "Lior", 2, "Bad service");
		db.addRestaurantReview(1, "Roni", 3, "Bad service but tasty meals");
		db.addRestaurantReview(1, "Shira", 5, "Great service");
		
		//-----Mandarin----- restaurant #2
		db.addRestaurant("Mandarin", "Technion, Ulman Building 3rd floor", 32.777170, 35.023840, 0, "mandarin.jpg","0545882481");

		db.addOpeningTime(2, 1, new Time(7, 0, 0), new Time(20, 0, 0));
		db.addOpeningTime(2, 2, new Time(7, 0, 0), new Time(20, 0, 0));
		db.addOpeningTime(2, 3, new Time(7, 0, 0), new Time(20, 0, 0));
		db.addOpeningTime(2, 4, new Time(7, 0, 0), new Time(20, 0, 0));
		db.addOpeningTime(2, 5, new Time(7, 0, 0), new Time(20, 0, 0));
		db.addOpeningTime(2, 6, new Time(0, 0, 0), new Time(0, 0, 0));
		db.addOpeningTime(2, 7, new Time(0, 0, 0), new Time(0, 0, 0));
		
		db.addDish("Pastries", 12, "Butter/Almonds/Chocolate/Cheese", 2, 1, "croissant.jpg");		//dish #12
		db.addDishReview(12, "Dudu", 5, "Best chocolate croissant!");
		db.addDishReview(12, "Moshiko", 4, "Great almonds pastry");
		db.addDishReview(12, "Nofar", 4, "Very good");
		
		db.addDish("Muesli", 20, "Granola and friuts with yogurt and silan", 2, 1, "muesli.jpg");		//dish #13
		db.addDishReview(13, "Shani", 2, "Not so good");
		
		db.addDish("Shakshuka", 22, "Two eggs on a base of tomato and peppers sauce. Served with bread.", 2, 1, "shakshuka.jpg");	//dish #14
		db.addDishReview(14, "Anna", 5, "A very good shakshuka");
		
		db.addDish("Bagel toast", 20, "With yellow cheese, tomato, onion, olives. Extras available by demand (3nis).", 2, 2, "bagel_toast.jpg");	//dish #15
		db.addDishReview(15, "David", 2, "My toast was burned");
		db.addDishReview(15, "Guy", 3, "Too dry, not enought cheese");
		
		db.addDish("Pizza", 22, "Tomato sauce and mozzarella cheese. Toppings available by demand (3nis).", 2, 2, "pizza.jpg");	//dish #16
		db.addDishReview(16, "Raz", 3, "Nice pizza, not amazing");
		db.addDishReview(16, "Dor", 5, "Great! Like in Italy");
		
		db.addDish("Pasta pomodoro", 24, "Tomatos, basil, olive oil and garlic.", 2, 2, "pasta_tomatos.jpg");	//dish #17
		db.addDishReview(17, "Yaniv", 3, "Good pasta");
		
		db.addDish("Pasta fungi", 24, "Cream, white wine, mushrooms and garlic.", 2, 2, "pasta_cream.jpg");	//dish #18
		db.addDishReview(18, "Liat", 2, "Not so good");
		db.addDishReview(18, "Doron", 1, "Bad");
		
		db.addDish("Small coffee", 5, "Small coffee", 2, 4, "coffee.jpg");			//dish #19
		db.addDishReview(19, "Sapir", 5, "Best coffe in the technion");
		db.addDishReview(19, "Kobi", 5, "Really good coffee");
		db.addDish("Medium coffee", 8, "Medium coffee", 2, 4, "coffee.jpg");		//dish #20
		db.addDish("Large coffee", 10, "Large coffee", 2, 4, "coffee.jpg");			//dish #21
		db.addDish("Tea", 5, "Variety of flavors", 2, 4, "tea.jpg");				//dish #22
		
		//-----Greg----- restaurant #3
		db.addRestaurant("Greg", "Technion, Taub Building 1st floor", 32.777680, 35.021539, 0, "greg.jpg","0540000000");
		db.addOpeningTime(3, 1, new Time(9, 0, 0), new Time(19, 0, 0));
		db.addOpeningTime(3, 2, new Time(9, 0, 0), new Time(19, 0, 0));
		db.addOpeningTime(3, 3, new Time(9, 0, 0), new Time(19, 0, 0));
		db.addOpeningTime(3, 4, new Time(9, 0, 0), new Time(19, 0, 0));
		db.addOpeningTime(3, 5, new Time(9, 0, 0), new Time(19, 0, 0));
		db.addOpeningTime(3, 6, new Time(8, 0, 0), new Time(14, 0, 0));
		db.addOpeningTime(3, 7, new Time(0, 0, 0), new Time(0, 0, 0));
		
		db.addDish("Pastries", 14, "Butter/Almonds/Chocolate/Cheese", 3, 1, "croissant.jpg");		//dish #23
		db.addDishReview(23, "Moran", 2, "Not fresh enough");
		
		db.addDish("Muesli", 20, "Granola and friuts with yogurt", 3, 1, "muesli.jpg");		//dish #24
		db.addDishReview(24, "Shiran", 4, "The yogurt is great");
		db.addDishReview(24, "Keren", 4, "Good, more fruits and it will be perfect");
		
		db.addDish("Bagel toast", 16, "With yellow cheese, tomato, onion, olives. Extras available by demand (3nis).", 3, 2, "bagel_toast.jpg");	//dish #25
		db.addDishReview(25, "Avi", 5, "Really good");
		
		db.addDish("Sandwich", 16, "Omelette/cream cheese/avocado/tuna/salmon", 3, 2, "sandwich.jpg");	//dish #26
		db.addDishReview(26, "Shahar", 3, "Tasty but too small");
		db.addDishReview(26, "Lior", 5, "The salmon sandwich is great");
		db.addDishReview(26, "Hassan", 4, "Good tuna sandwich");
		
		db.addDish("Village salad", 25, "Lettuce, cucumber, tomatoes, carrot, cabbage, purple onion, yam, black lentils. with vinaigrette sauce.", 3, 1, "salad.jpg");	//dish #27
		db.addDishReview(27, "Danny", 4, "Very fresh and tasty");
		
		db.addDish("Halumi salad", 25, "Lettuce, cucumber, tomatoes, cabbage, mushrooms, halumi cheese and wallnuts. with vinaigrette sauce.", 3, 1, "salad.jpg");	//dish #28
		db.addDishReview(28, "Shirly", 5, "Amazing halumi cheese");
		
		db.addDish("Pasta pettucini", 24, "Cream sauce, sweet chilly, yam and green onion.", 3, 2, "pasta_cream.jpg");	//dish #29
		db.addDishReview(29, "Liron", 4, "Very good!");
		
		db.addDish("Pasta fresca", 24, "Tomato sauce, onion, olives, peppers and garlic", 3, 2, "pasta_tomatos.jpg");	//dish #30
		db.addDishReview(30, "Roni", 3, "Tasty but not enough sauce");
		
		db.addDish("Cheese lasagna", 25, "Layers of pasta leaves, cheese, vegetables, tomatos and cream sauce.", 3, 2, "lasagna.jpg");	//dish #31
		db.addDishReview(31, "Daniel", 5, "A very good lasagna!");
		db.addDishReview(31, "Elinor", 5, "Good, the sauce is great");
		
		db.addDish("Pizza", 24, "Tomato sauce and mozzarella cheese. Toppings available by demand (3nis).", 3, 2, "pizza.jpg");	//dish #32
		db.addDishReview(32, "Ran", 4, "A really nice pizza");
		
		db.addDish("Small coffee", 5, "Small coffee", 3, 4, "coffee.jpg");			//dish #33
		db.addDishReview(33, "Omri", 4, "Good coffee");
		db.addDish("Medium coffee", 8, "Medium coffee", 3, 4, "coffee.jpg");		//dish #34
		db.addDish("Large coffee", 10, "Large coffee", 3, 4, "coffee.jpg");			//dish #35
		db.addDish("Tea", 5, "Variety of flavors", 3, 4, "tea.jpg");				//dish #36
		db.addDish("Juice", 10, "Freshly squeezed orange/lemon/grapefruit juice", 3, 4, "juice.jpg");		//dish #37
		
    }
}
