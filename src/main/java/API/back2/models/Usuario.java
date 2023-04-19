package API.back2.models;

import API.back2.dto.DTOCadastroUsuario;
import API.back2.enums.NivelAcesso;
import API.back2.dto.DTODBUsuario;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


@Table(name = "usuarios")
@Entity(name = "Usuario")
@Getter
@NoArgsConstructor //usado para instanciar a classe sem a necessidade de passar argumentos para o construtor.
@AllArgsConstructor //Esse construtor inclui todos os campos da classe como argumentos e usado para instanciar a classe com valores para todos os campos.
@EqualsAndHashCode
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    private String email;

    private String login;

    private String senha;

    private LocalDate data_cadastro;

    @Enumerated(EnumType.STRING)
    private NivelAcesso nivel_acesso;


    public Usuario(DTODBUsuario dados) {
        this.nome = dados.nome();
        this.email = dados.email();
        this.login = dados.login();
        this.senha = dados.senha();
        this.data_cadastro = dados.dataCadastro();
        this.nivel_acesso = dados.nivelAcesso();
    }

    @PutMapping
    public void atualizarInf(DTOCadastroUsuario dados){
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.email() != null) {
            this.email = dados.email();
        }
        if (dados.login() != null) {
            this.login = dados.login();
        }
        if (dados.senha() != null){
            this.senha = dados.senha();
        }
        if (dados.dataCadastro() != null){
            this.data_cadastro = dados.dataCadastro();
        }
        if (dados.nivelAcesso() != null) {
            this.nivel_acesso = dados.nivelAcesso();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(nivel_acesso.toString()));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
