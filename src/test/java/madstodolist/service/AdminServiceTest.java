package madstodolist.service;

import madstodolist.dto.UsuarioData;
import madstodolist.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class AdminServiceTest {

    @Autowired
    AdminService adminService;

    @Autowired
    UsuarioService usuarioService;

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
    public void servicioDevuelveTodosLosUsuarios() {
        ArrayList<Long> usuarioIds = addUsuariosBD();

        UsuarioData usuario = usuarioService.findById(usuarioIds.get(0));
        UsuarioData usuario2 = usuarioService.findById(usuarioIds.get(1));

        List<UsuarioData> usuarios = adminService.allUsuarios();

        // usuario
        assertThat(usuario.getId()).isEqualTo(usuarios.get(0).getId());
        assertThat(usuario.getEmail()).isEqualTo(usuarios.get(0).getEmail());
        assertThat(usuario.getNombre()).isEqualTo(usuarios.get(0).getNombre());
        // usuario2
        assertThat(usuario2.getId()).isEqualTo(usuarios.get(1).getId());
        assertThat(usuario2.getEmail()).isEqualTo(usuarios.get(1).getEmail());
        assertThat(usuario2.getNombre()).isEqualTo(usuarios.get(1).getNombre());
    }

    @Test
    public void bloquearUsuario(){
        ArrayList<Long> usuarioIds = addUsuariosBD();

        UsuarioData usuario = usuarioService.findById(usuarioIds.get(0));
        usuario.setEmail("admin@ua");
        usuario.setAdmin(true);
        UsuarioData admin = usuarioService.registrar(usuario);

        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            adminService.bloquearUsuario(admin.getId());
        });

        UsuarioData usuario2 = usuarioService.findById(usuarioIds.get(1));
        usuario2.setEmail("blocked@ua");
        UsuarioData blocked = usuarioService.registrar(usuario2);

        Assertions.assertDoesNotThrow(() -> {
            adminService.bloquearUsuario(blocked.getId());
        });
        Assertions.assertEquals(true, usuarioService.findById(blocked.getId()).getBlocked());
    }

    @Test
    public void desbloquearUsuario(){
        ArrayList<Long> usuarioIds = addUsuariosBD();

        UsuarioData usuario2 = usuarioService.findById(usuarioIds.get(1));
        usuario2.setEmail("blocked@ua");
        UsuarioData blocked = usuarioService.registrar(usuario2);
        adminService.bloquearUsuario(blocked.getId());

        Assertions.assertDoesNotThrow(() -> {
            adminService.desbloquearUsuario(blocked.getId());
        });
        Assertions.assertEquals(false, usuarioService.findById(blocked.getId()).getBlocked());
    }
}
