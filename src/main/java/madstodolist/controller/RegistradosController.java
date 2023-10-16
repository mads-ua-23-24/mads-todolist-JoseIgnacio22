package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoAutorizadoException;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class RegistradosController {
    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    private void comprobarAdministrador(UsuarioData usuario) {
        if (!usuario.getAdmin())
            throw new UsuarioNoAutorizadoException();
    }

    @GetMapping("/registrados")
    public String registrados(Model model) {

        UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
        comprobarAdministrador(usuario);

        List<UsuarioData> usuarios = usuarioService.allUsuarios();
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarios", usuarios);

        return "listaUsuarios";
    }

    @GetMapping("/registrados/{id}")
    public String descripcionUsuario(@PathVariable(value="id") Long idUsuario, Model model, HttpSession session) {

        UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
        comprobarAdministrador(usuario);

        UsuarioData usuario1 = usuarioService.findById(idUsuario);
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuario1", usuario1);

        return "descripcionUsuario";
    }
}