import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.lang.Integer;

import static java.lang.Integer.parseInt;


public class ReservationRefuge {

    public static boolean yesOrNo(String message){
        boolean response = false;
        Scanner userInput = new Scanner(System.in);
        System.out.println(message);
        String reponseChoiceStr = userInput.next();
        if (reponseChoiceStr.equals("Y")){
            response = true;
        }
        return response;
    }

    public static boolean disponibiliteNuit(String mail, Date dateDebut, int nbNuits, Connect connect, int nbPlaces) {
        for (int i = 0; i < nbNuits; i++) {
            int nbrReservation = 0;

            long actualDateLong = dateDebut.getTime() + (i * 24L * 60 * 60 * 1000);
            Date actualDate = new Date(actualDateLong);

            try {
                PreparedStatement preparedStatement = connect.getConnection().prepareStatement(
                        "SELECT COUNT(*) " +
                                "FROM reservation_refuge " +
                                "WHERE mail_r = ? " +
                                "AND nbr_nuit > 0 " +
                                "AND date_r <= TO_DATE(?, 'YYYY-MM-DD') " +
                                "AND date_r + nbr_nuit >= TO_DATE(?, 'YYYY-MM-DD')"
                );
                preparedStatement.setString(1, mail);
                preparedStatement.setString(2, actualDate.toString());  // Convertir la date en chaîne au format adapté
                preparedStatement.setString(3, actualDate.toString());

                ResultSet res = preparedStatement.executeQuery();
                if (res.next()) {
                    nbrReservation = res.getInt(1);
                }
                if (nbrReservation >= nbPlaces) {
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    public static boolean disponibiliteFood(String food, String mail, Date dateDebut, int nbNuits, Connect connect, int nbPlaces) {
        for (int i = 0; i < nbNuits; i++) {
            try {
                int nbrReservationRepas = 0;

                long actualDateLong = dateDebut.getTime() + (i * 24L * 60 * 60 * 1000);
                Date actualDate = new Date(actualDateLong);

                PreparedStatement preparedStatement = connect.getConnection().prepareStatement(
                        "SELECT COUNT(*) " +
                                "FROM reservation_refuge " +
                                "WHERE mail_r = ? " +
                                "AND nbr_" + food + " > 0 " +
                                "AND date_r <= TO_DATE(?, 'YYYY-MM-DD') " +
                                "AND date_r + nbr_nuit >= TO_DATE(?, 'YYYY-MM-DD')"
                );
                preparedStatement.setString(1, mail);
                preparedStatement.setString(2, actualDate.toString());
                preparedStatement.setString(3, actualDate.toString());

                ResultSet res = preparedStatement.executeQuery();
                if (res.next()) {
                    nbrReservationRepas = res.getInt(1);
                }
                if (nbrReservationRepas >= nbPlaces) {
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }



    public static List<Object[]> isFood(String mail, Connect connect){
        List<Object[]> foodList = new ArrayList<>();
        foodList.add(new Object[]{false, 0});
        foodList.add(new Object[]{false, 0});
        foodList.add(new Object[]{false, 0});
        foodList.add(new Object[]{false, 0});

        String[] type = {"dej", "din", "souper", "cc"};

        for (int i = 0; i < type.length; i++) {
            try {
                Statement stmt = connect.getConnection().createStatement();
                ResultSet res = stmt.executeQuery("SELECT prix_" + type[i] +
                        " FROM A_" + type[i] +
                        " WHERE mail_r = '" + mail + "'");
                if (res.next()) {
                    foodList.get(i)[0] = true;
                    foodList.get(i)[1] = res.getFloat(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return foodList;
    }

        public static String getMemberID (Connect connect){
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

    public static Date dateReservation () {
        Scanner userInput = new Scanner(System.in);
        int annee = 0;
        while (annee < 2023 || annee > 2026) {
            System.out.println("Année de réservation?");
            String anneeStr = userInput.next();
            annee = parseInt(anneeStr);
        }
        int mois = -1;
        while (mois < 1 || mois > 12) {
            System.out.println("Mois de réservation? (en nombre)");
            String moisStr = userInput.next();
            mois = parseInt(moisStr) - 1;
        }
        int jour = 0;
        while (jour < 1 || jour > 31) {
            System.out.println("Jour de réservation?");
            String jourStr = userInput.next();
            jour = parseInt(jourStr);
        }
        return new Date(annee, mois, jour);
    }

        public static void reservationRefuge (Connect connect, String memberID){

            Map<Integer, Object[]> idRefuge = new HashMap<>();
            try {
                Statement stmt = connect.getConnection().createStatement();
                ResultSet res = stmt.executeQuery("SELECT nom, sect, nbr_place_repas, nbr_place_dormir, mail_r, prix_n " +
                        "FROM  Refuge " +
                        "ORDER BY nom ASC, date_ouv ASC, date_ferm ASC, nbr_place_dormir ASC");
                int index = 1;
                System.out.println("\n");
                while (res.next()) {
                    System.out.println(index);
                    System.out.println("Nom : " + res.getString("nom")
                            + "\n" + "Secteur : " + res.getString("sect")
                            + "\n" + "Nombre de place repas : " + res.getInt("nbr_place_repas")
                            + "\n" + "Nombre de place dormir : " + res.getInt("nbr_place_dormir"));
                    System.out.println("\n");
                    idRefuge.put(index, new Object[]{res.getString("mail_r"), res.getInt("nbr_place_dormir"), res.getInt("nbr_place_repas"), res.getFloat("prix_n")});
                    index++;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            Scanner userInput = new Scanner(System.in);
            System.out.println("Tapez le numéro du refuge dans lequel vous voulez réserver?");
            String refugeChoiceStr = userInput.next();
            int refugeChoice = parseInt(refugeChoiceStr);
            String mail = (String) idRefuge.get(refugeChoice)[0];
            int nbr_places_nuit = (int) idRefuge.get(refugeChoice)[1];
            int nbr_places_repas = (int) idRefuge.get(refugeChoice)[2];
            float prix_nuit = (float) idRefuge.get(refugeChoice)[3];


            Date dateReservation = dateReservation();

            System.out.println("Combien de nuits voulez-vous rester?");
            String nbJoursChoiceStr = userInput.next();
            int nbJoursChoice = parseInt(nbJoursChoiceStr);

            int prix_tot = 0;

            int nbNuit = 0;
            if (yesOrNo("Voulez-vous dormir dans ce refuge [Y/N]")) {
                if (disponibiliteNuit(mail, dateReservation, nbJoursChoice, connect, nbr_places_nuit)) {
                    nbNuit = nbJoursChoice;
                    prix_tot += prix_nuit * nbJoursChoice;
                }
                else{
                    System.out.println("Malheureusement, il n'y a plus de place pour dormir sur cette période.");
                }
            }


            int[] food={0,0,0,0};
            List<Object[]> foodList = isFood(mail, connect);
            String[] type = {"dej", "din", "souper", "casse-croute"};
            for (int i = 0; i < type.length; i++) {
                if ((boolean) foodList.get(i)[0]) {
                    if (yesOrNo("Voulez-vous un " + type[i] + " dans ce refuge [Y/N]")) {
                        if (disponibiliteFood(type[i],mail,dateReservation,nbJoursChoice,connect,nbr_places_repas)) {
                            food[i] += nbNuit;
                            prix_tot += (float)(foodList.get(i)[1])*nbJoursChoice;
                        }else{System.out.println("Malheureusement, il n'y a plus de place.");}
                    }
                }
            }


            if (nbNuit != 0 || food[0] != 0 || food[1] != 0 || food[2] != 0 || food[3] != 0)
                try {
                    String insertQuery = "INSERT INTO reservation_refuge(date_r, heure, id_utilisateur, mail_r, nbr_nuit, nbr_dej, nbr_din, nbr_souper, nbr_cc, prix_tot) " +
                            "VALUES (?, '9 heure', ?, ?, ?, ?, ?, ?, ?, ?)";

                    try (PreparedStatement insertStatement = connect.getConnection().prepareStatement(insertQuery)) {
                        insertStatement.setDate(1, new java.sql.Date(dateReservation.getTime()));
                        insertStatement.setString(2, memberID);
                        insertStatement.setString(3, mail);
                        insertStatement.setInt(4, nbNuit);
                        insertStatement.setInt(5, food[0]);
                        insertStatement.setInt(6, food[1]);
                        insertStatement.setInt(7, food[2]);
                        insertStatement.setInt(8, food[3]);
                        insertStatement.setInt(9, prix_tot);

                        insertStatement.executeUpdate();
                    }

                    String updateQuery = "UPDATE compte_utilisateur SET somme_res = somme_res + ? WHERE id_utilisateur = ?";
                    try (PreparedStatement updateStatement = connect.getConnection().prepareStatement(updateQuery)) {
                        updateStatement.setInt(1, prix_tot);
                        updateStatement.setString(2, memberID);

                        updateStatement.executeUpdate();
                    }

                    connect.getConnection().commit();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            System.out.println("Votre réservation a bien été prise en compte.\n" +
                    "Aurevoir.");

        }

        public static void main (String[]args){
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
                    connect.getConnection().setAutoCommit(false);
                    memberID = getMemberID(connect);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            reservationRefuge(connect, memberID);

            connect.closeConnection();

        }
    }