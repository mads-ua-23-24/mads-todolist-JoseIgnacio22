package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.OperacionNoPermitidaException;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.dto.UsuarioData;
import madstodolist.service.AdminService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {
    @Autowired
    AdminService adminService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    private void comprobarAdministrador(UsuarioData usuario) {
        if (!usuario.getAdmin())
            throw new OperacionNoPermitidaException();
    }

    private Long comprobarUsuarioLogeado() {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null)
            throw new UsuarioNoLogeadoException();
        return idUsuarioLogeado;
    }

    @GetMapping("/registrados")
    public String listadoUsuarios(Model model, HttpSession session) {

        Long idUsuario = comprobarUsuarioLogeado();
        UsuarioData usuario = usuarioService.findById(idUsuario);

        comprobarAdministrador(usuario);

        List<UsuarioData> usuarios = adminService.allUsuarios();

        model.addAttribute("logeado", usuario);
        model.addAttribute("usuarios", usuarios);

        return "listaUsuarios";
    }

    @GetMapping("/registrados/{id}")
    public String detalleUsuario(Model model, @PathVariable(value="id") Long idUsuario) {
        Long idUsuarioLogeado = comprobarUsuarioLogeado();
        UsuarioData usuarioLogeado = usuarioService.findById(idUsuarioLogeado);

        comprobarAdministrador(usuarioLogeado);

        UsuarioData usuario = usuarioService.findById(idUsuario);
        model.addAttribute("logeado", usuarioLogeado);
        model.addAttribute("usuario", usuario);
        return "descripcionUsuario";
    }

    @GetMapping("/registrados/{id}/bloquear")
    public String bloquearUsuario(Model model, @PathVariable(value="id") Long idUsuario) {
        Long idUsuarioLogeado = comprobarUsuarioLogeado();
        UsuarioData usuarioLogeado = usuarioService.findById(idUsuarioLogeado);
        UsuarioData usuarioABloquear = usuarioService.findById(idUsuario);

        comprobarAdministrador(usuarioLogeado);

        if (usuarioABloquear.getAdmin())
            throw new OperacionNoPermitidaException();

        adminService.bloquearUsuario(idUsuario);

        return "redirect:/registrados/";
    }

    @GetMapping("/registrados/{id}/desbloquear")
    public String desbloquearUsuario(Model model, @PathVariable(value="id") Long idUsuario) {
        Long idUsuarioLogeado = comprobarUsuarioLogeado();
        UsuarioData usuarioLogeado = usuarioService.findById(idUsuarioLogeado);
        UsuarioData usuarioABloquear = usuarioService.findById(idUsuario);

        comprobarAdministrador(usuarioLogeado);

        if (usuarioABloquear.getAdmin())
            throw new OperacionNoPermitidaException();

        adminService.desbloquearUsuario(idUsuario);

        return "redirect:/registrados/";
    }
}