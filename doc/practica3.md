# Documentación de la práctica 3

###### Cambios respecto a la práctica 2:

- He cambiado parte de la implementación del código de la práctica 2 gracias a la implementación del examen.
- Por ejemplo, separar el código de admin de ```UsuarioService``` en ```AdminService```, ```RegistradosController``` pasa a ser ```AdminController```.
- Buscada solución a los problemas que no pude solucionar, como puede ser ocultar el tab de "Registrados" de la barra de menú a los usuarios que no sean administradores, cambiar el estado del botón "bloquear/desbloquear" y muchos más.
- El commit es el siguiente: [1fe288b](https://github.com/mads-ua-23-24/mads-todolist-JoseIgnacio22/commit/1fe288bbedf27469256f088634ee30dae35afb8e)

#### Listado de nuevas clases y métodos implementados:

- **Listado de equipos. Capa interna.** Se ha añadido nuevo módelo ```Equipo.java```, nuevo repositorio ```EquipoRepository```, nuevo método ```getEquipos()``` en ```Usuario.java```, nueva clase ```EquipoData.java```, nueva clase servicio ```EquipoService.java``` y nueva clase de excepción ```EquipoServiceException()```. Extra: añadido método ```findAllByOrderByNombreAsc()``` en ```EquipoRepository.java```.
- **Listado de equipos. Capa externa.** Se ha añadido nuevo controller ```EquipoController.java``` con los métodos ```listadoEquipos```y ```detalleEquipo```.
- **Gestionar pertenencia al equipo. Capa interna.** Se ha añadido nuevo método ```findByNombre(nombre)``` en ```EquipoRepository.java```, nuevo método ```removeUsuario(usuario)``` en ```Equipo.java``` y nuevo métodos: ```usuarioPerteneceEquipo(id, id)```, ```eliminarUsuarioDeEquipo(id,id)``` en ```EquipoService.java```.
- **Gestionar pertenencia al equipo. Capa externa.** Se ha añadido nuevos métodos ```formNuevoEquipo```, ```nuevoEquipo```, ```unirseAEquipo``` y ```salirseAEquipo``` en ```EquipoController.java```.
- **Gestión de equipo. Capa interna.** Se han añadido nuevos métodos: ```eliminarEquipo(id)```, ```eliminarUsuariosDeEquipo(id)``` y ```editarEquipo(id, nombre)``` en ```EquipoService.java```.

#### Listado de plantillas thyemeleaf añadidas:
- **Listado de equipos.** Se ha añadido nueva plantilla ```listaEquipos.html```, se ha modificado ```fragments.html``` con ruta a ```/equipos``` y nueva plantilla ```descripcionEquipo.html```.
- **Gestionar pertenencia al equipo.** Se ha añadido nueva plantilla ```formNuevoEquipo.html```

#### Tests implementados:
- **Listado de equipos. Capa interna.** Nueva clase test ```EquipoTest.java``` con los métodos ```crearEquipo()```, ```grabarYBuscarEquipo()```, ```comprobarIgualdadEquipos()```, ```comprobarRelaciónBaseDatos()```, ```comprobarFindALl()```. Nueva clase test ```EquipoServiceTest.java``` con los métodos ```crearRecuperarEquipo()```, ```listadoEquiposOrdenAlfabetico()```, ```añadirUsuarioAEquipo()```, ```recuperarEquiposDeUsuario()``` y ```comprobarExcepciones()```.
- **Listado de equipos. Capa externa.** Nueva clase test ```EquipoWebTest.java``` con los métodos ```listaEquipos()```, ```detalleEquipoMuestraInformacionCorrecta()```, . Nuevo método ```barraMenuContieneEquipos()``` en ```BarraWebTest.java```.
- **Gestionar pertenencia al equipo. Capa interna.** Se han añadido nuevos métodos: ```crearEquipoExcepciones()```, ```añadirUsuarioAEquipoYaUnidoExcepcion()```, ```usuarioPerteneceEquipo()``` y ```eliminarUsuarioDeEquipo()```   en ```EquipoServiceTest.java```.
- **Gestionar pertenencia al equipo. Capa Externa.** Se han añadido nuevos métodos: ```getNuevoEquipoDevuelveForm()```, ```postNuevoEquipoDevuelveRedirectYAñadeEquipo()```, ```postNuevoEquipoExistenteIndicaError()```, ```muestraBotónAñadirEquipo()```, ```unirseRedirectYCorrecto()``` y ```salirseRedirectYCorrecto()``` en ```EquipoWebTest.java```.
- **Gestión de equipo. Capa interna.** Se han añadido nuevos métodos: ```eliminarEquipo()```, ```eliminarEquipoLanzaExcepción()```, ```eliminarUsuariosDeEquipo()```, ```eliminarUsuariosDeEquipoLanzaExcepción()```, ```editarEquipo()``` y ```editarEquipoLanzaExcepción()``` en ```EquipoServiceTest.java```.

#### Código fuente relevante:
- En el método ```findAllOrdenadoPorNombre()``` de la clase servicio ```EquipoService.java``` utilzamos un nuevo método repositorio ```findAllByOrderByNombreAsc()```:
```java
     @Transactional(readOnly = true)
    public List<EquipoData> findAllOrdenadoPorNombre() {
        List<Equipo> equipos = equipoRepository.findAllByOrderByNombreAsc();

        // Map the list of Usuario entities to UsuarioData DTOs
        List<EquipoData> equiposData = equipos.stream()
        .map(equipo -> modelMapper.map(equipo, EquipoData.class))
        .collect(Collectors.toList());

        return equiposData;
    }
    ...
    EquipoRepository.java
    ...
        List<Equipo> findAllByOrderByNombreAsc();
```
- En el método ```crearEquipo()``` de la clase servicio ```EquipoService.java``` utilzamos un nuevo método repositorio ```findByNombre(nombre)``` para controlar excepciones:
```java
    @Transactional
    public EquipoData crearEquipo(String nombre) {
        if (nombre == null) throw new EquipoServiceException("El nombre del equipo no puede ser nulo");

        Optional<Equipo> equipoDB = equipoRepository.findByNombre(nombre);
        if (equipoDB.isPresent()) throw new EquipoServiceException("El equipo " + nombre + " ya está creado");

        Equipo equipo = new Equipo(nombre);
        equipoRepository.save(equipo);
        return modelMapper.map(equipo, EquipoData.class);
    }
    ...
    EquipoRepository.java
    ...
        Optional<Equipo> findByNombre(String nombre);
```
- Método ```usuarioPerteneceEquipo(id, id)``` en ```EquipoService.java``` que comprueba la pertenencia de un usuario en un equipo:
```java
    @Transactional(readOnly = true)
    public Boolean usuarioPerteneceEquipo(Long idEquipo, Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) throw new EquipoServiceException("No existe usuario con id " + idUsuario);
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        if (equipo == null) throw new EquipoServiceException("No existe equipo con id " + idEquipo);
        return equipo.getUsuarios().contains(usuario);
    }
```
- **Importante.** En el método ```listadoEquipos``` en ```EquipoController.java``` pasamos al modelo el atributo "equipoService":
```java
    @GetMapping("/equipos")
    public String listadoEquipos(Model model, HttpSession session) {
        Long idUsuarioLogeado = comprobarUsuarioLogeado();
        UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);

        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();

        model.addAttribute("logeado", usuario);
        model.addAttribute("equipos", equipos);
        model.addAttribute("equipoService", equipoService);

        return "listaEquipos";
    }
```
Y en la plantilla ```listaEquipos.html``` utilizamos la función ```usuarioPerteneceEquipo(id, id)``` para saber si mostrar el botón de "Unirse" o "Salirse" de la siguiente forma:
```html
    <tr th:each="equipo: ${equipos}">
        <td th:text="${equipo.id}"></td>
        <td>
            <a style="text-decoration: underline;" th:href="@{'/equipos/' + ${equipo.id}}" th:text="${equipo.nombre}"></a>
        </td>
        <td th:if="${!equipoService.usuarioPerteneceEquipo(equipo.id,logeado.id)}"><a class="btn btn-primary btn-xs" th:href="@{'/equipos/' + ${equipo.id} + '/unirse'}"/>Unirse</a></td>
        <td th:if="${equipoService.usuarioPerteneceEquipo(equipo.id,logeado.id)}"><a class="btn btn-danger btn-xs" th:href="@{'/equipos/' + ${equipo.id} + '/salirse'}"/>Salirse</a></td>
    </tr>
```
#### Refactorización de código:
- Método ```comprobarUsuarioLogeado()``` en ```EquipoController.java```:
```java
    private Long comprobarUsuarioLogeado() {
        Long idUsuarioLogeado = (Long) session.getAttribute("idUsuarioLogeado");
        if (idUsuarioLogeado == null) throw new EquipoWebException("No hay ningún usuario logeado");
        return idUsuarioLogeado;
    }
```
- Ampliación en tests ```EquipoWebTest.java```:
   - Commits: 
     - De [39e7f0d](https://github.com/mads-ua-23-24/mads-todolist-JoseIgnacio22/commit/39e7f0de29f1ebd34d65071ba91c5962002ad2c2) se añaden en [25b9650](https://github.com/mads-ua-23-24/mads-todolist-JoseIgnacio22/commit/25b96503963bb9bfa99502965ca996726e21c385) y en [3769e6e](https://github.com/mads-ua-23-24/mads-todolist-JoseIgnacio22/commit/3769e6e8cffe5be90e70101058d00ae242d9e420)
     - Se eliminan en [a9fb368](https://github.com/mads-ua-23-24/mads-todolist-JoseIgnacio22/commit/a9fb36838180fae9db652c4a585d6c8bd41e2ff1) 

## Repositorio GitHub

El repositorio de la práctica está en: [GitHub](https://github.com/mads-ua-23-24/mads-todolist-JoseIgnacio22)

## Repositorio Docker Hub

La imagen docker está en: [Docker Hub](https://hub.docker.com/r/joseignacio22/mads-todolist)

## Trello

Tablero en trello: [Trello](https://trello.com/b/lr6Fz8mP/todolist-mads)