import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.lang.Integer;

import static java.lang.Integer.parseInt;

public class ReservationFormation {

    public static String getMemberID(Connect connect) {
        String MemberID = null;
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT id_utilisateur " +
                    "FROM Membre " +
                    "WHERE mdp = '" + connect.getPassword() + "'" +
                    "AND mail_membre = '" + connect.getEmail() + "'");
            if (res.next()) {
                MemberID = res.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return MemberID;
    }

    public static void reservationFormation(Connect connect, String memberID) {
        Map<Integer, String[]> idFormation = new HashMap<>();
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT nom, date_d, descr, durée, nb_places, rang, année " +
                    "FROM Formation " +
                    "ORDER BY date_d ASC,nom ASC ");
            int index = 1;
            System.out.println("\n");
            while (res.next()) {
                System.out.println(index);
                System.out.println("Nom : " + res.getString("nom")
                        + "\n" + "Date de début : " + res.getString("date_d")
                        + "\n" + "Description : " + res.getString("descr")
                        + "\n" + "Durée: " + res.getInt("durée") + " jours"
                        + "\n" + "Nombre de places: " + res.getInt("nb_places"));
                System.out.println("\n");
                idFormation.put(index, new String[]{res.getString("année"), res.getString("rang")});
                index += 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Scanner userInput = new Scanner(System.in);
        System.out.println("Tapez le numéro de la formation que vous voulez réserver?");
        String formationChoiceStr = userInput.next();
        int formationChoice = parseInt(formationChoiceStr);



        try {

            int nbrPlace = -1;
            int nbrReservation = -1;
            float prix_res = -1;

            // Nombre de place dans la formation
            Statement stmt = connect.getConnection().createStatement();
            ResultSet res_formation = stmt.executeQuery("SELECT * " +
                    "FROM Formation " +
                    "WHERE rang = '" + idFormation.get(formationChoice)[1] + "' AND " +
                    "année = '" + idFormation.get(formationChoice)[0] + "'");

            if (res_formation.next()) {
                nbrPlace = res_formation.getInt("nb_places");
                prix_res = res_formation.getFloat("prix");
            }

            // Nombre de réservation pour l'instant
            Statement stmt0 = connect.getConnection().createStatement();
            ResultSet res = stmt0.executeQuery("SELECT COUNT(*) FROM reservation_formation WHERE année = '"+ res_formation.getString("année")+"' AND rang = '" + res_formation.getInt("rang")+"'");
            if (res.next()){nbrReservation = res.getInt(1);}


            if (nbrPlace > nbrReservation){

                Statement stmt1 = connect.getConnection().createStatement();
                connect.getConnection().beginRequest();
                String req2 = "'" + memberID + "'" + ", " + "'" + res_formation.getString("année") + "'" + ", " + "'" + res_formation.getInt("rang") + "'" + ", " + "0";
                String req = "INSERT INTO reservation_formation values ("+req2+")";

                stmt1.executeUpdate(req);
                String sql = "UPDATE compte_utilisateur SET somme_res = somme_res + "+prix_res+" WHERE id_utilisateur = "+ "'"+memberID+"'";
                stmt.executeUpdate(sql);
                connect.getConnection().commit();

                System.out.println("Vous avez bien été inscrit dans la formation souhaité, vous pouvez peut etre aussi vous inscrire a la formation de kung fu norvégien.");
            } else {
                Statement stmt1 = connect.getConnection().createStatement();
                connect.getConnection().beginRequest();
                stmt1.executeUpdate("INSERT INTO reservation_formation VALUES (" + memberID + ", '" + res_formation.getString("année") + "', " + res_formation.getInt("rang") + ", " + (nbrPlace - nbrReservation + 1) + ")");

                connect.getConnection().commit();
                System.out.println("Malheureusement, il n'y a plus de place pour cette formation, vous avez été placé en liste d'attente" +
                        "\n, vous pouvez peut-être essayer notre formation en Kung FU italien");
            }



            // Afficher : reservation faite :) bonne journée
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static boolean is_adherent(Connect connect, String memberID) {
        boolean is_adherent = false;
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT id_adhérent " +
                                                "FROM Adhérent " +
                                                "WHERE id_adhérent = '" + memberID + "'" );
            if (res.next()) {
                is_adherent = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return is_adherent;
    }


    public static void main(String[] args) {

        try{
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
                connect.getConnection().setAutoCommit(false);
                memberID = getMemberID(connect);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (is_adherent(connect, memberID)){
            reservationFormation(connect, memberID);
        }

        connect.closeConnection();

    }
}