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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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
}
