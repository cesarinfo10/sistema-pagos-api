# Sistema de Pagos API

API REST para procesamiento de transacciones de pago con validaciones de negocio y simulación de banco emisor.

## DENTRO DEL REPOSITORIO ENCONTRARÁN
- API completa
- Colección completa en postman donde están paso a paso como ejecutar los endpoint (directorio: COLECCION_POSTMAN)
- Guía completa de usuario

## RECURSOS PARA HACER EL SISTEMA
- Requerimiento proporcionado por la empresa.
- Consultas web
- Conocimientos propios
- IA Copilot

## Métrica de desarrollo
- 60% Conocimientos propios ( pensado del sistema, estructura, base de datos por medio de entidad, desarrollo )
- 40% Ayuda de IA (ayuda en avances de codificación en réplicas más que nada, apoyo en documentación, apoyo en test y también en algunos errores )


## Requisitos

- Java 17+
- Maven 3.9+
- MySQL 5.5+ (o Docker)

## Ejecución

### Opción 1: Local (con MySQL propio)

1. Crear base de datos:
```sql
CREATE DATABASE pagosdb;
```

2. Configurar `application.properties` con tus credenciales de MySQL

3. Ejecutar aplicación:
```bash
mvn spring-boot:run
```

La API estará disponible en: `http://localhost:8001`

### Opción 2: Con Docker

```bash
docker-compose up --build
```

La API estará en `http://localhost:8001` y MySQL en puerto `3307`

## Arquitectura

```
Cliente → Controller → Service → Repository → Database
```

**Flujo de procesamiento de pago:**
```
1. Request POST /payments
2. PaymentController recibe datos
3. PaymentService valida comercio activo
4. Valida límite de monto
5. Valida tarjeta (patrón)
6. Consulta issuer mock (simula banco)
7. Guarda transacción en BD
8. Response con resultado
```

## Endpoints

### 1. Registrar Comercio

**POST** `/comercios`

```json
{
  "idComercio": "COM001",
  "nombre": "Mi Tienda",
  "montoMaximoTransaccion": 50000.00,
  "estado": "ACTIVO"
}
```

**Respuesta:**
```json
{
  "id": 1,
  "idComercio": "COM001",
  "nombre": "Mi Tienda",
  "montoMaximoTransaccion": 50000.00,
  "estado": "ACTIVO",
  "fechaCreacion": 1735249200000
}
```

---

### 2. Procesar Pago

**POST** `/payments`

```json
{
  "merchantId": "COM001",
  "amount": 1500.50,
  "currency": "USD",
  "cardToken": "tok_5555444433332222",
  "expirationDate": "12/25",
  "operationType": "COMPRA"
}
```

**Respuesta (Aprobado):**
```json
{
  "transactionId": "TXN-ABC123DEF456GH",
  "status": "APPROVED",
  "responseCode": "00",
  "message": "Transacción aprobada",
  "timestamp": 1735249300000
}
```

**Respuesta (Rechazado por límite):**
```json
{
  "transactionId": "TXN-XYZ789JKL012MN",
  "status": "DECLINED",
  "responseCode": "E001",
  "message": "Monto excede el límite permitido para el comercio",
  "timestamp": 1735249350000
}
```

---

### 3. Consultar Transacción

**GET** `/payments/{transactionId}`

```
GET /payments/TXN-ABC123DEF456GH
```

**Respuesta:**
```json
{
  "id": 1,
  "idTransaccion": "TXN-ABC123DEF456GH",
  "idComercio": "COM001",
  "monto": 1500.50,
  "moneda": "USD",
  "tokenTarjeta": "tok_5555444433332222",
  "fechaVencimiento": "12/25",
  "tipoOperacion": "COMPRA",
  "estado": "APPROVED",
  "codigoRespuesta": "00",
  "mensajeRespuesta": "Transacción aprobada",
  "fechaCreacion": 1735249300000,
  "fechaProcesada": 1735249305000
}
```

---

### 4. Listar Transacciones con Filtros

**GET** `/payments?merchantId=COM001&status=APPROVED`

```
GET /payments?merchantId=COM001
GET /payments?status=DECLINED
GET /payments?merchantId=COM001&status=APPROVED
GET /payments
```

**Respuesta:** Array de transacciones

---

### 5. Listar Comercios

**GET** `/comercios`

**Respuesta:** Array de comercios

---

### 6. Obtener Comercio

**GET** `/comercios/{idComercio}`

```
GET /comercios/COM001
```

---

### 7. Actualizar Estado de Comercio

**PUT** `/comercios/{idComercio}/estado`

```json
{
  "estado": "INACTIVO"
}
```

## Estados de Transacción

- **PENDIENTE**: Transacción iniciada
- **APPROVED**: Aprobada por el banco
- **DECLINED**: Rechazada (límite, tarjeta inválida, o banco)
- **ERROR**: Error del sistema

## Códigos de Respuesta

- **00**: Transacción aprobada
- **05**: Rechazada por banco emisor
- **E001**: Monto excede límite del comercio
- **E002**: Tarjeta no permitida (simulada)
- **E999**: Error interno del sistema

## Reglas de Negocio

1. **Comercio debe estar ACTIVO**
2. **Monto no puede exceder el límite del comercio**
3. **Tarjetas que coincidan con patrón configurable son rechazadas**
   - Por defecto: `4111111111111111`
   - Configurable en `application.properties`
4. **Issuer mock responde aleatoriamente** (70% aprobado, 30% rechazado)

## Configuración

En `application.properties`:

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/pagosdb
spring.datasource.username=root
spring.datasource.password=

# Patrón de tarjeta a rechazar (regex)
pagos.tarjeta.patron-rechazo=^4111111111111111$
```

## Colección Postman

Importa estos ejemplos en Postman:

1. Crear comercio → POST `/comercios`
2. Procesar pago exitoso → POST `/payments`
3. Procesar pago rechazado (monto alto) → POST `/payments`
4. Consultar transacción → GET `/payments/{id}`
5. Listar transacciones filtradas → GET `/payments?merchantId=COM001`


## Tecnologías

- Spring Boot 3.5.9
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- Docker
