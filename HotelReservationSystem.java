import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;

public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";

    private static final String username = "root";

    private static final String password = "lovekesh@123";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            while(true){
                System.out.println();

                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.println("Choose an Option: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(connection,scanner);
                        break;
                
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getRoomNumber(connection,scanner);
                        break;
                    case 4:
                        updateReservation(connection,scanner);
                        break;
                    case 5:
                        deleteReservation(connection,scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                         System.out.println("Invalid choice. Try again.");
                }
            }
        }catch(SQLException e ){
            System.out.println(e.getMessage());
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }
    private static void reserveRoom(Connection connection, Scanner scanner){
        try{
            System.out.print("Enter Guest name: ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.print("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = scanner.next();

            String sql = "INSERT INTO reservations(guest_name, room_number, contact_number)"+
                         "VALUES('"+ guestName + "', " + roomNumber + ", '" + contactNumber + "')";
            try(Statement statement = connection.createStatement()){

                int affectedRows = statement.executeUpdate(sql);

                if(affectedRows > 0){
                    System.out.println("Reservation successful!!");
                }else{
                    System.out.println("Reservation unsuccessful!!");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewReservation(Connection connection){
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

        try(Statement statement = connection.createStatement();
           ResultSet resultSet = statement.executeQuery(sql)) {

            while(resultSet.next()){
               int reservationId = resultSet.getInt("reservation_id");
               System.out.println("Reservation ID is: "+reservationId);

               String guestName = resultSet.getString("guest_name");
               System.out.println("Guest name: "+guestName);

               int roomNumber = resultSet.getInt("room_number");
               System.out.println("Room Number: "+ roomNumber);

               String contactNumber = resultSet.getString("contact_number");
               System.out.println("Contact Number: "+contactNumber);

               String reservationDate = resultSet.getTimestamp("reservation_date").toString();
               System.out.println("Reservation Date: "+reservationDate);
            

             // System.out.println("Reservation ID is: "+reservationId);
             // System.out.println("Guest name: "+guestName);
             // System.out.println("Room Number: "+ roomNumber);
             // System.out.println("Contact Number: "+contactNumber);
             // System.out.println("Reservation Date: "+reservationDate);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
           }
    }
    private static void getRoomNumber(Connection connection, Scanner scanner)
    {
        try
      {
        System.out.print("Enter Reservation Id: ");
        int reservationId = scanner.nextInt();
        System.out.print("Enter Guest Name: ");
        String guestName = scanner.next();

        String sql = "SELECT room_number FROM reservations " +
        "WHERE reservation_id = " + reservationId + 
        " AND guest_name = '" + guestName + "'";

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

                if(resultSet.next()){
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation Id "+reservationId +
                                       " and Guest "+ guestName + " is: " + roomNumber);
                }else{
                    System.out.println("Reservation not found for given Id and guest Name.");
                }
            }
         }catch(SQLException e){
                e.printStackTrace();
            }
    }

    private static void updateReservation(Connection connection, Scanner scanner){
        try{
        System.out.print("Enter reservation ID to update: ");
        int reservationId = scanner.nextInt();
        scanner.nextLine(); // consume the next line character 

        if(!reservationExists(connection,reservationId)){
            System.out.println("Reservation not found for given ID.");
            return;
        }

        System.out.print("Enter new guest name: ");
        String newGuestName = scanner.next();
        System.out.print("Enter new room number: ");
        int newRoomNumber = scanner.nextInt();
        System.out.print("Enter new contact number: ");
        String newContactNumber = scanner.next();

        String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
        "room_number = " + newRoomNumber + ", " +
        "contact_number = '" + newContactNumber + "' " +
        "WHERE reservation_id = " + reservationId;

        try(Statement statement =   connection.createStatement()) {
            int affectedRows = statement.executeUpdate(sql);

            if(affectedRows > 0){
                System.out.println("Reservation updated successfully..");
            }else{
                System.out.println("Reservation update failed");
            }
        }
    }catch(SQLException e){
            e.printStackTrace();
        }
    }
    private static void deleteReservation(Connection connection,Scanner scanner){
        try{
            System.out.print("Enter reservation id for delete: ");
            int reservationId = scanner.nextInt();

            if(!reservationExists(connection,reservationId)){
                System.out.println("Reservation not found for given ID.");
                return;
            }
            String sql = "DELETE FROM reservations WHERE reservation_id = "+reservationId;

            try(Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);

                if(affectedRows > 0){
                    System.out.println("Reservation deleted successfully.");
                }else{
                    System.out.println("Reservation deletetion failed..");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection , int reservationId){
        try{
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = "+ reservationId;

            try(Statement statement = connection.createStatement();
               ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // if there is a result , the reservation exists 
               }
        }catch(SQLException e){
            e.printStackTrace();
            return false; // if reservation does not exixts 
        }
    }

    public static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i = 5;
        while (i != 0) {
            System.out.print(".");
            Thread.sleep(400);
            i--;
        }
        System.out.println();
        System.out.println("Thnank You Using Hotel Reservation System!!!");
    }
}