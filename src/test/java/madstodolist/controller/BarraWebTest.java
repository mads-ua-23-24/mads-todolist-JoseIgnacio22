package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class BarraWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TareaService tareaService;

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    // Método para inicializar los datos de prueba en la BD
    // Devuelve un mapa con los identificadores del usuario y de la primera tarea añadida

    Map<String, Object> addUsuarioTareasBD() {
        // Create a map to store the objects
        Map<String, Object> resultMap = new HashMap<>();

        // Añadimos un usuario a la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setNombre("Usuario Ejemplo");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);

        // Y añadimos dos tareas asociadas a ese usuario
        TareaData tarea1 = tareaService.nuevaTareaUsuario(usuario.getId(), "Lavar coche");
        tareaService.nuevaTareaUsuario(usuario.getId(), "Renovar DNI");

        // Store the objects in the map
        resultMap.put("usuario", usuario);
        resultMap.put("tarea", tarea1);

        return resultMap;
    }

    @Test
    public void barraMenuEnListadoDeTareas() throws Exception {
        // GIVEN
        // Un usuario con dos tareas en la BD
        UsuarioData usuario = (UsuarioData) addUsuarioTareasBD().get("usuario");
        Long usuarioId = usuario.getId();

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        // WHEN, THEN
        // se realiza la petición GET al listado de tareas del usuario,
        // el HTML devuelto contiene la barra con las referencias e
        // items correctos y el nombre del usuario

        String url = "/usuarios/" + usuarioId.toString() + "/tareas";

        this.mockMvc.perform(get(url))
                .andExpect((content().string(allOf(
                        containsString("ToDoList"),
                        containsString("href=\"/about\">"),
                        containsString("Tareas"),
                        containsString("href=\"" + url + "\""),
                        containsString(usuario.getNombre())
                        ))));
    }

    @Test
    public void barraEnAboutLogeado() throws Exception {
        // GIVEN
        // Un usuario con dos tareas en la BD
        UsuarioData usuario = (UsuarioData) addUsuarioTareasBD().get("usuario");
        Long usuarioId = usuario.getId();

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        // WHEN, THEN
        // se realiza la petición GET al listado de tareas del usuario,
        // el HTML devuelto contiene la barra con las referencias e
        // items correctos y el nombre del usuario

        String url = "/about";

        this.mockMvc.perform(get(url))
                .andExpect((content().string(allOf(
                        containsString("ToDoList"),
                        containsString("href=\"/about\">"),
                        containsString("Tareas"),
                        containsString("href=\"" + url + "\""),
                        containsString(usuario.getNombre())
                ))));
    }

    @Test
    public void barraEnAboutNoLogeado() throws Exception {

        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        // WHEN, THEN
        // se realiza la petición GET al listado de tareas del usuario,
        // el HTML devuelto contiene la barra con las referencias e
        // items correctos y el nombre del usuario

        String url = "/about";

        this.mockMvc.perform(get(url))
                .andExpect(content().string(allOf(
                        containsString("ToDoList"),
                        containsString("Login"),
                        containsString("Registro"),
                        not(containsString("Tareas"))
                )));
    }

    @Test
    public void barraMenuEnNuevaTarea() throws Exception {
        // GIVEN
        // Un usuario con dos tareas en la BD
        UsuarioData usuario = (UsuarioData) addUsuarioTareasBD().get("usuario");
        Long usuarioId = usuario.getId();

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        // WHEN, THEN
        // se realiza la petición GET al listado de tareas del usuario,
        // el HTML devuelto contiene la barra con las referencias e
        // items correctos y el nombre del usuario

        String url = "/usuarios/" + usuarioId.toString() + "/tareas/nueva";
        String urlTareas = "/usuarios/" + usuarioId.toString() + "/tareas";

        this.mockMvc.perform(get(url))
                .andExpect((content().string(allOf(
                        containsString("ToDoList"),
                        containsString("href=\"/about\">"),
                        containsString("Tareas"),
                        containsString("href=\"" + urlTareas + "\""),
                        containsString(usuario.getNombre())
                ))));
    }

    @Test
    public void barraMenuEnEditarTarea() throws Exception {
        // GIVEN
        // Un usuario con dos tareas en la BD

        Map<String, Object> resultMap = addUsuarioTareasBD();
        UsuarioData usuario = (UsuarioData) resultMap.get("usuario");
        TareaData tarea = (TareaData) resultMap.get("tarea");

        Long usuarioId = usuario.getId();
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        // WHEN, THEN
        // se realiza la petición GET al listado de tareas del usuario,
        // el HTML devuelto contiene la barra con las referencias e
        // items correctos y el nombre del usuario

        String url = "/tareas/" + tarea.getId() + "/editar";
        String urlTareas = "/usuarios/" + usuarioId.toString() + "/tareas";

        this.mockMvc.perform(get(url))
                .andExpect((content().string(allOf(
                        containsString("ToDoList"),
                        containsString("href=\"/about\">"),
                        containsString("Tareas"),
                        containsString("href=\"" + urlTareas + "\""),
                        containsString(usuario.getNombre())
                ))));
    }

    @Test
    public void barraMenuContieneEquipos() throws Exception {
        // GIVEN
        // Añadimos un usuario a la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setNombre("Usuario Ejemplo");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);

        Long usuarioId = usuario.getId();
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        // WHEN, THEN
        String url = "/about";

        this.mockMvc.perform(get(url))
                .andExpect(content().string(allOf(
                        containsString("Equipos")
                )));


    }
}
