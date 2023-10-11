package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    private Long comprobarUsuarioLogeado() {
        return managerUserSession.usuarioLogeado();
    }
    @GetMapping("/about")
    public String about(Model model) {
        if (comprobarUsuarioLogeado() == null) {
            model.addAttribute("barra", "menubarSinLogin");
        }else{
            model.addAttribute("barra", "menubar");
            UsuarioData usuario = usuarioService.findById(comprobarUsuarioLogeado());
            model.addAttribute("usuario", usuario);
        }
        return "about";
    }

}