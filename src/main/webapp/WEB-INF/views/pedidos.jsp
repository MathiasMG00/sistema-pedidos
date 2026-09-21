<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <!doctype html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>Sistema de Pedidos - ISIL</title>
                <c:url var="cssUrl" value="/assets/css/app.css" />
                <link rel="stylesheet" href="${cssUrl}">
            </head>

            <body>
                <h1>Sistema de Pedidos</h1>

                <p class="nota">
                    Flujo: Navegador → PedidoServlet → PedidoService (EJB) → JPA → H2
                </p>

                <c:if test="${not empty error}">
                    <div class="aviso aviso--error" role="alert">
                        <span>
                            <c:out value="${error}" />
                        </span>
                    </div>
                </c:if>
                <!-- Mensaje de error que se reciben del fetch de JavaScript (PUT y DELETE) -->
                <div id="errorOperacion" class="aviso aviso--error" role="alert" hidden>
                    <strong>No se pudo completar la operación</strong>
                    <span id="errorOperacionTexto"></span>
                </div>

                <c:if test="${not empty param.creado}">
                    <div class="aviso aviso--creado" role="status">
                        <span>
                            Pedido #
                            <c:out value="${param.creado}" />
                            registrado correctamente.
                        </span>
                    </div>
                </c:if>
                <c:if test="${not empty param.actualizado}">
                    <div class="aviso aviso--actualizado" role="status">
                        <span>
                            Pedido #
                            <c:out value="${param.actualizado}" />
                            actualizado correctamente.
                        </span>
                    </div>
                </c:if>
                <c:if test="${not empty param.eliminado}">
                    <div class="aviso aviso--eliminado" role="status">
                        <span>
                            Pedido #
                            <c:out value="${param.eliminado}" />
                            eliminado correctamente.
                        </span>
                    </div>
                </c:if>

                <!-- Sección de registro de pedidos -->
                <c:set var="editando" value="${not empty pedidoEditar}" />
                <c:url var="pedidosUrl" value="/pedidos" />

                <!-- Usamos el mismo formulario para registrar y editar.
                Cambiando el título y el valor de los campos según corresponda. -->

                <h2>${editando ? 'Editar pedido' : 'Registrar pedido'}</h2>

                <form id="formPedido" method="post" action="${pedidosUrl}" class="form-grid">

                    <input type="hidden" name="pedidoId" value="${pedidoEditar.id}">

                    <label>
                        Cliente
                        <input name="cliente" required maxlength="120" placeholder="Ej. Ana Torres"
                            value="<c:out value='${editando ? pedidoEditar.cliente : clienteIngresado}'/>">
                    </label>
                    <label>
                        Producto
                        <select name="productoId" required>

                            <!-- Seleccionamos el producto si estamos editando -->
                            <c:forEach var="producto" items="${productos}">
                                <option value="${producto.id}" ${editando && producto.id==pedidoEditar.producto.id
                                    ? 'selected' : '' }>
                                    <c:out value="${producto.nombre}" />
                                    - S/
                                    <fmt:formatNumber value="${producto.precio}" minFractionDigits="2"
                                        maxFractionDigits="2" />
                                    - stock:
                                    <c:out value="${producto.stock}" />
                                </option>
                            </c:forEach>
                        </select>
                    </label>

                    <label>
                        Cantidad
                        <input name="cantidad" type="number" min="1" value="${editando ? pedidoEditar.cantidad 
                                : (empty cantidadIngresada ? 1 : cantidadIngresada)}" required>
                    </label>

                    <button id="btnSubmit" type="submit" class="${editando ? 'btn-actualizar' : ''}">
                        ${editando ? 'Actualizar' : 'Registrar'}
                    </button>

                    <c:if test="${editando}">
                        <a href="${pedidosUrl}">Cancelar</a>
                    </c:if>
                </form>

                <h2>Pedidos registrados</h2>

                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Cliente</th>
                            <th>Producto</th>
                            <th>Cantidad</th>
                            <th>Total</th>
                            <th>Fecha</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="pedido" items="${pedidos}">
                            <tr>
                                <td>
                                    <c:out value="${pedido.id}" />
                                </td>
                                <td>
                                    <c:out value="${pedido.cliente}" />
                                </td>
                                <td>
                                    <c:out value="${pedido.producto.nombre}" />
                                </td>
                                <td>
                                    <c:out value="${pedido.cantidad}" />
                                </td>
                                <td>
                                    S/
                                    <fmt:formatNumber value="${pedido.total}" minFractionDigits="2"
                                        maxFractionDigits="2" />
                                </td>
                                <td>
                                    <c:out value="${pedido.fecha}" />
                                </td>
                                <td>
                                    <c:url var="editarUrl" value="/pedidos">
                                        <c:param name="action" value="editar" />
                                        <c:param name="pedidoId" value="${pedido.id}" />
                                    </c:url>
                                    <div class="acciones-pedido">
                                        <a href="${editarUrl}" class="accion accion--editar">Editar</a>

                                        <button type="button" class="accion accion--eliminar btn-eliminar"
                                            data-pedido-id="${pedido.id}">
                                            Eliminar
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty pedidos}">
                            <tr>
                                <td colspan="6">
                                    Aún no hay pedidos.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>


                <script>
                    // Script para mostrar los mensajes de error del fetch 
                    const avisoError = document.getElementById("errorOperacion");
                    const textoError = document.getElementById("errorOperacionTexto");

                    function mostrarErrorOperacion(texto) {
                        textoError.textContent = texto;
                        avisoError.hidden = false;

                        avisoError.scrollIntoView({
                            behavior: "smooth",
                            block: "nearest"
                        });
                    }

                    function limpiarErrorOperacion() {
                        avisoError.hidden = true;
                        textoError.textContent = "";
                    }

                    // Script de JavaScript para manejar la edicion de pedidos para el PUT
                    const formulario = document.getElementById("formPedido");

                    formulario.addEventListener("submit", async function (event) {
                        const campos = formulario.elements;
                        const pedidoId = campos.namedItem("pedidoId").value;

                        if (!pedidoId) return;

                        event.preventDefault();
                        limpiarErrorOperacion();

                        const btn = document.getElementById("btnSubmit");

                        btn.disabled = true;

                        try {
                            const respuesta = await fetch(formulario.action, {
                                method: "PUT",
                                headers: {
                                    "Content-Type": "application/json"
                                },
                                body: JSON.stringify({
                                    pedidoId: pedidoId,
                                    cliente: campos.namedItem("cliente").value,
                                    productoId: campos.namedItem("productoId").value,
                                    cantidad: campos.namedItem("cantidad").value
                                })
                            });

                            const texto = await respuesta.text();

                            if (!respuesta.ok) {
                                mostrarErrorOperacion(texto || "No se pudo actualizar el pedido.");
                                return;
                            }

                            const destino = new URL(formulario.action);
                            destino.searchParams.set("actualizado", pedidoId);
                            window.location.assign(destino.href);

                        } catch (error) {
                            mostrarErrorOperacion(
                                "No se pudo confirmar el resultado. Recarga el listado para comprobar el pedido antes de volver a intentarlo."
                            );
                        } finally {
                            btn.disabled = false;
                        }
                    });

                    // Script de JavaScript para manejar la eliminacion de un pedido para el DELETE
                    document.querySelectorAll(".btn-eliminar").forEach(function (btn) {
                        btn.addEventListener("click", async function () {
                            const pedidoId = btn.dataset.pedidoId;

                            const confirmado = window.confirm(
                                "¿Deseas eliminar el pedido #" + pedidoId
                                + "? Se devolverán sus unidades al stock."
                            );
                            if (!confirmado) return;

                            btn.disabled = true;

                            try {
                                const url = new URL(formulario.action);
                                url.searchParams.set("pedidoId", pedidoId);

                                const respuesta = await fetch(url.href, {
                                    method: "DELETE"
                                });

                                const texto = await respuesta.text();

                                if (!respuesta.ok) {
                                    mostrarErrorOperacion(texto || "No se pudo eliminar el pedido.");
                                    return;
                                }

                                const destino = new URL(formulario.action);
                                destino.searchParams.set("eliminado", pedidoId);
                                window.location.assign(destino.href);

                            } catch (error) {
                                mostrarErrorOperacion(
                                    "No se pudo confirmar el resultado. Recarga el listado para comprobar el pedido antes de volver a intentarlo."
                                );
                            } finally {
                                btn.disabled = false;
                            }
                        });
                    });

                </script>
            </body>

            </html>