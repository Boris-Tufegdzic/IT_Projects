import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class main_interface {
    public static void main(String[] args){
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Connect connect = null;
        String memberID = null;
        while (memberID == null) {
            try {
                connect = new Connect();
                String url = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
                String user = "tufegdzb";
                String passwd = "tufegdzb";
                connect.set_connection(DriverManager.getConnection(url, user, passwd));
                memberID = Parcours.getMemberID(connect);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        while (true) {

            Scanner userInput = new Scanner(System.in);
            System.out.println("Que voulez vous faire ? Parcourir les Formation,les Refuges,les Materiels(1),Reserver un refuge(2),Reserver une formation(3),Reserver Materiel(4),exercer son droit à l'oubli(5), Quitter l'interface(6)");
            String choixParcours = userInput.next();
            if (choixParcours.equals("1")) {
                int my_choice = 0;
                while (my_choice == 0) {
                    my_choice = Parcours.choixParcours();
                }
                if (my_choice == 1) {
                    Parcours.getFormation(connect);
                } else if (my_choice == 2) {
                    Parcours.getMaterial(connect);
                } else if (my_choice== 3) {
                    Parcours.getRefuge(connect);
                }
            } else if (choixParcours.equals("2")) {
                ReservationRefuge.reservationRefuge(connect,memberID);
            } else if (choixParcours.equals("3")) {
                ReservationFormation.reservationFormation(connect,memberID);
            } else if (choixParcours.equals("4")) {
                if (ReservationFormation.is_adherent(connect, memberID)){
                    ReservationMateriel.louerRetourMateriel(connect, memberID);
                }else {
                    System.out.println("vous n'êtes pas adhérent,vous ne pouvez pas louer ou retourner un materiel");
                }
            } else if (choixParcours.equals("5")) {
                Droit_a_l_oubli.Suppression(connect, memberID);
            } else if (choixParcours.equals("6")){
                connect.closeConnection();
                return;
            }
            else {
                System.out.println("Je n'ai pas compris votre réponse, veuillez réessayer");

            }
        }
    }
}