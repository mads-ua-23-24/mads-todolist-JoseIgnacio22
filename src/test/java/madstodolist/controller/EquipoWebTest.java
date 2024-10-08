package madstodolist.controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.OperacionNoPermitidaException;
import madstodolist.dto.EquipoData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import madstodolist.service.EquipoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class EquipoWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EquipoService equipoService;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Test
    public void listaEquipos() throws Exception {
        // GIVEN
        // Un usuario en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario.setNombre("Usuario Ejemplo");
        usuario = usuarioService.registrar(usuario);
        // Y dos equipos en la base de datos
        EquipoData equipo1 = equipoService.crearEquipo("Proyecto P1");
        EquipoData equipo2 = equipoService.crearEquipo("Proyecto P2");

        // WHEN
        // El usuario se logea
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        // THEN
        // El usuario puede ver la lista de equipos
        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(allOf(
                        containsString("<h2>Listado de equipos</h2>"),
                        containsString("Proyecto P1"),
                        containsString("Proyecto P2")
                )));
    }

    @Test
    public void detalleEquipoMuestraInformacionCorrecta() throws Exception {
        // GIVEN
        // Un usuario en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario.setNombre("Usuario Ejemplo");
        usuario = usuarioService.registrar(usuario);
        // Y un equipo en la base de datos
        EquipoData equipo = equipoService.crearEquipo("Proyecto P1");
        Long equipoId = equipo.getId();

        // Pertenece al equipo
        equipoService.añadirUsuarioAEquipo(equipoId, usuario.getId());

        // WHEN
        // El usuario se logea
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());
        String url = "/equipos/" + equipoId;

        // THEN
        this.mockMvc.perform(get(url))
                .andExpect(content().string(allOf(
                        containsString("Proyecto P1"),
                        containsString("<th>Nombre</th>"),
                        containsString(usuario.getNombre()),
                        containsString("<th>Correo</th>"),
                        containsString(usuario.getEmail())
                )));
    }

    @Test
    public void getNuevoEquipoDevuelveForm() throws Exception {
        // GIVEN
        // Un usuario en la BD
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario.setNombre("Usuario Ejemplo");
        usuario = usuarioService.registrar(usuario);

        // WHEN
        // El usuario se logea
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        // WHEN, THEN
        // si ejecutamos una petición GET para crear un nuevo equipo,
        // el HTML resultante contiene un formulario y la ruta con
        // la acción para crear el nuevo equipo.

        String urlPeticion = "/equipos/nuevo";
        String urlAction = "action=\"/equipos/nuevo\"";

        this.mockMvc.perform(get(urlPeticion))
                .andExpect((content().string(allOf(
                        containsString("form method=\"post\""),
                        containsString(urlAction)
                ))));
    }

    @Test
    public void postNuevoEquipoDevuelveRedirectYAñadeEquipo() throws Exception {
        // GIVEN
        // Un usuario en la BD
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario.setNombre("Usuario Ejemplo");
        usuario = usuarioService.registrar(usuario);

        // WHEN
        // El usuario se logea
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        // WHEN, THEN
        // realizamos la petición POST para añadir un equipo,
        // el estado HTTP que se devuelve es un REDIRECT al listado
        // de equipos.

        String urlPost = "/equipos/nuevo";
        String urlRedirect = "/equipos";

        this.mockMvc.perform(post(urlPost)
                        .param("nombre", "Equipo 1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlRedirect));

        // y si después consultamos el listado de equipos con una petición
        // GET el HTML contiene el equipo añadido.

        this.mockMvc.perform(get(urlRedirect))
                .andExpect((content().string(containsString("Equipo 1"))));
    }

    @Test
    public void postNuevoEquipoExistenteIndicaError() throws Exception {
        // GIVEN
        // Un usuario en la BD
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario.setNombre("Usuario Ejemplo");
        usuario = usuarioService.registrar(usuario);
        // Y un equipo en la base de datos
        EquipoData equipo = equipoService.crearEquipo("Proyecto P1");

        // WHEN
        // El usuario se logea
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        // WHEN, THEN
        // realizamos la petición POST para añadir un equipo,
        // el estado HTTP que se devuelve es OK y el HTML contiene
        // un mensaje de error.

        String urlPost = "/equipos/nuevo";

        this.mockMvc.perform(post(urlPost)
                        .param("nombre", "Proyecto P1"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("form method=\"post\""),
                        containsString("action=\"/equipos/nuevo\""),
                        containsString("Equipo ya existente")
                )));
    }

    @Test
    public void muestraBotónAñadirEquipo() throws Exception {
        // GIVEN
        // Un usuario en la BD
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario.setNombre("Usuario Ejemplo");
        usuario = usuarioService.registrar(usuario);

        // WHEN
        // El usuario se logea
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());
        String url = "/equipos";

        // THEN
        this.mockMvc.perform(get(url))
                .andExpect(content().string(allOf(
                        containsString("<p><a class=\"btn btn-primary\" href=\"/equipos/nuevo\"> Crear equipo</a></p>")
                )));
    }

    @Test
    public void unirseRedirectYCorrecto() throws Exception {
        // GIVEN
        // Un usuario en la BD
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario.setNombre("Usuario Ejemplo");
        usuario = usuarioService.registrar(usuario);
        // Y un equipo en la base de datos
        EquipoData equipo = equipoService.crearEquipo("Proyecto P1");

        // WHEN
        // El usuario se logea
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());
        // WHEN, THEN
        // realizamos la petición GET para unirse,
        // el estado HTTP que se devuelve es REDIRECT,

        String url = "/equipos/" + equipo.getId() + "/unirse";
        String urlRedirect = "/equipos";

        this.mockMvc.perform(get(url))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlRedirect));

        // y cuando se pide el listado de equipos, aparece salirse.

        this.mockMvc.perform(get(urlRedirect))
                .andExpect(content().string(
                        allOf(not(containsString("Unirse")),
                                containsString("Salirse"))));
    }

    @Test
    public void salirseRedirectYCorrecto() throws Exception {
        // GIVEN
        // Un usuario en la BD
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario.setNombre("Usuario Ejemplo");
        usuario = usuarioService.registrar(usuario);
        // Y un equipo en la base de datos
        EquipoData equipo = equipoService.crearEquipo("Proyecto P1");
        // Y el usuario pertenece al equipo
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        // WHEN
        // El usuario se logea
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());
        // WHEN, THEN
        // realizamos la petición GET para salirse,
        // el estado HTTP que se devuelve es REDIRECT,

        String url = "/equipos/" + equipo.getId() + "/salirse";
        String urlRedirect = "/equipos";

        this.mockMvc.perform(get(url))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlRedirect));

        // y cuando se pide el listado de equipos, aparece unirse.

        this.mockMvc.perform(get(urlRedirect))
                .andExpect(content().string(
                        allOf(not(containsString("Salirse")),
                                containsString("Unirse"))));
    }

    @Test
    public void eliminarEquipo() throws Exception{
        // GIVEN
        // Un usuario en la BD
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario.setAdmin(true);
        usuario.setNombre("Usuario Ejemplo");
        usuario = usuarioService.registrar(usuario);
        // Y un equipo en la base de datos
        EquipoData equipo = equipoService.crearEquipo("Proyecto P1");
        // Y el usuario pertenece al equipo
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        // WHEN
        // El usuario se logea
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());
        // WHEN, THEN
        // realizamos la petición DELETE para eliminar el equipo,
        // el estado HTTP que se devuelve es OK,

        String url = "/equipos/" + equipo.getId();
        String urlRedirect = "/equipos";

        this.mockMvc.perform(delete(url))
                .andExpect(status().isOk());

        // y cuando se pide el listado de equipos, no aparece el equipo.

        this.mockMvc.perform(get(urlRedirect))
                .andExpect(content().string(
                        allOf(not(containsString("Proyecto P1")))));
    }

    @Test
    public void postEditarEquipo() throws Exception{
        // GIVEN
        // Un usuario en la BD
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario.setAdmin(true);
        usuario.setNombre("Usuario Ejemplo");
        usuario = usuarioService.registrar(usuario);
        // Y un equipo en la base de datos
        EquipoData equipo = equipoService.crearEquipo("Proyecto P1");

        // WHEN
        // El usuario se logea
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());
        // WHEN, THEN
        // realizamos la petición POST para editar el equipo,
        // el estado HTTP que se devuelve es OK,

        String url = "/equipos/" + equipo.getId() + "/editar";
        String urlRedirect = "/equipos";

        this.mockMvc.perform(post(url)
                        .param("nombre", "Proyecto P2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlRedirect));

        // y cuando se pide el listado de equipos, aparece el equipo editado.

        this.mockMvc.perform(get(urlRedirect))
                .andExpect(content().string(
                        allOf(not(containsString("Proyecto P1")),
                                containsString("Proyecto P2"))));
    }

    @Test
    public void getEditarEquipo() throws Exception{
        // GIVEN
        // Un usuario en la BD
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario.setAdmin(true);
        usuario.setNombre("Usuario Ejemplo");
        usuario = usuarioService.registrar(usuario);
        // Y un equipo en la base de datos
        EquipoData equipo = equipoService.crearEquipo("Proyecto P1");

        // WHEN
        // El usuario se logea
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());
        // WHEN, THEN
        // realizamos la petición GET para editar el equipo,
        // el estado HTTP que se devuelve es OK,

        String url = "/equipos/" + equipo.getId() + "/editar";

        this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        allOf(containsString("form method=\"post\""),
                                containsString("action=\"/equipos/" + equipo.getId() + "/editar\""),
                                containsString("value=\"" + equipo.getNombre() + "\""))));
    }

    @Test
    public void listaEquiposMuestraBotonesAdministrador() throws Exception{
        // GIVEN
        // Un usuario en la BD
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("admin@ua");
        usuario.setPassword("123");
        usuario.setAdmin(true);
        usuario.setNombre("Usuario Ejemplo");
        usuario = usuarioService.registrar(usuario);
        // Y dos equipos en la base de datos
        EquipoData equipo = equipoService.crearEquipo("Proyecto P1");

        // WHEN
        // El usuario se logea
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        // THEN
        // El usuario puede ver la lista de equipos
        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(allOf(
                        containsString("Gestión"),
                        containsString("<a class=\"btn btn-warning btn-xs\" href=\"/equipos/" + equipo.getId() + "/editar\"/>Editar</a>"),
                        containsString("<button class=\"btn btn-danger btn-xs\" onmouseover=\"\" style=\"cursor: pointer;\""),
                        containsString("onclick=\"del(&#39;/equipos/" + equipo.getId() + "&#39;)\">Borrar</button>")

                )));
    }

    @Test
    public void postEditarEquipoExistenteIndicaError() throws Exception {
        // GIVEN
        // Un usuario en la BD
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("admin@ua");
        usuario.setPassword("123");
        usuario.setAdmin(true);
        usuario.setNombre("Usuario Ejemplo");
        usuario = usuarioService.registrar(usuario);
        // Y dos equipos en la base de datos
        EquipoData equipo = equipoService.crearEquipo("Proyecto P1");
        EquipoData equipo2 = equipoService.crearEquipo("Proyecto P2");

        // WHEN
        // El usuario se logea
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        // WHEN, THEN
        // realizamos la petición POST para añadir un equipo,
        // el estado HTTP que se devuelve es OK y el HTML contiene
        // un mensaje de error.

        String url = "/equipos/" + equipo.getId() + "/editar";

        this.mockMvc.perform(post(url)
                        .param("nombre", equipo2.getNombre()))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("form method=\"post\""),
                        containsString("action=\"" + url + "\""),
                        containsString("Equipo ya existente")
                )));
    }
}
