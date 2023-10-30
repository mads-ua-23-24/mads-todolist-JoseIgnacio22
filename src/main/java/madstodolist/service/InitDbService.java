package madstodolist.service;

import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.repository.TareaRepository;
import madstodolist.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
// Se ejecuta solo si el perfil activo es 'dev'
@Profile("dev")
public class InitDbService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TareaRepository tareaRepository;

    // Se ejecuta tras crear el contexto de la aplicación
    // para inicializar la base de datos
    @PostConstruct
    public void initDatabase() {

        // Añadimos un usuario de prueba con dos tareas

        Usuario usuario = new Usuario("user@ua");
        usuario.setNombre("Usuario Ejemplo");
        usuario.setPassword("123");
        usuarioRepository.save(usuario);

        Tarea tarea1 = new Tarea(usuario, "Lavar coche");
        tareaRepository.save(tarea1);

        Tarea tarea2 = new Tarea(usuario, "Renovar DNI");
        tareaRepository.save(tarea2);

        // Añadimos un usuario admin

        Usuario admin = new Usuario("admin@ua");
        admin.setNombre("Usuario Admin");
        admin.setPassword("123");
        admin.setAdmin(true);
        usuarioRepository.save(admin);

        // Añadimos un usuario bloqueado

        Usuario bloqueado = new Usuario("bloqueado@ua");
        bloqueado.setNombre("Usuario Bloqueado");
        bloqueado.setPassword("123");
        bloqueado.setBlocked(true);
        usuarioRepository.save(bloqueado);
    }
}
