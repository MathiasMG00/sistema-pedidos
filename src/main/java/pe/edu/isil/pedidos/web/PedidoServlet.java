package pe.edu.isil.pedidos.web;

import jakarta.ejb.EJB;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.isil.pedidos.domain.Pedido;
import pe.edu.isil.pedidos.service.PedidoService;
import pe.edu.isil.pedidos.service.PedidoException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/pedidos")
public class PedidoServlet extends HttpServlet {
    @EJB
    private PedidoService pedidoService;

    /**
     * Maneja las solicitudes GET para mostrar la página de pedidos.
     * 
     * @param request  Objeto HttpServletRequest que contiene la solicitud del
     *                 cliente.
     * @param response Objeto HttpServletResponse que contiene la respuesta al
     *                 cliente.
     * @throws ServletException Si ocurre un error en el servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String action = request.getParameter("action");

            if ("editar".equals(action)) {

                Long pedidoId = Long.valueOf(request.getParameter("pedidoId"));
                Pedido pedido = pedidoService.buscarPedido(pedidoId);

                if (pedido == null) {
                    mostrarErrorFound(request, response, "Pedido no encontrado.");
                    return;
                } else {
                    request.setAttribute("pedidoEditar", pedido);
                }
            }

            cargarDatosVista(request);
            request.getRequestDispatcher("/WEB-INF/views/pedidos.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            mostrarErrorNegocio(request, response, "ID de pedido inválido.");
        } catch (PedidoException e) {
            mostrarErrorNegocio(request, response, e.getMessage());
        } catch (RuntimeException e) {
            mostrarErrorGeneral(request, response);
        }
    }

    /**
     * Maneja las solicitudes POST para registrar un nuevo pedido.
     * 
     * @param request  Objeto HttpServletRequest que contiene la solicitud del
     *                 cliente.
     * @param response Objeto HttpServletResponse que contiene la respuesta al
     *                 cliente.
     * @throws ServletException Si ocurre un error en el servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            String cliente = request.getParameter("cliente");
            Long productoId = Long.valueOf(request.getParameter("productoId"));
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));

            Pedido pedido = pedidoService.registrarPedido(cliente, productoId, cantidad);

            // Patrón PRG (Post/Redirect/Get) para evitar reenvíos de formularios
            response.sendRedirect(request.getContextPath() + "/pedidos?creado="
                    + pedido.getId());

        } catch (NumberFormatException e) {
            mostrarErrorNegocio(request, response, "Producto o cantidad inválidos.");
        } catch (PedidoException e) {
            mostrarErrorNegocio(request, response, e.getMessage());
        } catch (RuntimeException e) {
            mostrarErrorGeneral(request, response);
        }
    }

    // Metodo PUT, para manejar las actualizaciones
    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            JsonObject datos;
            try (JsonReader rd = Json.createReader(request.getReader())) {
                datos = rd.readObject();
            }

            Long pedidoId = Long.valueOf(datos.getString("pedidoId"));
            String cliente = datos.getString("cliente");
            Long productoId = Long.valueOf(datos.getString("productoId"));
            int cantidad = Integer.parseInt(datos.getString("cantidad"));

            if (pedidoService.buscarPedido(pedidoId) == null) {
                mostrarErrorFound(request, response, "Pedido no encontrado.");
                return;
            }

            pedidoService.actualizarPedido(pedidoId, cliente, productoId, cantidad);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Pedido actualizado correctamente.");

        } catch (NumberFormatException e) {
            mostrarErrorNegocio(request, response, "Id de pedido inválido.");
        } catch (PedidoException e) {
            mostrarErrorNegocio(request, response, e.getMessage());
        } catch (RuntimeException e) {
            mostrarErrorGeneral(request, response);
        }
    }

    // Metodo DELETE, para manejar la eliminación de pedidos
    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            Long pedidoId = Long.valueOf(request.getParameter("pedidoId"));

            if (pedidoService.buscarPedido(pedidoId) == null) {
                mostrarErrorFound(request, response, "Pedido no encontrado.");
                return;
            }

            pedidoService.eliminarPedido(pedidoId);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Pedido eliminado correctamente.");

        } catch (NumberFormatException e) {
            mostrarErrorNegocio(request, response, "Id de pedido inválido.");
        } catch (PedidoException e) {
            mostrarErrorNegocio(request, response, e.getMessage());
        } catch (RuntimeException e) {
            mostrarErrorGeneral(request, response);
        }
    }

    /**
     * Carga los datos necesarios para la vista de pedidos.
     * 
     * @param request Objeto HttpServletRequest que contiene la solicitud del
     *                cliente.
     */
    private void cargarDatosVista(HttpServletRequest request) {
        request.setAttribute("productos", pedidoService.listarProductos());
        request.setAttribute("pedidos", pedidoService.listarPedidos());
    }

    /**
     * Muestra un mensaje de error en la vista de pedidos.
     * 
     * @param request  Objeto HttpServletRequest que contiene la solicitud del
     *                 cliente.
     * @param response Objeto HttpServletResponse que contiene la respuesta al
     *                 cliente.
     * @param mensaje  Mensaje de error a mostrar.
     * @throws ServletException Si ocurre un error en el servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    private void mostrarErrorNegocio(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        request.setAttribute("clienteIngresado", request.getParameter("cliente"));
        request.setAttribute("cantidadIngresada", request.getParameter("cantidad"));
        request.setAttribute("error", mensaje);

        cargarDatosVista(request);

        request.getRequestDispatcher("/WEB-INF/views/pedidos.jsp")
                .forward(request, response);
    }

    /**
     * Muestra un mensaje de error general en la vista de error.
     * 
     * @param request  Objeto HttpServletRequest que contiene la solicitud del
     *                 cliente.
     * @param response Objeto HttpServletResponse que contiene la respuesta al
     *                 cliente.
     * @throws ServletException Si ocurre un error en el servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    private void mostrarErrorGeneral(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        request.setAttribute("error", "Ocurrió un error interno al procesar la solicitud.");

        request.getRequestDispatcher("/WEB-INF/views/error.jsp")
                .forward(request, response);
    }

    /**
     * Muestra un mensaje de error de recurso no encontrado en la vista de error.
     */
    private void mostrarErrorFound(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);

        request.setAttribute("error", mensaje);

        request.getRequestDispatcher("/WEB-INF/views/error.jsp")
                .forward(request, response);
    }
}