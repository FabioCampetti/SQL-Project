import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class VentanaAdmin extends JFrame {

    protected JTable tabla;
    protected JTextArea textSentencias;
    protected JList<String> listTablas;
    protected JList<String> listAtributos;
    protected ConexionDB conexion;
    protected DefaultListModel<String> defaultListTablas;
    protected DefaultListModel<String> defaultListAtributos;
    protected JFrame Ventana=this;

    public VentanaAdmin(ConexionDB c)
    {
        conexion=c;
        inicializar();
    }

    private void inicializar()
    {
        this.setTitle("ADMINISTRADOR");
        this.setResizable(false);
        this.setBounds(100, 100, 836, 628);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JMenuBar barraDeMenuAdmin = new JMenuBar();
        JMenu menuOpciones=new JMenu("Menu");
        menuOpciones.setFont(new Font("Segoe UI", Font.BOLD, 12));

        barraDeMenuAdmin.add(menuOpciones);

        JMenuItem mntmCerrarSesion = new JMenuItem("Cerrar Sesion");
        mntmCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 10));
        menuOpciones.add(mntmCerrarSesion);

        mntmCerrarSesion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                conexion.desconectarBD();
                Ventana.dispose();
                new VentanaPrincipal();
            }
        });

        JMenuItem mntmSalir = new JMenuItem("Salir");
        mntmSalir.setFont(new Font("Segoe UI", Font.BOLD, 10));

        mntmSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                conexion.desconectarBD();
                System.exit(0);
            }
        });

        menuOpciones.add(mntmSalir);
        this.setJMenuBar(barraDeMenuAdmin);

        //Declaracion panel principal
        JPanel panelPrincipal = new JPanel();
        this.getContentPane().add(panelPrincipal);
        panelPrincipal.setLayout(new GridLayout(2,1,10,5));

        //Delacaracion Panel que contiene la Tabla
        JPanel panelTabla = new JPanel();
        panelPrincipal.add(panelTabla);
        panelTabla.setBorder(new LineBorder(new Color(0,0,0),1,true));
        panelTabla.setLayout(new GridLayout(1,1,5,0));

        tabla = new JTable();
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        panelTabla.add(new JScrollPane(tabla));

        //Panel que contiene los comandos y las listas
        JPanel panelOperaciones = new JPanel();
        panelPrincipal.add(panelOperaciones);
        panelOperaciones.setLayout(new GridLayout(1,2,10,0));

        //Panel que contiene el Text para ingresar la sentencia
        JPanel panelConsultas = new JPanel();
        panelOperaciones.add(panelConsultas);
        panelConsultas.setLayout(new GridLayout(2,1,0,0));

        textSentencias = new JTextArea();
        textSentencias.setText("SELECT * FROM aeropuertos;");
        panelConsultas.add(textSentencias);
        actualizarTabla();

        //Panel que contiene los botones
        JPanel panelBotones = new JPanel();
        panelConsultas.add(panelBotones);
        panelBotones.setLayout(new FlowLayout());

        JButton botonConsultar = new JButton();
        botonConsultar.setText("CONSULTAR");
        botonConsultar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarTabla();
            }
        });
        panelBotones.add(botonConsultar);
        JButton botonEjecutar = new JButton();
        botonEjecutar.setText("MODIFICAR");
        botonEjecutar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conexion.actualizacion(textSentencias.getText().trim());
            }
        });
        panelBotones.add(botonEjecutar);
        JPanel panelListas = new JPanel();
        panelOperaciones.add(panelListas);
        panelListas.setLayout(new GridLayout(1,2,20,0));



        //Declaracion listas y panel que contiene las listas
        defaultListTablas = new DefaultListModel<String>();
        listTablas= new JList<String>(defaultListTablas);
        listTablas.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
        llenarListaTablas();
        panelListas.add(new JScrollPane(listTablas));

        listTablas.addListSelectionListener(new ListSelectionListener()
        {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                defaultListAtributos.clear();
                if(defaultListTablas.size()>0)
                    llenarListaAtributos(listTablas.getSelectedValue());
            }
        });

        defaultListAtributos = new DefaultListModel<String>();
        listAtributos = new JList<String>(defaultListAtributos);
        listAtributos.setBorder(new LineBorder(new Color(0,0,0),1,true));
        panelListas.add(new JScrollPane(listAtributos));

        this.setVisible(true);
    }

    //Actualiza la tabla para una consulta ingresada
    private void actualizarTabla() {
        try {
            ResultSet rs = conexion.consultaAdmin(textSentencias.getText().trim());

            if (rs != null) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnas = rsmd.getColumnCount();

                DefaultTableModel modelo = new DefaultTableModel() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                tabla.setModel(modelo);

                for (int i = 1; i <= columnas; i++) {
                    modelo.addColumn(rsmd.getColumnName(i));
                }

                while (rs.next()) {
                    Object[] fila = new Object[columnas];

                    for (int i = 0; i < columnas; i++) {
                        if (rsmd.getColumnType(i + 1) == Types.DATE)
                            fila[i] = Fechas.convertirStringSQL(rs.getString(i + 1));
                        else
                            fila[i] = rs.getString(i + 1);
                    }
                    modelo.addRow(fila);
                }
            }
        } catch (SQLException ex)
            {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                        ex.getMessage() + "\n",
                        "Error al ejecutar la consulta.",
                        JOptionPane.ERROR_MESSAGE);
            }
    }

    private void llenarListaTablas() {
        try {
            ResultSet rs = conexion.consultaAdmin("SHOW TABLES; ");
            if (rs != null) {
                while (rs.next()) {
                    defaultListTablas.addElement(rs.getString(1));
                }
                rs.close();
            }
        } catch (SQLException ex) {
        }
    }

    private void llenarListaAtributos(String nombreTabla){
        try{
            ResultSet rs = conexion.consultaAdmin("DESCRIBE "+nombreTabla+";");
            if(rs!=null)
            {
                while (rs.next())
                {
                    defaultListAtributos.addElement(rs.getString(1));
                }
            }
        }
        catch (SQLException ex){}
    }

}