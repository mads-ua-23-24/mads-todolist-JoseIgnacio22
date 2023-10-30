package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class AdminWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    Map<String, Object> addUsuariosBD() {
        // Create a map to store the objects

        Map<String, Object> resultMap = new HashMap<>();

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setNombre("Usuario 1");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);

        UsuarioData usuario2 = new UsuarioData();
        usuario2.setEmail("user2@ua");
        usuario2.setNombre("Usuario 2");
        usuario2.setPassword("123");
        usuario2 = usuarioService.registrar(usuario2);

        UsuarioData usuario3 = new UsuarioData();
        usuario3.setEmail("user3@ua");
        usuario3.setNombre("Usuario 3");
        usuario3.setPassword("123");
        usuario3.setBlocked(true); // El usuario 3 está bloqueado
        usuario3 = usuarioService.registrar(usuario3);

        UsuarioData usuarioAdmin = new UsuarioData();
        usuarioAdmin.setEmail("admin@ua");
        usuarioAdmin.setNombre("Admin");
        usuarioAdmin.setPassword("123");
        usuarioAdmin.setAdmin(true);
        usuarioAdmin = usuarioService.registrar(usuarioAdmin);

        resultMap.put("usuario1", usuario);
        resultMap.put("usuario2", usuario2);
        resultMap.put("usuario3", usuario3);
        resultMap.put("admin", usuarioAdmin);

        return resultMap;
    }

    @Test
    public void detalleUsuarioMuestraInformacionCorrecta() throws Exception {
        // GIVEN
        // Usuarios añadidos en la BD

        Map<String, Object> usuariosMap = addUsuariosBD();

        UsuarioData usuarioAdmin = (UsuarioData) usuariosMap.get("admin");
        Long usuarioAdminId = usuarioAdmin.getId();
        UsuarioData usuario = (UsuarioData) usuariosMap.get("usuario1");
        Long usuarioId = usuario.getId();

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioAdminId);

        // WHEN, THEN
        // Perform a GET request to the detail page of a specific user
        // The HTML content should contain the user's name and email
        String url = "/registrados/" + usuarioId;

        this.mockMvc.perform(get(url))
                .andExpect(content().string(allOf(
                        containsString("<strong>Nombre:</strong>"),
                        containsString(usuario.getNombre()),
                        containsString("<strong>Correo:</strong>"),
                        containsString(usuario.getEmail())
                )));
    }
}
