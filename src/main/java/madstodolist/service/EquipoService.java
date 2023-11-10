package madstodolist.service;

import madstodolist.dto.EquipoData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import madstodolist.repository.EquipoRepository;
import madstodolist.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EquipoService {

    @Autowired
    EquipoRepository equipoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public EquipoData crearEquipo(String nombre) {
        if (nombre == null) throw new EquipoServiceException("El nombre del equipo no puede ser nulo");

        Optional<Equipo> equipoDB = equipoRepository.findByNombre(nombre);
        if (equipoDB.isPresent()) throw new EquipoServiceException("El equipo " + nombre + " ya está creado");

        Equipo equipo = new Equipo(nombre);
        equipoRepository.save(equipo);
        return modelMapper.map(equipo, EquipoData.class);
    }

    @Transactional(readOnly = true)
    public EquipoData recuperarEquipo(Long id) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        if (equipo == null) throw new EquipoServiceException("No existe equipo con id " + id);
        return modelMapper.map(equipo, EquipoData.class);
    }

    @Transactional(readOnly = true)
    public List<EquipoData> findAllOrdenadoPorNombre() {
        List<Equipo> equipos = equipoRepository.findAllByOrderByNombreAsc();

        // Map the list of Usuario entities to UsuarioData DTOs
        List<EquipoData> equiposData = equipos.stream()
                .map(equipo -> modelMapper.map(equipo, EquipoData.class))
                .collect(Collectors.toList());

        return equiposData;
    }

    @Transactional
    public void añadirUsuarioAEquipo(Long id, Long id1) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        if (equipo == null) throw new EquipoServiceException("No existe equipo con id " + id);
        Usuario usuario = usuarioRepository.findById(id1).orElse(null);
        if (usuario == null) throw new EquipoServiceException("No existe usuario con id " + id1);
        if (equipo.getUsuarios().contains(usuario)) throw new EquipoServiceException("El usuario ya pertenece al equipo");
        equipo.addUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioData> usuariosEquipo(Long id) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        if (equipo == null) throw new EquipoServiceException("No existe equipo con id " + id);
        // Hacemos uso de Java Stream API para mapear la lista de entidades a DTOs.
        return equipo.getUsuarios().stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioData.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EquipoData> equiposUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) throw new EquipoServiceException("No existe usuario con id " + id);
        return usuario.getEquipos().stream()
                .map(equipo -> modelMapper.map(equipo, EquipoData.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Boolean usuarioPerteneceEquipo(Long idEquipo, Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) throw new EquipoServiceException("No existe usuario con id " + idUsuario);
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        if (equipo == null) throw new EquipoServiceException("No existe equipo con id " + idEquipo);
        return equipo.getUsuarios().contains(usuario);
    }

    @Transactional
    public void eliminarUsuarioDeEquipo(Long id, Long id1) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        if (equipo == null) throw new EquipoServiceException("No existe equipo con id " + id);
        Usuario usuario = usuarioRepository.findById(id1).orElse(null);
        if (usuario == null) throw new EquipoServiceException("No existe usuario con id " + id1);
        if (!equipo.getUsuarios().contains(usuario)) throw new EquipoServiceException("El usuario no pertenece al equipo");
        equipo.removeUsuario(usuario);
    }

    @Transactional
    public void eliminarEquipo(Long id) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        if (equipo == null) throw new EquipoServiceException("No existe equipo con id " + id);
        equipoRepository.delete(equipo);
    }

    @Transactional
    public void eliminarUsuariosDeEquipo(Long id) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        if (equipo == null) throw new EquipoServiceException("No existe equipo con id " + id);
        equipo.getUsuarios().clear();
    }

    @Transactional
    public void editarEquipo(Long id, String s) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        equipo.setNombre(s);
    }
}