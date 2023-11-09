package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.OperacionNoPermitidaException;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.dto.EquipoData;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class EquipoController {

    @Autowired
    EquipoService equipoService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    private Long comprobarUsuarioLogeado() {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null)
            throw new UsuarioNoLogeadoException();
        return idUsuarioLogeado;
    }

    @GetMapping("/equipos")
    public String listadoEquipos(Model model, HttpSession session) {
        Long idUsuarioLogeado = comprobarUsuarioLogeado();
        UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);

        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();

        model.addAttribute("logeado", usuario);
        model.addAttribute("equipos", equipos);
        model.addAttribute("equipoService", equipoService);

        return "listaEquipos";
    }

    @GetMapping("/equipos/{id}")
    public String detalleEquipo(Model model, @PathVariable(value="id") Long idEquipo) {
        Long idUsuarioLogeado = comprobarUsuarioLogeado();
        UsuarioData usuarioLogeado = usuarioService.findById(idUsuarioLogeado);

        EquipoData equipo = equipoService.recuperarEquipo(idEquipo);
        List<UsuarioData> usuarios = equipoService.usuariosEquipo(idEquipo);
        model.addAttribute("logeado", usuarioLogeado);
        model.addAttribute("equipo", equipo);
        model.addAttribute("usuarios", usuarios);

        return "descripcionEquipo";
    }

    @GetMapping("/equipos/nuevo")
    public String formNuevaTarea(@ModelAttribute EquipoData equipoData, Model model,
                                 HttpSession session) {

        Long idUsuarioLogeado = comprobarUsuarioLogeado();

        UsuarioData usuarioLogeado = usuarioService.findById(idUsuarioLogeado);
        model.addAttribute("logeado", usuarioLogeado);

        return "formNuevoEquipo";
    }

    @PostMapping("/equipos/nuevo")
    public String nuevaTarea(@ModelAttribute EquipoData equipoData,
                             Model model, RedirectAttributes flash,
                             HttpSession session) {

        Long idUsuarioLogeado = comprobarUsuarioLogeado();
        try{
            equipoService.crearEquipo(equipoData.getNombre());
        } catch (Exception e) {
            model.addAttribute("error", "Equipo ya existente");
            return "formNuevoEquipo";
        }
        flash.addFlashAttribute("mensaje", "Equipo creado correctamente");
        return "redirect:/equipos";
    }

    @GetMapping("/equipos/{id}/unirse")
    public String unirseAEquipo(Model model, @PathVariable(value="id") Long idEquipo) {
        Long idUsuarioLogeado = comprobarUsuarioLogeado();

        equipoService.añadirUsuarioAEquipo(idEquipo, idUsuarioLogeado);


        return "redirect:/equipos";
    }
}
