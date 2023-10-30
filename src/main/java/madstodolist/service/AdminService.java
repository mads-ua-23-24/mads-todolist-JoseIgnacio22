package madstodolist.service;

import madstodolist.dto.UsuarioData;
import madstodolist.model.Usuario;
import madstodolist.repository.TareaRepository;
import madstodolist.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class AdminService {

    Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TareaRepository tareaRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<UsuarioData> allUsuarios() {
        logger.debug("Devolviendo todos los usuarios registrados");

        // Fetch all users from the database
        Iterable<Usuario> usuariosIterable = usuarioRepository.findAll();
        List<Usuario> usuarios = StreamSupport.stream(usuariosIterable.spliterator(), false)
                .collect(Collectors.toList());

        // Map the list of Usuario entities to UsuarioData DTOs
        List<UsuarioData> usuariosData = usuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioData.class))
                .collect(Collectors.toList());

        // Optionally sort the list by user ID or some other field
        Collections.sort(usuariosData, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
        return usuariosData;
    }

    @Transactional
    public void bloquearUsuario(Long idUsuario) {
        logger.debug("Bloqueando usuario con id " + idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null)
            throw new UsuarioServiceException("No existe usuario con id " + idUsuario);
        if (usuario.getBlocked())
            throw new UsuarioServiceException("El usuario con id " + idUsuario + " ya está bloqueado");
        if (usuario.getAdmin())
            throw new UsuarioServiceException("No se puede bloquear un usuario administrador");
        if (usuario != null) {
            usuario.setBlocked(true);
            usuarioRepository.save(usuario);
        }
    }

    @Transactional
    public void desbloquearUsuario(Long idUsuario) {
        logger.debug("Desbloqueando usuario con id " + idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null)
            throw new UsuarioServiceException("No existe usuario con id " + idUsuario);
        if (!usuario.getBlocked())
            throw new UsuarioServiceException("El usuario con id " + idUsuario + " no está bloqueado");
        if (usuario.getAdmin())
            throw new UsuarioServiceException("No se puede desbloquear un usuario administrador");
        if (usuario != null) {
            usuario.setBlocked(false);
            usuarioRepository.save(usuario);
        }
    }
}
