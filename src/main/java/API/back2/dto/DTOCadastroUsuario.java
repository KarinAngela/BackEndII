package API.back2.dto;

import API.back2.enums.NivelAcesso;
import API.back2.models.Usuario;

import java.time.LocalDate;

public record DTOCadastroUsuario(

        String nome,
        String email,
        String senha,
        String login,
        LocalDate dataCadastro,
        NivelAcesso nivelAcesso){

    public DTOCadastroUsuario(Usuario usuario) {
        this(
             usuario.getNome(),
             usuario.getEmail(),
             usuario.getLogin(),
             usuario.getSenha(),
             usuario.getData_cadastro(),
             usuario.getNivel_acesso() );
    }
}
