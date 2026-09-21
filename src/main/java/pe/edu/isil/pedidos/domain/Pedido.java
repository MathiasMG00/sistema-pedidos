package pe.edu.isil.pedidos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String cliente;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    @Column(nullable = false)
    private int cantidad;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;
    @Column(nullable = false)
    private LocalDateTime fecha;

    protected Pedido() {
        // Constructor requerido por JPA.
    }

    public Pedido(String cliente, Producto producto, int cantidad, BigDecimal total) {
        this.cliente = cliente;
        this.producto = producto;
        this.cantidad = cantidad;
        this.total = total;
        this.fecha = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    // Metodo para actualizar los datos
    public void actualizarPedido(String cliente, Producto producto,
            int cantidad, BigDecimal total) {
        if (cliente == null || cliente.isBlank()) {
            throw new IllegalArgumentException("El cliente es obligatorio.");
        }

        if (producto == null) {
            throw new IllegalArgumentException("El producto es obligatorio.");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        if (total == null || total.signum() < 0) {
            throw new IllegalArgumentException("El total debe ser un valor mayor o igual a cero.");
        }

        this.cliente = cliente.trim();
        this.producto = producto;
        this.cantidad = cantidad;
        this.total = total;
    }
}
