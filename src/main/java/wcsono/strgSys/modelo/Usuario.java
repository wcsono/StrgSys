package wcsono.strgSys.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @Column(nullable = false, unique = true, length = 50)
    private String user;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String email;

    // Control de estado: true = activo, false = no se puede usar/eliminar
    private boolean estUsuario = true;
}
