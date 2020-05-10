import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

//import com.mysql.jdbc.Statement;
import java.sql.*;
import java.awt.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JOptionPane;

public class VentanaEmpleado extends JFrame {
	
	/*Variables Globales Graficas*/
	
	private JPanel contentPane;
	private JPanel panel1;
	private JPanel panel2;
	//private DefaultListModel<String> modeloListaVuelosIda;
	
	private JComboBox<String> cbxCiudadOrigen;
	private JComboBox<String> cbxCiudadDestino;
	private LinkedList<Ciudad> ciudadesOrigen;
	private LinkedList<Ciudad> ciudadesDestino;
	
	private DefaultListModel<String> modeloListaVuelosIda;
	private LinkedList<String> listaVuelosIda;
	private JScrollPane scrTablaVuelosIda;
	private DefaultTableModel tablaVuelosIdaModel;
	private JTable tablaVuelosIda;
	
	private DefaultListModel<String> modeloListaVuelosVuelta;
	private LinkedList<String> vuelosVuelta;
	private JScrollPane scrTablaVuelosVuelta;
	private DefaultTableModel tablaVuelosVueltaModel;
	private JTable tablaVuelosVuelta;
	
	private DefaultListModel<String> modeloListaVuelosSelecc;
	private LinkedList<String> vuelosSelecc;
	private JScrollPane scrTablaVueloSeleccIda;
	private DefaultTableModel tablaVueloSeleccModelIda;
	private JTable tablaVueloSeleccIda;
	
	private JScrollPane scrTablaVueloSeleccVuelta;
	private DefaultTableModel tablaVueloSeleccModelVuelta;
	private JTable tablaVueloSeleccVuelta;
	
	
	JFormattedTextField frmtdtxtfldFechaIda;
	JFormattedTextField frmtdtxtfldFechaVuelta;
	
	private ConexionDB cnx = null;
	
	/* Variables globales Logicas */
	private String ciudadOrigenActual; 
	private String ciudadDestinoActual; 
	private boolean idaYVuelta = false;
	private JFrame Ventana = this;
	
	private String legajo;
		
	public VentanaEmpleado(ConexionDB c,String legajo){
		super();
		cnx = c;
		this.initGUI();
		this.legajo=legajo;
	}
	
	private void initGUI(){
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(150, 150, 955, 623);
		setVisible(true);
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		
		JMenuBar barraDeMenuEmpleado = new JMenuBar();
        JMenu menuOpciones=new JMenu("Menu");
        menuOpciones.setFont(new Font("Segoe UI", Font.BOLD, 12));

        barraDeMenuEmpleado.add(menuOpciones);

        JMenuItem mntmCerrarSesion = new JMenuItem("Cerrar Sesion");
        mntmCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 11));
        menuOpciones.add(mntmCerrarSesion);

        mntmCerrarSesion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                cnx.desconectarBD();
                Ventana.dispose();
                new VentanaPrincipal();
            }
        });

        JMenuItem mntmSalir = new JMenuItem("Salir");
        mntmSalir.setFont(new Font("Segoe UI", Font.BOLD, 11));

        mntmSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                cnx.desconectarBD();
                System.exit(0);
            }
        });
        
        menuOpciones.add(mntmSalir);
        this.setJMenuBar(barraDeMenuEmpleado);
		
		//DEFINICION DE PANELES (EL 2 ESTA OCULTO HASTA QUE SE REALIZA LA BUSQUEDA DE VUELOS)
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
        this.setTitle("Empleado");
		contentPane.setLayout(null);
		
		panel1 = new JPanel();
		panel1.setBounds(20, 27, 909, 147);
		contentPane.add(panel1);
		panel1.setLayout(null);
		
		panel2 = new JPanel();
		panel2.setBounds(20, 175, 909, 406);
		contentPane.add(panel2);
		panel2.setLayout(null);
		panel2.setVisible(false);
		
		//DEFINICION DE ELEMENTOS EN EL PANEL 1 (Visible inicialmente -> Parametros para busqueda de vuelos)
		
		//Label: Ciudad Origen
		JLabel lblCiudadOrigen = new JLabel("Ciudad Origen :");
		lblCiudadOrigen.setBounds(10, 27, 124, 20);
		panel1.add(lblCiudadOrigen);
		
		//Combo box: Ciudades Origen
		cbxCiudadOrigen = new JComboBox<String>();
		llenarCiudadesOrigen();
		cbxCiudadOrigen.setSelectedIndex(-1);
		cbxCiudadOrigen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(cbxCiudadOrigen.getSelectedIndex()!=-1){
					ciudadOrigenActual = (String) cbxCiudadOrigen.getSelectedItem();
				}
				limpiarTablas();
			}
		});
		cbxCiudadOrigen.setBounds(111, 27, 109, 20);
		panel1.add(cbxCiudadOrigen);
		
		//Label: Ciudad Destino
		JLabel lblCiudadDestino = new JLabel("Ciudad Destino :");
		lblCiudadDestino.setBounds(10, 55, 124, 20);
		panel1.add(lblCiudadDestino);
		
		//Combo box: Ciudades Destino
		cbxCiudadDestino = new JComboBox<String>();
		llenarCiudadesDestino();
		cbxCiudadDestino.setSelectedIndex(-1);
		cbxCiudadDestino.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(cbxCiudadDestino.getSelectedIndex()!=-1){
					ciudadDestinoActual = (String) cbxCiudadDestino.getSelectedItem();
				}
				limpiarTablas();
			}
		});
		cbxCiudadDestino.setBounds(111, 55, 109, 20);
		panel1.add(cbxCiudadDestino);
		
		//Label para Radiobuttons (Viajes ida solamente o ida y vuelta)
		JLabel lblTipoDeViaje = new JLabel("Seleccione el tipo de viaje:");
		lblTipoDeViaje.setBounds(244, 27, 145, 20);
		panel1.add(lblTipoDeViaje);
		
		JRadioButton rdbtnViajeIda = new JRadioButton("Solo Ida");
		JRadioButton rdbtnIdaYVuelta = new JRadioButton("Ida y Vuelta");
		
		try {
			MaskFormatter mask = new MaskFormatter("####-##-##");
			
			frmtdtxtfldFechaIda = new JFormattedTextField(mask);
			frmtdtxtfldFechaVuelta = new JFormattedTextField(mask);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		//Especificacion para Radio Button: Solo Ida
		rdbtnViajeIda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnViajeIda.isSelected()){
					idaYVuelta = false;
					rdbtnIdaYVuelta.setSelected(false);
					frmtdtxtfldFechaVuelta.setText("");
					frmtdtxtfldFechaVuelta.setEnabled(false);
				}
				limpiarTablas();
			}
		});
		rdbtnViajeIda.setSelected(true);
		rdbtnViajeIda.setBounds(244, 54, 94, 23);
		panel1.add(rdbtnViajeIda);
		
		//Especificacion para Radio Button: Ida y Vuelta
		rdbtnIdaYVuelta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnIdaYVuelta.isSelected()){
					idaYVuelta = true;
					rdbtnViajeIda.setSelected(false);
					frmtdtxtfldFechaVuelta.setEnabled(true);
				}
				limpiarTablas();
			}
		});
		rdbtnIdaYVuelta.setBounds(351, 54, 109, 23);
		panel1.add(rdbtnIdaYVuelta);
		
		//Fechas de Viajes de Ida- Ida y Vuelta
		
		//Label para Fecha de Viaje de Ida
		JLabel lblFechaViajeIda = new JLabel("Fecha Viaje Ida");
		lblFechaViajeIda.setBounds(466, 28, 114, 14);
		panel1.add(lblFechaViajeIda);
		
		//Formatted Text Field para Fecha de Viaje de Ida
		frmtdtxtfldFechaIda.setBounds(590, 25, 91, 20);
		frmtdtxtfldFechaIda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				limpiarTablas();
			}
		});
		panel1.add(frmtdtxtfldFechaIda);
						
		//Label para Fecha de Viaje de Vuelta
		JLabel lblFechaViajeVuelta = new JLabel("Fecha Viaje Vuelta");
		lblFechaViajeVuelta.setBounds(466, 58, 114, 14);
		panel1.add(lblFechaViajeVuelta);

		//Formatted Text Field para Fecha de Viaje de Vuelta
		frmtdtxtfldFechaVuelta.setBounds(590, 55, 91, 20);
		frmtdtxtfldFechaVuelta.setEnabled(false);
		frmtdtxtfldFechaVuelta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				limpiarTablas();
			}
		});
		panel1.add(frmtdtxtfldFechaVuelta);
		
		//Boton para Buscar Vuelos a partir de los parometros ingresados en los datos del Panel 1
		
		JButton btnBuscarVuelos = new JButton("Buscar Vuelos");
		btnBuscarVuelos.setBounds(273, 88, 164, 48);
		panel1.add(btnBuscarVuelos);
		btnBuscarVuelos.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								if(cbxCiudadOrigen.getSelectedIndex()!=-1 && cbxCiudadDestino.getSelectedIndex()!=-1 && (rdbtnViajeIda.isSelected() || rdbtnIdaYVuelta.isSelected()) && validarFecha(frmtdtxtfldFechaIda.getText()) && (validarFecha(frmtdtxtfldFechaVuelta.getText()) || !frmtdtxtfldFechaVuelta.isEnabled())) {
								    buscarVuelos();
									panel2.setVisible(true);
								}
								else
									JOptionPane.showMessageDialog(contentPane, "Ingrese todos los datos requeridos antes de Buscar Vuelos", "Error", JOptionPane.ERROR_MESSAGE);
							}
						});
		
		//Boton para Reservar Vuelos a partir de los parometros seleccionados en las tablas
			JButton btnReservarVuelos = new JButton("Reservar");
			btnReservarVuelos.setBounds(473, 88, 164, 48);
			panel1.add(btnReservarVuelos);
			btnReservarVuelos.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					reservar();
				}
			});
		
		//--------------------------------------TABLA DE VUELOS DE IDA-------------------------------------

		modeloListaVuelosIda = new DefaultListModel<String> ();
		listaVuelosIda = new LinkedList<String>();

		modeloListaVuelosIda = new DefaultListModel<String> ();
		
		tablaVuelosIdaModel =  new DefaultTableModel ( new String[][] {}, new String[] {"Numero Vuelo", "Aeropuerto Salida", "Hora Salida","Aeropuerto Llegada","Hora Llegada","Modelo de Avion","Tiempo Estimado"})
		{
			@SuppressWarnings("rawtypes")
			Class[] types = new Class[] {java.lang.Integer.class, java.lang.String.class, java.lang.String.class,
					java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class};
			
			boolean[] canEdit = new boolean[] { false, false, false, false, false, false, false };
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) 
			{
				return types[columnIndex];
			}
			
			public boolean isCellEditable(int rowIndex, int columnIndex) 
			{
				return canEdit[columnIndex];
			}
		};
		
		scrTablaVuelosIda = new JScrollPane();
		scrTablaVuelosIda.setBounds(10, 36, 684, 155);
		scrTablaVuelosIda.setViewportView(tablaVuelosIda);
		panel2.add(scrTablaVuelosIda, BorderLayout.CENTER);
		
		tablaVuelosIda = new JTable();
		tablaVuelosIda.setEnabled(true);
		tablaVuelosIda.setBounds(10, 10, 300, 300);
		tablaVuelosIda.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
                int row=tablaVuelosIda.getSelectedRow();
                if(row!=-1)
	                mostrarDatosVuelo(tablaVuelosIda.getValueAt(row, 0).toString(),frmtdtxtfldFechaIda.getText(),tablaVueloSeleccModelIda,tablaVueloSeleccIda);
	        }
	    });

		scrTablaVuelosIda.setViewportView(tablaVuelosIda);
		tablaVuelosIda.setRowSelectionAllowed(true);
		tablaVuelosIda.setModel(tablaVuelosIdaModel);
		tablaVuelosIda.setAutoCreateRowSorter(true);
		
		JLabel lblVuelosIda = new JLabel("Detalle Vuelos Ida");
		lblVuelosIda.setBounds(10, 11, 97, 14);
		panel2.add(lblVuelosIda);
		
		//--------------------------------------TABLA DE VUELOS DE VUELTA-------------------------------------
		
		JLabel lblDetalleVuelosVuelta = new JLabel("Detalle Vuelos Vuelta");
		lblDetalleVuelosVuelta.setBounds(10, 202, 109, 14);
		panel2.add(lblDetalleVuelosVuelta);
		
		modeloListaVuelosVuelta = new DefaultListModel<String> ();
		vuelosVuelta = new LinkedList<String>();

		//modeloVuelosVuelta = new DefaultListModel<String> ();
		
		tablaVuelosVueltaModel =  new DefaultTableModel ( new String[][] {}, new String[] {"Numero Vuelo", "Aeropuerto Salida", "Hora Salida","Aeropuerto Llegada","Hora Llegada","Modelo de Avion","Tiempo Estimado"})
		{
			@SuppressWarnings("rawtypes")
			Class[] types = new Class[] {java.lang.Integer.class, java.lang.String.class, java.lang.String.class,
					java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class};
			
			boolean[] canEdit = new boolean[] { false, false, false, false, false, false, false };
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) 
			{
				return types[columnIndex];
			}
			
			public boolean isCellEditable(int rowIndex, int columnIndex) 
			{
				return canEdit[columnIndex];
			}
		};
		
		scrTablaVuelosVuelta = new JScrollPane();
		scrTablaVuelosVuelta.setBounds(10, 227, 684, 168);
		scrTablaVuelosVuelta.setViewportView(tablaVuelosVuelta);
		panel2.add(scrTablaVuelosVuelta, BorderLayout.CENTER);
		
		tablaVuelosVuelta = new JTable();
		tablaVuelosVuelta.setEnabled(true);
		tablaVuelosVuelta.setBounds(10, 10, 300, 300);
		scrTablaVuelosVuelta.setViewportView(tablaVuelosVuelta);
		tablaVuelosVuelta.setRowSelectionAllowed(true);
		tablaVuelosVuelta.setModel(tablaVuelosVueltaModel);
		tablaVuelosVuelta.setAutoCreateRowSorter(true);
        tablaVuelosVuelta.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
            	int row =tablaVuelosVuelta.getSelectedRow();
            	if(row!=-1)
                mostrarDatosVuelo(tablaVuelosVuelta.getValueAt(row, 0).toString(),frmtdtxtfldFechaVuelta.getText(),tablaVueloSeleccModelVuelta,tablaVueloSeleccVuelta);
            }
        });
		
		//--------------------------------------TABLA DE VUELO SELECCIONADO IDA-------------------------------------
		
		JLabel lblVueloSeleccionado = new JLabel("Vuelo Seleccionado");
		lblVueloSeleccionado.setBounds(735, 11, 117, 24);
		panel2.add(lblVueloSeleccionado);
		
		tablaVueloSeleccModelIda =  new DefaultTableModel ( new String[][] {}, new String[] {"Clase", "Asientos Disponibles", "Precio Pasaje"})
		{
			@SuppressWarnings("rawtypes")
			Class[] types = new Class[] {java.lang.Integer.class, java.lang.String.class, java.lang.String.class,
					java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class};
			
			boolean[] canEdit = new boolean[] { false, false, false, false, false, false, false };
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) 
			{
				return types[columnIndex];
			}
			
			public boolean isCellEditable(int rowIndex, int columnIndex) 
			{
				return canEdit[columnIndex];
			}
		};
		
		scrTablaVueloSeleccIda = new JScrollPane();
		scrTablaVueloSeleccIda.setBounds(723, 36, 176, 155);
		scrTablaVueloSeleccIda.setViewportView(tablaVueloSeleccIda);
		panel2.add(scrTablaVueloSeleccIda);

		tablaVueloSeleccIda = new JTable();
		tablaVueloSeleccIda.setEnabled(true);
		tablaVueloSeleccIda.setBounds(10, 10, 50, 50);
		tablaVueloSeleccIda.setRowSelectionAllowed(true);
		scrTablaVueloSeleccIda.setViewportView(tablaVueloSeleccIda);
		tablaVueloSeleccIda.setModel(tablaVueloSeleccModelIda);
		tablaVueloSeleccIda.setAutoCreateRowSorter(true);
		
	//--------------------------------------TABLA DE VUELO SELECCIONADO VUELTA-------------------------------------
		
		JLabel labelVueloSeleccionadoVuelta = new JLabel("Vuelo de Vuelta Seleccionado");
		labelVueloSeleccionadoVuelta.setBounds(735, 202, 152, 24);
		panel2.add(labelVueloSeleccionadoVuelta);
		
		tablaVueloSeleccModelVuelta =  new DefaultTableModel ( new String[][] {}, new String[] {"Clase", "Asientos Disponibles", "Precio Pasaje"})
		{
			@SuppressWarnings("rawtypes")
			Class[] types = new Class[] {java.lang.Integer.class, java.lang.String.class, java.lang.String.class,
					java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class};
			
			boolean[] canEdit = new boolean[] { false, false, false, false, false, false, false };
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) 
			{
				return types[columnIndex];
			}
			
			public boolean isCellEditable(int rowIndex, int columnIndex) 
			{
				return canEdit[columnIndex];
			}
		};
		
		scrTablaVueloSeleccVuelta = new JScrollPane();
		scrTablaVueloSeleccVuelta.setBounds(723, 227, 176, 155);
		scrTablaVueloSeleccVuelta.setViewportView(tablaVueloSeleccVuelta);
		panel2.add(scrTablaVueloSeleccVuelta);
		
		tablaVueloSeleccVuelta = new JTable();
		tablaVueloSeleccVuelta.setEnabled(true);
		tablaVueloSeleccVuelta.setRowSelectionAllowed(true);
		tablaVueloSeleccVuelta.setBounds(10, 10, 50, 50);
		scrTablaVueloSeleccVuelta.setViewportView(tablaVueloSeleccVuelta);
		tablaVueloSeleccVuelta.setModel(tablaVueloSeleccModelVuelta);
		tablaVueloSeleccVuelta.setAutoCreateRowSorter(true);
		
		
	}
	
	//-------------------------------- FIN INIT GUI -----------------------------------------------------------
		
	private void llenarCiudadesOrigen() {
		
		try {
		// Genero los parquimetros y ubicaciones asociadas con el inpector
			String ciudadesO = "SELECT DISTINCT a.ciudad\r\n" + 
					"		FROM vuelos_programados vp NATURAL JOIN aeropuertos a\r\n" + 
					"		WHERE vp.aeropuerto_salida = a.codigo;";
			
			ResultSet rs1 = cnx.consulta(ciudadesO);
					
			String aux;
			cbxCiudadOrigen.removeAll();
			ciudadesOrigen = new LinkedList<Ciudad>();
					
			// Agrego los parquimetros a la lista de parquimetros y al comboBox
			while(rs1.next())
			{
				ciudadesOrigen.addLast(new Ciudad(rs1.getString("ciudad")));
				aux = ciudadesOrigen.getLast().getNombre();
				cbxCiudadOrigen.addItem(aux);
			}
		}
		catch(SQLException ex)
		{
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	
	private void llenarCiudadesDestino() {
		
		try {
		// Genero los parquimetros y ubicaciones asociadas con el inpector
			String ciudadesD = "SELECT DISTINCT a.ciudad\r\n" + 
					"		FROM vuelos_programados vp NATURAL JOIN aeropuertos a\r\n" + 
					"		WHERE vp.aeropuerto_llegada = a.codigo;";

			ResultSet rs1 = cnx.consulta(ciudadesD);
					
			String aux;
			cbxCiudadDestino.removeAll();
			ciudadesDestino = new LinkedList<Ciudad>();
					
			// Agrego los parquimetros a la lista de parquimetros y al comboBox
			while(rs1.next())
			{
				ciudadesDestino.addLast(new Ciudad(rs1.getString("ciudad")));
				aux = ciudadesDestino.getLast().getNombre();
				cbxCiudadDestino.addItem(aux);
			}
		}
		catch(SQLException ex)
		{
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	
	private void buscarVuelos()
	{	
		try {
            ciudadOrigenActual = (String) cbxCiudadOrigen.getSelectedItem();
            ciudadDestinoActual = (String) cbxCiudadDestino.getSelectedItem();
            String fechaIda = frmtdtxtfldFechaIda.getText();

            String query = "SELECT DISTINCT VUELO_NRO,AEROP_SALIDA,HORA_SALIDA,AEROP_LLEGADA,HORA_LLEGADA,MODELO,TIEMPO_ESTIMADO_DE_VUELO\r\n" +
                    "	FROM vuelos_disponibles\r\n" +
                    "	WHERE CIUDAD_SALIDA='" + ciudadOrigenActual + "' AND CIUDAD_LLEGADA='" + ciudadDestinoActual + "' AND FECHA='" + fechaIda + "';";

            ResultSet rs = cnx.consulta(query);

            this.tablaVuelosIdaModel.setRowCount(0);
            int i = 0;
            // Cargo los vuelos de IDA en el primer JTable
            while (rs.next()) {
                String vueloNro = (rs.getString("VUELO_NRO"));
                String aeropSalida = rs.getString("AEROP_SALIDA");
                String horaSalida = rs.getString("HORA_SALIDA");
                String aeropLlegada = rs.getString("AEROP_LLEGADA");
                String horaLlegada = rs.getString("HORA_LLEGADA");
                String modeloAvion = rs.getString("MODELO");
                String tiempoVuelo = (rs.getString("TIEMPO_ESTIMADO_DE_VUELO"));
                this.tablaVuelosIdaModel.setRowCount(i + 1);
                this.tablaVuelosIda.setValueAt(vueloNro, i, 0);
                this.tablaVuelosIda.setValueAt(aeropSalida, i, 1);
                this.tablaVuelosIda.setValueAt(horaSalida, i, 2);
                this.tablaVuelosIda.setValueAt(aeropLlegada, i, 3);
                this.tablaVuelosIda.setValueAt(horaLlegada, i, 4);
                this.tablaVuelosIda.setValueAt(modeloAvion, i, 5);
                this.tablaVuelosIda.setValueAt(tiempoVuelo, i, 6);
                i++;
            }

            if(idaYVuelta) {
            String fechaVuelta = frmtdtxtfldFechaVuelta.getText();

            String query2 = "SELECT DISTINCT VUELO_NRO,AEROP_SALIDA,HORA_SALIDA,AEROP_LLEGADA,HORA_LLEGADA,MODELO,TIEMPO_ESTIMADO_DE_VUELO\r\n" +
                    "	FROM vuelos_disponibles\r\n" +
                    "	WHERE CIUDAD_SALIDA='" + ciudadDestinoActual + "' AND CIUDAD_LLEGADA='" + ciudadOrigenActual + "' AND FECHA='" + fechaVuelta + "';";

            ResultSet rs2 = cnx.consulta(query2);

            this.tablaVuelosVueltaModel.setRowCount(0);
            int j = 0;
            // Cargo las multas en el JTable de Multas al final del Scroll Pane
            while (rs2.next()) {
                String vueloNro = (rs2.getString("VUELO_NRO"));
                String aeropSalida = rs2.getString("AEROP_SALIDA");
                String horaSalida = rs2.getString("HORA_SALIDA");
                String aeropLlegada = rs2.getString("AEROP_LLEGADA");
                String horaLlegada = rs2.getString("HORA_LLEGADA");
                String modeloAvion = rs2.getString("MODELO");
                String tiempoVuelo = (rs2.getString("TIEMPO_ESTIMADO_DE_VUELO"));
                this.tablaVuelosVueltaModel.setRowCount(j + 1);
                this.tablaVuelosVuelta.setValueAt(vueloNro, j, 0);
                this.tablaVuelosVuelta.setValueAt(aeropSalida, j, 1);
                this.tablaVuelosVuelta.setValueAt(horaSalida, j, 2);
                this.tablaVuelosVuelta.setValueAt(aeropLlegada, j, 3);
                this.tablaVuelosVuelta.setValueAt(horaLlegada, j, 4);
                this.tablaVuelosVuelta.setValueAt(modeloAvion, j, 5);
                this.tablaVuelosVuelta.setValueAt(tiempoVuelo, j, 6);
                j++;
            }
		}
            else
            {
                DefaultTableModel dm = tablaVuelosVueltaModel;
                int rowCount = dm.getRowCount();
               //Remove rows one by one from the end of the table
                for (int y = rowCount - 1; y >= 0; y--) 
                    dm.removeRow(y);
                dm = tablaVueloSeleccModelVuelta;
                rowCount = dm.getRowCount();
               //Remove rows one by one from the end of the table
                for (int y = rowCount - 1; y >= 0; y--) {
                    dm.removeRow(y);
                }
            }
		}
		catch(SQLException ex)
		{
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		limpiarTablas();
	}
	
	private void mostrarDatosVuelo(String nroVuelo,String fecha,DefaultTableModel tablaVueloSeleccModel, JTable tablaVueloSelecc ) {
		String query ="SELECT DISTINCT CLASE, ASIENTOS_DISPONIBLES, PRECIO FROM vuelos_disponibles WHERE vuelos_disponibles.VUELO_NRO='"+nroVuelo+"' AND FECHA='"+fecha+"';";
		ResultSet rs = cnx.consulta(query);
		
		tablaVueloSeleccModel.setRowCount(0);
		int i=0;
		// Cargo los vuelos de IDA en el primer JTable
		try {
			while(rs.next())
			{
				String clase = (rs.getString("CLASE"));
				String cantAsientos = rs.getString("ASIENTOS_DISPONIBLES");
				String precio = rs.getString("PRECIO");
				tablaVueloSeleccModel.setRowCount(i + 1);
				tablaVueloSelecc.setValueAt(clase, i, 0);
				tablaVueloSelecc.setValueAt(cantAsientos, i, 1);
				tablaVueloSelecc.setValueAt(precio, i, 2);
				i++;
			}
		} catch (SQLException ex)
		{
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	
	
	private class Ciudad{
		private String nombre;
		
		public Ciudad(String ciu){
			this.nombre = ciu;
			
		}
		
		public String getNombre(){
			return nombre;
		}
		
	}

	private boolean validarFecha(String fecha){
        //String regex = "^[0-3][0-9]/[0-3][0-9]/(?:[0-9][0-9])?[0-9][0-9]$";
        String regex = "((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])";

        Pattern pattern = Pattern.compile(regex);


            Matcher matcher = pattern.matcher(fecha);
            boolean salida=matcher.matches();
        return salida;
    }
	
	public void limpiarTablas() {
		tablaVuelosIda.clearSelection();
		tablaVuelosVuelta.clearSelection();
		tablaVueloSeleccIda.clearSelection();
		tablaVueloSeleccVuelta.clearSelection();
	}
	
	public void reservar() {
		if(tablasCompletas())
		{
			String tipoDNI = JOptionPane.showInputDialog("Ingrese el tipo de documento del pasajero");
			String nroDNI = JOptionPane.showInputDialog("Ingrese el numero de documento del pasajero");
			String nroVueloIda=tablaVuelosIda.getValueAt(tablaVuelosIda.getSelectedRow(),0).toString();
			String claseVueloIda=tablaVueloSeleccIda.getValueAt(tablaVueloSeleccIda.getSelectedRow(),0).toString();
			String query= "call reservar_ida('"+nroVueloIda+"', '"+frmtdtxtfldFechaIda.getText()+"', '"+claseVueloIda+"', '"+tipoDNI+"', "+nroDNI+", "+legajo+");";
			if(idaYVuelta)
			{String nroVueloVuelta=tablaVuelosVuelta.getValueAt(tablaVuelosVuelta.getSelectedRow(),0).toString();
			String claseVueloVuelta=tablaVueloSeleccVuelta.getValueAt(tablaVueloSeleccVuelta.getSelectedRow(),0).toString();
			query= "call reservar_ida_vuelta('"+nroVueloIda+"', '"+frmtdtxtfldFechaIda.getText()+"', '"+claseVueloIda+"', '"+nroVueloVuelta+"', '"+frmtdtxtfldFechaVuelta.getText()+"', '"+claseVueloVuelta+"', '"+tipoDNI+"', "+nroDNI+", "+legajo+");";
			}
					ResultSet rs=cnx.consulta(query);
					String res="";
					try {
						while(rs.next())
						{
							res += (rs.getString("resultado"));
						}
					} catch (SQLException ex)
					{
						System.out.println("SQLException: " + ex.getMessage());
						System.out.println("SQLState: " + ex.getSQLState());
						System.out.println("VendorError: " + ex.getErrorCode());
					}
					
					JOptionPane.showMessageDialog(null, res,
			                "Resultado Reserva",
			                JOptionPane.INFORMATION_MESSAGE);
		}else
		{
			JOptionPane.showMessageDialog(null, "Seleccion todos los datos correctamente",
	                "EROR",
	                JOptionPane.ERROR_MESSAGE);
		}
	limpiarTablas();	
	}
		public boolean tablasCompletas() {
			int rowSelectedVueloIda=tablaVuelosIda.getSelectedRow();
			int rowSelectedClaseIda=tablaVueloSeleccIda.getSelectedRow();
			String fechaVueloIda=frmtdtxtfldFechaIda.getText();
			int rowSelectedVueloVuelta=tablaVuelosVuelta.getSelectedRow();
			int rowSelectedClaseVuelta=tablaVueloSeleccVuelta.getSelectedRow();
			String fechaVueloVuelta=frmtdtxtfldFechaVuelta.getText();
			if(rowSelectedVueloIda==-1 || rowSelectedClaseIda==-1 || !validarFecha(fechaVueloIda))
				return false;
			else
				if(idaYVuelta)
					{if(rowSelectedVueloVuelta==-1 || rowSelectedClaseVuelta==-1 || !validarFecha(fechaVueloVuelta))
					return false;}
			return true;
		}
			
}