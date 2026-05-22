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

    @Column(length = 20)
    private String telCli;


    @Column(nullable = false)
    private Integer estCliente = 1; // 1=activo, 0=inactivo

}
