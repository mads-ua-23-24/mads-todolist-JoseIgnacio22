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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class RegistradosWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    // Moqueamos el managerUserSession para poder moquear el usuario logeado
    @MockBean
    private ManagerUserSession managerUserSession;

    private ArrayList<Long> addUsuariosBD() {
        // Añadimos un usuario a la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario.setNombre("Usuario Ejemplo");
        usuario = usuarioService.registrar(usuario);

        UsuarioData usuario2 = new UsuarioData();
        usuario2.setEmail("user2@ua");
        usuario2.setPassword("123");
        usuario2.setNombre("Usuario Ejemplo 2");
        usuario2 = usuarioService.registrar(usuario2);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(usuario.getId());
        ids.add(usuario2.getId());
        return ids;
    }

    @Test
    public void listaUsuarios() throws Exception {
        ArrayList<Long> usuarioIds = addUsuariosBD();

        UsuarioData usuario = usuarioService.findById(usuarioIds.get(0));
        UsuarioData usuario2 = usuarioService.findById(usuarioIds.get(1));

        // Moqueamos el método usuarioLogeado
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        this.mockMvc.perform(get("/registrados"))
                .andExpect((content().string(allOf(
                        containsString(usuario.getId().toString()),
                        containsString(usuario.getEmail()),
                        containsString(usuario2.getId().toString()),
                        containsString(usuario2.getEmail())
                ))));
    }

    @Test
    public void getRegistradosDevuelveBarraMenu() throws Exception {
        ArrayList<Long> usuarioIds = addUsuariosBD();
        UsuarioData usuario = usuarioService.findById(usuarioIds.get(0));
        // Moqueamos el método usuarioLogeado
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        this.mockMvc.perform(get("/registrados"))
                .andExpect((content().string(allOf(
                        containsString("Tareas"),
                        containsString("Cuenta"),
                        containsString(usuario.getNombre()),
                        containsString("Cerrar sesión " + usuario.getNombre())
                ))));
    }
}