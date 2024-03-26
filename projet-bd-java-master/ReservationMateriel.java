import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class ReservationMateriel {


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

    public static boolean disponibiliteMateriel(String marque,String modele, int annee, Date dateRecup, int nbJours,int nbPieceVoulu,int nbPieceDispo, Connect connect) {
        for (int i = 0; i < nbJours; i++) {
            int nbrLocation = 0;
            long actualDateLong = dateRecup.getTime() + (i * 24L * 60 * 60 * 1000);
            Date actualDate = new Date(actualDateLong);

            try {
                PreparedStatement preparedStatement = connect.getConnection().prepareStatement(
                        "SELECT SUM(nbr_pièce) " +
                                "FROM location_mat " +
                                "WHERE marque = ? " +
                                "AND modèle = ? " +
                                "AND année = ? " +
                                "AND date_recup <= TO_DATE(?, 'YYYY-MM-DD') " +
                                "AND date_retour >= TO_DATE(?, 'YYYY-MM-DD')"
                );
                preparedStatement.setString(1, marque);
                preparedStatement.setString(2, modele);
                preparedStatement.setInt(3, annee);
                preparedStatement.setString(4, actualDate.toString());
                preparedStatement.setString(5, actualDate.toString());

                ResultSet res = preparedStatement.executeQuery();
                if (res.next()) {
                    nbrLocation = res.getInt(1);
                }
                if (nbrLocation+nbPieceVoulu > nbPieceDispo) {
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
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
        while (mois < 0 || mois > 12) {
            System.out.println("Numéro du mois de réservation?");
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

    public static void louerMateriel (Connect connect, String memberID) {


        Map<Integer, Object[]> idact = new HashMap<>();
        Map<Integer, Object[]> idlots = new HashMap<>();

        try {
            /** Affichage des toutes les activités différentes*/
            Statement firstStmt = connect.getConnection().createStatement();
            ResultSet firstRes = firstStmt.executeQuery("SELECT DISTINCT act " +
                    "FROM Lot_Est_Adapte");
            int indexAct = 1;
            System.out.println("\n");
            while (firstRes.next()) {
                System.out.println(indexAct);
                System.out.println(firstRes.getString("act"));
                System.out.println("\n");
                idact.put(indexAct, new Object[]{firstRes.getString("act")});
                indexAct++;
            }

            /** récupération du choix de l'utilisateur*/
            System.out.println("saisissez l'activité conserné");
            Scanner userInput = new Scanner(System.in);
            String lotChoiceStr = userInput.next();
            int indexActSelected = parseInt(lotChoiceStr);


            /** Affichage du matériel pour l'activitée choisie**/
            Statement stmt = connect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT Lot.marque, Lot.modèle, Lot.année, Lot.nb_pièce, Lot.prix_perte " +
                    "FROM Lot " +
                    "JOIN Lot_Est_Adapte ON Lot.marque = Lot_Est_Adapte.marque " +
                    "AND Lot.modèle = Lot_Est_Adapte.modèle " +
                    "AND Lot.année = Lot_Est_Adapte.année " +
                    "WHERE Lot_Est_Adapte.act = '" + idact.get(indexActSelected)[0] + "' " +
                    "ORDER BY Lot.année ASC, Lot.marque ASC");
            int index = 1;
            System.out.println("\n");
            while (res.next()) {
                System.out.println(index);
                System.out.println("marque : " + res.getString("marque")
                        + "\n" + "Modèle : " + res.getString("modèle")
                        + "\n" + "année : " + res.getInt("année")
                        + "\n" + "nombre de pièce : " + res.getInt("nb_pièce")
                        + "\n" + "prix perte: " + res.getInt("prix_perte") + "euros");
                System.out.println("\n");
                idlots.put(index, new Object[]{res.getString("marque"), res.getString("modèle"), res.getInt("année"), res.getInt("nb_pièce")});
                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Scanner userInput = new Scanner(System.in);
        System.out.println("Tapez le numéro du lot que vous voulez réserver?");
        String lotChoiceStr = userInput.next();
        int lotChoice = parseInt(lotChoiceStr);

        String marque = (String) idlots.get(lotChoice)[0];
        String modele = (String) idlots.get(lotChoice)[1];
        int annee = (int) idlots.get(lotChoice)[2];
        int nbPieceDispo = (int) idlots.get(lotChoice)[3];

        Date dateReservation = dateReservation();

        System.out.println("Combien de jours voulez-vous réserver votre lot?");
        String nbJoursChoiceStr = userInput.next();
        int nbJoursChoice = parseInt(nbJoursChoiceStr);

        long actualDateLong = dateReservation.getTime() + (nbJoursChoice * 24L * 60 * 60 * 1000);
        Date dateRendu = new Date(actualDateLong);

        System.out.println("Combien de pièces voulez vous réserver?");
        String nbPieceChoiceStr = userInput.next();
        int nbPieceChoice = parseInt(nbPieceChoiceStr);


        int nbPiecesReservee = 0;
        if (disponibiliteMateriel(marque, modele, annee, dateReservation, nbJoursChoice, nbPieceChoice, nbPieceDispo, connect)) {
            nbPiecesReservee = nbPieceChoice;
        } else {
            System.out.println("Malheureusement, il n'y a plus assez de pièces disponibles sur cette période.");
        }


        if (nbPiecesReservee != 0) {
            try {
                String insertQuery = "INSERT INTO location_mat(marque, modèle, année, id_adhérent, date_recup, date_retour, nbr_pièce, rendu) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement insertStatement = connect.getConnection().prepareStatement(insertQuery)) {
                    insertStatement.setString(1, marque);
                    insertStatement.setString(2, modele);
                    insertStatement.setInt(3, annee);
                    insertStatement.setString(4, memberID);
                    insertStatement.setDate(5, new java.sql.Date(dateReservation.getTime()));
                    insertStatement.setDate(6, new java.sql.Date(dateRendu.getTime()));
                    insertStatement.setInt(7, nbPiecesReservee);
                    insertStatement.setInt(8, 0);

                    insertStatement.executeUpdate();
                }

                connect.getConnection().commit();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Votre réservation a bien été prise en compte.\n" +
                               "Aurevoir.");

        }
    }

    public static int calculPrixTot(String marque, String modele, int annee, int nbPiece, Connect connect){
        int prix_tot = 0;
        try{
            Statement stmt = connect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT prix_perte" +
                                                " FROM Lot" +
                                                " WHERE marque = '" + marque + "'" +
                                                " AND modèle = '" + modele + "'" +
                                                " AND année = " + annee);
            if (res.next()){
                prix_tot = res.getInt(1) * nbPiece;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prix_tot;
    }

    public static void retourMateriel(Connect connect, String memberID){
        Map<Integer, Object[]> idLots = new HashMap<>();
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT * " +
                    "FROM location_mat " +
                    "WHERE id_adhérent = '" + memberID + "' " +
                    "AND rendu = 0 " +
                    "ORDER BY date_retour ASC");

            int index = 1;
            System.out.println("\n");
            while (res.next()) {
                System.out.println(index);
                System.out.println("Marque : " + res.getString("marque")
                        + "\n" + "Modèle : " + res.getString("modèle")
                        + "\n" + "Année : " + res.getInt("année")
                        + "\n" + "Date de récupération : " + res.getDate("date_recup")
                        + "\n" + "Date de retour : " + res.getDate("date_retour")
                        + "\n" + "Nombre de pièces =" + res.getInt("nbr_pièce"));
                System.out.println("\n");
                idLots.put(index, new Object[]{res.getString("marque"), res.getString("modèle"), res.getInt("année"), res.getInt("nbr_pièce")});
                index++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Scanner userInput = new Scanner(System.in);
        System.out.println("Tapez le numéro du lot que vous avez rendu?");
        String lotRenduStr = userInput.next();
        int lotRendu = parseInt(lotRenduStr);

        String marque = (String) idLots.get(lotRendu)[0];
        String modele = (String) idLots.get(lotRendu)[1];
        int annee = (int) idLots.get(lotRendu)[2];
        int nbPiece = (int) idLots.get(lotRendu)[3];

        int nbPiecePerdues = -1;
        while (nbPiecePerdues<0 || nbPiecePerdues>nbPiece){
            System.out.println("Combien de pièces avez vous perdues/abîmées?");
            String nbPiecePerduesStr = userInput.next();
            nbPiecePerdues = parseInt(nbPiecePerduesStr);
        }

        int prix_tot = calculPrixTot(marque, modele, annee, nbPiece, connect);

        String updateQuery = "UPDATE compte_utilisateur SET somme_mat_abime = somme_mat_abime + ? WHERE id_utilisateur = ?";
        try (PreparedStatement updateStatement = connect.getConnection().prepareStatement(updateQuery)) {
            updateStatement.setInt(1, prix_tot);
            updateStatement.setString(2, memberID);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
        e.printStackTrace();
        }

        String updateQuery2 = "UPDATE Lot SET nb_pièce = nb_pièce - ? WHERE marque = ? AND modèle = ? AND année = ?";
        try (PreparedStatement updateStatement = connect.getConnection().prepareStatement(updateQuery2)) {
            updateStatement.setInt(1, nbPiecePerdues);
            updateStatement.setString(2, marque);
            updateStatement.setString(3, modele);
            updateStatement.setInt(4, annee);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String updateQuery3 = "UPDATE location_mat SET rendu = 1 WHERE marque = ? AND modèle = ? AND année = ?";
        try (PreparedStatement updateStatement = connect.getConnection().prepareStatement(updateQuery3)) {
            updateStatement.setString(1, marque);
            updateStatement.setString(2, modele);
            updateStatement.setInt(3, annee);

            updateStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            connect.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void louerRetourMateriel(Connect connect, String memberID){
        String choix = "";
        while (!(choix.equals("R") || choix.equals("L"))) {
            Scanner userInput = new Scanner(System.in);
            System.out.println("Voulez-vous louer ou rendre du matériel? [L/R]");
            choix = userInput.next();
        }
        if (choix.equals("L")){
            louerMateriel(connect, memberID);
        } else if (choix.equals("R")){
            retourMateriel(connect, memberID);
        }
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
        if (ReservationFormation.is_adherent(connect, memberID)){
            louerRetourMateriel(connect, memberID);
        }

        connect.closeConnection();

    }
}