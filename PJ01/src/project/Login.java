package project;

import java.util.ArrayList;

import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.sql.*;

public class Login extends Application{
	//initiallize global variables
	User tem = new User();
	int sceneIndex = 0;
	double OTP;
	char OTPR;
	boolean kill = false;
	private static ArrayList<User> users;

	
	
	public static void setPassword(Scanner scanner, User user) {
		// variables to hold two passwords for comparison - if they are equal then change the password
		String s1;
		String s2;
		
		// collecting input for first and second password attempts
		System.out.println("Hello " + user.username + " , please enter in a password below: ");
		s1 = scanner.nextLine();
		
		System.out.println("Please enter your password again: ");
		s2 = scanner.nextLine();
		
		// if the strings are not equal, call the function again
		if(!s1.equals(s2)) {
			System.out.println("Error, passwords do not match: ");
			setPassword(scanner, user);
		}
		// otherwise, set the password to either string
		else {
			user.password = s1;
			return;
		}
		
	}
	
	
	public static void finishSettingUp(Scanner scanner, User user) {
		System.out.println("Finish setting up your account: ");
		
		// input for email
		System.out.println("Enter email: ");
		user.email = scanner.nextLine();
		
		// getting user inputs for first, middle, last name
		System.out.println("Enter first name: ");
		user.firstName = scanner.nextLine();
		
		System.out.println("Enter middle name: ");
		user.middleName = scanner.nextLine();
		
		System.out.println("Enter last name: ");
		user.lastName = scanner.nextLine();
		
		// preferred name is optional, so first collect input 
		System.out.println("Enter preferred name (optional - leave blank if none): ");
		String temp = scanner.nextLine();
		
		// if it is not empty, then set user's pref name to temp and change boolean
		if(!temp.isEmpty()) {
			user.preferredName = temp;
			user.prefName = true;
		}
		
		
	}
	
	
	public static boolean checkPassword(Scanner scanner, User user) {
		System.out.println("Please Enter Password");
		// storing the input in string s
		String s = scanner.nextLine();
		
		// if s is equal to the suspected username's password, the user is valid
		if(s.equals(user.password)) {
			return true;
		}
		
		// if s is not equal to the suspected password, return false
		else {
			System.out.println("Error, incorrect password");
			return false;
		}
		
	}
	
	// the goal of this function is to check that the inputted line is a single char of a type allowed
	// returns a boolean if the char follows all of required conditions
	public static boolean checkChar(Scanner scanner, String allowed, String input) {
		
		
		// first check if the input is more than one char
		// if it is not, pass the checkChar function again and then return to avoid recursive loops
		if(!input.matches("[A-Za-z]{1}")) {
			System.out.println("Error, input is not a single char");
			return checkChar(scanner, allowed, input);
		}
		
		// a boolean flag to check if the inputted string is in the allowed list of chars
		boolean flag = false;
		
		for(int i = 0;i<allowed.length();i++) {
			if(input.charAt(0) == allowed.charAt(i)) {
				flag = true;
			}
		}
		
		// final check to see if the flag is true - if it is not 
		// then the user has inputted a random char
		if(flag == false) {
			System.out.println("Error, that is not an existing command");
			return checkChar(scanner, allowed, input);
		}
		
		
		return true;
	}
	
	// this function prints a greeting to the user after they log in
	// it also displays their name/username and role
	public static void printWelcome(User user) {
		// if the user has a preferred name, use it
		if(user.prefName) {
			System.out.print("Welcome " + user.preferredName + " | ");
		}
		// if the user does not have a preferred name, do not use it
		else {
			System.out.print("Welcome " + user.username + " | ");
		}
		
		// switch statement to find what role the user is and output their role
		switch(user.currentRole) {
			case 'a':
				System.out.println("Admin");
				return;
			case 's':
				System.out.println("Student");
				return;
			case 'i':
				System.out.println("Instructor");
				return;
			default:
				return;
		
		}
	}
	
	
	
	public static void loginScreen(Scanner scanner) {
		String s;
		
		// loop runs until a username is inputted
		while(true) {
			System.out.println("Enter Username: ");
			s = scanner.nextLine();
			int currentUser;
			
			// check to see if username is in current list of usernames
			boolean match = false;
			for(currentUser = 0;currentUser<users.size();currentUser++) {
				if(s.equals(users.get(currentUser).username)) {
					// if the username is, save what user it belongs to in currentUser integer
					// eventually want to compare saved user's password to inputted password
					match = true;
					break;
				}
			}
			
			if(!match) {
				System.out.println("Username not recognized");
				continue;
			}
			
			// check to see if a match was found
			
			
			// if password is wrong, refresh the login screen.
			if(!checkPassword(scanner, users.get(currentUser))) {
				continue;
			}
			
			// creating a user variable to avoid having to type users.get(currentUser) every time
			User user = users.get(currentUser);
			
			// check what role(s) 
			if(user.roles.size()>1){
				System.out.println("You have multiple roles, enter a letter depending on what role you wish you access the program in.");
				System.out.println("Admin = (a), Student = (s), Instructor = (i)");
				
				// calling checkChar to verify that the user has inputted either a, s, or i
				s = scanner.nextLine();
				checkChar(scanner, "asi", s);
				
				// set user's current role depending on input
				user.currentRole = s.charAt(0);
			}
			// if user only has one role, set their current role to their only one
			else {
				user.currentRole = user.roles.get(0);
				printWelcome(user);
				
			}
			
			
			
		}
		
	}
	

	//Scene logic for going between screens
	public void start(Stage MainScreen) {
		Stage startScreen = new Stage();
		//initial startup screen
			if(sceneIndex == 0) {
				initial(startScreen);
			}
			else if(sceneIndex == 1) {
				login(startScreen);
			}
			else if(sceneIndex == 2) {
				menu(startScreen);
			}
			else if(sceneIndex == 3) {
				adminMenu(startScreen);
			}
			else if(sceneIndex == 4) {
				//userMenu(startScreen);
			}
			else if(sceneIndex == 5) {
				OTPMenu(startScreen);
			}
			else {
				System.out.print("goodbye!");
			}
		
        
    }
	
	
	//very first login screen to establish the 
	public void initial(Stage startScreen) {
    	System.out.println("Starting Login");
    	startScreen.setTitle("Login Screen");
        
        Button btn = new Button();
        btn.setText("Create admin");
        btn.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
                sceneIndex = 1;
                start(startScreen);
                startScreen.close();
            }
        });
        //create the arraylist of users
    	users = new ArrayList<User>();
    	
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        startScreen.setScene(new Scene(root, 300, 250));
        startScreen.show();
	}
	
	//admin login screen for the very first user
	public void login(Stage startScreen) {
		System.out.println("Admin Login");
		startScreen.setTitle("Admin login");
		
		//initialize a grid setup for the windows
		BorderPane bPane = new BorderPane();
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		
		gridPane.setPadding(new Insets(5,5,5,5));
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		bPane.setCenter(gridPane);
		
		//create a new textfield for admin username
		TextField adminUser = new TextField("");
		
		//create a new textfield for admin password
		TextField adminPass = new TextField("");
		
		TextField adminPassConf = new TextField("");
		
		// create a stack pane
        StackPane adminWindow = new StackPane();
        
        // add textfields
        adminWindow.getChildren().add(adminUser);
        adminWindow.getChildren().add(adminPass);
        
        Scene sc = new Scene(bPane, 900, 500);
        
      //Create Labels
        Label User = new Label("Username");
        Label Pass = new Label("Password");
        Label Pass2 = new Label("Password Confirm");
        
        Button back = new Button("Back");
        Button sub = new Button("Submit");
        

        //Add all controls to Grid
        gridPane.add(User, 0, 0);
        gridPane.add(Pass, 0, 1);
        gridPane.add(Pass2, 0, 2);
        gridPane.add(adminUser, 1, 0);
        gridPane.add(adminPass, 1, 1);
        gridPane.add(adminPassConf,1, 2);
        gridPane.add(back, 0, 3);
        gridPane.add(sub, 2, 3);
        
        // set the scene
        startScreen.setScene(sc);
 
        startScreen.show();
		
		Admin firstAdmin = new Admin();
		
		sub.setOnAction(new EventHandler<ActionEvent>()
	    {
	      @Override      
	      //when the submit button is pressed
	      public void handle(ActionEvent e)
	      {
	    	  //check if passwords match and username isnt empty
	        if(adminPass.getText().equals(adminPassConf.getText()) && User != null) {
	        	//set username to text
	    		firstAdmin.username = adminUser.getText();
	    		//set password to text
	    		firstAdmin.password = adminPass.getText();
	    		// adding 2 role (admin) to newly created user
	    		firstAdmin.roles.add('a');
	    		// adding first user to list of usernames in system
	    		users.add(firstAdmin);
	    		// displaying log out message and sending program to loginScreen
	    		System.out.println("New Account Created! Logging you out.");
	    		sceneIndex = 2;
	    		start(startScreen);
                startScreen.close();
	        }
	        else
	        {
	        	System.out.print("Passwords don't match or username is empty!");
	        }
	      }
	    });
		
	}
	
	//main menu screen with login with username or login with code
	public void menu(Stage startScreen) {
		System.out.println("Menu");
		startScreen.setTitle("Menu login");
		
		//initialize a grid setup for the windows
		BorderPane bPane = new BorderPane();
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		
		gridPane.setPadding(new Insets(5,5,5,5));
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		bPane.setCenter(gridPane);
		
		//create a new textfield for admin username
		TextField user = new TextField("");
		
		//create a new textfield for admin password
		TextField pass = new TextField("");
		
		TextField code = new TextField("");
		
		// create a stack pane
        StackPane adminWindow = new StackPane();
        
        // add textfields
        adminWindow.getChildren().add(user);
        adminWindow.getChildren().add(pass);
        adminWindow.getChildren().add(code);
        
        Scene sc = new Scene(bPane, 900, 500);
        
        //Create Labels
        Label User = new Label("Username");
        Label Pass = new Label("Password");
        Label Pass2 = new Label("One Time Code");
        
        Button close = new Button("Close");
        Button sub = new Button("Submit");

        gridPane.add(User, 0, 0);
        gridPane.add(Pass, 0, 1);
        gridPane.add(Pass2, 0, 2);
        gridPane.add(user, 1, 0);
        gridPane.add(pass, 1, 1);
        gridPane.add(code,1, 2);
        gridPane.add(close, 0, 3);
        gridPane.add(sub, 2, 3);
        
        // set the scene
        startScreen.setScene(sc);
 
        startScreen.show();
		
		
		sub.setOnAction(new EventHandler<ActionEvent>()
	    {
	      @Override      
	      //when the submit button is pressed
	      public void handle(ActionEvent e)
	      {
	    	  //checks if user is in array
	    	  for (int i = 0; i < users.size(); i++) {
	    		  //if the user is found
	    		  if(users.get(i).username.equals(user.getText())){
	    			  //check if passwords match
	    			  if(users.get(i).password.equals(pass.getText())) {
	    				  System.out.print(users.get(i).roles.get(0));
	    				  //check if the user is an admin and proceed to admin login
	    				  if(users.get(i).roles.get(0) == 'a')
	    				  {
	    					sceneIndex = 3;
		    				start(startScreen);
		    				startScreen.close();
	    				  }
	    				  //if not an admin go to normal login
	    				  else {
		    				  sceneIndex = 4;
		    				  start(startScreen);
		    				  startScreen.close();
	    				  }
	    			  }
	    		  }
	  	        else if(code.getText().equals(String.valueOf(OTP)) && OTP != 0)
		        {
	  	        	OTP = 0;
		  	        sceneIndex = 5;
	  				start(startScreen);
	  				startScreen.close();
		        }
	    		  else
		    	  {
	    			  //user not found
		    		  System.out.print("Invalid User!");
		    	  }
	    	  }
	      }
	    });
		//close the window
		close.setOnAction((ActionEvent e) ->
	    {
	       // startScreen.close();      
	    }); 
	}
	
	//Admin menu screen
		public void adminMenu(Stage startScreen) {
			System.out.println("Admin Menu");
			startScreen.setTitle("Admin Menu");
			
			
			//initialize a grid setup for the windows
			BorderPane bPane = new BorderPane();
			GridPane gridPane = new GridPane();
			gridPane.setAlignment(Pos.CENTER);
			
			gridPane.setPadding(new Insets(5,5,5,5));
			gridPane.setHgap(10);
			gridPane.setVgap(10);
			bPane.setCenter(gridPane);
			
			//create a new textfield for user select
			TextField user = new TextField("");
			
			//create a new textfield for a OTP
			TextField code = new TextField("");
			
			//create a new textfield for a OTP role
			TextField role = new TextField(" a, s or i");
			
			//create a new textfield for a response
			TextField response = new TextField("");
			
			// create a stack pane
	        StackPane adminWindow = new StackPane();
	        
	        // add textfields
	        adminWindow.getChildren().add(user);
	        adminWindow.getChildren().add(code);
	        adminWindow.getChildren().add(role);
	        
	        Scene sc = new Scene(bPane, 900, 500);
	        
	        //Create Labels
	        Label t = new Label("User Select");
	        Label one = new Label("One Time Code Gen");
	        
	        Button close = new Button("log out");
	        Button gen = new Button("Generate");
	        Button del = new Button("Delete");
	        Button list = new Button("List Users");
	        Button find = new Button("Find");

	        gridPane.add(t, 1, 0);
	        gridPane.add(response, 3, 0);
	        gridPane.add(one, 1, 3);
	        gridPane.add(user, 1, 2);
	        gridPane.add(find, 2, 2);
	        gridPane.add(code,1, 4);
	        gridPane.add(close, 0, 4);
	        gridPane.add(gen, 2, 4);
	        gridPane.add(role, 4, 3);
	        gridPane.add(del, 2, 3);
	        gridPane.add(list, 3, 3);
	        
	        // set the scene
	        startScreen.setScene(sc);
	 
	        startScreen.show();
			
			
			gen.setOnAction(new EventHandler<ActionEvent>()
		    {
		      @Override      
		      //when the generate button is pressed
		      public void handle(ActionEvent e)
		      {
		    	  //set the one time password
		    	  if(role.getText().equals("a") || role.getText().equals("i") || role.getText().equals("s")) {
		    		  System.out.print("otpppppp");
		    	  OTP = Math.random()*1000000;
		    	  code.appendText(String.valueOf(OTP));
		    	  OTPR = role.getText().charAt(0);
		    	  }
		      }
		    });
			//find button
			find.setOnAction((ActionEvent e) ->
		    {
		    	//checks if user is in array
		    	  for (int i = 0; i < users.size(); i++) {
		    		  //if the user is found
		    		  if(users.get(i).username.equals(user.getText())){
		    			  kill = true;
		    			  tem = users.get(i);
		    			  response.appendText("User Found!");
		    		  }
		    	  }
		    }); 
			
			//delete selected user
			del.setOnAction((ActionEvent e) ->
		    {
		        if( kill == true) {
		        	users.remove(users.indexOf(tem));
		        	kill = false;
		        }
		    });
			
			//close the window
			close.setOnAction((ActionEvent e) ->
		    {
		    	sceneIndex = 2;
		    	start(startScreen);
		        startScreen.close();      
		    }); 
			//close the window
			list.setOnAction((ActionEvent e) ->
		    {
		    	for(int i = 0; i < users.size(); i++){
		    		System.out.print(users.get(i).username + " " + users.get(i).roles.get(0) + "\n");
		    	}
		    }); 
		}
		
		//OTP login screen for the very first user
		public void OTPMenu(Stage startScreen) {
			System.out.println("OTP Login");
			startScreen.setTitle("OTP login");
			
			//initialize a grid setup for the windows
			BorderPane bPane = new BorderPane();
			GridPane gridPane = new GridPane();
			gridPane.setAlignment(Pos.CENTER);
			
			gridPane.setPadding(new Insets(5,5,5,5));
			gridPane.setHgap(10);
			gridPane.setVgap(10);
			bPane.setCenter(gridPane);
			
			//create a new textfield for admin username
			TextField uUser = new TextField("");
			
			//create a new textfield for admin password
			TextField uPass = new TextField("");
			
			TextField uPassConf = new TextField("");
			
			// create a stack pane
	        StackPane uWindow = new StackPane();
	        
	        // add textfields
	        uWindow.getChildren().add(uUser);
	        uWindow.getChildren().add(uPass);
	        
	        Scene sc = new Scene(bPane, 900, 500);
	        
	      //Create Labels
	        Label User = new Label("Username");
	        Label Pass = new Label("Password");
	        Label Pass2 = new Label("Password Confirm");
	        
	        Button back = new Button("Back");
	        Button sub = new Button("Submit");
	        

	        //Add all controls to Grid
	        gridPane.add(User, 0, 0);
	        gridPane.add(Pass, 0, 1);
	        gridPane.add(Pass2, 0, 2);
	        gridPane.add(uUser, 1, 0);
	        gridPane.add(uPass, 1, 1);
	        gridPane.add(uPassConf,1, 2);
	        gridPane.add(back, 0, 3);
	        gridPane.add(sub, 2, 3);
	        
	        // set the scene
	        startScreen.setScene(sc);
	 
	        startScreen.show();
			
			User userer = new User();
			
			sub.setOnAction(new EventHandler<ActionEvent>()
		    {
		      @Override      
		      //when the submit button is pressed
		      public void handle(ActionEvent e)
		      {
		    	  //check if passwords match and username isnt empty
		        if(uPass.getText().equals(uPassConf.getText()) && User != null) {
		        	//set username to text
		        	userer.username = uUser.getText();
		    		//set password to text
		        	userer.password = uPass.getText();
		    		// adding the otp role to newly created user
		        	userer.roles.add(OTPR);
		    		// adding first user to list of usernames in system
		    		users.add(userer);
		    		// displaying log out message and sending program to loginScreen
		    		System.out.println("New Account Created! Logging you out.");
		    		sceneIndex = 2;
		    		start(startScreen);
	                startScreen.close();
		        }
		        else
		        {
		        	System.out.print("Passwords don't match or username is empty!");
		        }
		      }
		    });
			
		}
	
	public static void main(String[] args) {
		launch(args);

	}
	
}