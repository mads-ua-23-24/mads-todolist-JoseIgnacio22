# Documentación de la práctica 2

#### Listado de nuevas clases y métodos implementados:

- **Barra de menú.** Se ha añadido código para la comprobación de usuario logueado en ```HomeController.java```.
- **Listado de usuarios.** Se ha añadido nuevo controlador ```RegistradosController.java``` con el método get de la ruta ```/registrados```. Un método público nuevo: ```List<UsuarioData> allUsuarios()``` en ```UsuarioService.java```.
- **Descripción de usuario.** Se ha añadido nuevo método get para la ruta ```/registrados/{id}``` en ```RegistradosController.java```.
- **Usuario administrador.** Se ha modificado el contenido del método post de la ruta ```/login```, del método get y post de la ruta ```/registro``` del controlador ```LoginController.java```. Nuevo atributo privado ```Boolean admin``` con sus getters y setters en ```RegistroData.java```, ```UsuarioData.java``` y ```Usuario.java```. Nuevo método público ```Boolean existsAdmin()``` en ```UsuarioService.java```
- **Protección de registrados y descripción de usuario.** Se ha añadido la excepción de controlador: ```UsuarioNoAutorizadoException.java```. Se ha añadido método privado ```comprobarAdministrador()``` en ```RegistradosController.java```.
- **Bloqueo usuario.** Se ha modificado el contenido del método post de la ruta ```/login``` del controlador ```LoginController.java```. Se ha añadido nuevo método put para la ruta ```/registrados/{id}``` en ```RegistradosController.java```. Nuevo atributo privado ```Boolean blocked``` con sus getters y setters en ```UsuarioData.java``` y ```Usuario.java```. Nuevos métodos públicos: ```void bloquearUsuario()``` y ```void desbloquearUsuario()``` en ```UsuarioService.java```

#### Listado de plantillas thyemeleaf añadidas:
- **Barra de menú.** Se ha añadido parte de contenido para añadir dos barras de menú diferentes en ```fragments.html```.
- **Listado de usuarios.** Se ha añadido nueva plantilla ```listaUsuarios.html```.
- **Descripción de usuario.** Se ha añadido nueva plantilla ```descripcionUsuario.html``` y un botón en ```listaUsuarios.html``` con su script.
- **Usuario administrador.** Se ha añadido un checkbox en ```formRegistro.html```.
- **Bloqueo usuario.** Se ha añadido botones en ```ListadoUsuarios.html```.

#### Tests implementados:
- **Barra de menú.** Nuevos métodos: ```getAboutDevuelveBarraMenuSinLogin()``` y ```getAboutDevuelveBarraMenu()``` en ```AcercaDeWebTest.java```. Nuevo método ```getTareasDevuelveBarraMenu()``` en ```TareaWebTest.java```.
- **Listado de usuarios.** Nueva clase test ```RegistradosWebTest.java``` con los métodos ```listaUsuarios()``` y ```getRegistradosDevuelveBarraMenu()```. Nuevo método ```servicioDevuelveTodosLosUsuarios()``` en ```UsuarioServiceTest.java```.
- **Descripción de usuario.** Nuevo método ```getDescripcionDeUsuario()``` en ```RegistradosWebTest.java```.
- **Usuario administrador.** Nuevos métodos ```getRegistroAdmin()``` y ```postLoginConAdmin()``` en ```UsuarioWebTest.java```. Nuevos métodos ```existeAdmin()``` y ```existeMasDeUnAdmin()``` en ```UsuarioServiceTest.java```.
- **Protección de registrados y descripción de usuario.** Se ha añadido método ```getUnauthorized()``` en ```RegistradosWebTest.java```.
- - **Bloqueo usuario.** Se ha modificado método ```listaUsuarios()``` en ```RegistradosWebTest.java```. Nuevos métodos ```bloquearUsuario()``` y ```desbloquearUsuario()``` en ```UsuarioWebTest.java```. Nuevos métodos ```bloquearUsuario()``` y ```desbloquearUsuario()``` en ```UsuarioServiceTest.java```.

#### Código fuente relevante:
- En ```UsuarioService.java``` método creado para devolver todos los usuarios que me ha sido útil para futuras comprobaciones de usuarios administradores existentes:
```java
    @Transactional(readOnly = true)
    public List<UsuarioData> allUsuarios() {
        logger.debug("Devolviendo todos los usuarios");
        Iterable <Usuario> usuariosIterados = usuarioRepository.findAll();
        List<Usuario> usuarios = (List<Usuario>) usuariosIterados;
        List<UsuarioData> usuariosDATA = usuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioData.class))
                .collect(Collectors.toList());

        // Ordenamos la lista por id de usuario
        Collections.sort(usuariosDATA, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
        return usuariosDATA;
    }
```
- Añadir el ```<atributo> = false``` para que no sean null inicialmete y evitar errores.
```java
    private Boolean admin = false;
    private Boolean bloocked = false;
```
- En ```UsuarioService.java``` método creado para saber si existe ya un usuario administrador ya que sólo puede haber uno:
```java
    @Transactional(readOnly = true)
    public Boolean existsAdmin() {
        logger.debug("Indicando si hay administradores registrados");
        List<UsuarioData> usuarios = allUsuarios();
        // Comprobamos que no haya ningún usuario registrado como administrador
        return usuarios.stream().anyMatch(UsuarioData::getAdmin);
    }
```
- Dentro de  ```UsuarioService.java``` manejamos que no haya más de un administrador registrado:
```java
    public UsuarioData registrar(UsuarioData usuario){
        ...
        else if(existsAdmin()&&usuario.getAdmin()){ // Comprobamos que no haya más de un administrador
            throw new UsuarioServiceException("Ya existe un administrador registrado");
        }...
    }
```
- Dentro de ```UsuarioService.java``` manejamos el estado de un usuario bloqueado:
```java
    public LoginStatus login(String eMail, String password) {
        ...
        } else if (usuario.get().getBlocked()){
            return LoginStatus.USER_BLOCKED;
        }...
    }
```
- En ```UsuarioService.java``` método creado para bloquear un usuario dado un id (hay otro para desbloquear):
```java
    @Transactional
    public void bloquearUsuario(Long idUsuario) {
        logger.debug("Bloqueando usuario " + idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new UsuarioServiceException("No existe usuario con id " + idUsuario);
        }else if(usuario.getAdmin()){
            throw new UsuarioServiceException("No se puede bloquear un administrador");
        }
        usuario.setBlocked(true);
        usuarioRepository.save(usuario);
    }
```

#### Complicaciones que no he conseguido lograr:
- En el html de la barra de menú, que el usuario aparezca a la derecha del todo y quepa cerrar sesión en la pantalla.
- Que Registrados de la barra menú sea visible sólo para el usuario administrador.
- En los test que dado un admin ya creado, comprobar que el checkbox no aparece en ```/registro```.
- Cambiar el nombre del botón de bloquear/desbloquear usuario en función de si está bloqueado o no.
- Que aparezca en ```/registrados``` un evento que indique que se ha bloqueado/desbloqueado un usuario.
- Idear el como sería el método put del controlador de ```/registrados/{id}``` para bloquear/desbloquear un usuario. No había visto el script de delete en la plantilla. **Logrado.**

## Repositorio GitHub

El repositorio de la práctica está en: [GitHub](https://github.com/mads-ua-23-24/mads-todolist-JoseIgnacio22)

## Repositorio Docker Hub

La imagen docker está en: [Docker Hub](https://hub.docker.com/r/joseignacio22/mads-todolist)

## Trello

Tablero en trello: [Trello](https://trello.com/b/lr6Fz8mP/todolist-mads)