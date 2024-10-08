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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class AcercaDeWebTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    private Long addUsuarioBD(){
        // Añadimos un usuario a la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario.setNombre("Usuario Ejemplo");
        usuario = usuarioService.registrar(usuario);

        return usuario.getId();
    }
    @Test
    public void getAboutDevuelveNombreAplicacion() throws Exception {
        // Moqueamos el método usuarioLogeado
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        this.mockMvc.perform(get("/about"))
                .andExpect(content().string(containsString("ToDoList")));
    }
}