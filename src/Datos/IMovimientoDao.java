package Datos;

import java.util.ArrayList;
import java.util.List;

import Dominio.Cuenta;
import Dominio.Movimiento;



public interface IMovimientoDao {
	
	boolean crearMovimiento(Movimiento movimiento);
    List<Movimiento> listarMovimiento();
    Movimiento obtenerMovimientoPorId(int idMovimiento);
	Cuenta obtenerCuentaPorId(int idCuenta);
	ArrayList<Movimiento> obtenerUltimasTransferencias(int idCuenta);
}