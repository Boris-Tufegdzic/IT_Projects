
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.lang.Integer;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;


public class Parcours {

    public static String getMemberID(Connect connect) {
        String MemberID = null;
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT id_utilisateur " +
                    "FROM Membre " +
                    "WHERE mdp = '" + connect.getPassword() + "'" +
                    "AND mail_membre = '" + connect.getEmail() + "'");
            if (res.next() == true) {
                MemberID = res.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return MemberID;
    }

    public static void getFormation(Connect connect) {
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
        System.out.println("Tapez le numéro de la formation que vous voulez accéder?");
        String choixFormation = userInput.next();
        int choix = parseInt(choixFormation);
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT * " +
                    "FROM Formation " +
                    "WHERE rang = '" + idFormation.get(choix)[1] + "' AND " +
                    "année = '" + idFormation.get(choix)[0] + "'");

            if (res.next() == true) {
                System.out.println("Nom : " + res.getString("nom")
                        + "\n" + "Date de début : " + res.getString("date_d")
                        + "\n" + "Description : " + res.getString("descr")
                        + "\n" + "Durée: " + res.getInt("durée") + " jours"
                        + "\n" + "Nombre de places: " + res.getInt("nb_places")
                        + "\n" + "Année: " + res.getInt("année")
                        + "\n" + "Rang: " + res.getInt("rang")
                        + "\n" + "Prix: " + res.getInt("prix") + " euros"
                );
                System.out.println("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    public static void getMaterial(Connect connect) {
        Map<Integer, Object[]> idMaterial = new HashMap<>();

        ArrayList<String> catMere = listCatMere(connect);

        String sous_cat = ask(connect, catMere);

        ArrayList<String> listSousCat = sousCat(connect, sous_cat);

        int n = listSousCat.size();

        while (n > 0){
            sous_cat = ask(connect, listSousCat);
            listSousCat = sousCat(connect, sous_cat);
            n = listSousCat.size();
        }

        ArrayList<String[]> keys = getkeyfromCat(connect, sous_cat);

        int l = keys.size();
        try {

            for (int k = 0; k < l; k++){
                String m = keys.get(k)[0];
                int a = parseInt(keys.get(k)[1]);
                String mod = keys.get(k)[2];

                Statement stmt = connect.getConnection().createStatement();
                ResultSet res = stmt.executeQuery("SELECT marque, modèle, année, nb_pièce " +
                        "FROM LOT " +
                        "WHERE marque = '" + m + "' " +
                        "AND année = '" + a + "' " +
                        "AND modèle = '" + mod + "'" +
                        "ORDER BY marque ASC,année ASC ");

                int index = 1;
                System.out.println("\n");
                while (res.next()) {
                    System.out.println(index);
                    System.out.println("marque : " + res.getString("marque")
                            + "\n" + "année : " + res.getInt("année")
                            + "\n" + "modèle : " + res.getString("modèle")
                            + "\n" + "nb_pièce: " + res.getInt("nb_pièce"));
                    System.out.println("\n");
                    idMaterial.put(index, new Object[]{res.getString("marque"), res.getInt("année"), res.getString("modèle")});
                    index += 1;
                }


            }

            try {
                Scanner userInput = new Scanner(System.in);
                System.out.println("Tapez le numéro de la formation que vous voulez accéder?");
                String choixFormation = userInput.next();
                int choix = parseInt(choixFormation);

                Statement stmt1 = connect.getConnection().createStatement();
                ResultSet result = stmt1.executeQuery("SELECT * " +
                        "FROM Lot " +
                        "WHERE marque = '" + idMaterial.get(choix)[0] + "' AND " +
                        "modèle = '" + idMaterial.get(choix)[2] + "' AND " +
                        "année = " + idMaterial.get(choix)[1]);
                if (result.next() == true) {
                    System.out.println("Marque : " + result.getString("marque")
                            + "\n" + "Modèle : " + result.getString("modèle")
                            + "\n" + "Année : " + result.getInt("année")
                            + "\n" + "nombre de pièces : " + result.getInt("nb_pièce")
                            + "\n" + "Prix perte : " + result.getInt("année") + " euros");
                    System.out.println("\n");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    /**récupère toutes les sous cat d'une cat*/
    public static ArrayList<String> sousCat(Connect connect, String cat){
        ArrayList<String> list = new ArrayList<String>();
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT sous_cat FROM A_POUR_SOUS_CAT WHERE cat = '"+cat+"'");
            while (res.next()){
                list.add(res.getString("sous_cat"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Fonction retournant la liste des catégories mères*/
    public static ArrayList<String> listCatMere(Connect connect){
        ArrayList<String> list = new ArrayList<String>();
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT DISTINCT cat FROM A_POUR_SOUS_CAT WHERE cat NOT IN (SELECT DISTINCT sous_cat FROM A_POUR_SOUS_CAT)");
            while (res.next()){
                list.add(res.getString("cat"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<String[]> getkeyfromCat (Connect connect, String sous_cat){
        ArrayList<String> sons = sousCat(connect, sous_cat);
        ArrayList<String[]> keys = new ArrayList<String[]>();

        if (sons.size()!=0){
            System.out.println("la catégorie n'est pas une feuille");
        }
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT marque, année, modèle FROM A_POUR_CAT WHERE cat = '"+sous_cat+"'");
            while (res.next()){
                keys.add(new String[]{res.getString("marque"), ""+res.getInt("année"), res.getString("modèle")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return keys;
    }

    public static String ask(Connect connect, ArrayList<String> list_cat){
        int n = list_cat.size();
        for (int i = 0; i < n; i++){
            System.out.println("\n");
            System.out.println(i+1);
            System.out.println(list_cat.get(i));
            System.out.println("\n");
        }

        Scanner userInput = new Scanner(System.in);
        System.out.println("Tapez le numéro de la catégorie");
        String choixcat = userInput.next();
        int choix = parseInt(choixcat);

        return (list_cat.get(choix-1));


    }

    public static void getRefuge(Connect connect) {

        Map<Integer, String> idRefuge = new HashMap<>();
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT nom, sect, nbr_place_repas, nbr_place_dormir, mail_r " +
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
                idRefuge.put(index, res.getString("mail_r"));
                index++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Scanner userInput = new Scanner(System.in);
        System.out.println("Tapez le numéro du refuge auquel vous voulez accéder");
        String choixFormation = userInput.next();
        int choix = parseInt(choixFormation);
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT * " +
                    "FROM Refuge " +
                    "WHERE mail_r = '" + idRefuge.get(choix) + "'");

            if (res.next() == true) {
                System.out.println("Nom : " + res.getString("nom")
                        + "\n" + "mail du refuge : " + res.getString("mail_r")
                        + "\n" + "Secteur : " + res.getString("sect")
                        + "\n" + "date ouverture: " + res.getString("date_ouv")
                        + "\n" + "date d'ouverture: " + res.getString("date_ferm")
                        + "\n" + "nombre de place repas: " + res.getInt("nbr_place_repas")
                        + "\n" + "nombre de place pour dormir: " + res.getInt("nbr_place_dormir")
                        + "\n" + "texte de présentation: " + res.getString("txt_pres")
                        + "\n" + "type de paiement: " + res.getString("type_paiement")
                        + "\n" + "prix d'une nuit: " + res.getInt("prix_n")
                );
                System.out.println("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public static int choixParcours() {
        Scanner userInput = new Scanner(System.in);
        System.out.println("Accéder aux formations (1), aux matériels (2) ou aux refuges (3)?");
        String choixParcours = userInput.next();
        if (choixParcours.equals("1")) {
            return 1;
        } else if (choixParcours.equals("2")) {
            return 2;
        } else if (choixParcours.equals("3")) {
            return 3;
        } else {
            return 0;
        }
    }


    public static void main(String[] args) {

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
                memberID = getMemberID(connect);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        int choixParcours = 0;
        while (choixParcours == 0) {
            choixParcours = choixParcours();
        }
        if (choixParcours == 1) {
            getFormation(connect);
        } else if (choixParcours == 2) {
            getMaterial(connect);
        } else if (choixParcours == 3) {
            getRefuge(connect);
        }


        connect.closeConnection();

    }
}