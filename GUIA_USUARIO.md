# Guía de Usuario - Sistema de Pagos API
## Tutorial Paso a Paso para Usuarios Nuevos

---

## 📋 Tabla de Contenidos

1. [Requisitos Previos](#requisitos-previos)
2. [Iniciar el Sistema](#iniciar-el-sistema)
3. [Configurar Postman](#configurar-postman)
4. [Paso 1: Crear un Comercio](#paso-1-crear-un-comercio)
5. [Paso 2: Consultar Comercios](#paso-2-consultar-comercios)
6. [Paso 3: Procesar un Pago](#paso-3-procesar-un-pago)
7. [Paso 4: Consultar una Transacción](#paso-4-consultar-una-transacción)
8. [Paso 5: Listar Transacciones](#paso-5-listar-transacciones)
9. [Paso 6: Actualizar un Comercio](#paso-6-actualizar-un-comercio)
10. [Casos de Uso Adicionales](#casos-de-uso-adicionales)
11. [Códigos de Respuesta](#códigos-de-respuesta)
12. [Solución de Problemas](#solución-de-problemas)

---

## Requisitos Previos

Antes de comenzar, asegúrate de tener:

- ✅ Java 17 o superior instalado
- ✅ Maven 3.9 o superior instalado
- ✅ MySQL instalado O Docker instalado
- ✅ Postman instalado (o cualquier cliente REST)

---

## Iniciar el Sistema

### Opción A: Con Docker (Recomendado)

### Que es lo que susederá:
Cuando ejecutes docker-compose up --build, el sistema:

1. Construirá la imagen Docker de tu aplicación usando Maven
2. Levantará MySQL en el puerto 3307 (host) y puerto 3306 (contenedor)
3. Esperará a que MySQL esté OK
4. Iniciará la aplicación Spring Boot en el puerto 8001
5. Conectará automáticamente la app con MySQL usando la red interna de Docker

### Si queda claro entonces se puede proceder:
1. Abre una terminal en la carpeta del proyecto
2. Ejecuta:
   ```bash
   docker-compose up --build
   ```
3. Espera a ver el mensaje: `Started PagosApplication`
4. La API estará disponible en: **http://localhost:8001**


### Opción B: Local (Sin Docker)

1. Crea la base de datos en MySQL:
   ```sql
   CREATE DATABASE pagosdb;
   ```

2. Configura tus credenciales en `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/pagosdb
   spring.datasource.username=TU_USUARIO
   spring.datasource.password=TU_PASSWORD
   ```

3. Ejecuta la aplicación:
   ```bash
   mvn spring-boot:run
   ```

4. La API estará disponible en: **http://localhost:8001**

---

## Configurar Postman

### URL Base
Todas las peticiones usan la misma URL base:
```
http://localhost:8001
```

### Headers por Defecto
Agrega este header a TODAS las peticiones:
```
Content-Type: application/json
```

---

## Paso 1: Crear un Comercio

**¿Por qué?** Antes de procesar pagos, necesitas registrar un comercio (tienda/empresa) en el sistema.

### Petición HTTP

**Método:** `POST`  
**URL:** `http://localhost:8001/comercios`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "idComercio": "COM001",
  "nombre": "Tienda El Ejemplo",
  "montoMaximoTransaccion": 10000.00,
  "estado": "ACTIVO"
}
```

### Respuesta Exitosa (200 OK)
```json
{
  "id": 1,
  "idComercio": "COM001",
  "nombre": "Tienda El Ejemplo",
  "montoMaximoTransaccion": 10000.00,
  "estado": "ACTIVO",
  "fechaCreacion": 1735258800000
}
```

### Pasos en Postman:
1. Crea una nueva petición
2. Selecciona método **POST**
3. Ingresa la URL: `http://localhost:8001/comercios`
4. Ve a la pestaña **Headers** y agrega `Content-Type: application/json`
5. Ve a la pestaña **Body**
6. Selecciona **raw** y **JSON**
7. Copia y pega el JSON del body
8. Haz clic en **Send**

---

## Paso 2: Consultar Comercios

**¿Por qué?** Para verificar que el comercio fue creado correctamente.

### Petición HTTP

**Método:** `GET`  
**URL:** `http://localhost:8001/comercios`  
**Headers:** No requiere headers especiales

### Respuesta Exitosa (200 OK)
```json
[
  {
    "id": 1,
    "idComercio": "COM001",
    "nombre": "Tienda El Ejemplo",
    "montoMaximoTransaccion": 10000.00,
    "estado": "ACTIVO",
    "fechaCreacion": 1735258800000
  }
]
```

### Consultar un Comercio Específico

**Método:** `GET`  
**URL:** `http://localhost:8001/comercios/COM001`

### Respuesta Exitosa (200 OK)
```json
{
  "id": 1,
  "idComercio": "COM001",
  "nombre": "Tienda El Ejemplo",
  "montoMaximoTransaccion": 10000.00,
  "estado": "ACTIVO",
  "fechaCreacion": 1735258800000
}
```

---

## Paso 3: Procesar un Pago

**¿Por qué?** Esta es la funcionalidad principal del sistema - procesar una transacción de pago.

### Petición HTTP

**Método:** `POST`  
**URL:** `http://localhost:8001/payments`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON) - Ejemplo Básico Exitoso:**
```json
{
  "merchantId": "COM001",
  "amount": 1500.50,
  "currency": "USD",
  "cardToken": "tok_visa_4242",
  "expirationDate": "12/25",
  "operationType": "COMPRA"
}
```

**Body (JSON) - Para probar Monto Excede Límite:**
```json
{
  "merchantId": "COM001",
  "amount": 15000.00,
  "currency": "USD",
  "cardToken": "tok_visa_4242",
  "expirationDate": "12/25",
  "operationType": "COMPRA"
}
```

**Body (JSON) - Para probar Tarjeta Bloqueada:**
```json
{
  "merchantId": "COM001",
  "amount": 1500.50,
  "currency": "USD",
  "cardToken": "4111111111111111",
  "expirationDate": "12/25",
  "operationType": "COMPRA"
}
```

**Nota:** El rechazo por banco es aleatorio (~30% de probabilidad). Intenta varias veces con el ejemplo básico para verlo.

### Campos Explicados:
- **merchantId**: ID del comercio que creaste (debe estar ACTIVO)
- **amount**: Cantidad a cobrar (debe ser menor al límite del comercio)
- **currency**: Moneda de la transacción (USD, EUR, etc.)
- **cardToken**: Token de la tarjeta (cualquier string, evita `4111111111111111`)
- **expirationDate**: Fecha de vencimiento en formato MM/YY
- **operationType**: Tipo de operación (COMPRA, DEVOLUCION, etc.)

### Respuesta Exitosa - Pago Aprobado (200 OK)
```json
{
  "transactionId": "TXN-A1B2C3D4-E5F6-7890",
  "status": "APPROVED",
  "responseCode": "00",
  "message": "Transacción aprobada",
  "timestamp": 1735258905000
}
```

### Respuesta - Pago Rechazado por Banco (200 OK)
```json
{
  "transactionId": "TXN-B2C3D4E5-F6G7-8901",
  "status": "DECLINED",
  "responseCode": "05",
  "message": "Transacción rechazada por el emisor",
  "timestamp": 1735259005000
}
```

### Respuesta - Monto Excede Límite (200 OK)
```json
{
  "transactionId": "TXN-C3D4E5F6-G7H8-9012",
  "status": "DECLINED",
  "responseCode": "E001",
  "message": "Monto excede el límite permitido para el comercio",
  "timestamp": 1735259102000
}
```

### Respuesta - Tarjeta Bloqueada (200 OK)
```json
{
  "transactionId": "TXN-D4E5F6G7-H8I9-0123",
  "status": "DECLINED",
  "responseCode": "E002",
  "message": "Tarjeta no permitida",
  "timestamp": 1735259202000
}
```

---

## Paso 4: Consultar una Transacción

**¿Por qué?** Para verificar el estado de un pago específico usando su ID de transacción.

### Petición HTTP

**Método:** `GET`  
**URL:** `http://localhost:8001/payments/{idTransaccion}`

**⚠️ IMPORTANTE:** Debes reemplazar `{idTransaccion}` con el **transactionId REAL** que recibiste en el Paso 3.

**Ejemplo con ID ficticio (NO funcionará):**
```
http://localhost:8001/payments/TXN-A1B2C3D4-E5F6-7890
```

**Cómo obtener el ID correcto:**
1. Ve al Paso 3 y procesa un pago
2. Copia el `transactionId` de la respuesta (ejemplo: `TXN-3F8B2A91-1C4E-4D7F`)
3. Úsalo en la URL: `http://localhost:8001/payments/TXN-3F8B2A91-1C4E-4D7F`

### Respuesta Exitosa (200 OK)
```json
{
  "id": 1,
  "idTransaccion": "TXN-A1B2C3D4-E5F6-7890",
  "idComercio": "COM001",
  "monto": 1500.50,
  "moneda": "USD",
  "tokenTarjeta": "tok_visa_4242",
  "fechaVencimiento": "12/25",
  "tipoOperacion": "COMPRA",
  "estado": "APPROVED",
  "codigoRespuesta": "00",
  "mensajeRespuesta": "Transacción aprobada",
  "fechaCreacion": 1735258900000,
  "fechaProcesada": 1735258905000
}
```

### Respuesta - Transacción No Encontrada (404 Not Found)

**Para probar este error, usa un ID que NO existe:**

**Ejemplo de petición con ID inválido:**
```
GET http://localhost:8001/payments/TXN-INVALIDO-123
```

**Respuesta esperada:**
```json
{
  "error": "Transacción no encontrada",
  "message": "Transacción no encontrada: TXN-INVALIDO-123"
}
```

**⚠️ Nota:** Este error aparece cuando:
- El ID de transacción no existe en la base de datos
- El ID está mal escrito o es incorrecto
- Usas un ID de ejemplo ficticio en lugar del ID real

---

## Paso 5: Listar Transacciones

**¿Por qué?** Para ver todas las transacciones o filtrar por criterios específicos.

### Listar Todas las Transacciones

**Método:** `GET`  
**URL:** `http://localhost:8001/payments`

### Respuesta Exitosa (200 OK)
```json
[
  {
    "id": 1,
    "idTransaccion": "TXN-A1B2C3D4-E5F6-7890",
    "idComercio": "COM001",
    "monto": 1500.50,
    "moneda": "USD",
    "estado": "APPROVED",
    "codigoRespuesta": "00",
    "fechaCreacion": 1735258900000
  },
  {
    "id": 2,
    "idTransaccion": "TXN-B2C3D4E5-F6G7-8901",
    "idComercio": "COM001",
    "monto": 2000.00,
    "moneda": "USD",
    "estado": "DECLINED",
    "codigoRespuesta": "05",
    "fechaCreacion": 1735259000000
  }
]
```

### Filtrar por Comercio

**Método:** `GET`  
**URL:** `http://localhost:8001/payments?idComercio=COM001`

### Filtrar por Estado

**Método:** `GET`  
**URL:** `http://localhost:8001/payments?estado=APPROVED`

### Filtrar por Comercio y Estado

**Método:** `GET`  
**URL:** `http://localhost:8001/payments?idComercio=COM001&estado=APPROVED`

---

## Paso 6: Actualizar un Comercio por el Estado

**¿Por qué?** Para modificar el estado del comercio (ACTIVO/INACTIVO).

### Petición HTTP

**Método:** `PUT`  
**URL:** `http://localhost:8001/comercios/COM001/estado`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON) - Para activar:**
```json
{
  "estado": "ACTIVO"
}
```

**Body (JSON) - Para desactivar:**
```json
{
  "estado": "INACTIVO"
}
```

### Respuesta Exitosa (200 OK)
```json
{
  "id": 1,
  "idComercio": "COM001",
  "nombre": "Tienda El Ejemplo",
  "montoMaximoTransaccion": 10000.00,
  "estado": "INACTIVO",
  "fechaCreacion": 1735258800000
}
```

**⚠️ Importante:** 
- Solo puedes cambiar el **estado** del comercio (ACTIVO/INACTIVO)
- No puedes modificar nombre ni montoMaximoTransaccion por esta vía
- Si un comercio está INACTIVO, no podrá procesar pagos

---

## Casos de Uso Adicionales

### Caso 1: Intentar Pago con Comercio Inactivo

**Petición:**
```json
POST http://localhost:8001/payments
{
  "merchantId": "COM001",
  "amount": 1500.50,
  "currency": "USD",
  "cardToken": "tok_visa_4242",
  "expirationDate": "12/25",
  "operationType": "COMPRA"
}
```

**Respuesta (200 OK):**
```json
{
  "transactionId": "TXN-ERROR123",
  "status": "ERROR",
  "responseCode": "E999",
  "message": "Error interno del sistema: Comercio inactivo: COM001"
}
```

### Caso 2: Intentar Pago con Comercio Inexistente

**Petición:**
```json
POST http://localhost:8001/payments
{
  "merchantId": "COM999",
  "amount": 1500.50,
  "currency": "USD",
  "cardToken": "tok_visa_4242",
  "expirationDate": "12/25",
  "operationType": "COMPRA"
}
```

**Respuesta (200 OK):**
```json
{
  "transactionId": "TXN-ERROR456",
  "status": "ERROR",
  "responseCode": "E999",
  "message": "Error interno del sistema: Comercio no encontrado: COM999"
}
```


### Estados de Transacción

| Estado | Descripción |
|--------|-------------|
| `APPROVED` | Transacción aprobada por el banco |
| `DECLINED` | Transacción rechazada por el banco o reglas de negocio |
| `ERROR` | Error en el procesamiento |
| `PENDIENTE` | Estado inicial antes de procesar |

### Códigos de Respuesta del Sistema

| Código | Significado |
|--------|-------------|
| `00` | Transacción aprobada |
| `05` | Transacción rechazada por el emisor |
| `E001` | Monto excede el límite del comercio |
| `E002` | Tarjeta bloqueada/no permitida |
| `E999` | Error interno del sistema |

### Códigos HTTP

| Código HTTP | Significado |
|-------------|-------------|
| `200 OK` | Petición exitosa |
| `201 Created` | Recurso creado exitosamente |
| `204 No Content` | Operación exitosa sin contenido de respuesta |
| `400 Bad Request` | Datos inválidos en la petición |
| `404 Not Found` | Recurso no encontrado |
| `500 Internal Server Error` | Error interno del servidor |

---

## Solución de Problemas

### Problema: "Connection refused" al hacer peticiones

**Solución:**
1. Verifica que la aplicación esté corriendo: `docker-compose ps` o busca el proceso Java
2. Confirma que el puerto 8001 esté disponible
3. Intenta acceder a: http://localhost:8001/actuator/health

### Problema: "Comercio no encontrado"

**Solución:**
1. Verifica que creaste el comercio primero (Paso 1)
2. Usa el mismo `idComercio` en las peticiones
3. Consulta la lista de comercios: `GET http://localhost:8001/comercios`

### Problema: "Monto excede el límite"

**Solución:**
1. Verifica el `montoMaximoTransaccion` del comercio
2. Asegúrate de que el monto en el pago sea menor
3. Si necesitas aumentar el límite, actualiza el comercio (Paso 6)

### Problema: Tarjeta siempre rechazada

**Solución:**
- Evita usar el token `4111111111111111` que está bloqueado por configuración
- Usa cualquier otro valor, por ejemplo: `tok_visa_4242`, `tok_mc_5555`, etc.

### Problema: Base de datos no inicia con Docker

**Solución:**
1. Detén Docker: `docker-compose down`
2. Elimina volúmenes: `docker-compose down -v`
3. Vuelve a iniciar: `docker-compose up --build`

---

## Flujo Completo de Ejemplo

### Escenario: Tienda Online procesa su primer pago

1. **Crear el comercio:**
```bash
POST http://localhost:8001/comercios
{
  "idComercio": "TIENDA001",
  "nombre": "Mi Tienda Online",
  "montoMaximoTransaccion": 5000.00,
  "estado": "ACTIVO"
}
```

2. **Procesar un pago exitoso:**
```bash
POST http://localhost:8001/payments
{
  "merchantId": "TIENDA001",
  "amount": 299.99,
  "currency": "USD",
  "cardToken": "tok_visa_4242424242424242",
  "expirationDate": "12/26",
  "operationType": "COMPRA"
}
```

3. **Guardar el transactionId de la respuesta** (ejemplo: `TXN-ABC123`)

4. **Consultar el estado del pago:**
```bash
GET http://localhost:8001/payments/TXN-ABC123
```

5. **Ver todas las transacciones de la tienda:**
```bash
GET http://localhost:8001/payments?idComercio=TIENDA001
```

6. **Ver solo las aprobadas:**
```bash
GET http://localhost:8001/payments?idComercio=TIENDA001&estado=APPROVED
```

---

## Resumen de Endpoints

| Método | URL | Descripción |
|--------|-----|-------------|
| `POST` | `/comercios` | Crear un comercio |
| `GET` | `/comercios` | Listar todos los comercios |
| `GET` | `/comercios/{id}` | Consultar un comercio |
| `PUT` | `/comercios/{id}/estado` | Actualizar estado del comercio |
| `POST` | `/payments` | Procesar un pago |
| `GET` | `/payments/{id}` | Consultar una transacción |
| `GET` | `/payments` | Listar transacciones (con filtros opcionales) |
| `GET` | `/transacciones` | Listar todas las transacciones |
| `GET` | `/transacciones/{id}` | Consultar transacción por ID |

---

## Notas Finales

- ✅ Todos los pagos siempre retornan `200 OK`, incluso si son rechazados (verifica el campo `estado`)
- ✅ El sistema usa un mock de banco que aprueba ~70% de transacciones aleatoriamente
- ✅ Las tarjetas que coinciden con el patrón `^4111111111111111$` siempre son rechazadas
- ✅ Los timestamps están en formato epoch (milisegundos desde 1970)
- ✅ Puedes cambiar el patrón de tarjetas bloqueadas en `application.properties`

