package wcsono.strgSys.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCliente;

    @Column(nullable = false, unique = true, length = 20)
    private String codCli;

    @Column(nullable = false, length = 100)
    private String nomCli;

    @Column(length = 150)
    private String dirCli;

    @Column(length = 100)
    private String emailCli;

    // Control de estado: true = activo, false = no se puede eliminar si está en uso
    private boolean estCliente = true;
}
