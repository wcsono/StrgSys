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

    @Column(nullable = false)
    private Integer nivelAcceso;
// 1 = Administrador, 2 = Operador, 3 = Almacén

    @Column(nullable = false)
    private Integer estUsuario = 1;
    // 1=activo, 0=inactivo

}
