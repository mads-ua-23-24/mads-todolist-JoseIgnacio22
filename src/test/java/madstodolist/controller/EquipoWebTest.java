package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void usuarioSinEquipoMuestraUnirse() throws Exception {
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
        String url = "/equipos";

        // THEN
        this.mockMvc.perform(get(url))
                .andExpect(content().string(allOf(
                        containsString(equipo.getId().toString()),
                        containsString(equipo.getNombre()),
                        containsString("Acción"),
                        containsString("Unirse"),
                        not(containsString("Salirse"))
                )));
    }

    @Test
    public void usuarioConEquipoMuestraSalirse() throws Exception {
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
        String url = "/equipos";

        // THEN
        this.mockMvc.perform(get(url))
                .andExpect(content().string(allOf(
                        containsString(equipo.getId().toString()),
                        containsString(equipo.getNombre()),
                        containsString("Acción"),
                        containsString("Salirse"),
                        not(containsString("Unirse"))
                )));
    }
}
