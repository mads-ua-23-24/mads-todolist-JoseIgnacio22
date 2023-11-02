package madstodolist.model;

import javax.validation.constraints.NotNull;

public class Equipo {
    @NotNull
    private String nombre;

    // Constructor vacío necesario para JPA/Hibernate.
    // No debe usarse desde la aplicación.
    public Equipo() {}

    // Constructor público con los atributos obligatorios. En este caso el nombre.
    public Equipo(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
