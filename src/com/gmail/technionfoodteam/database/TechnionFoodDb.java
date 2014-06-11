package com.gmail.technionfoodteam.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.LinkedList;

import com.gmail.technionfoodteam.model.Dish;
import com.gmail.technionfoodteam.model.DishReview;
import com.gmail.technionfoodteam.model.IntStringPair;
import com.gmail.technionfoodteam.model.Restaurant;
import com.gmail.technionfoodteam.model.RestaurantReview;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
public class TechnionFoodDb {
	private final String TBL_RESTAURANT = "restaurant_tbl";
	private final String TBL_DISH_TYPES = "dish_types_tbl";
	private final String TBL_DISHES = "dishes_tbl";
	private final String TBL_REST_REVIEWS = "restaurant_reviews_tbl";
	private final String TBL_DISH_REVIEWS = "dish_reviews_tbl";
	private final String TBL_OPENING_HOURS="opening_hours_tbl";
	private final String VIEW_RESTAURANT = "restaurant_view";
	private final String VIEW_DISHES= "dishes_view";
	private final String VIEW_DISHES_EXT= "dishes_view_ext";
	
	
	
	private final String REST_ID = "id";
	private final String REST_NAME = "name";
	private final String REST_ADDRESS = "addr";
	private final String REST_LNG = "lng";
	private final String REST_LAT = "lat";
	private final String REST_PICTURE = "logo";
	private final String REST_NUM_OF_REVIEWS = "numberOfReviews";
	private final String REST_SUM_OF_REVIEWS = "sumOfReviews";
	
	private final String DISH_ID="id";
	private final String DISH_NAME = "name";
	private final String DISH_PRICE = "price";
	private final String DISH_DESCRIPTION = "description";
	private final String DISH_REST_ID = "rest_id";
	private final String DISH_TYPE = "dish_type";
	private final String DISH_PICTURE = "logo";
	private final String DISH_NUM_OF_REVIEWS = "numberOfReviews";
	private final String DISH_SUM_OF_REVIEWS = "sumOfReviews";			
	
	private final String OH_REST_ID="rest_id";
	private final String OH_DAY="day";
	private final String OH_START_TIME = "start_time";
	private final String OH_END_TIME = "end_time";
	private final String OH_PK="oh_primary_key";

	private final String RR_ID="id";
	private final String RR_REST_ID="rest_id";
	private final String RR_USERNAME = "username";
	private final String RR_RANKING = "ranking";
	private final String RR_DESCRIPTION="description";
	
	private final String DR_ID ="id";
	private final String DR_DISH_ID ="dish_id";
	private final String DR_USERNAME ="username";
	private final String DR_RANKING ="ranking";
	private final String DR_DESCRIPTION ="description";
	
	private final String DT_ID="id";
	private final String DT_NAME="name";
	
	private BoneCP connectionPool;
	private final Integer basicStringColumnSize = 255;
	private final Integer contentSize = 1023;
	private final String url="jdbc:mysql://localhost:3306/";
	private final String dbName="technionfood";
	private final String userName = "root";
	private final String password ="1234";
	private Connection constructorOnlyDBConnection; // Constructor use only
	private static final TechnionFoodDb singleton = new TechnionFoodDb();
    public static TechnionFoodDb getInstance() {
        return singleton;
    }

	private TechnionFoodDb(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			constructorOnlyDBConnection = DriverManager.getConnection(url,userName,password);
			createDataBase();
			constructorOnlyDBConnection.close();
			BoneCPConfig config = new BoneCPConfig();
			config.setJdbcUrl(url+dbName);
			config.setUsername(userName);
			config.setPassword(password);
			config.setMinConnectionsPerPartition(5);
			config.setMaxConnectionsPerPartition(10);
			config.setPartitionCount(3);
			connectionPool = new BoneCP(config);
		} catch (Exception e) {
			System.err.println("Fatal error. The exception was thrown "
					+ "during database initialization: " + e);
		}		
	}
	public void releaseDB(){
		connectionPool.shutdown();
	}

/*
 * -----------------------------------------------------------------
 *|							 HELPERS								| 
 * ----------------------------------------------------------------- 
 * */
	private Connection getAutoCommitConnection() throws SQLException{
		Connection con = connectionPool.getConnection();
		con.setAutoCommit(true);
		return con;
	}
	/*private Connection getNotAutoCommitConnection() throws SQLException{
		Connection con = connectionPool.getConnection();
		con.setAutoCommit(false);
		return con;
	}*/

	private void dropTable(String tableName) throws SQLException{
		Statement stmt;
		stmt = constructorOnlyDBConnection.createStatement();
		stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName);
	}
	private void dropView(String view) throws SQLException{
		Statement stmt;
		stmt = constructorOnlyDBConnection.createStatement();
		stmt.executeUpdate("DROP VIEW IF EXISTS " + view);		
	}
	private void createDataBase() throws SQLException{
		Statement stmt;
		stmt = constructorOnlyDBConnection.createStatement();
		
		stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
		constructorOnlyDBConnection.close();
		constructorOnlyDBConnection = DriverManager.getConnection(url+dbName,userName,password);
		dropTable(TBL_DISH_REVIEWS);
		dropTable(TBL_DISHES);
		dropTable(TBL_DISH_TYPES);
		dropTable(TBL_REST_REVIEWS);
		dropTable(TBL_OPENING_HOURS);
		dropTable(TBL_RESTAURANT);
		dropView(VIEW_RESTAURANT);
		dropView(VIEW_DISHES);
		dropView(VIEW_DISHES_EXT);
		createRestaurantsTable();
		createRestaurantReviewTable();
		createOpeningHoursTable();
		createDisheTypesTable();
		createDishesTable();
		createDishReviewTable();
		createRestaurantsView();
		createDishesView();
		createDishesViewExt();
	}
	private void createRestaurantsView() throws SQLException{
		Statement stmt;
		stmt = constructorOnlyDBConnection.createStatement();
		stmt.executeUpdate(" CREATE OR REPLACE VIEW "+VIEW_RESTAURANT+" AS " + 
				"SELECT " + TBL_RESTAURANT+".* , count("+TBL_REST_REVIEWS+"."+RR_RANKING+") AS "+REST_NUM_OF_REVIEWS+", sum("+TBL_REST_REVIEWS+"."+RR_RANKING+") AS "+REST_SUM_OF_REVIEWS+" "
				+ " FROM "+TBL_RESTAURANT+" LEFT OUTER JOIN  "+TBL_REST_REVIEWS+" "
				+ " ON "+TBL_RESTAURANT+"."+ REST_ID+" = "+TBL_REST_REVIEWS+"."+RR_REST_ID+" "
				+ "GROUP BY "+TBL_RESTAURANT+"."+ REST_ID);
	}
	private void createDishesView() throws SQLException{
		Statement stmt;
		stmt = constructorOnlyDBConnection.createStatement();
		stmt.executeUpdate(" CREATE OR REPLACE VIEW "+VIEW_DISHES+" AS " + 
				"SELECT " + TBL_DISHES+".* , count("+TBL_DISH_REVIEWS+"."+DR_RANKING+") AS "+DISH_NUM_OF_REVIEWS+", sum("+TBL_DISH_REVIEWS+"."+DR_RANKING+") AS "+DISH_SUM_OF_REVIEWS+" "
				+ " FROM "+TBL_DISHES+" LEFT OUTER JOIN  "+TBL_DISH_REVIEWS+" "
				+ " ON "+TBL_DISHES+"."+ DISH_ID+" = "+TBL_DISH_REVIEWS+"."+DR_DISH_ID+" "
				+ "GROUP BY "+TBL_DISHES+"."+ DISH_ID);
	}
	private void createDishesViewExt() throws SQLException{
		Statement stmt;
		stmt = constructorOnlyDBConnection.createStatement();
		stmt.executeUpdate(" CREATE OR REPLACE VIEW "+VIEW_DISHES_EXT+" AS " + 
				"SELECT dishes_view.*, restaurant_tbl.name AS rest_name FROM dishes_view, restaurant_tbl where dishes_view.rest_id = restaurant_tbl.id");
	}
	private void createRestaurantsTable() throws SQLException{
		Statement stmt;
		stmt = constructorOnlyDBConnection.createStatement();
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+ TBL_RESTAURANT+" ( "
				+ REST_ID + " int NOT NULL AUTO_INCREMENT, "
				+ REST_NAME + " VARCHAR(" + basicStringColumnSize + ") NOT NULL, "
				+ REST_ADDRESS + " VARCHAR(" + basicStringColumnSize + ") NOT NULL, "
				+ REST_LAT + " DOUBLE, "
				+ REST_LNG + " DOUBLE, "
				+ REST_PICTURE + " VARCHAR(" + basicStringColumnSize + ") , "
				+ "PRIMARY KEY("+REST_ID+")"
				+ ")");
	}
	private void createDishesTable() throws SQLException{
		Statement stmt;
		stmt = constructorOnlyDBConnection.createStatement();
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+ TBL_DISHES+" ( "
				+ DISH_ID + " int NOT NULL AUTO_INCREMENT, "
				+ DISH_NAME + " VARCHAR(" + basicStringColumnSize + ") NOT NULL, "
				+ DISH_PRICE + " DOUBLE, "
				+ DISH_DESCRIPTION + " VARCHAR(" + contentSize + ") NOT NULL, "
				+ DISH_REST_ID + " int not null, "
				+ DISH_TYPE + " int not null , "
				+ DISH_PICTURE + " VARCHAR(" + basicStringColumnSize + ") , "
				+ "PRIMARY KEY("+DISH_ID+") , "
				+" FOREIGN KEY ("+DISH_REST_ID+") REFERENCES "+TBL_RESTAURANT+"("+REST_ID+") , "
				+" FOREIGN KEY ("+DISH_TYPE+") REFERENCES "+TBL_DISH_TYPES+"("+DT_ID+")"
				+ ")");
	}
	private void createOpeningHoursTable() throws SQLException{
		Statement stmt;
		stmt = constructorOnlyDBConnection.createStatement();
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+ TBL_OPENING_HOURS+" ( "
				+ OH_REST_ID + " int NOT NULL , "
				+ OH_DAY + " int NOT NULL , "
				+ OH_START_TIME + " TIME NOT NULL , "
				+ OH_END_TIME + " TIME NOT NULL , "			
				+ " CONSTRAINT "+ OH_PK+" PRIMARY KEY ("+OH_REST_ID + "," + OH_DAY+","+ OH_START_TIME+") , "
				+" FOREIGN KEY ("+OH_REST_ID+") REFERENCES "+TBL_RESTAURANT+"("+REST_ID+")"
				+ ")");
	}
	private void createRestaurantReviewTable() throws SQLException{
		Statement stmt;
		stmt = constructorOnlyDBConnection.createStatement();
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+ TBL_REST_REVIEWS+" ( "
				+ RR_ID + " int NOT NULL AUTO_INCREMENT, "
				+ RR_REST_ID + " int NOT NULL, "
				+ RR_USERNAME + " VARCHAR(" + basicStringColumnSize + ") , "
				+ RR_RANKING + " DOUBLE, "
				+ RR_DESCRIPTION + " VARCHAR(" + contentSize + ") , "
				+ " PRIMARY KEY("+RR_ID+") , "
				+ " FOREIGN KEY ("+RR_REST_ID+") REFERENCES "+TBL_RESTAURANT+"("+REST_ID+")"
				+ ")");
	}
	private void createDishReviewTable() throws SQLException{
		Statement stmt;
		stmt = constructorOnlyDBConnection.createStatement();
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+ TBL_DISH_REVIEWS+" ( "
				+ DR_ID + " int NOT NULL AUTO_INCREMENT, "
				+ DR_DISH_ID + " int NOT NULL, "
				+ DR_USERNAME + " VARCHAR(" + basicStringColumnSize + ") , "
				+ DR_RANKING + " DOUBLE, "
				+ DR_DESCRIPTION + " VARCHAR(" + contentSize + ") , "
				+ " PRIMARY KEY("+DR_ID+") , "
				+ " FOREIGN KEY ("+DR_DISH_ID+") REFERENCES "+TBL_DISHES+"("+DISH_ID+")"
				+ ")");
	}
	private void createDisheTypesTable() throws SQLException{
		Statement stmt;
		stmt = constructorOnlyDBConnection.createStatement();
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+ TBL_DISH_TYPES+" ( "
				+ DT_ID + " int NOT NULL AUTO_INCREMENT, "
				+ DT_NAME + " VARCHAR(" + basicStringColumnSize + ") , "
				+ " PRIMARY KEY("+DT_ID+") "
				+ ")");
	}
	
	public boolean addRestaurant(String name, String address, double lat, double lng,
			double ranking, String path) throws SQLException{
		Connection con = getAutoCommitConnection();
		boolean res = true;
		try{
			PreparedStatement stmt = con.prepareStatement("INSERT INTO "+TBL_RESTAURANT+" VALUES"
					+ "(?,?,?,?,?,?)");
			stmt.setInt(1, 0);
			stmt.setString(2, name);
			stmt.setString(3, address);
			stmt.setDouble(4, lat);
			stmt.setDouble(5, lng);
			stmt.setString(6, path);
			stmt.executeUpdate();
		}
		catch(SQLException ex){
			res = false;
			System.err.println("Error during inserting new restaurant: \n" + ex.getMessage());
		}finally{
			con.close();
		}
		return res;
	}
	public boolean addDish(String name, double price, String description,
			int restId, int dishType,String path) throws SQLException{
		Connection con = getAutoCommitConnection();
		boolean res = true;
		try{
			PreparedStatement stmt = con.prepareStatement("INSERT INTO "+TBL_DISHES+" VALUES"
					+ "(?,?,?,?,?,?,?)");
			stmt.setInt(1, 0);
			stmt.setString(2, name);
			stmt.setDouble(3, price);
			stmt.setString(4, description);
			stmt.setInt(5, restId);
			stmt.setInt(6, dishType);
			stmt.setString(7, path);
			stmt.executeUpdate();
		}
		catch(SQLException ex){
			res = false;
			System.err.println("Error during inserting new dish: \n" + ex.getMessage());
		}finally{
			con.close();
		}
		return res;
	}
	public boolean addDishReview(int dishId, String username, double rating,
			String description) throws SQLException{
		Connection con = getAutoCommitConnection();
		boolean res = true;
		try{
			PreparedStatement stmt = con.prepareStatement("INSERT INTO "+TBL_DISH_REVIEWS+" VALUES"
					+ "(?,?,?,?,?)");
			stmt.setInt(1, 0);
			stmt.setInt(2, dishId);
			stmt.setString(3, username);
			stmt.setDouble(4, rating);
			stmt.setString(5, description);
			stmt.executeUpdate();
		}
		catch(SQLException ex){
			res = false;
			System.err.println("Error during inserting new dish review: \n" + ex.getMessage());
		}finally{
			con.close();
		}
		return res;
	}
	public boolean addRestaurantReview(int restId, String username, double rating,
			String description) throws SQLException{
		Connection con = getAutoCommitConnection();
		boolean res = true;
		try{
			PreparedStatement stmt = con.prepareStatement("INSERT INTO "+TBL_REST_REVIEWS+" VALUES"
					+ "(?,?,?,?,?)");
			stmt.setInt(1, 0);
			stmt.setInt(2, restId);
			stmt.setString(3, username);
			stmt.setDouble(4, rating);
			stmt.setString(5, description);
			stmt.executeUpdate();
		}
		catch(SQLException ex){
			res = false;
			System.err.println("Error during inserting new restaurant review: \n" + ex.getMessage());
		}finally{
			con.close();
		}
		return res;
	}
	public boolean addOpeningTime(int restId, int day, Time startTime, Time endTime) throws SQLException{
		Connection con = getAutoCommitConnection();
		boolean res = true;
		try{
			PreparedStatement stmt = con.prepareStatement("INSERT INTO "+TBL_OPENING_HOURS+" VALUES"
					+ "(?,?,?,?)");
			stmt.setInt(1, restId);
			stmt.setInt(2, day);
			stmt.setTime(3, startTime);
			stmt.setTime(4, endTime);
			stmt.executeUpdate();
		}
		catch(SQLException ex){
			res = false;
			System.err.println("Error during inserting opening times: \n" + ex.getMessage());
		}finally{
			con.close();
		}
		return res;
	}
	public boolean addDishType(String typeName) throws SQLException{
		Connection con = getAutoCommitConnection();
		boolean res = true;
		try{
			PreparedStatement stmt = con.prepareStatement("INSERT INTO "+TBL_DISH_TYPES+" VALUES"
					+ "(?,?)");
			stmt.setInt(1, 0);
			stmt.setString(2, typeName);
			stmt.executeUpdate();
		}
		catch(SQLException ex){
			res = false;
			System.err.println("Error during inserting dish type \n" + ex.getMessage());
		}finally{
			con.close();
		}
		return res;
	}
	public LinkedList<Restaurant> getAllRestaurants(){
		LinkedList<Restaurant> res = new LinkedList<Restaurant>();
		Connection con = null;
		try {
			con = getAutoCommitConnection();
			//PreparedStatement stmt = con.prepareStatement("SELECT * FROM " +TBL_RESTAURANT);
			PreparedStatement stmt = con.prepareStatement(
					"SELECT * FROM "+VIEW_RESTAURANT);
			ResultSet queryRes = stmt.executeQuery();
			while(queryRes.next()){
				int devider = queryRes.getInt(REST_NUM_OF_REVIEWS);
				double ranking = 0;
				if(devider!= 0){
					ranking = ((double)queryRes.getInt(REST_SUM_OF_REVIEWS)) / devider;
				}
				Restaurant restaurant = new Restaurant(queryRes.getInt(REST_ID),
						queryRes.getString(REST_NAME), queryRes.getString(REST_ADDRESS),
						queryRes.getDouble(REST_LAT), queryRes.getDouble(REST_LNG), 
						ranking, queryRes.getString(REST_PICTURE));
				res.add(restaurant);
			}
		}catch(Exception ex){
			System.out.println("Error during creating restaurant list:\n" + ex.getMessage());
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return res;
	}
	public Restaurant getRestaurant(int restId){
		Restaurant restaurant = null;
		Connection con = null;
		try {
			con = getAutoCommitConnection();
			PreparedStatement stmt = con.prepareStatement(
					"SELECT * FROM " +VIEW_RESTAURANT + 
					" WHERE " + REST_ID + " = " + restId);
			ResultSet queryRes = stmt.executeQuery();

			if(queryRes.first()){
				int devider = queryRes.getInt(REST_NUM_OF_REVIEWS);
				double ranking = 0;
				if(devider!= 0){
					ranking = ((double)queryRes.getInt(REST_SUM_OF_REVIEWS)) / devider;
				}
				restaurant = new Restaurant(queryRes.getInt(REST_ID),
						queryRes.getString(REST_NAME), queryRes.getString(REST_ADDRESS),
						queryRes.getDouble(REST_LAT), queryRes.getDouble(REST_LNG), 
						ranking, queryRes.getString(REST_PICTURE));
			}
		}catch(Exception ex){
			System.out.println("Error during extrating resturant with id: "+ restId + " from db: " + ex.getMessage());
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return restaurant;
	}
	public Dish getDish(int dishId){
		Dish dish = null;
		Connection con = null;
		try {
			con = getAutoCommitConnection();
			PreparedStatement stmt = con.prepareStatement(
					"SELECT * FROM " +VIEW_DISHES_EXT + 
					" WHERE " + DISH_ID + " = " + dishId);
			ResultSet queryRes = stmt.executeQuery();
			if(queryRes.first()){
				int devider = queryRes.getInt(DISH_NUM_OF_REVIEWS);
				double ranking = 0;
				if(devider!= 0){
					ranking = ((double)queryRes.getInt(DISH_SUM_OF_REVIEWS)) / devider;
				}
				dish = new Dish(queryRes.getInt(DISH_ID), queryRes.getString(DISH_NAME),
						queryRes.getDouble(DISH_PRICE), queryRes.getString(DISH_DESCRIPTION),
						queryRes.getInt(DISH_REST_ID), queryRes.getInt(DISH_TYPE), ranking,
						queryRes.getString(DISH_PICTURE), queryRes.getString("rest_name"));
			}
		}catch(Exception ex){
			System.out.println("Error during extrating dish with id: "+ dishId + " from db: " + ex.getMessage());
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dish;
	}
	public LinkedList<Dish> getAllRestaurantDishes(int restId){
		LinkedList<Dish> resDishes = new LinkedList<Dish>();
		Connection con = null;
		try {
			con = getAutoCommitConnection();
			PreparedStatement stmt = con.prepareStatement(
					"SELECT * FROM " +VIEW_DISHES_EXT + 
					" WHERE " + DISH_REST_ID + " = ?" );
			stmt.setInt(1, restId);
			ResultSet queryRes = stmt.executeQuery();
			while(queryRes.next()){
				int devider = queryRes.getInt(DISH_NUM_OF_REVIEWS);
				double ranking = 0;
				if(devider!= 0){
					ranking = ((double)queryRes.getInt(DISH_SUM_OF_REVIEWS)) / devider;
				}
				Dish dish = new Dish(queryRes.getInt(DISH_ID),
						queryRes.getString(DISH_NAME), queryRes.getDouble(DISH_PRICE),
						queryRes.getString(DISH_DESCRIPTION), queryRes.getInt(DISH_REST_ID),
						queryRes.getInt(DISH_TYPE),ranking, queryRes.getString(DISH_PICTURE), queryRes.getString("rest_name"));
				resDishes.add(dish);
			}
		}catch(Exception ex){
			System.out.println("Error during creating restaurant dishes list:\n" + ex.getMessage());
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return resDishes;
	}
	public LinkedList<DishReview> getAllDishReviews(int dishId){
		LinkedList<DishReview> listOfReviews = new LinkedList<DishReview>();
		Connection con = null;
		try {
			con = getAutoCommitConnection();
			PreparedStatement stmt = con.prepareStatement(
					"SELECT * FROM " +TBL_DISH_REVIEWS + 
					" WHERE " + DR_DISH_ID + " = ?" );
			stmt.setInt(1, dishId);
			ResultSet queryRes = stmt.executeQuery();
			while(queryRes.next()){
				DishReview review = new DishReview(queryRes.getInt(DR_DISH_ID),
						queryRes.getString(DR_USERNAME), queryRes.getDouble(DR_RANKING),
						queryRes.getString(DR_DESCRIPTION));
				listOfReviews.add(review);
			}
		}catch(Exception ex){
			System.out.println("Error during creating dishe reviews list:\n" + ex.getMessage());
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return listOfReviews;
	}
	public LinkedList<RestaurantReview> getAllRestaurantReviews(int restId){
		LinkedList<RestaurantReview> listOfReviews = new LinkedList<RestaurantReview>();
		Connection con = null;
		try {
			con = getAutoCommitConnection();
			PreparedStatement stmt = con.prepareStatement(
					"SELECT * FROM " +TBL_REST_REVIEWS + 
					" WHERE " + RR_REST_ID + " = ?" );
			stmt.setInt(1, restId);
			ResultSet queryRes = stmt.executeQuery();
			while(queryRes.next()){
				RestaurantReview review = new RestaurantReview(queryRes.getInt(RR_REST_ID),
						queryRes.getString(RR_USERNAME), queryRes.getDouble(RR_RANKING),
						queryRes.getString(RR_DESCRIPTION));
				listOfReviews.add(review);
			}
		}catch(Exception ex){
			System.out.println("Error during creating dishe reviews list:\n" + ex.getMessage());
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return listOfReviews;
	}
	public LinkedList<IntStringPair> getTypesValues(){
		LinkedList<IntStringPair> res = new LinkedList<IntStringPair>();
		Connection con = null;
		try {
			con = getAutoCommitConnection();
			PreparedStatement stmt = con.prepareStatement(
					"SELECT * FROM " +TBL_DISH_TYPES);
			ResultSet queryRes = stmt.executeQuery();
			while(queryRes.next()){
				res.add(new IntStringPair(queryRes.getInt(DT_ID), queryRes.getString(DT_NAME)));
			}
			
		}catch(Exception ex){
			System.out.println("Error during getting list of dish types:\n" + ex.getMessage());
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return res;
	}
}
