# Sistema de pedidos
Proyecto realizado para el curso de Desarrollo de Aplicaciones Empresariales Avanzado.
A partir del proyecto entregado por el docente, se agregaron las opciones de editar y eliminar pedidos, manteniendo el control del stock.

## Tecnologías utilizadas
- Java 21
- Jakarta EE 11
- JSP y Jakarta Tags
- Servlets
- JPA
- Base de datos H2
- Maven
- WildFly
- JavaScript y Fetch

## Requisitos
Para ejecutar el proyecto se necesita:
- JDK 21.
- Maven.
- WildFly compatible con Jakarta EE 11.
- Un navegador web.

## Cómo ejecutar el proyecto

1. Descargar o clonar este repositorio.
2. Abrir una terminal en la carpeta donde se encuentra `pom.xml`.
3. Compilar el proyecto:
   ```bash
   mvn clean package
   ```

4. Iniciar WildFly y desplegar el archivo generado:
   ```text
   target/sistema-pedidos.war
   ```

5. Abrir la aplicación en el navegador. Si WildFly está usando el puerto 8081:
   ```text
   http://localhost:8081/sistema-pedidos/pedidos
   ```
   
## Funcionalidades
- Registrar pedidos.
- Consultar los pedidos registrados.
- Editar cliente, producto y cantidad.
- Recalcular el total cuando cambia el producto o la cantidad.
- Eliminar pedidos con confirmación.
- Ajustar y reponer stock.
- Mostrar mensajes de éxito y error.

## Organización del proyecto
- `domain`: contiene las entidades `Pedido` y `Producto`.
- `service`: contiene las reglas de negocio y las validaciones.
- `web`: contiene el Servlet que recibe las solicitudes.
- `WEB-INF/views`: contiene las vistas JSP.
- `assets/css`: contiene los estilos de la aplicación.

El recorrido principal es:
Navegador → PedidoServlet → PedidoService → JPA/EntityManager → H2.

La JSP genera el HTML en el servidor. El Servlet recibe las solicitudes y el servicio se encarga de las reglas de negocio.

## Métodos HTTP

| Método | Operación |
|---|---|
| GET | Mostrar pedidos y cargar los datos para editar. |
| POST | Registrar un pedido. |
| PUT | Actualizar un pedido. |
| DELETE | Eliminar un pedido. |

Se utiliza el mismo formulario para registrar y editar.
El registro se envía mediante POST. La edición utiliza JavaScript con Fetch para enviar un PUT con los datos en formato JSON.
La eliminación utiliza Fetch con DELETE y envía el ID del pedido en la URL.

## Manejo del stock
Cuando se cambia la cantidad del mismo producto, solo se descuenta o devuelve la diferencia.
Si se cambia de producto, se devuelven las unidades al producto anterior y se descuentan del nuevo.
Al eliminar un pedido, sus unidades regresan al stock.
Los cambios del pedido y del stock se realizan dentro de una transacción. Si la operación falla, no deben quedar cambios parciales.

## Cómo probar las operaciones
1. Registrar un pedido y comprobar su total y stock.
2. Editar solo el cliente y verificar que los demás datos no cambien.
3. Aumentar y disminuir la cantidad, comprobando el stock y el total.
4. Cambiar el producto y revisar el stock de ambos productos.
5. Intentar actualizar con stock insuficiente, datos inválidos e ID inexistente.
6. Eliminar un pedido y comprobar que sus unidades regresen al stock.

Para comprobar PUT y DELETE:
1. Abrir las herramientas del navegador con F12.
2. Entrar a Network y activar Preserve log.
3. Actualizar o eliminar un pedido.
4. Revisar el método HTTP, el código de respuesta y el resultado en la aplicación.
