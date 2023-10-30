package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class RegistroWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void testUserIsNotAdminByDefault() throws Exception {
        this.mockMvc.perform(post("/registro")
                        .param("email", "newuser@ua")
                        .param("password", "123")
                        .param("nombre", "New User")
                        .param("admin", String.valueOf(false))
        );
        // THEN
        // assert that the new user is not an admin
        UsuarioData newUserData = usuarioService.findByEmail("newuser@ua");
        assertThat(newUserData.getAdmin()).isFalse();
    }

    @Test
    public void testUserIsAdminIfCheckboxSelected() throws Exception {
        this.mockMvc.perform(post("/registro")
                        .param("email", "newuser@ua")
                        .param("password", "123")
                        .param("nombre", "New User")
                        .param("admin", String.valueOf(true))
                );
        // THEN
        // assert that the new user is an admin
        UsuarioData newUserData = usuarioService.findByEmail("newuser@ua");
        assertThat(newUserData.getAdmin()).isTrue();
    }

    @Test
    public void testCheckBoxApareceEnFormulario() throws Exception {
        // GIVEN
        // No existen usuarios en la BD
        this.mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                // THEN
                // El formulario de registro contiene la posibilidad de marcar la casilla de administrador
                .andExpect(content().string(containsString("Admin?")));
    }

    @Test
    public void checkBoxNoApareceEnFormulario() throws Exception {
        // GIVEN
        // Un usuario admin en la BD

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("admin@ua");
        usuario.setNombre("Admin");
        usuario.setPassword("123");
        usuario.setAdmin(true);
        usuario = usuarioService.registrar(usuario);

        this.mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                // THEN
                // El formulario de registro contiene la posibilidad de marcar la casilla de administrador
                .andExpect(content().string(not(containsString("Admin?"))));
    }
}
