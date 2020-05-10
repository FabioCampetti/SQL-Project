import java.sql.*;

import javax.swing.JOptionPane;

public class ConexionDB {

    protected Connection conexion;

    public ConexionDB() {
        conexion = null;
    }

    public String conectarEmpleado(String legajo, String clave) {

        String error = "";
        //Si no esta iniciada ninguna conexion
        if (conexion == null) {
            try {
                String servidor = "localhost:3306";
                String baseDatos = "vuelos";
                String url = "jdbc:mysql://" + servidor + "/" + baseDatos +
                        "?serverTimezone=America/Argentina/Buenos_Aires";

                conexion = DriverManager.getConnection(url, "empleado", "empleado");

                ResultSet rs = consulta("select * from empleados where legajo="+ legajo +" and password=md5('"+ clave +"');");
                if (rs == null || !rs.next()) {
                    desconectarBD();
                    error = null;
                }

                if (rs != null)
                    rs.close();
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                error = null;
            }
        }
        return error;
    }

    public String conectarAdmin(String usuario, String clave)
    {

        String error = "";

        try
        {
            String servidor = "localhost:3306";
            String baseDatos = "vuelos";
            String url = "jdbc:mysql://" + servidor + "/" + baseDatos +
                    "?serverTimezone=America/Argentina/Buenos_Aires";
            conexion = DriverManager.getConnection(url, usuario, clave);

        }
        catch (SQLException ex)
        {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            error = null;
            conexion = null;
        }

        return error;

    }

    public ResultSet consulta(String sql) {
        try {
            // Se crea una sentencia jdbc para realizar la consulta
            Statement stmt = conexion.createStatement();

            // Se ejecuta la sentencia y se recibe un resultado
            ResultSet rs = stmt.executeQuery(sql);

            return rs;
        }
        catch (SQLException ex)
        {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return null;
    }

    public void desconectarBD() {
        if (conexion != null)
        {
            try
            {
                conexion.close();
                conexion = null;
            }
            catch (SQLException ex)
            {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        }
    }

    public ResultSet consultaAdmin(String sql)
    {
        try
        {
            // Se crea una sentencia jdbc para realizar la consulta
            Statement stmt = conexion.createStatement();

            // Se ejecuta la sentencia y se recibe un resultado
            ResultSet rs = stmt.executeQuery(sql);

            return rs;
        }
        catch (SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            JOptionPane.showMessageDialog(null,
                    ex.getMessage() + "\n",
                    "Error al ejecutar la consulta",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    //Sentencia de actualizacion para la base
    public void actualizacion (String sql)
    {
        try
        {
            // Se crea una sentencia jdbc para realizar la consulta
            Statement stmt = conexion.createStatement();

            // Se ejecuta la sentencia y se recibe un resultado
            stmt.executeUpdate(sql);

            stmt.close();
            JOptionPane.showMessageDialog(null, "Modificacion realizada correctamente",
                    "OK",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            JOptionPane.showMessageDialog(null,
                    ex.getMessage() + "\n",
                    "Error al ejecutar la consulta",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}