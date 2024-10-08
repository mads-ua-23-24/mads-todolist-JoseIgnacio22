package madstodolist.service;

import madstodolist.dto.UsuarioData;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import madstodolist.dto.EquipoData;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class EquipoServiceTest {

    @Autowired
    EquipoService equipoService;

    @Autowired
    UsuarioService usuarioService;

    @Test
    public void crearRecuperarEquipo() {
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        assertThat(equipo.getId()).isNotNull();

        EquipoData equipoBd = equipoService.recuperarEquipo(equipo.getId());
        assertThat(equipoBd).isNotNull();
        assertThat(equipoBd.getNombre()).isEqualTo("Proyecto 1");
    }

    @Test
    public void listadoEquiposOrdenAlfabetico() {
        // GIVEN
        // Dos equipos en la base de datos
        equipoService.crearEquipo("Proyecto BBB");
        equipoService.crearEquipo("Proyecto AAA");

        // WHEN
        // Recuperamos los equipos
        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();

        // THEN
        // Los equipos están ordenados por nombre
        assertThat(equipos).hasSize(2);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Proyecto AAA");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto BBB");
    }

    @Test
    public void añadirUsuarioAEquipo() {
        // GIVEN
        // Un usuario y un equipo en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");

        // WHEN
        // Añadimos el usuario al equipo
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        // THEN
        // El usuario pertenece al equipo
        List<UsuarioData> usuarios = equipoService.usuariosEquipo(equipo.getId());
        assertThat(usuarios).hasSize(1);
        assertThat(usuarios.get(0).getEmail()).isEqualTo("user@ua");
    }

    @Test
    public void recuperarEquiposDeUsuario() {
        // GIVEN
        // Un usuario y dos equipos en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        EquipoData equipo1 = equipoService.crearEquipo("Proyecto 1");
        EquipoData equipo2 = equipoService.crearEquipo("Proyecto 2");
        equipoService.añadirUsuarioAEquipo(equipo1.getId(), usuario.getId());
        equipoService.añadirUsuarioAEquipo(equipo2.getId(), usuario.getId());

        // WHEN
        // Recuperamos los equipos del usuario
        List<EquipoData> equipos = equipoService.equiposUsuario(usuario.getId());

        // THEN
        // El usuario pertenece a los dos equipos
        assertThat(equipos).hasSize(2);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Proyecto 1");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto 2");
    }

    @Test
    public void comprobarExcepciones() {
        // Comprobamos las excepciones lanzadas por los métodos
        // recuperarEquipo, añadirUsuarioAEquipo, usuariosEquipo y equiposUsuario
        assertThatThrownBy(() -> equipoService.recuperarEquipo(1L))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.añadirUsuarioAEquipo(1L, 1L))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.usuariosEquipo(1L))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.equiposUsuario(1L))
                .isInstanceOf(EquipoServiceException.class);

        // Creamos un equipo pero no un usuario y comprobamos que también se lanza una excepción
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        assertThatThrownBy(() -> equipoService.añadirUsuarioAEquipo(equipo.getId(), 1L))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void crearEquipoExcepciones() {
        // GIVEN
        String nombre = null;
        //Un equipo ya creado
        equipoService.crearEquipo("Proyecto P1");
        //WHEN, THEN
        // Comprobamos las excepciones de crearEquipo
        assertThatThrownBy(() -> equipoService.crearEquipo(nombre))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.crearEquipo("Proyecto P1"))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void añadirUsuarioAEquipoYaUnidoExcepcion() {
        // GIVEN
        // Un usuario y un equipo en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        //WHEN
        // Añadimos el usuario al equipo
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        //THEN
        // Comprobamos las excepciones de añadirUsuarioAEquipo
        UsuarioData finalUsuario = usuario;
        assertThatThrownBy(() -> equipoService.añadirUsuarioAEquipo(equipo.getId(), finalUsuario.getId()))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void usuarioPerteneceEquipo() {
        // GIVEN
        // Un usuario y dos equipos en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        EquipoData equipo1 = equipoService.crearEquipo("Proyecto 2");
        //WHEN
        // Añadimos el usuario al equipo
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        //THEN
        // Comprobamos que el usuario pertenece al equipo
        assertThat(equipoService.usuarioPerteneceEquipo(equipo.getId(), usuario.getId())).isTrue();
        // Comprobamos que el usuario no pertenece al segundo equipo
        assertThat(equipoService.usuarioPerteneceEquipo(equipo1.getId(), usuario.getId())).isFalse();
        //Comprobamos excepciones
        assertThatThrownBy(() -> equipoService.usuarioPerteneceEquipo(2L, 2L))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.usuarioPerteneceEquipo(equipo.getId(), 2L))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void eliminarUsuarioDeEquipo() {
        // GIVEN
        // Un usuario y un equipo en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        //WHEN
        // Añadimos el usuario al equipo
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());
        // Eliminamos el usuario del equipo
        equipoService.eliminarUsuarioDeEquipo(equipo.getId(), usuario.getId());
        //THEN
        // Comprobamos que el usuario no pertenece al equipo
        assertThat(equipoService.usuarioPerteneceEquipo(equipo.getId(), usuario.getId())).isFalse();
        //Comprobamos excepciones
        assertThatThrownBy(() -> equipoService.eliminarUsuarioDeEquipo(2L, 2L))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.eliminarUsuarioDeEquipo(equipo.getId(), 2L))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void eliminarEquipo() {
        // GIVEN
        // Un equipo en la base de datos
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        //WHEN
        // Eliminamos el equipo
        equipoService.eliminarEquipo(equipo.getId());
        //THEN
        //Comprobamos que es null
        assertThatThrownBy(() -> equipoService.recuperarEquipo(equipo.getId()))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void eliminarEquipoLanzaExcepción() {
        // GIVEN
        // Equipo inexistente
        Long id = 1L;
        //WHEN, THEN
        //Comprobamos la excepción
        assertThatThrownBy(() -> equipoService.eliminarEquipo(id))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void eliminarUsuariosDeEquipo(){
        // GIVEN
        // Dos usuarios y un equipo en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        UsuarioData usuario1 = new UsuarioData();
        usuario1.setEmail("user1@ua");
        usuario1.setPassword("123");
        usuario1 = usuarioService.registrar(usuario1);
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        //WHEN
        // Añadimos los usuarios al equipo
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario1.getId());
        // Eliminamos los usuarios del equipo
        equipoService.eliminarUsuariosDeEquipo(equipo.getId());
        //THEN
        // Comprobamos que los usuarios no pertenecen al equipo
        assertThat(equipoService.usuarioPerteneceEquipo(equipo.getId(), usuario.getId())).isFalse();
        assertThat(equipoService.usuarioPerteneceEquipo(equipo.getId(), usuario1.getId())).isFalse();
    }

    @Test
    public void eliminarUsuariosDeEquipoLanzaExcepción(){
        // GIVEN
        // Equipo inexistente
        Long id = 1L;
        //WHEN, THEN
        //Comprobamos la excepción
        assertThatThrownBy(() -> equipoService.eliminarUsuariosDeEquipo(id))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void editarEquipo(){
        // GIVEN
        // Un equipo en la base de datos
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        //WHEN
        // Editamos el equipo
        equipoService.editarEquipo(equipo.getId(), "Proyecto 2");
        //THEN
        //Comprobamos que el nombre del equipo ha cambiado
        assertThat(equipoService.recuperarEquipo(equipo.getId()).getNombre()).isEqualTo("Proyecto 2");
    }

    @Test
    public void editarEquipoLanzaExcepción(){
        // GIVEN
        // Un equipo en la bd
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        // Un equipo con el mismo nombre
        equipoService.crearEquipo("Proyecto 2");
        // Equipo inexistente
        Long id = 1L;

        //WHEN, THEN
        //Comprobamos la excepción
        assertThatThrownBy(() -> equipoService.editarEquipo(id, "Proyecto 2"))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.editarEquipo(equipo.getId(), "Proyecto 2"))
                .isInstanceOf(EquipoServiceException.class);
    }
}