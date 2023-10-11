package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RegistradosController {
    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/registrados")
    public String about(Model model) {

        UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
        List<UsuarioData> usuarios = usuarioService.allUsuarios();
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarios", usuarios);

        return "listaUsuarios";
    }
}
