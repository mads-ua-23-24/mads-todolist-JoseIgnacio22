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

#### Complicaciones?:

## Repositorio GitHub

El repositorio de la práctica está en: [GitHub](https://github.com/mads-ua-23-24/mads-todolist-JoseIgnacio22)

## Repositorio Docker Hub

La imagen docker está en: [Docker Hub](https://hub.docker.com/r/joseignacio22/spring-boot-demoapp)

## Trello

Tablero en trello: [Trello](https://trello.com/b/lr6Fz8mP/todolist-mads)