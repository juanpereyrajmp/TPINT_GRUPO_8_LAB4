package DatosImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Datos.IClienteDao;
import Dominio.Cliente;
import Dominio.Cuenta;
import Dominio.Provincia;




public class ClienteDao implements IClienteDao{
	
	private static ClienteDao instancia = null;
	private static final String insert = "insert into cliente(nombre_usuario, dni, cuil, nombre, apellido, sexo, nacionalidad, nacimiento ,domicilio, localidad, id_provincia, email, telefono) VALUES(?, ?, ?,?, ?, ?,?,?, ?, ?,?, ?, ?)";
	
	private static final String select = "SELECT * FROM Cliente WHERE Activo = 1";
	private static final String selectById ="Select * from Cliente where Activo =1 and id_cliente = ?";
	private static final String selectByDni ="Select * from Cliente where Activo =1 and dni = ?";
	private static final String selectByNombreUser ="Select * from Cliente where Activo = 1 and nombre_usuario = ?";
	private static final String delete = "UPDATE cliente SET activo = 0 WHERE id_cliente = ?";
	private static final String Update= "UPDATE cliente SET nombre = ?, " +
            "apellido = ?, " +
            "sexo = ?, " +
            "nacionalidad = ?, " +
            "domicilio = ?, " +
            "localidad = ?, " +
            "telefono = ?, " +
            "nacimiento = ?, "+
            "id_provincia = ? "+
            "WHERE id_cliente = ? AND activo = 1; ";
	
	private static final String selectByCuentaCliente="SELECT c.id_cuenta, c.tipo AS tipo_cuenta, c.creacion AS fecha_creacion, c.cbu, c.saldo, c.activa, cl.id_cliente, cl.nombre, cl.apellido, cl.email, cl.telefono" + 
			                                          "FROM cuenta c INNER JOIN cliente cl ON c.id_cliente = cl.id_cliente" + 
			                                           "WHERE cl.id_cliente=?";
	
	public ClienteDao() {}
	
	public static ClienteDao ObtenerInstancia() {
		if(instancia == null) {
			instancia = new ClienteDao();
		}
		return instancia;
	}
	@Override
	public Cliente Modificar(Cliente cliente) {

	    PreparedStatement statement;
	    Connection conexion = Conexion.getConexion().getSQLConexion();
	    boolean isUpdateExitoso = false;
    

	    try {
	        statement = conexion.prepareStatement(Update);
	     
	        
	        statement.setString(1, cliente.getNombre());
	        statement.setString(2, cliente.getApellido());
	        statement.setInt(3, cliente.getSexo());
	        statement.setString(4, cliente.getNacionalidad());
	        statement.setString(5, cliente.getDomicilio());
	        statement.setString(6, cliente.getLocalidad());
	        statement.setString(7, cliente.getTelefono());
	        statement.setDate(8, java.sql.Date.valueOf(cliente.getNacimiento())); 
	        statement.setInt(9, cliente.getProvincia().getId_provincia());
	        statement.setInt(10, cliente.getId());
	        
	        int rowsUpdated = statement.executeUpdate();
	        if (rowsUpdated > 0) {
	            isUpdateExitoso = true;
	        }

	        if (isUpdateExitoso) {
	            conexion.commit();
	            return cliente;
	        } else {
	            conexion.rollback();
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        try {
	            conexion.rollback();
	        } catch (Exception rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
	    }

	    return cliente;
	}

	public boolean insert(String nombre_usuario, int dni, String cuil, String nombre, String apellido, int sexo,
            String nacionalidad, LocalDate nacimiento, String domicilio, String localidad, int id_provincia, String email,
            String telefono) 
    {

        PreparedStatement statement;
        Connection conexion = Conexion.getConexion().getSQLConexion();
        boolean isInsertExitoso = false;

        try 
        {
            statement = conexion.prepareStatement(insert);
            statement.setString(1, nombre_usuario);
            statement.setInt(2, dni);
            statement.setString(3, cuil);
            statement.setString(4, nombre);
            statement.setString(5, apellido);
            statement.setInt(6, sexo);
            statement.setString(7, nacionalidad);
            statement.setDate(8, java.sql.Date.valueOf(nacimiento)); //local date a DATE 
            statement.setString(9, domicilio);
            statement.setString(10, localidad);
            statement.setInt(11, id_provincia);
            statement.setString(12, email);
            statement.setString(13, telefono);

            int rows = statement.executeUpdate(); 
            if(rows>0)
            {
                conexion.commit();
                isInsertExitoso = true;
            }
        }

        catch (SQLException e) 
            {
                e.printStackTrace();
                try 
                {
                    conexion.rollback();
                } 
                catch (SQLException e1) 
                {
                    e1.printStackTrace();
                }
            }

            return isInsertExitoso;
}
	@Override
	public List<Cliente> listar() {
		PreparedStatement statement;
        ResultSet resultSet;
        ArrayList<Cliente> clientes = new ArrayList<>();
        
        Connection conexion = Conexion.getConexion().getSQLConexion();
        
        try {
            statement = conexion.prepareStatement(select);
            resultSet = statement.executeQuery();
            
            while(resultSet.next()) {
                Cliente cliente = new Cliente();
                cliente.setNombreUsuario(resultSet.getString("nombre_usuario"));
                cliente.setDni(resultSet.getInt("dni"));
                cliente.setCuil(resultSet.getString("cuil"));
                
                cliente.setNombre(resultSet.getString("nombre"));
                cliente.setApellido(resultSet.getString("apellido"));
                cliente.setSexo(resultSet.getInt("sexo"));
                cliente.setNacionalidad(resultSet.getString("nacionalidad"));
                Provincia prov = new Provincia(resultSet.getInt("id_provincia"),"");
                cliente.setProvincia(prov);
                cliente.setDomicilio(resultSet.getString("domicilio"));
                cliente.setLocalidad(resultSet.getString("localidad"));
                cliente.setEmail(resultSet.getString("email"));
                cliente.setTelefono(resultSet.getString("telefono"));
                cliente.setActivo(resultSet.getBoolean("activo"));
                cliente.setNacimiento(resultSet.getDate("nacimiento").toLocalDate());
                cliente.setId(resultSet.getInt("id_cliente"));
                
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return clientes;
	}
	@Override
	public Cliente getClienteById(int Id) {
		PreparedStatement statement;
        ResultSet resultSet;
       
        
        Connection conexion = Conexion.getConexion().getSQLConexion();
        Cliente cliente = new Cliente();
        try {
            statement = conexion.prepareStatement(selectById);
            statement.setInt(1,Id);
            resultSet = statement.executeQuery();
           
            if(resultSet.next() ) {
                cliente.setNombreUsuario(resultSet.getString("nombre_usuario"));
                cliente.setDni(resultSet.getInt("dni"));
                cliente.setCuil(resultSet.getString("cuil"));
                Provincia prov = new Provincia(resultSet.getInt("id_provincia"),"");
                cliente.setProvincia(prov);
                cliente.setNombre(resultSet.getString("nombre"));
                cliente.setApellido(resultSet.getString("apellido"));
                cliente.setSexo(resultSet.getInt("sexo"));
                cliente.setNacionalidad(resultSet.getString("nacionalidad"));
                cliente.setDomicilio(resultSet.getString("domicilio"));
                cliente.setLocalidad(resultSet.getString("localidad"));
                cliente.setEmail(resultSet.getString("email"));
                cliente.setTelefono(resultSet.getString("telefono"));
                cliente.setNacimiento(resultSet.getDate("nacimiento").toLocalDate());
                cliente.setActivo(resultSet.getBoolean("activo"));
                cliente.setId(Id);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cliente;
	}
	@Override
	public Cliente getClienteByDni(int dni) {
		PreparedStatement statement;
        ResultSet resultSet;
       
        
        Connection conexion = Conexion.getConexion().getSQLConexion();
        Cliente cliente = new Cliente();
        try {
            statement = conexion.prepareStatement(selectByDni);
            statement.setInt(1,dni);
            resultSet = statement.executeQuery();
           
            if(resultSet.next() ) {
                cliente.setNombreUsuario(resultSet.getString("nombre_usuario"));
                cliente.setDni(resultSet.getInt("dni"));
                cliente.setCuil(resultSet.getString("cuil"));
                Provincia prov = new Provincia(resultSet.getInt("id_provincia"),"");
                cliente.setProvincia(prov);
                cliente.setNombre(resultSet.getString("nombre"));
                cliente.setApellido(resultSet.getString("apellido"));
                cliente.setSexo(resultSet.getInt("sexo"));
                cliente.setNacionalidad(resultSet.getString("nacionalidad"));
                cliente.setDomicilio(resultSet.getString("domicilio"));
                cliente.setLocalidad(resultSet.getString("localidad"));
                cliente.setEmail(resultSet.getString("email"));
                cliente.setTelefono(resultSet.getString("telefono"));
                cliente.setNacimiento(resultSet.getDate("nacimiento").toLocalDate());
                cliente.setActivo(resultSet.getBoolean("activo"));
                
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cliente;
	}
	
	public boolean eliminarCliente(int idCliente) {
	    PreparedStatement statement;
	    Connection conexion = Conexion.getConexion().getSQLConexion();
	    boolean isDeleteExitoso = false;

	    try {
	        statement = conexion.prepareStatement(delete);
	        statement.setInt(1, idCliente);
	        
	        int rowsUpdated = statement.executeUpdate();
	        
	        if (rowsUpdated > 0) {
	            conexion.commit();
	            isDeleteExitoso = true;
	        } else {
	            conexion.rollback();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        try {
	            conexion.rollback();
	        } catch (SQLException rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
	    }

	    return isDeleteExitoso;
	}

	public Cliente getClienteByNombreUsuario(String nombre_usuario) {
		PreparedStatement statement;
        ResultSet resultSet;
       
        
        Connection conexion = Conexion.getConexion().getSQLConexion();
        Cliente cliente = new Cliente();
        try {
            statement = conexion.prepareStatement(selectByNombreUser);
            statement.setString(1,nombre_usuario);
            resultSet = statement.executeQuery();
           
            if(resultSet.next() ) {
                cliente.setNombreUsuario(resultSet.getString("nombre_usuario"));
                cliente.setDni(resultSet.getInt("dni"));
                cliente.setCuil(resultSet.getString("cuil"));
                Provincia prov = new Provincia(resultSet.getInt("id_provincia"),"");
                cliente.setProvincia(prov);
                cliente.setNombre(resultSet.getString("nombre"));
                cliente.setApellido(resultSet.getString("apellido"));
                cliente.setSexo(resultSet.getInt("sexo"));
                cliente.setNacionalidad(resultSet.getString("nacionalidad"));
                cliente.setDomicilio(resultSet.getString("domicilio"));
                cliente.setLocalidad(resultSet.getString("localidad"));
                cliente.setEmail(resultSet.getString("email"));
                cliente.setTelefono(resultSet.getString("telefono"));
                cliente.setActivo(resultSet.getBoolean("activo"));
                cliente.setId(resultSet.getInt("id_cliente"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cliente;
	}
	
	
	
	
	
}