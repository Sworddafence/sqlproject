/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Arrays;


/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class GameRental {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.

   public String Auth;
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of GameRental store
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public GameRental(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end GameRental

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      int b = stmt.executeUpdate (sql);
      //System.out.println(b);
      // close the instruction
      stmt.close ();
   }//end executeUpdate

    public String getAuth() {
        return this.Auth;
    }

    // Setter method for 'auth'
    public void setAuth(String auth) {
        this.Auth = auth;
    }

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            GameRental.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      GameRental esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the GameRental object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new GameRental (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              esql.setAuth(authorisedUser);
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Profile");
                System.out.println("2. Update Profile");
                System.out.println("3. View Catalog");
                System.out.println("4. Place Rental Order");
                System.out.println("5. View Full Rental Order History");
                System.out.println("6. View Past 5 Rental Orders");
                System.out.println("7. View Rental Order Information");
                System.out.println("8. View Tracking Information");

                //the following functionalities basically used by employees & managers
                System.out.println("9. Update Tracking Information");

                //the following functionalities basically used by managers
                System.out.println("10. Update Catalog");
                System.out.println("11. Update User");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewProfile(esql); break;
                   case 2: updateProfile(esql); break;
                   case 3: viewCatalog(esql); break;
                   case 4: placeOrder(esql); break;
                   case 5: viewAllOrders(esql); break;
                   case 6: viewRecentOrders(esql); break;
                   case 7: viewOrderInfo(esql); break;
                   case 8: viewTrackingInfo(esql); break;
                   case 9: updateTrackingInfo(esql); break;
                   case 10: updateCatalog(esql); break;
                   case 11: updateUser(esql); break;



                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   //read string just reads string
   public static String readString(String prompt) {
      String input;
      do {
          System.out.print(prompt);
          try {
              input = in.readLine();
              break;
          } catch (Exception e) {
              System.out.println("Error reading input: " + e.getMessage());
          }
      } while (true);
      return input;
  }
   
  //print
   public static void print(String prompt){
      System.out.println(prompt);
      return;
   }

   public static List<List<String>> easyRet(String input, GameRental esql){
      List<List<String>> t = null;
      try {
         t = esql.executeQueryAndReturnResult(input);
      } catch (SQLException e) {
         System.out.println("Error executing SQL: " + e.getMessage());
      }
      return t;
  }

  public static void easyPrint(String input, GameRental esql){
      List<List<String>> t = null;
      try {
         t = esql.executeQueryAndReturnResult(input);
      } catch (SQLException e) {
         System.out.println("Error executing SQL: " + e.getMessage());
      }
      for (List<String> row : t) {
         for (String cell : row) {
             System.out.printf("%-60s", cell);
         }
         System.out.println();
     }
  }

  public static void smallEasyPrint(List<List<String>> t){
   for (List<String> row : t) {
      for (String cell : row) {
          System.out.printf("%-30s", cell);
      }
      System.out.println();
  }
}

   public static void callUpdate(String input, GameRental esql) {
      try {
          esql.executeUpdate(input);
      } catch (SQLException e) {
          System.out.println("Error executing SQL: " + e.getMessage());
      }
  }

  /*
    * Creates a new user
    **/
   public static void CreateUser(GameRental esql){
      System.out.print("Please type in your username: ");
      
      String username;
      do {
         try {
            username = in.readLine();
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue; 
         }
      }while(true);
      String pwd;
      System.out.print("Please type in your password: ");
      do {
         try {
            pwd = in.readLine();
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue; 
         }
      }while(true);

      String phonenum;
      System.out.print("Please type in your phonenum: ");
      do {
         try {
            phonenum = in.readLine();
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue; 
         }
      }while(true);
      
    
   
      String input = "INSERT INTO users (login, password, role, favgames, phonenum, numoverduegames) VALUES (\'" + username + "\', \'" + pwd + "\', \'customer\', \'\', \'" + phonenum + "\', 0)"; 
      try {
         esql.executeUpdate(input);
      } catch (SQLException e) {
         System.out.println("Error executing SQL: " + e.getMessage());
         return;
      }
      print("User Created");

   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(GameRental esql){
      
      System.out.print("Please type in your username: ");
      String username;
      do {
         try {
            username = in.readLine();
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue; 
         }
      }while(true);
      String pwd;
      System.out.print("Please type in your password: ");
      do {
         try {
            pwd = in.readLine();
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue; 
         }
      }while(true);
      
      String input = "select * from users where users.login = \'" + username + "\' AND users.password = \'" + pwd +"\'";
      List<List<String>> temp;
      try {

         temp = esql.executeQueryAndReturnResult(input);

         if(!temp.isEmpty()){
            System.out.println("Login Successful!");
            return username;
         }else{
            System.out.println("Login Failed");
            return null;
         }

      } catch (SQLException e) {
         System.out.println("Error executing SQL: " + e.getMessage());
      }
   return null;
   }//end

// Rest of the functions definition go in here

   public static void viewProfile(GameRental esql) {

      System.out.println("Favorite games\n-----------");
      String input = "select favgames from users where users.login = \'" + esql.getAuth() + "\'";
      
      List<List<String>> t = easyRet(input, esql);
      for (String element : t.get(0)) {
         System.out.println(element);
      }

      System.out.print("Phone Number - "); 
      input = "select phonenum from users where users.login = \'" + esql.getAuth() + "\'";
      print(easyRet(input, esql).get(0).get(0));
      System.out.print("Number of over due Games - "); 
      input = "select numoverduegames from users where users.login = \'" + esql.getAuth() + "\'";
      print(easyRet(input, esql).get(0).get(0));

   
   }
   public static void updateProfile(GameRental esql) {

      
      System.out.print("UPDATE PROFILE\n--------------\n1. Update your game list\n2. Change your password\n");
      switch(readChoice()){
         case 1:
            System.out.print("1. Add to your game list\n2. delete a game from your game list\n"); 
            switch(readChoice()){
               case 1:
                  String input1 = "select favgames from users where users.login = \'" + esql.getAuth() + "\'"; 
                  List<List<String>> temp1;
                  try {
                     temp1 = esql.executeQueryAndReturnResult(input1);
                  } catch (SQLException e) {
                     System.out.println("Error executing SQL: " + e.getMessage());
                     return;
                  }

                  String commaexist = ", ";
                  System.out.println(temp1.get(0).get(0));
                  if(temp1.get(0).get(0).equals("")){
                     commaexist = "";
                     //System.out.println("hello?"); 
                  }
                  System.out.print("Type in the name of the game that you would like to add: ");
                  String gameName1;
                  do {
                     try {
                        gameName1 = in.readLine();
                        break;
                     }catch (Exception e) {
                        System.out.println("Your input is invalid!");
                        continue; 
                     }
                  }while(true);


                  input1 = "SELECT gamename FROM catalog WHERE gameName ILIKE \'" + gameName1 + "\'";

                  try {
                     temp1 = esql.executeQueryAndReturnResult(input1);
                  } catch (SQLException e) {
                     System.out.println("Error executing SQL: " + e.getMessage());
                     return;
                  }
                  if(temp1.isEmpty()){
                     System.out.println("Error game given does not exist in our catalog.");
                     return;
                  }

                  gameName1 = temp1.get(0).get(0);

                  //System.out.println(gameName);

                  input1 = "UPDATE users SET favgames = CONCAT(favgames, \'" + commaexist + gameName1 + "\') WHERE login = \'" + esql.getAuth() + "\'";
               
                  try {
                     esql.executeUpdate(input1);
                  } catch (SQLException e) {
                     System.out.println("Error executing SQL: " + e.getMessage());
                     return;
                  }
                  System.out.println("Profile updated sucuessfully"); 
                  break;
               case 2:
                  String input = "select favgames from users where users.login = \'" + esql.getAuth() + "\'"; 
                  List<List<String>> temp;
                  List<List<String>> tempcheck = new ArrayList<>();
                  List<String> useless = new ArrayList<>();
                  tempcheck.add(useless);
                  
                  try {
                     temp = esql.executeQueryAndReturnResult(input);
                  } catch (SQLException e) {
                     System.out.println("Error executing SQL: " + e.getMessage());
                     return;
                  }

                  System.out.println(temp.get(0).get(0));
                  if(temp.get(0).get(0).equals("")){
                     commaexist = "";
                     System.out.println("You have no games in your favorite games list to delete");
                     return; 
                  }
                  
                  String[] gamesArray = temp.get(0).get(0).split(", ", -2);
                  System.out.println("FAVORITE GAMES LIST\n----------------\n");
                  for (String a : gamesArray)
                     System.out.println(a);


                  System.out.print("Type in the name of the game that you would like to delete: ");
                  String gameName;
                  do {
                     try {
                        gameName = in.readLine();
                        break;
                     }catch (Exception e) {
                        System.out.println("Your input is invalid!");
                        continue; 
                     }
                  }while(true);


                  input = "SELECT gamename FROM catalog WHERE gameName ILIKE \'" + gameName + "\'";

                  try {
                     temp = esql.executeQueryAndReturnResult(input);
                  } catch (SQLException e) {
                     System.out.println("Error executing SQL: " + e.getMessage());
                     return;
                  }
                  if(temp.isEmpty()){
                     System.out.println("Error game given does not exist in our catalog.");
                     return;
                  }
                  boolean found = false;

                  gameName = temp.get(0).get(0);
                  for (String str : gamesArray) {
                     if (str.equals(gameName)) {
                         found = true;
                         break;
                     }
                  }
                  if(!found){
                     System.out.println("Game is not in your favorite games list");
                     return;
                  }
                  

                  List<String> templist = new ArrayList<String>(Arrays.asList(gamesArray));
                  templist.remove(gameName);
                  gamesArray = templist.toArray(new String[0]);



                  String newfav = String.join(", ", gamesArray);

                  input = "UPDATE users SET favgames = \'"+ newfav +"\' WHERE login = \'"+ esql.getAuth() + "\'";
               
                  try {
                     esql.executeUpdate(input);
                  } catch (SQLException e) {
                     System.out.println("Error executing SQL: " + e.getMessage());
                     return;
                  }
                  System.out.println("Profile updated sucuessfully"); 
                  break;
               }
            break;
         case 2:   
            String pwd = readString("Please type your current password\n"); 

            String input2 = "select * from users where users.login = \'" + esql.getAuth() + "\' AND users.password = \'" + pwd +"\'";
            List<List<String>> temp3;
            try {
               temp3 = esql.executeQueryAndReturnResult(input2);
               if(temp3.isEmpty()){
                  System.out.println("Wrong Password");
                  return;
               }
            } catch (SQLException e) {
               System.out.println("Error executing SQL: " + e.getMessage());
            }

            String newpwd = readString("Please type your new password\n");
            callUpdate("UPDATE users SET password = \'" + newpwd + "\' WHERE login = \'" + esql.getAuth() + "\';", esql);
            print("Sucuessfully Updated Password");
      
      }

   }

      
   public static void viewCatalog(GameRental esql) {
      print("\nWould you like to search by\n---------------------\n1. Genre\n2. Price");
      switch(readChoice()){
         case 1:
            String genre = readString("What genre would you like to search: ");
            String input1 =  "SELECT gameName, price, gameid FROM catalog WHERE LOWER(genre) ILIKE LOWER(\'" + genre + "\')";
            print("\nWould you like to search by\n---------------------\n1. Ascending Price\n2. Descending Price\n"); 
            
            String orderby1 = null;
            switch(readChoice()){
               case 1:
                  orderby1 = "ORDER BY price";
                  break;
               case 2:
                  orderby1 = "ORDER BY price DESC";
                  break;
            }
            System.out.println("Name                                                        Price\n-----------------------------------------------------------------\n");
            input1 = input1 + orderby1;
            easyPrint(input1, esql);

            break;

         case 2:
            String genre2 = readString("What is the highest price you want to see: ");
            String input2 =  "SELECT gameName, price, gameid FROM catalog WHERE price < " + genre2 + "";
            print("\nWould you like to search by\n---------------------\n1. Ascending Price\n2. Descending Price\n"); 
            
            String orderby2 = null;
            switch(readChoice()){
               case 1:
                  orderby2 = "ORDER BY price";
                  break;
               case 2:
                  orderby2 = "ORDER BY price DESC";
                  break;
            }
            System.out.println("Name                                                        Price                                                        GameID\n--------------------------------------------------------------------------------------------------------------------------------\n");
            input2 = input2 + orderby2;
            easyPrint(input2, esql);

            break;
      }


   }
   public static void placeOrder(GameRental esql) {
      String isorder = "";
      float totalprice = 0;
      int totalnumgames = 0;
      
      List<List<String>> L = easyRet("SELECT total_count from newrentalorderid", esql);

      int newRentalNum = Integer.parseInt(L.get(0).get(0));
      newRentalNum++;

      List<String> gamesordered = new ArrayList<>();
      List<Integer> numordered = new ArrayList<>();
//
      List<List<String>> temp2 = new ArrayList<>();
      while(!isorder.equals("q")){
      String g = "";
      boolean correct = false;
         while(!correct){
            g = readString("Input the gameID that you would like to rent: ");
            String input = "SELECT gameid FROM catalog WHERE gameid = \'" + g + "\'";
            
            try {
               temp2 = esql.executeQueryAndReturnResult(input);
               if(!temp2.isEmpty()){
                  correct = true;
               }else{
                  System.out.println("Invalid gameID");
               }
            } catch (SQLException e) {
               System.out.println("Error executing SQL: " + e.getMessage());
            }
         }
         gamesordered.add(temp2.get(0).get(0));
         //List<List<String>> t = easyRet(input, esql);

         System.out.print("How many copies do you want for: ");

         int numcopies;
         // returns only if a correct value is given.
         do {
            try { // read the integer, parse it and break.
               numcopies = Integer.parseInt(in.readLine());
               break;
            }catch (Exception e) {
               System.out.println("Your input is invalid!");
               continue;
            }//end try
         }while (true);


         List<List<String>> t = easyRet("select price from catalog where catalog.gameid = \'" + g + "\'", esql);
         float onePrice = new Float(t.get(0).get(0));
         totalprice = totalprice + ((float)numcopies*onePrice);

         //rounds the total price
         totalprice = (float) (Math.round(totalprice * 100.0) / 100.0);

         numordered.add(numcopies);
         totalnumgames = totalnumgames + numcopies;
         print("You're total price is: $" + totalprice);
         print("Total number of games: " + totalnumgames);
         
         isorder = readString("Press q to continue to checkout, Enter to continue adding games:  ");
      }

      print("You have ordered " + totalnumgames + " games, totaling $" + totalprice + "\n");
      String yn = readString("Are you sure you want to order? Press y:  ");
      if(!yn.equals("y")){
         return;
      }
      temp2 = easyRet("SELECT DATE_TRUNC('second', CURRENT_TIMESTAMP AT TIME ZONE 'UTC')", esql);
      String curdate = temp2.get(0).get(0);
      temp2 = easyRet("SELECT DATE_TRUNC('day', CURRENT_DATE + INTERVAL '1 month')", esql);
      String dueDate = temp2.get(0).get(0);
      String input = "INSERT INTO RentalOrder (rentalorderid, login, noofgames, totalprice, ordertimestamp, duedate) VALUES ('gamerentalorder" + newRentalNum + "', '" + esql.getAuth() + "', " + totalnumgames + ", " + totalprice + ", '" + curdate + "', '" + dueDate + "')";

      try {
         esql.executeUpdate(input);
      } catch (SQLException e) {
         System.out.println("Error executing SQL: " + e.getMessage());
      }

      for (int j = 0; j < gamesordered.size(); j++) {
         input = "INSERT INTO gamesinorder (rentalorderid, gameid, unitsordered) VALUES ('gamerentalorder" + newRentalNum +"', '" + gamesordered.get(j) + "', "+ numordered.get(j) +")";
         //print(input);
         try {
            esql.executeUpdate(input);
         } catch (SQLException e) {
            System.out.println("Error executing SQL: " + e.getMessage());
         }
      }
      input = "INSERT INTO trackinginfo (trackingid, rentalorderid, status, currentlocation, couriername, lastupdatedate, additionalcomments) VALUES ('trackingid" + newRentalNum + "', 'gamerentalorder"+ newRentalNum +"', 'Order Recieved', 'Unknown', 'Unknown', '" + curdate + "', '')";
      try {
         esql.executeUpdate(input);
      } catch (SQLException e) {
         System.out.println("Error executing SQL: " + e.getMessage());
      }


      //}

   }
   public static void viewAllOrders(GameRental esql) {
      print("-------------\nAll your Rental IDs\n-------------");
      easyPrint("SELECT rentalorderid FROM RentalOrder WHERE login = '" + esql.getAuth() + "'", esql);
      print("");
      return;
   }
   public static void viewRecentOrders(GameRental esql) {
      print("-------------\nMost Recent Rental IDs\n-------------");
      easyPrint("SELECT rentalorderid FROM RentalOrder WHERE login = '" + esql.getAuth() + "' ORDER BY ordertimestamp DESC LIMIT 5", esql);
      print("");
      return;
   }
   public static void viewOrderInfo(GameRental esql) {
      String b = readString("Enter the Rental Id that you would like to view: ");
      //List<List<String>> temp = easyRet("SELECT t.trackingid, r.ordertimestamp, r.duedate, r.totalprice FROM rentalorder r, trackinginfo t WHERE r.rentalorderid = t.rentalorderid AND r.login = '"+esql.getAuth()+"' AND r.rentalorderid = '"+ b +"'", esql);
      
      String ii = "SELECT t.trackingid, r.ordertimestamp, r.duedate, r.totalprice FROM rentalorder r, trackinginfo t WHERE r.rentalorderid = '" + b + "' AND r.rentalorderid = t.rentalorderid AND (r.login = '" + esql.getAuth() + "' OR EXISTS (SELECT 1 FROM users WHERE login = '" + esql.getAuth() + "' AND role = 'manager'))";
      List<List<String>> temp = easyRet(ii,esql);

      
      if(temp.get(0).isEmpty()){
         print("Invalid Rental Id/You do not have permission to view this rental ID");
         return;
      }
      System.out.print("Tracking ID - " + temp.get(0).get(0));
      print("");
      System.out.print("Order Date - " + temp.get(0).get(1));
      print("");
      System.out.print("Due Date - " + temp.get(0).get(2));
      print("");
      System.out.print("Total price - $" + temp.get(0).get(3));
      print("");
      System.out.print("List of games - ");
      temp = easyRet("SELECT c.gamename FROM catalog c, rentalorder r, gamesinorder g WHERE r.rentalorderid = g.rentalorderid AND g.gameid = c.gameid AND r.rentalorderid = '" + b + "'", esql);
      for (List<String> game: temp){
         System.out.print(game.get(0) + " ");
      }
      print("\n");
      return;
   }
   public static void viewTrackingInfo(GameRental esql) {
      String b = readString("Enter tracking ID that you want to view: ");
      List<List<String>> temp = easyRet("SELECT t.courierName, r.rentalOrderID, t.currentLocation, t.status, t.lastupdatedate, t.additionalComments  FROM rentalorder r, trackinginfo t WHERE r.rentalorderid = t.rentalorderid AND (r.login = '" + esql.getAuth() + "' OR EXISTS (SELECT 1 FROM users WHERE login = '" + esql.getAuth() + "' AND role = 'manager'))  AND t.trackingid = '" + b + "'", esql);

      if(temp.isEmpty()){
         print("Wrong Tracking ID/You do not have access to view this file");
         return;
      }

      print("Courier Name                 Rental Order Id                Current Locaation             Status                        Last Updated Date             Additional Comments");
      smallEasyPrint(temp);
      return;
   }
   public static void updateTrackingInfo(GameRental esql) {
      List<List<String>> temp = easyRet("SELECT * FROM users WHERE role IN ('employee', 'manager') AND login = '" + esql.getAuth() + "'", esql);

      if(temp.isEmpty()){
         print("You are not authorized to update tracking information");
         return;
      }

      String TID = readString("Enter the tracking ID that you would like to update: ");

      print("\n----------CHOOSE WHAT TO CHANGE----------\n1. currentLocation\n2. courierName\n3. status\n5. additionalComments");
      String change = "";
      switch(readChoice()){
         case 1: change = "currentlocation";
                 break;
         case 2: change = "couriername";
                 break;
         case 3: change = "status";
                 break;
         case 4: change = "additionalComments";
                 break;
         default: System.out.println("Invalid choice!");
                 break;
      }
      String whatGoes = readString("Please enter what you would like to change it to: ");


      String input = "UPDATE trackinginfo SET " + change + " = '" + whatGoes + "' WHERE trackingid = '" + TID + "'";
      
      try {
         esql.executeUpdate(input);
      } catch (SQLException e) {
         System.out.println("Error executing SQL: " + e.getMessage());
         return;
      }
      print("Sucuessfully Changed!");
      return;
   }
   public static void updateCatalog(GameRental esql) {
      List<List<String>> temp = easyRet("SELECT * FROM users WHERE role IN ('manager') AND login = '" + esql.getAuth() + "'", esql);

      if(temp.isEmpty()){
         print("You are not authorized to update tracking information");
         return;
      }

      String TID = readString("Enter the Game ID that you would like to update: ");

      print("\n----------CHOOSE WHAT TO CHANGE----------\n1. gamename\n2. genre\n3. price\n4. description\n5. imageurl");
      String change = "";
      switch(readChoice()){
         case 1: 
             change = "gamename";
             break;
         case 2: 
             change = "genre";
             break;
         case 3: 
             change = "price";
             break;
         case 4: 
             change = "description";
             break;
         case 5: 
             change = "imageurl";
             break;
         default:
             System.out.println("Invalid choice!");
     }
      String whatGoes = readString("Please enter what you would like to change it to: ");

      String input = "UPDATE catalog SET " + change + " = '" + whatGoes + "' WHERE gameid = '" + TID + "'";
      
      try {
         esql.executeUpdate(input);
      } catch (SQLException e) {
         System.out.println("Error executing SQL: " + e.getMessage());
         return;
      }
      print("Sucuessfully Changed!");
      return;
   }
   public static void updateUser(GameRental esql) {

      List<List<String>> temp = easyRet("SELECT * FROM users WHERE role IN ('manager') AND login = '" + esql.getAuth() + "'", esql);

      if(temp.isEmpty()){
         print("You are not authorized to update tracking information");
         return;
      }

      String TID = readString("Enter the Login of the user that you would like to update: ");

      print("\n----------CHOOSE WHAT TO CHANGE----------\n1. password\n2. role\n3. favgames\n4. phonenum\n5. numoverduegames");
      String change = "";
      switch(readChoice()){
          case 1: 
              change = "password";
              break;
          case 2: 
              change = "role";
              break;
          case 3: 
              change = "favgames";

              System.out.print("1. Add to the game list\n2. delete a game from the game list\n"); 
              switch(readChoice()){
                 case 1:
                    String input1 = "select favgames from users where users.login = \'" + TID + "\'"; 
                    List<List<String>> temp1;
                    try {
                       temp1 = esql.executeQueryAndReturnResult(input1);
                    } catch (SQLException e) {
                       System.out.println("Error executing SQL: " + e.getMessage());
                       return;
                    }
  
                    String commaexist = ", ";
                    System.out.println(temp1.get(0).get(0));
                    if(temp1.get(0).get(0).equals("")){
                       commaexist = "";
                       //System.out.println("hello?"); 
                    }
                    System.out.print("Type in the name of the game that you would like to add: ");
                    String gameName1;
                    do {
                       try {
                          gameName1 = in.readLine();
                          break;
                       }catch (Exception e) {
                          System.out.println("Your input is invalid!");
                          continue; 
                       }
                    }while(true);
  
  
                    input1 = "SELECT gamename FROM catalog WHERE gameName ILIKE \'" + gameName1 + "\'";
  
                    try {
                       temp1 = esql.executeQueryAndReturnResult(input1);
                    } catch (SQLException e) {
                       System.out.println("Error executing SQL: " + e.getMessage());
                       return;
                    }
                    if(temp1.isEmpty()){
                       System.out.println("Error game given does not exist in our catalog.");
                       return;
                    }
  
                    gameName1 = temp1.get(0).get(0);
  
                    //System.out.println(gameName);
  
                    input1 = "UPDATE users SET favgames = CONCAT(favgames, \'" + commaexist + gameName1 + "\') WHERE login = \'" + TID + "\'";
                 
                    try {
                       esql.executeUpdate(input1);
                    } catch (SQLException e) {
                       System.out.println("Error executing SQL: " + e.getMessage());
                       return;
                    }
                    System.out.println("Profile updated sucuessfully"); 
                    break;
                 case 2:
                    String input = "select favgames from users where users.login = \'" + TID + "\'"; 
                    List<List<String>> temp3;
                    List<List<String>> tempcheck = new ArrayList<>();
                    List<String> useless = new ArrayList<>();
                    tempcheck.add(useless);
                    
                    try {
                       temp3 = esql.executeQueryAndReturnResult(input);
                    } catch (SQLException e) {
                       System.out.println("Error executing SQL: " + e.getMessage());
                       return;
                    }
  
                    System.out.println(temp3.get(0).get(0));
                    if(temp3.get(0).get(0).equals("")){
                       commaexist = "";
                       System.out.println("You have no games in your favorite games list to delete");
                       return; 
                    }
                    
                    String[] gamesArray = temp3.get(0).get(0).split(", ", -2);
                    System.out.println("FAVORITE GAMES LIST\n----------------\n");
                    for (String a : gamesArray)
                       System.out.println(a);
  
  
                    System.out.print("Type in the name of the game that you would like to delete: ");
                    String gameName;
                    do {
                       try {
                          gameName = in.readLine();
                          break;
                       }catch (Exception e) {
                          System.out.println("Your input is invalid!");
                          continue; 
                       }
                    }while(true);
  
  
                    input = "SELECT gamename FROM catalog WHERE gameName ILIKE \'" + gameName + "\'";
  
                    try {
                       temp3 = esql.executeQueryAndReturnResult(input);
                    } catch (SQLException e) {
                       System.out.println("Error executing SQL: " + e.getMessage());
                       return;
                    }
                    if(temp3.isEmpty()){
                       System.out.println("Error game given does not exist in our catalog.");
                       return;
                    }
                    boolean found = false;
  
                    gameName = temp3.get(0).get(0);
                    for (String str : gamesArray) {
                       if (str.equals(gameName)) {
                           found = true;
                           break;
                       }
                    }
                    if(!found){
                       System.out.println("Game is not in your favorite games list");
                       return;
                    }
                    
  
                    List<String> templist = new ArrayList<String>(Arrays.asList(gamesArray));
                    templist.remove(gameName);
                    gamesArray = templist.toArray(new String[0]);
  
  
  
                    String newfav = String.join(", ", gamesArray);
  
                    input = "UPDATE users SET favgames = \'"+ newfav +"\' WHERE login = \'"+ TID + "\'";
                 
                    try {
                       esql.executeUpdate(input);
                    } catch (SQLException e) {
                       System.out.println("Error executing SQL: " + e.getMessage());
                       return;
                    }
                    System.out.println("Profile updated sucuessfully"); 
                    break;
                 }

              return;
          case 4: 
              change = "phonenum";
              break;
          case 5: 
              change = "numoverduegames";
              break;
          default:
              System.out.println("Invalid choice!");
      }
      String whatGoes = readString("Please enter what you would like to change it to: ");

      String input = "UPDATE users SET " + change + " = '" + whatGoes + "' WHERE login = '" + TID + "'";
      
      try {
         esql.executeUpdate(input);
      } catch (SQLException e) {
         System.out.println("Error executing SQL: " + e.getMessage());
         return;
      }
      print("Sucuessfully Changed!");
      return;

   }


}//end GameRental

