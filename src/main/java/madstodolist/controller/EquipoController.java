package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.dto.UsuarioData;
import madstodolist.dto.EquipoData;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/equipos")
    public String listadoEquipos(Model model, HttpSession session) {
        Long idUsuario = managerUserSession.usuarioLogeado();
        if (idUsuario == null)
            throw new UsuarioNoLogeadoException();
        UsuarioData usuario = usuarioService.findById(idUsuario);

        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();

        model.addAttribute("logeado", usuario);
        model.addAttribute("equipos", equipos);

        return "listaEquipos";
    }
}
