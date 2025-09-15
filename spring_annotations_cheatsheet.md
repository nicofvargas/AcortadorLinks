# El Manual Definitivo de Spring Boot: De Cero a API Profesional

Este documento es una guía de referencia exhaustiva para construir aplicaciones robustas con Spring Boot. Cubre desde los conceptos fundamentales del framework hasta los patrones de diseño avanzados para crear APIs REST de nivel profesional.

## Capítulo 1: El Corazón de Spring - Inversión de Control (IoC) y Configuración

Todo en Spring gira en torno al contenedor de IoC, que gestiona el ciclo de vida de los objetos (beans) y sus dependencias.

### 1.1 Anotaciones de Configuración y Arranque

-   **`@SpringBootApplication`**: La anotación principal. Es una combinación de tres anotaciones:
    -   `@Configuration`: Marca la clase como una fuente de definiciones de beans.
    -   `@EnableAutoConfiguration`: Habilita el mecanismo de autoconfiguración de Spring Boot, que intenta configurar inteligentemente tu aplicación basándose en las dependencias del classpath.
    -   `@ComponentScan`: Le dice a Spring que escanee el paquete actual y sus subpaquetes en busca de componentes (`@Component`, `@Service`, etc.).

-   **`@Configuration`**: Designa una clase para declarar uno o más beans mediante métodos anotados con `@Bean`.

-   **`@Bean`**: Se aplica a un método dentro de una clase `@Configuration`. Indica que el método produce un bean que será gestionado por el contenedor de Spring.
    -   `name`: (Opcional) Asigna un nombre específico al bean.
    -   `initMethod`, `destroyMethod`: (Opcional) Especifica métodos de callback para el ciclo de vida del bean.

    ```java
    @Configuration
    public class AppConfig {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
    ```

### 1.2 Anotaciones de Detección de Componentes (Estereotipos)

-   **`@Component`**: La anotación genérica para cualquier bean gestionado por Spring.
-   **`@Service`**: Para la capa de lógica de negocio.
-   **`@Repository`**: Para la capa de acceso a datos. Activa la traducción de excepciones de persistencia.
-   **`@Controller`**: Para la capa web en aplicaciones Spring MVC tradicionales.
-   **`@RestController`**: Para la capa web en APIs REST. Combina `@Controller` y `@ResponseBody`.

### 1.3 Inyección de Dependencias y Configuración

-   **`@Autowired`**: Inyecta automáticamente un bean. **La mejor práctica es usarla en el constructor de la clase.**
-   **`@Qualifier("nombreDelBean")`**: Desambigua la inyección cuando hay múltiples beans del mismo tipo.
-   **`@Primary`**: Le da mayor prioridad a un bean cuando hay múltiples candidatos para la inyección.
-   **`@Value("${propiedad.en.application.properties}")`**: Inyecta un valor desde un fichero de propiedades.
-   **`@ConfigurationProperties(prefix = "mi.config")`**: Vincula un conjunto de propiedades a los campos de un objeto. Es más potente y seguro que usar `@Value` para múltiples propiedades.

---

## Capítulo 2: La Capa Web - Construyendo APIs REST

-   **`@RestController`**: Define una clase como un controlador de API REST.

### 2.1 Mapeo de Peticiones

-   **`@RequestMapping(path="/api", method=RequestMethod.GET, consumes="app/json", produces="app/json")`**: La anotación de mapeo más general.
    -   `path` (o `value`): La ruta URL.
    -   `method`: El método HTTP (GET, POST, etc.).
    -   `consumes`: El `Content-Type` que el endpoint acepta.
    -   `produces`: El `Content-Type` que el endpoint devolverá.
-   **`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`**: Atajos más limpios y específicos para cada método HTTP.

### 2.2 Extracción de Datos de la Petición

-   **`@PathVariable`**: Extrae un valor de la ruta (ej. `/productos/{id}`).
    -   `name`: El nombre de la variable en la ruta. Opcional si coincide con el del parámetro.
-   **`@RequestParam`**: Extrae un parámetro de la query string (ej. `?nombre=laptop`).
    -   `name`: El nombre del parámetro.
    -   `required`: `true` por defecto. Si es `false`, el parámetro es opcional.
    -   `defaultValue`: Provee un valor si el parámetro no está presente.
-   **`@RequestBody`**: Deserializa el cuerpo de la petición (JSON) en un objeto Java (DTO).
-   **`@RequestHeader`**: Extrae una cabecera HTTP.

### 2.3 Manejo Global de Excepciones

-   **`@ControllerAdvice`**: Define una clase que sirve como un interceptor global para los controladores.
-   **`@ExceptionHandler(MiExcepcion.class)`**: Dentro de una clase `@ControllerAdvice`, define un método que manejará una excepción específica, permitiendo centralizar la lógica de errores y devolver respuestas de error consistentes.

    ```java
    @ControllerAdvice
    public class GlobalExceptionHandler {
        @ExceptionHandler(RecursoNoEncontradoException.class)
        public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
            ErrorResponse error = new ErrorResponse("NOT_FOUND", ex.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }
    ```

---

## Capítulo 3: La Capa de Persistencia - JPA y Spring Data

### 3.1 Mapeo Objeto-Relacional con Entidades JPA

-   **`@Entity`**: Marca una clase como una tabla de la base de datos.
-   **`@Table(name="productos", uniqueConstraints={@UniqueConstraint(columnNames={"codigo_sku"})})`**: Personaliza la tabla.
-   **`@Id`**: Define la clave primaria.
-   **`@GeneratedValue(strategy=GenerationType.IDENTITY)`**: Configura la generación de la clave primaria.
-   **`@Column(name="nombre_producto", length=100, nullable=false, unique=true)`**: Personaliza una columna.
-   **Relaciones**:
    -   **`@ManyToOne(fetch = FetchType.LAZY)`**: Muchos a uno. `LAZY` es la mejor práctica para el rendimiento.
    -   **`@OneToMany(mappedBy="producto", cascade=CascadeType.ALL, orphanRemoval=true)`**: Uno a muchos.
        -   `mappedBy`: Indica que la otra entidad es la dueña de la relación.
        -   `cascade`: Propaga operaciones (guardar, borrar) a las entidades hijas.
        -   `orphanRemoval`: Borra un hijo si se elimina de la colección del padre.
    -   **`@JoinColumn(name="categoria_id", referencedColumnName="id")`**: Especifica la columna de la clave foránea.

### 3.2 Repositorios con Spring Data JPA

Defines una **interfaz** que extiende de `JpaRepository` y Spring Data JPA crea la implementación concreta en tiempo de ejecución.

-   **`JpaRepository<T, ID>`**: La interfaz principal a extender.
    -   `T`: El tipo de la Entidad que gestionará el repositorio (ej. `Producto`).
    -   `ID`: El tipo de la clave primaria de la Entidad (ej. `Long`).

#### Métodos Heredados de `JpaRepository` (Vienen Gratis)

**Operaciones de Guardado (Crear y Actualizar):**
-   **`S save(S entity)`**: Guarda una entidad. Si la entidad no tiene un ID, realiza una inserción (`INSERT`). Si ya tiene un ID, realiza una actualización (`UPDATE`). **Retorno:** La entidad guardada (`S`), que ahora incluirá el ID generado.
-   **`List<S> saveAll(Iterable<S> entities)`**: Guarda una colección de entidades en un lote. **Retorno:** Una `List<S>` con las entidades guardadas.

**Operaciones de Búsqueda (Leer):**
-   **`Optional<T> findById(ID id)`**: Busca una entidad por su clave primaria. **Retorno:** Un `Optional<T>`, vacío si no se encuentra. **Es el método preferido para buscar por ID.**
-   **`T getReferenceById(ID id)`**: Obtiene una referencia "perezosa" (lazy) a una entidad. No golpea la base de datos hasta que se accede a un campo. **Retorno:** Un proxy a la entidad `T`.
-   **`List<T> findAll()`**: Devuelve todas las entidades de la tabla. **¡Cuidado en tablas grandes!**
-   **`List<T> findAllById(Iterable<ID> ids)`**: Devuelve todas las entidades para una colección de IDs.
-   **`boolean existsById(ID id)`**: Comprueba si una entidad con un ID dado existe. Más eficiente que `findById().isPresent()`. **Retorno:** `true` o `false`.
-   **`long count()`**: Devuelve el número total de entidades en la tabla. **Retorno:** `long`.

**Operaciones de Borrado:**
-   **`void deleteById(ID id)`**: Borra una entidad por su clave primaria.
-   **`void delete(T entity)`**: Borra una entidad dada.
-   **`void deleteAllById(Iterable<? extends ID> ids)`**: Borra una colección de entidades por sus IDs.
-   **`void deleteAll()`**: Borra todas las entidades de la tabla. **¡Peligroso!**

**Query Methods**: Spring crea consultas a partir del nombre del método.
-   `findByNombre(String nombre)`
-   `findByPrecioGreaterThan(Double precio)`
-   `findByNombreContainingIgnoreCaseOrderByPrecioDesc(String keyword)`
-   **`@Query("SELECT p FROM Producto p WHERE p.activo = true")`**: Para consultas JPQL personalizadas.
-   **`@Modifying`**: Indica que una `@Query` modifica datos (`UPDATE`, `DELETE`). Requiere `@Transactional`.


### **Sección Adicional para el Manual: El Lenguaje de los Query Methods**

Spring Data JPA puede generar consultas automáticamente a partir del nombre de un método. Esto funciona como un Lenguaje de Dominio Específico (DSL) con un vocabulario y una gramática definidos. La estructura general es:

`[Verbo][Top/First][Sujeto(s)][Palabras Clave][OrderBy]`

---

### Verbos Principales (Prefijos de Consulta)

Estos definen la acción principal de la consulta.

-   **`find...By`**: Es el verbo más común. Se traduce a una consulta `SELECT`. Puede devolver un único objeto, un `Optional`, una `List` o un `Stream`.
    -   **Ejemplo:** `List<Usuario> findByNombre(String nombre);`
    -   **SQL (aprox):** `SELECT * FROM usuario WHERE nombre = ?`

-   **`read...By`**: Un sinónimo de `find...By`. Funciona exactamente igual.
    -   **Ejemplo:** `Optional<Usuario> readByEmail(String email);`

-   **`get...By`**: Otro sinónimo de `find...By`.
    -   **Ejemplo:** `Usuario getByUsername(String username);`

-   **`query...By`**: Otro sinónimo más de `find...By`.
    -   **Ejemplo:** `Stream<Usuario> queryByActivoTrue();`

-   **`count...By`**: Se traduce a una consulta `SELECT COUNT(...)`. Devuelve el número de registros que coinciden con los criterios.
    -   **Ejemplo:** `long countByCiudad(String ciudad);`
    -   **SQL (aprox):** `SELECT COUNT(*) FROM usuario WHERE ciudad = ?`

-   **`exists...By`**: Comprueba la existencia de al menos un registro que coincida. Es más eficiente que `count...By > 0`.
    -   **Ejemplo:** `boolean existsByEmail(String email);`
    -   **SQL (aprox):** `SELECT 1 FROM usuario WHERE email = ? LIMIT 1` (la consulta real puede variar según la base de datos para máxima eficiencia).

-   **`delete...By`** o **`remove...By`**: Se traduce a una consulta `DELETE`. Estos métodos deben estar anotados con `@Transactional` si se llaman desde fuera de un servicio transaccional.
    -   **Ejemplo:** `long deleteByActivoFalse();`
    -   **Retorno:** Puede devolver `void` o un `long` con el número de filas eliminadas.

---

### Palabras Clave para la Cláusula `WHERE`

Estas palabras clave se colocan después del nombre del campo en el "Sujeto" para definir la condición de la comparación.

-   **`And`**: Concatena condiciones con un `AND` lógico.
    -   **Ejemplo:** `findByNombreAndCiudad(String nombre, String ciudad);`
    -   **SQL:** `... WHERE nombre = ? AND ciudad = ?`

-   **`Or`**: Concatena condiciones con un `OR` lógico.
    -   **Ejemplo:** `findByNombreOrEmail(String nombre, String email);`
    -   **SQL:** `... WHERE nombre = ? OR email = ?`

-   **`Is`**, **`Equals`**: El comportamiento por defecto. Compara por igualdad. A menudo se omite.
    -   **Ejemplo:** `findByNombre("Nico")` es igual a `findByNombreIs("Nico")`.

-   **`IsNot`**, **`Not`**: Compara por desigualdad (`<>` o `!=`).
    -   **Ejemplo:** `findByNombreNot("Nico");`
    -   **SQL:** `... WHERE nombre <> ?`

-   **`IsNull`**, **`IsNotNull`**: Comprueba si un campo es `NULL` o no.
    -   **Ejemplo:** `findByFechaBajaIsNull();`
    -   **SQL:** `... WHERE fecha_baja IS NULL`

-   **`IsTrue`**, **`IsFalse`**: Atajos para campos booleanos.
    -   **Ejemplo:** `findByActivoTrue();`
    -   **SQL:** `... WHERE activo = true`

-   **`Like`**, **`NotLike`**: Para búsqueda de patrones en cadenas de texto (`LIKE`).
    -   **Ejemplo:** `findByNombreLike("Nic%");` (El `%` debes añadirlo tú).

-   **`StartingWith`**: Atajo para `LIKE 'patrón%'`.
    -   **Ejemplo:** `findByNombreStartingWith("Nic");`

-   **`EndingWith`**: Atajo para `LIKE '%olas'`.
    -   **Ejemplo:** `findByNombreEndingWith("olas");`

-   **`Containing`**: Atajo para `LIKE '%patrón%'`.
    -   **Ejemplo:** `findByNombreContaining("ico");`

-   **`IgnoreCase`**: Se añade al final de una condición de `String` para que la comparación no distinga entre mayúsculas y minúsculas.
    -   **Ejemplo:** `findByNombreContainingIgnoreCase("nico");`
    -   **SQL:** `... WHERE LOWER(nombre) LIKE LOWER('%nico%')`

-   **`Between`**: Para rangos (fechas, números).
    -   **Ejemplo:** `findByFechaRegistroBetween(LocalDate inicio, LocalDate fin);`
    -   **SQL:** `... WHERE fecha_registro BETWEEN ? AND ?`

-   **`LessThan`**, **`LessThanEqual`**: Menor que (`<`), menor o igual que (`<=`).
    -   **Ejemplo:** `findByEdadLessThan(18);`

-   **`GreaterThan`**, **`GreaterThanEqual`**: Mayor que (`>`), mayor o igual que (`>=`).
    -   **Ejemplo:** `findBySalarioGreaterThanEqual(50000.0);`

-   **`In`**, **`NotIn`**: Comprueba si el valor de un campo está en una colección.
    -   **Ejemplo:** `findByCiudadIn(List<String> ciudades);`
    -   **SQL:** `... WHERE ciudad IN (?, ?, ...)`

---

### Modificadores de Resultados

-   **`First`** o **`Top`**: Limitan el número de resultados devueltos. Se pueden usar con un número opcional.
    -   **Ejemplo:** `findFirst5ByOrderByFechaRegistroDesc();` (Devuelve los 5 usuarios más recientes).
    -   **Ejemplo:** `findTopByOrderBySalarioDesc();` (Devuelve el usuario con el salario más alto).

-   **`Distinct`**: Se añade después del verbo para eliminar duplicados.
    -   **Ejemplo:** `findDistinctByCiudad(String ciudad);`
    -   **SQL:** `SELECT DISTINCT ...`

### Ordenación

-   **`OrderBy`**: Se añade al final del nombre del método para ordenar los resultados.
    -   **`Asc`**: Orden ascendente (por defecto si no se especifica).
    -   **`Desc`**: Orden descendente.
    -   **Ejemplo:** `findByCiudadOrderByNombreAsc(String ciudad);`
    -   **SQL:** `... WHERE ciudad = ? ORDER BY nombre ASC`
    -   **Ejemplo con múltiples campos:** `findByCiudadOrderByApellidoAscNombreAsc(String ciudad);`

---

## Capítulo 4: La Capa de Servicio - El Cerebro de la Aplicación

La capa de servicio es el corazón de tu aplicación. Si el controlador es la "cara" y el repositorio es la "memoria", el servicio es el **"cerebro"**. Su responsabilidad principal es orquestar la **lógica de negocio** de la aplicación.

### 4.1 Propósito y Responsabilidades Clave

Un servicio bien diseñado **NO** es simplemente un intermediario que llama al repositorio. Tiene responsabilidades críticas:

1.  **Orquestación:** Es el director de orquesta. Un solo método de servicio puede necesitar llamar a múltiples repositorios, a otros servicios, o incluso a APIs externas para cumplir con una tarea de negocio.
2.  **Lógica de Negocio:** Aquí es donde se aplican las reglas. Por ejemplo: "Un usuario no puede comprar un producto si no hay suficiente inventario", "Antes de crear un pedido, se debe validar la dirección del cliente", "Un usuario premium tiene un 10% de descuento".
3.  **Gestión de Transacciones:** El servicio define los límites de las transacciones. Una operación de negocio (como "transferir dinero") puede implicar múltiples escrituras en la base de datos (restar de una cuenta, sumar a otra). El servicio se asegura de que estas operaciones ocurran como una unidad atómica (o todo tiene éxito, o todo se revierte).
4.  **Abstracción:** El controlador no necesita saber si los datos vienen de una base de datos, de una caché o de una API externa. Solo habla con el servicio. El servicio abstrae la complejidad de la obtención y manipulación de los datos.
5.  **Seguridad a Nivel de Método:** Es el lugar ideal para aplicar reglas de seguridad (ej. `@PreAuthorize("hasRole('ADMIN')")`) para asegurar que solo los usuarios autorizados puedan ejecutar ciertas lógicas de negocio.

### 4.2 Anotaciones Fundamentales

-   **`@Service`**: Es una especialización de `@Component`. Semánticamente, le dice a otros desarrolladores (y a ti mismo en el futuro) que esta clase contiene la lógica de negocio central.

-   **`@Transactional`**: Esta es la anotación más importante y poderosa de la capa de servicio. Le dice a Spring que envuelva la ejecución de un método en una transacción de base de datos.
    -   **¿Qué es una Transacción?** Es una secuencia de operaciones que se tratan como una única unidad de trabajo (propiedades ACID). Si cualquier parte de la transacción falla, todas las operaciones anteriores dentro de esa transacción se revierten (rollback), dejando la base de datos en su estado original.
    -   **Uso Recomendado:**
        1.  Anota la **clase entera** con `@Transactional`. Esto establece una política por defecto para todos los métodos públicos de la clase.
        2.  Anota **cada método que solo lee datos** con `@Transactional(readOnly = true)`. Esto sobrescribe la configuración de la clase y le da una pista importante al proveedor de JPA y a la base de datos para que apliquen optimizaciones de rendimiento, ya que no necesitan prepararse para escrituras.

    -   **Parámetros Clave de `@Transactional`:**
        -   `readOnly` (boolean): `true` para operaciones de solo lectura. **Es una optimización crucial.**
        -   `propagation` (`Propagation` enum): Define cómo se comporta el método si ya existe una transacción en curso.
            -   `REQUIRED` (por defecto): Se une a la transacción existente o crea una nueva si no hay ninguna. Es lo que usarás el 99% del tiempo.
            -   `REQUIRES_NEW`: Siempre crea una nueva transacción, suspendiendo la actual si existe. Útil para operaciones de auditoría o logging que deben confirmarse incluso si la transacción principal falla.
            -   `SUPPORTS`, `MANDATORY`, `NOT_SUPPORTED`, `NEVER`: Otros niveles para casos de uso más avanzados.
        -   `isolation` (`Isolation` enum): Define el nivel de aislamiento de la transacción frente a otras transacciones concurrentes para prevenir problemas como lecturas sucias o lecturas fantasma.
        -   `rollbackFor` (Class[]): Por defecto, las transacciones solo hacen rollback para `RuntimeException` y `Error`, pero no para excepciones "checked" (`Exception`). Este parámetro te permite especificar para qué excepciones "checked" también debería hacerse rollback.

### 4.3 Ejemplo de un Servicio Bien Estructurado

Este ejemplo une todos los conceptos: orquestación, lógica de negocio, transacciones y el uso de mappers y excepciones personalizadas.

```java
// En el paquete com.tuproyecto.service
@Service
@Transactional // 1. Política de transacción por defecto para toda la clase
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final PedidoMapper pedidoMapper;
    private final ServicioDePagoExterno servicioDePago; // Otro servicio inyectado

    @Autowired
    public PedidoServiceImpl(PedidoRepository pedidoRepository, ProductoRepository productoRepository, PedidoMapper pedidoMapper, ServicioDePagoExterno servicioDePago) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.pedidoMapper = pedidoMapper;
        this.servicioDePago = servicioDePago;
    }

    @Override
    public PedidoResponseDTO crearPedido(PedidoRequestDTO pedidoDto) {
        // 2. Lógica de negocio: Verificar inventario
        Producto producto = productoRepository.findById(pedidoDto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", pedidoDto.getProductoId()));

        if (producto.getStock() < pedidoDto.getCantidad()) {
            throw new InventarioInsuficienteException("No hay suficiente stock para el producto: " + producto.getNombre());
        }

        // 3. Orquestación: Actualizar el inventario
        producto.setStock(producto.getStock() - pedidoDto.getCantidad());
        productoRepository.save(producto); // Primera escritura en la BD

        // 4. Mapeo y creación del pedido
        Pedido nuevoPedido = pedidoMapper.aEntidad(pedidoDto);
        nuevoPedido.setEstado("CREADO");
        nuevoPedido.setTotal(producto.getPrecio() * pedidoDto.getCantidad());
        
        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido); // Segunda escritura en la BD

        // Si algo fallara aquí, ambas escrituras (actualizar stock y crear pedido) se revertirían
        // gracias a @Transactional.

        // 5. Devolver el DTO de respuesta
        return pedidoMapper.aDTO(pedidoGuardado);
    }

    @Override
    @Transactional(readOnly = true) // 6. Sobrescribir para optimización de lectura
    public Optional<PedidoResponseDTO> obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                               .map(pedidoMapper::aDTO);
    }
}
```
---

## Capítulo 5: Arquitectura de API Profesional: DTOs, Validación y Mapeo


Este capítulo es el más importante para pasar de un prototipo a una aplicación mantenible y segura. El patrón **DTO (Data Transfer Object)** es la piedra angular de una API bien diseñada.

### 5.1 El "Porqué": La Necesidad Imperativa de los DTOs

Usar entidades (`@Entity`) directamente en la capa web es una de las peores anti-patrones en Spring. Causa problemas graves y predecibles:

1.  **Acoplamiento Fuerte:** Tu API queda "pegada" a tu base de datos. Cualquier cambio en el esquema (renombrar una columna, cambiar un tipo) rompe el contrato con tus clientes.
2.  **Exposición de Datos Sensibles:** Es muy fácil exponer accidentalmente campos que nunca deberían salir de tu servidor (`passwordHash`, `datosInternos`, `salario`).
3.  **Vulnerabilidades de Inyección de Datos:** Un atacante podría enviar en el JSON campos que existen en tu entidad pero que no deberían ser modificables (ej. `"rol": "ADMIN"`), y si no lo manejas con cuidado, JPA podría intentar persistirlos.
4.  **Flexibilidad Nula:** La estructura de datos para crear un recurso (`POST`) es casi siempre diferente de la que usas para mostrarlo (`GET`). El DTO te permite tener "vistas" personalizadas de tus datos para cada caso de uso.
5.  **Errores de Carga Perezosa (`LazyInitializationException`):** Si tu entidad tiene relaciones `LAZY` y la transacción de base de datos se cierra antes de que el serializador JSON (Jackson) intente acceder a ellas, la aplicación fallará. Los DTOs evitan esto porque el mapeo se hace dentro de la transacción.

### 5.2 El Ciclo de Vida Completo de un DTO

#### A) DTO de Petición (Request) y Validación

Estos DTOs modelan los datos que **recibes** del cliente. Son el lugar perfecto para la **validación**.

```java
// En el paquete dto
// Lombok es tu mejor amigo aquí: @Data, @NoArgsConstructor, @AllArgsConstructor
public class ProductoRequestDTO {
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres.")
    private String nombre;

    @NotNull(message = "El precio es obligatorio.")
    @Positive(message = "El precio debe ser un número positivo.")
    private Double precio;

    @NotNull(message = "El ID de la categoría es obligatorio.")
    private Long categoriaId;
}
```
-   **Uso en el Controlador:** Se usa con `@RequestBody` y se activa la validación con `@Valid`.

    ```java
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO dto) {
        // ...
    }
    ```

#### B) DTO de Respuesta (Response)

Este DTO modela los datos que **envías** al cliente. Es una vista segura y controlada de tu entidad.

```java
// En el paquete dto
public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private String nombreCategoria;
    private LocalDateTime fechaCreacion;
}
```

#### C) Manejo de Errores de Validación

Cuando la validación con `@Valid` falla, Spring lanza una `MethodArgumentNotValidException`. Debemos atraparla con un `@ControllerAdvice` para devolver una respuesta de error limpia y útil al cliente.

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
```

### 5.3 El Arte del Mapeo: El Componente Mapper

La lógica de conversión no debe vivir ni en el controlador ni en el servicio. Debe estar en su propio componente, respetando el Principio de Responsabilidad Única.

#### A) Mapper Manual (Detallado)

```java
// En el paquete mapper
@Component
public class ProductoMapper {

    // Convierte de DTO de creación a Entidad
    public Producto aEntidad(ProductoRequestDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        // La asignación de la entidad Categoria se hará en el servicio
        return producto;
    }

    // Convierte de Entidad a DTO de respuesta
    public ProductoResponseDTO aDTO(Producto producto) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setFechaCreacion(producto.getFechaCreacion());
        if (producto.getCategoria() != null) {
            dto.setNombreCategoria(producto.getCategoria().getNombre());
        }
        return dto;
    }

    // Convierte una lista de Entidades a una lista de DTOs
    public List<ProductoResponseDTO> aDTOList(List<Producto> productos) {
        return productos.stream()
                .map(this::aDTO) // Llama al método aDTO por cada elemento
                .collect(Collectors.toList());
    }

    // Actualiza una Entidad existente con datos de un DTO
    public void actualizarEntidad(Producto producto, ProductoRequestDTO dto) {
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        // La lógica para actualizar la categoría se manejaría en el servicio
    }
}
```

#### B) MapStruct (El Estándar Profesional)

MapStruct genera la implementación de la interfaz anterior por ti.

```java
@Mapper(componentModel = "spring")
public interface ProductoMapper {
    @Mapping(source = "categoria.nombre", target = "nombreCategoria")
    ProductoResponseDTO aDTO(Producto producto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Producto aEntidad(ProductoRequestDTO dto);

    List<ProductoResponseDTO> aDTOList(List<Producto> productos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    void actualizarEntidad(@MappingTarget Producto producto, ProductoRequestDTO dto);
}
```

### 5.4 El Flujo Completo en Acción (con `PUT` para actualizar)

```java
// --- Servicio ---
@Service
@Transactional
public class ProductoService {
    // ... inyección de repositorios y mapper ...

    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto) {
        // 1. Buscar la entidad existente o lanzar excepción
        Producto productoExistente = productoRepo.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        
        // 2. Usar el mapper para actualizar los campos
        mapper.actualizarEntidad(productoExistente, dto);

        // 3. Lógica de negocio compleja (ej. cambiar categoría)
        Categoria categoria = categoriaRepo.findById(dto.getCategoriaId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));
        productoExistente.setCategoria(categoria);

        // 4. Guardar la entidad actualizada (JPA detecta los cambios)
        Producto productoActualizado = productoRepo.save(productoExistente);

        // 5. Devolver el DTO de respuesta
        return mapper.aDTO(productoActualizado);
    }
}

// --- Controlador ---
@PutMapping("/{id}")
public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO dto) {
    ProductoResponseDTO productoActualizado = productoService.actualizar(id, dto);
    return ResponseEntity.ok(productoActualizado);
}
```
---

## Capítulo 6: Patrones Avanzados y Clases Esenciales

### `ResponseEntity<T>`

Clase que representa la respuesta HTTP completa: **código de estado**, **cabeceras** y **cuerpo**.
-   `ResponseEntity.ok(body)` -> **200 OK**
-   `ResponseEntity.created(uri).body(body)` -> **201 Created**
-   `ResponseEntity.noContent().build()` -> **204 No Content** (para `DELETE` exitosos)
-   `ResponseEntity.badRequest().build()` -> **400 Bad Request**
-   `ResponseEntity.notFound().build()` -> **404 Not Found**

### `Optional<T>`: El Arte de Manejar la Ausencia

`Optional` es un objeto contenedor para evitar `NullPointerException` y escribir código más expresivo y seguro.

#### Métodos para Comprobar y Consumir
-   **`boolean isPresent()`**: Devuelve `true` si contiene un valor.
-   **`boolean isEmpty()`**: Devuelve `true` si está vacío.
-   **`T get()`**: **(EVITAR)**. Devuelve el valor o lanza excepción.
-   **`void ifPresent(Consumer<T> action)`**: Ejecuta una acción solo si el valor está presente.

#### Métodos para Transformar (Estilo Funcional)
-   **`Optional<U> map(Function<T, U> mapper)`**: Si hay un valor, lo transforma y devuelve un nuevo `Optional` con el resultado. Si está vacío, devuelve un `Optional` vacío.
-   **`Optional<U> flatMap(Function<T, Optional<U>> mapper)`**: Similar a `map`, pero para funciones que ya devuelven un `Optional`.

#### Métodos para Obtener el Valor de Forma Segura (Desenvolver)
-   **`T orElse(T other)`**: Devuelve el valor si está presente; si no, devuelve el valor `other`.
-   **`T orElseGet(Supplier<T> other)`**: Similar a `orElse`, pero la creación del valor por defecto (la lambda) solo se ejecuta si es necesario.
-   **`T orElseThrow(Supplier<? extends X> exceptionSupplier)`**: Devuelve el valor si está presente; si no, lanza la excepción proporcionada. **Es la mejor práctica en la capa de servicio.**

### El Patrón Definitivo (Revisitado con más detalle)

El patrón funcional combina `map` y `orElse` para crear un flujo de datos declarativo y seguro en el controlador.

```java
@GetMapping("/{id}")
public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
    // 1. El servicio devuelve un Optional<ProductoDTO>
    return productoService.obtenerDtoPorId(id)
           
           // 2. .map() se encarga del "Happy Path" (caso de éxito)
           // Si el Optional contiene un ProductoDTO, la función lambda lo toma...
           // ...y lo transforma en un ResponseEntity<200 OK> que contiene el DTO.
           // El resultado de .map() es un Optional<ResponseEntity<ProductoDTO>>.
           .map(dto -> ResponseEntity.ok(dto))
           
           // 3. .orElse() se encarga del "Unhappy Path" (caso de no encontrado)
           // Si el Optional que viene de .map() está vacío, .orElse() descarta la caja vacía...
           // ...y devuelve el valor por defecto que le hemos proporcionado: un ResponseEntity<404 Not Found>.
           .orElse(ResponseEntity.notFound().build());
}
```
---

## Capítulo 7: Pruebas (Testing) - La Red de Seguridad del Desarrollador

Escribir código sin pruebas es como caminar por la cuerda floja sin red. Spring Boot ofrece un potente ecosistema de pruebas para cada capa de la aplicación.

### 7.1 Pruebas Unitarias (Capa de Servicio)

-   **Objetivo:** Probar la lógica de una clase en total aislamiento.
-   **Herramientas:** JUnit 5, Mockito.
-   **Anotaciones Clave:**
    -   `@ExtendWith(MockitoExtension.class)`: Habilita Mockito.
    -   `@Mock`: Crea un objeto simulado (mock) de una dependencia.
    -   `@InjectMocks`: Crea una instancia de la clase a probar e inyecta los mocks creados con `@Mock`.

```java
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private ProductoMapper productoMapper;
    @InjectMocks
    private ProductoService productoService;

    @Test
    void cuandoSeCreaUnProducto_debeDevolverElProductoGuardado() {
        // Given (Dado) - Configuración del escenario
        ProductoRequestDTO dto = new ProductoRequestDTO(...);
        Producto productoSinGuardar = new Producto(...);
        Producto productoGuardado = new Producto(...);
        productoGuardado.setId(1L);

        when(productoMapper.aEntidad(dto)).thenReturn(productoSinGuardar);
        when(productoRepository.save(productoSinGuardar)).thenReturn(productoGuardado);
        when(productoMapper.aDTO(productoGuardado)).thenReturn(new ProductoResponseDTO(...));

        // When (Cuando) - Ejecución del método a probar
        ProductoResponseDTO resultado = productoService.crear(dto);

        // Then (Entonces) - Verificación de los resultados
        assertNotNull(resultado);
        verify(productoRepository, times(1)).save(productoSinGuardar); // Verifica que se llamó al método save
    }
}
```

### 7.2 Pruebas de Integración de la Capa Web (Controlador)

-   **Objetivo:** Probar que el controlador recibe peticiones HTTP, las procesa y devuelve la respuesta correcta, sin levantar toda la aplicación.
-   **Herramientas:** `MockMvc`.
-   **Anotaciones Clave:**
    -   `@WebMvcTest(ProductoController.class)`: Carga solo el contexto web para el controlador especificado.
    -   `@MockBean`: Reemplaza un bean del contexto de Spring por un mock de Mockito (ej. el servicio).
    -   `@Autowired`: Para inyectar `MockMvc`.

```java
@WebMvcTest(ProductoController.class)
class ProductoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductoService productoService;

    @Test
    void cuandoPeticionGetValida_debeDevolver200OK() throws Exception {
        // Given
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(1L);
        dto.setNombre("Test");
        when(productoService.obtenerDtoPorId(1L)).thenReturn(Optional.of(dto));

        // When & Then
        mockMvc.perform(get("/api/productos/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nombre", is("Test")));
    }
}
```

### 7.3 Pruebas de Integración de la Capa de Datos (Repositorio)

-   **Objetivo:** Probar que las consultas del repositorio funcionan correctamente contra una base de datos real (o en memoria).
-   **Anotaciones Clave:**
    -   `@DataJpaTest`: Configura un entorno de prueba de JPA, usando una base de datos H2 en memoria por defecto y haciendo rollback de las transacciones después de cada test.

```java
@DataJpaTest
class ProductoRepositoryTest {
    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void cuandoSeGuardaUnProducto_debePoderEncontrarsePorId() {
        // Given
        Producto producto = new Producto("Laptop", 1200.0);
        
        // When
        Producto guardado = productoRepository.save(producto);
        Optional<Producto> encontrado = productoRepository.findById(guardado.getId());

        // Then
        assertTrue(encontrado.isPresent());
        assertEquals("Laptop", encontrado.get().getNombre());
    }
}
```

---

## Capítulo 8: Configuración Avanzada y Perfiles

-   **`application.properties` vs `application.yml`**: YAML es a menudo preferido por su estructura jerárquica y menos repetitiva.
-   **`@Profile("nombre-perfil")`**: Permite que ciertos beans o configuraciones se activen solo cuando un perfil específico está activo. Es la herramienta fundamental para gestionar diferentes entornos (desarrollo, pruebas, producción).

    ```java
    // Fichero: application-dev.properties
    spring.datasource.url=jdbc:h2:mem:testdb
    
    // Fichero: application-prod.properties
    spring.datasource.url=jdbc:postgresql://prod-db-host:5432/mydb

    // Para activar un perfil, se puede hacer en application.properties:
    // spring.profiles.active=dev
    // O como argumento al ejecutar el JAR:
    // java -jar mi-app.jar --spring.profiles.active=prod
    ```

-   **`@ConfigurationProperties(prefix = "mi.app")`**: La forma moderna y segura de vincular propiedades a un objeto de configuración, con validación y autocompletado en el IDE.

    ```java
    @Configuration
    @ConfigurationProperties(prefix = "mi.app")
    @Data // Lombok
    public class AppProperties {
        private String nombre;
        private int hilos;
        private String api_key;
    }
    ```


## Capítulo 9: Arquitectura de Excepciones - Una Estrategia Profesional

Manejar errores es un pilar fundamental de una aplicación robusta. Una buena estrategia de excepciones debe ser **consistente**, **informativa** y **centralizada**. Este capítulo cubre cómo diseñar una arquitectura de excepciones global y cuándo es apropiado crear excepciones personalizadas.

### 9.1 La Base: El Manejador Global (`@ControllerAdvice`)

Como vimos, el `@ControllerAdvice` es el centro de nuestra estrategia. Su objetivo es atrapar excepciones (tanto las de Spring como las nuestras) y traducirlas a respuestas `ResponseEntity` consistentes.

### 9.2 El DTO de Error: Un Contrato de Error Unificado

Antes de manejar cualquier excepción, debemos definir el "contrato" de cómo se verán nuestros errores. No debemos devolver `Map<String, String>` en un caso y `List<String>` en otro. Crearemos un DTO de error global.

```java
// En un paquete com.tuproyecto.dto.error
@Data // Lombok
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private List<String> validationErrors; // Opcional, solo para errores de validación

    // Constructores personalizados para diferentes tipos de errores
    public ErrorResponseDTO(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponseDTO(int status, String error, List<String> validationErrors) {
        this.status = status;
        this.error = error;
        this.message = "La petición contiene errores de validación.";
        this.validationErrors = validationErrors;
        this.timestamp = LocalDateTime.now();
    }
}
```

### 9.3 Manejando Excepciones Globales (Las que no creas tú)

Estas son las excepciones que Spring o sus librerías asociadas lanzan. Nuestro `GlobalExceptionHandler` debe estar preparado para las más comunes. Esta es una estructura profesional y reutilizable para cualquier proyecto.

```java
// En el paquete com.tuproyecto.exception
@ControllerAdvice
@Slf4j // Para logging (Lombok)
public class GlobalExceptionHandler {

    // 1. Error de Validación (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            errors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 2. JSON Mal Formado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleMalformedJson(HttpMessageNotReadableException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "MALFORMED_JSON",
            "El cuerpo de la petición no es un JSON válido."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 3. Violación de Restricciones de la Base de Datos
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.CONFLICT.value(),
            "DATA_INTEGRITY_VIOLATION",
            "Conflicto de datos. Es posible que un recurso con un identificador único ya exista."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // 4. El "Atrapa-Todo" (Catch-All)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        // ¡MUY IMPORTANTE! Loguear la traza completa del error para depuración
        log.error("Se ha producido un error inesperado: ", ex);
        
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_SERVER_ERROR",
            "Ha ocurrido un error inesperado en el servidor. Por favor, contacte a soporte."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

### 9.4 Excepciones Personalizadas: ¿Cuándo y Por Qué?

La pregunta clave: **¿Cuándo es necesario crear una excepción para un módulo específico?**

La respuesta se basa en la **semántica del negocio**. Creas una excepción personalizada cuando quieres representar un **error de negocio específico** que tu aplicación debe entender y manejar de una forma particular.

**Regla de Oro:** Crea una excepción personalizada cuando un error no es simplemente un fallo técnico (como un JSON mal formado), sino una violación de una regla de negocio.

#### Casos de Uso Comunes para Excepciones Personalizadas:

1.  **Recurso No Encontrado:** Es el ejemplo más clásico. `findById` devuelve un `Optional` vacío. En la capa de servicio, en lugar de devolver el `Optional` o `null`, es mucho más limpio lanzar una excepción que describa el problema.
2.  **Reglas de Negocio Complejas:**
    -   `SaldoInsuficienteException`: En un módulo de banca.
    -   `InventarioAgotadoException`: En un módulo de e-commerce.
    -   `UsuarioYaRegistradoException`: En un módulo de registro.
    -   `OperacionNoPermitidaException`: En un módulo con control de permisos.

#### Cómo Crear y Usar una Excepción Personalizada

**Paso 1: Crear una Jerarquía de Excepciones de Negocio**

Es una buena práctica crear una clase base para todas tus excepciones de negocio.

```java
// En el paquete com.tuproyecto.exception
public abstract class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```
*¿Por qué `RuntimeException`?* Porque las excepciones de negocio suelen ser irrecuperables en el punto donde se lanzan, por lo que no queremos forzar a cada método a declararlas con `throws` (como haríamos con las excepciones "checked").

**Paso 2: Crear la Excepción Específica**

```java
// En el paquete com.tuproyecto.exception
public class RecursoNoEncontradoException extends BusinessException {
    public RecursoNoEncontradoException(String recurso, Long id) {
        super(String.format("%s no encontrado con ID: %d", recurso, id));
    }
}
```

**Paso 3: Lanzar la Excepción desde el Servicio**

El servicio es el lugar donde se aplican las reglas de negocio, por lo tanto, es el lugar donde se lanzan estas excepciones.

```java
// En ProductoService.java
@Transactional(readOnly = true)
public ProductoResponseDTO obtenerPorId(Long id) {
    Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
    
    return mapper.aDTO(producto);
}
```

**Paso 4: Manejar la Excepción en el `GlobalExceptionHandler`**

Ahora, simplemente añadimos un nuevo manejador para nuestra excepción personalizada.

```java
// En GlobalExceptionHandler.java
@ExceptionHandler(RecursoNoEncontradoException.class)
public ResponseEntity<ErrorResponseDTO> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
    ErrorResponseDTO errorResponse = new ErrorResponseDTO(
        HttpStatus.NOT_FOUND.value(),
        "RESOURCE_NOT_FOUND",
        ex.getMessage()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
}
```

### Conclusión de la Estrategia

1.  **Define un DTO de Error global** para tener respuestas consistentes.
2.  **Crea un `GlobalExceptionHandler`** con `@ControllerAdvice`.
3.  **Implementa manejadores para las excepciones comunes de Spring** (`MethodArgumentNotValidException`, `HttpMessageNotReadableException`, etc.).
4.  **Crea excepciones personalizadas** que extiendan de una `BusinessException` base para representar **errores de reglas de negocio** (`RecursoNoEncontradoException`, `SaldoInsuficienteException`, etc.).
5.  **Lanza tus excepciones personalizadas desde la capa de servicio.**
6.  **Añade manejadores para tus excepciones personalizadas** en el `GlobalExceptionHandler`.

Esta arquitectura te proporciona un sistema de manejo de errores robusto, centralizado y fácil de extender para cualquier módulo que añadas a tu aplicación en el futuro.



## Capítulo 10: Flujos de Trabajo de Desarrollo (Los Planos de Ensamblaje)

Este capítulo describe los procesos paso a paso para construir diferentes tipos de funcionalidades en una aplicación Spring Boot. Seguir un flujo de trabajo consistente acelera el desarrollo y garantiza que se respeten las mejores prácticas de arquitectura.

### 10.1 Flujo de Trabajo para un Módulo CRUD Completo

Este es el proceso fundamental para crear y gestionar cualquier recurso (Producto, Usuario, Pedido, etc.).

**Fase 0: Diseño y Contratos**
1.  **Definir la Entidad:** ¿Qué "cosa" del mundo real estoy modelando? (ej. `Libro`).
2.  **Definir los DTOs:**
    -   **Petición (Request):** ¿Qué datos necesito para crear o actualizar un `Libro`? (ej. `LibroRequestDTO` con `titulo`, `autorId`, `isbn`).
    -   **Respuesta (Response):** ¿Qué datos quiero mostrar al cliente sobre un `Libro`? (ej. `LibroResponseDTO` con `id`, `titulo`, `nombreAutor`, `isbn`).

**Fase 1: Capa de Persistencia (La Base)**
1.  **Crear la Entidad (`@Entity`):** Implementa la clase `Libro.java` en el paquete `entity`.
2.  **Crear el Repositorio (`@Repository`):** Implementa la interfaz `LibroRepository.java` extendiendo `JpaRepository<Libro, Long>`.

**Fase 2: Capa de Traducción (El Pegamento)**
1.  **Crear los DTOs:** Implementa las clases `LibroRequestDTO` y `LibroResponseDTO` en el paquete `dto`. Añade anotaciones de validación (`@NotBlank`, etc.) al DTO de petición.
2.  **Crear el Mapper (`@Component`):** Implementa la clase `LibroMapper.java` en el paquete `mapper` con los métodos de conversión entre Entidad y DTOs.

**Fase 3: Capa de Lógica de Negocio (El Cerebro)**
1.  **Crear la Interfaz del Servicio:** Define las firmas de los métodos en `LibroService.java` (ej. `crearLibro`, `obtenerLibroPorId`, etc.). **Las firmas deben usar DTOs.**
2.  **Implementar el Servicio (`@Service`):**
    -   Inyecta el `LibroRepository` y el `LibroMapper`.
    -   Implementa la lógica: recibe DTOs, usa el mapper para convertir a Entidades, aplica reglas de negocio (ej. verificar que el autor exista), llama al repositorio, y usa el mapper de nuevo para convertir la respuesta a un DTO.
    -   Lanza excepciones de negocio personalizadas (`AutorNoEncontradoException`, etc.).

**Fase 4: Capa de Exposición (La Fachada)**
1.  **Implementar el Controlador (`@RestController`):**
    -   Inyecta la interfaz `LibroService`.
    -   Crea los endpoints (`@GetMapping`, `@PostMapping`, etc.).
    -   El controlador solo debe llamar a los métodos del servicio y envolver los DTOs de respuesta en `ResponseEntity`.
    -   Usa `@Valid` para activar la validación en los endpoints `POST` y `PUT`.

**Fase 5: Capa de Seguridad (La Red)**
1.  **Implementar el Manejo de Excepciones:**
    -   Asegúrate de que tu `GlobalExceptionHandler` (`@ControllerAdvice`) está en su sitio.
    -   Añade manejadores (`@ExceptionHandler`) para las nuevas excepciones de negocio personalizadas que lanzaste desde el servicio.

---

### 10.2 Flujo de Trabajo para Módulos que NO son CRUD

No toda la funcionalidad es un simple CRUD. A menudo, necesitas implementar procesos de negocio complejos que involucran múltiples entidades o sistemas externos.

#### Caso 1: Un Endpoint de "Acción" (ej. Procesar un Pago)

Imagina un endpoint `POST /api/pedidos/{id}/pagar`. Este no crea ni actualiza un pedido en el sentido tradicional, sino que ejecuta una acción sobre él.

**Fase 0: Diseño y Contratos**
1.  **Identificar las Entidades Involucradas:** `Pedido`, `Usuario`, `MetodoDePago`.
2.  **Definir el DTO de Petición:** ¿Qué datos necesito para procesar el pago? (ej. `ProcesarPagoDTO` con `metodoDePagoId`, `monto`).
3.  **Definir el DTO de Respuesta:** ¿Qué quiero devolver si el pago es exitoso? (ej. `ResultadoPagoDTO` con `pedidoId`, `estado`, `fechaDePago`, `transaccionId`).

**Fase 1: Lógica de Negocio (El Foco Principal)**
1.  **Crear/Modificar el Servicio:**
    -   Añade un nuevo método a `PedidoService`: `ResultadoPagoDTO procesarPago(Long pedidoId, ProcesarPagoDTO pagoDto)`.
    -   **La lógica aquí es el rey:**
        a.  Busca el `Pedido` por ID. Si no existe, lanza `PedidoNoEncontradoException`.
        b.  Verifica el estado del pedido (ej. no se puede pagar un pedido ya cancelado). Si no es válido, lanza `EstadoPedidoInvalidoException`.
        c.  Busca el `MetodoDePago`.
        d.  **Orquestación:** Llama a un servicio externo de pagos (ej. un `@Service` que usa `RestTemplate` o `WebClient` para hablar con la API de Stripe/PayPal).
        e.  Si el pago externo falla, lanza `ErrorDePagoExternoException`.
        f.  Si tiene éxito, actualiza el estado del `Pedido` a "PAGADO" y guarda la información de la transacción.
        g.  Usa un `Mapper` para crear y devolver el `ResultadoPagoDTO`.

**Fase 2: Exposición y Contratos**
1.  **Crear los DTOs:** Implementa `ProcesarPagoDTO` y `ResultadoPagoDTO`.
2.  **Crear el Mapper:** Añade los nuevos métodos de conversión al `PedidoMapper`.
3.  **Crear el Endpoint en el Controlador:**
    -   Añade un método `POST` en `PedidoController`: `@PostMapping("/{id}/pagar")`.
    -   El método recibe el `id` del pedido y el `ProcesarPagoDTO`.
    -   Llama al método `pedidoService.procesarPago(...)`.
    -   Devuelve una `ResponseEntity` con el `ResultadoPagoDTO`.

**Fase 3: Seguridad**
1.  **Añadir Manejadores de Excepciones:** En el `GlobalExceptionHandler`, añade manejadores para las nuevas excepciones de negocio (`EstadoPedidoInvalidoException`, `ErrorDePagoExternoException`, etc.).

#### Caso 2: Un Endpoint de "Búsqueda Compleja" (ej. Búsqueda de Productos con Filtros)

Imagina un endpoint `GET /api/productos/buscar` que acepta múltiples filtros opcionales (`?categoria=...&precioMax=...&enStock=true`).

**Fase 0: Diseño y Contratos**
1.  **Identificar los Filtros:** ¿Por qué campos se podrá buscar? (categoría, rango de precios, disponibilidad, palabra clave).
2.  **Definir el DTO de Respuesta:** Probablemente el mismo `ProductoResponseDTO` del CRUD.

**Fase 1: Capa de Persistencia (El Foco Principal)**
1.  **Modificar el Repositorio:** Aquí es donde ocurre la magia. Necesitas una forma de construir una consulta dinámica. Hay varias estrategias:
    -   **Query Methods de Spring Data:** Si los filtros son pocos y simples, puedes crear un método como `findByCategoriaAndPrecioLessThanAndEnStockTrue(...)`. Esto se vuelve inmanejable rápidamente.
    -   **Especificaciones de JPA (JPA Criteria API):** **(La mejor práctica)**. Permite construir una consulta de forma programática y segura, añadiendo cláusulas `WHERE` dinámicamente según los filtros que se proporcionen.
    -   **Querydsl:** Una librería de terceros que ofrece una API fluida y type-safe para construir consultas.

**Fase 2: Lógica de Negocio**
1.  **Modificar el Servicio:**
    -   Crea un nuevo método: `List<ProductoResponseDTO> buscarProductos(String categoria, Double precioMax, Boolean enStock)`.
    -   El servicio construye el objeto de especificación de JPA basándose en los parámetros recibidos.
    -   Llama a un método especial del repositorio que acepta una especificación: `productoRepository.findAll(especificacion)`.
    -   Usa el `Mapper` para convertir la lista de Entidades a una lista de DTOs.

**Fase 3: Exposición**
1.  **Crear el Endpoint en el Controlador:**
    -   Añade un método `GET` en `ProductoController`: `@GetMapping("/buscar")`.
    -   Usa `@RequestParam` para cada filtro, con `required = false` para que sean opcionales.
    -   Llama al método `productoService.buscarProductos(...)` pasándole los filtros.
    -   Devuelve la lista de DTOs.