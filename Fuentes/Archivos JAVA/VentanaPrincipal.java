import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaPrincipal extends JFrame {

    private VentanaAdmin ventanaAdmin;
    private VentanaEmpleado ventanaEmpleado;
    private ConexionDB conexion;
    private JTextField Usuario;
    private JPasswordField Contraseña;
    protected JComboBox<String> tipoUsuario;
    
    public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaPrincipal ventana = new VentanaPrincipal();
					ventana.setLocationRelativeTo(null);
					ventana.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
    public VentanaPrincipal()
    {
        conexion = new ConexionDB();
        inicializar();
    }

    private void inicializar()
    {
        //Creo Ventana
        this.setType(Type.POPUP);
        this.setTitle("Ingreso al Sistema");
        this.setResizable(false);
        this.setBounds(100, 100, 406, 250);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout(0, 0));

        //Creo panel que contiene todos los paneles
        JPanel PanelLoginData = new JPanel();
        this.getContentPane().add(PanelLoginData, BorderLayout.CENTER);
        PanelLoginData.setLayout(new GridLayout(5, 1, 0, 0));

        //Inserto etiqueta en la primer fila de la grilla
        JLabel lblIngreseLosDatos = new JLabel("Ingrese sus datos de inicio de sesion:");
        PanelLoginData.add(lblIngreseLosDatos);
        lblIngreseLosDatos.setFont(new Font("Arial", Font.BOLD, 13));
        lblIngreseLosDatos.setHorizontalAlignment(SwingConstants.CENTER);

        //Inserto ComboBox para seleccionar el tipo de usuario
        JPanel panelTipoLogin = new JPanel();
        FlowLayout flowLayout_3 = (FlowLayout) panelTipoLogin.getLayout();
        flowLayout_3.setHgap(25);
        PanelLoginData.add(panelTipoLogin);

        tipoUsuario = new JComboBox<String>();
        tipoUsuario.addItem("Administrador");
        tipoUsuario.addItem("Empleado");
        panelTipoLogin.add(tipoUsuario);

        //Inserto la etiqueta usuario y el jtextfield
        JPanel panelUserLogin = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) panelUserLogin.getLayout();
        flowLayout_1.setHgap(25);
        PanelLoginData.add(panelUserLogin);

        JLabel lblUserLogin = new JLabel("Usuario:");
        panelUserLogin.add(lblUserLogin);

        Usuario = new JTextField();
        Usuario.setToolTipText("Empleados ingresen Nº de Legajo");

        Usuario.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e){

                ingresar();

            }});

        panelUserLogin.add(Usuario);
        Usuario.setColumns(10);

        //Inserto la etiqueta contraseÃ±a y el jpassswordfield
        JPanel panelPasswordLogin = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panelPasswordLogin.getLayout();
        PanelLoginData.add(panelPasswordLogin);

        JLabel lblPasswordLogin = new JLabel("Contraseña:");
        panelPasswordLogin.add(lblPasswordLogin);

        Contraseña = new JPasswordField();
        Contraseña.setColumns(10);

        Contraseña.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e){

                ingresar();

            }});

        panelPasswordLogin.add(Contraseña);

        //Inserto los botones de aceptar y cancelar
        JPanel PanelLoginButtons = new JPanel();
        PanelLoginData.add(PanelLoginButtons);

        JButton btnAceptarLogin = new JButton("Aceptar");

        btnAceptarLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {

                ingresar();

            }
        });

        PanelLoginButtons.add(btnAceptarLogin);

        JButton btnCancelarLogin = new JButton("Cancelar");

        btnCancelarLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });

        PanelLoginButtons.add(btnCancelarLogin);

        this.setVisible(true);
    }

    private void ingresar()
    {
        if (tipoUsuario.getSelectedItem().equals("Administrador"))
        {
            if(Usuario.getText().equals("admin"))
            {
                String clave = new String(Contraseña.getPassword());
                String state = conexion.conectarAdmin("admin", clave);
                if(state!=null)
                {
                    new VentanaAdmin(conexion);

                    this.dispose();
                }
                else
                {
                    JOptionPane.showMessageDialog(null,"El usuario o contraseña ingresados son incorrectos");
                }
            }
            else
            {
                JOptionPane.showMessageDialog(null,"El usuario o contraseña ingresados son incorrectos");
                Contraseña.setText("");
            }
        }

        if (tipoUsuario.getSelectedItem().equals("Empleado"))
        {
            String clave = new String(Contraseña.getPassword());
            String state = conexion.conectarEmpleado(Usuario.getText(), clave);
            if(state!=null)
            {
                new VentanaEmpleado(conexion,Usuario.getText());
                this.dispose();
            }
            else
            {
                JOptionPane.showMessageDialog(null,"El usuario o contraseña ingresados son incorrectos");
                Contraseña.setText("");
            }
        }

    }
}