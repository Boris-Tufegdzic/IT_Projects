
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Connection;


public class Connect {
    private String Password;
    private String Email;
    private Connection connection;
    private Scanner userInput;

    public Connect(){
        Scanner userInput = new Scanner(System.in);
        this.userInput = userInput;
        set_Email();
        set_Password();
        connection = null;
    }

    public void set_Password(){

        System.out.println("Veuillez saisir un mot de passe:");
        String password = userInput.next();

        this.Password = password;
    }

    public void set_Email(){

        System.out.println("Veuillez saisir un identifiant (Email):");
        String email = userInput.next();

        this.Email = email;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getPassword() {
        return Password;
    }

    public String getEmail() {
        return Email;
    }

    public void set_connection(Connection connection){
        this.connection = connection;
    }

    public void closeConnection(){
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
