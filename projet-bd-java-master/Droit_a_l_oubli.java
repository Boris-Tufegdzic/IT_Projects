import java.sql.*;
import java.util.Scanner;


public class Droit_a_l_oubli {
    public static Boolean verifie_somme(Connect connect,String memberID) {
        System.out.println("verification que la difference de valeur est nulle");
        float valeur = 0;
        try {
            Statement stmt = connect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT somme_res, somme_mat_abime, somme_deja_rembourse "+
                                                 "FROM  Compte_utilisateur "+
                                                 "WHERE id_utilisateur= '" + memberID + "'");

            if (res.next() == true) {
                valeur = res.getInt(1) + res.getInt(2) - res.getInt(3);
            }
        }
        catch (SQLException e) {
                e.printStackTrace();
            }
        return valeur>0;
    }
    public static void Suppression(Connect connect,String memberID){
        System.out.println("Suppression de votre identifiant");
        if (Droit_a_l_oubli.verifie_somme(connect,memberID)){
            System.out.println("il faut que tu payes ");
            return;
        }
        else {
        try{
            connect.getConnection().beginRequest();
            Statement stmt = connect.getConnection().createStatement();
            stmt.executeQuery("DELETE FROM Membre "
                                + "WHERE id_utilisateur= '" + memberID +"'");
            connect.getConnection().commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
         }
    }
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
                connect.getConnection().setAutoCommit(false);
                memberID = Parcours.getMemberID(connect);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Suppression(connect,memberID);
        connect.closeConnection();
    }
}


