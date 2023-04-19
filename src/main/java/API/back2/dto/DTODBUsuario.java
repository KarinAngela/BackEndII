package API.back2.dto;

import API.back2.enums.NivelAcesso;

import java.time.LocalDate;

public record DTODBUsuario(

        String nome,
        String email,
        String senha,
        String login,
        LocalDate dataCadastro,
        NivelAcesso nivelAcesso) {

    public DTODBUsuario(DTOCadastroUsuario dados, String senhaEncriptada) {
        this(dados.nome(),
                dados.email(),
                senhaEncriptada,
                dados.login(),
                dados.dataCadastro(),
                dados.nivelAcesso());
    }

}
