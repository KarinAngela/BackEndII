package API.back2.controllers;


import API.back2.dto.DTOCadastroUsuario;
import API.back2.dto.DTODBUsuario;
import API.back2.enums.NivelAcesso;
import API.back2.models.Usuario;
import API.back2.repositories.ProdutoRepository;
import API.back2.repositories.UsuarioRepository;
import API.back2.services.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
public class TestUsuarioController {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @MockBean
    private UsuarioRepository repository;

    @MockBean
    private ProdutoRepository produtoRepository;

    @Autowired
    private TokenService tokenService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void givenAnonymousUser_whenGetUsuarios_thenUnauthorized() throws Exception {
        mvc.perform(get("/usuarios")).andExpect(status().isUnauthorized());
    }

    @Test
    public void givenUserWithAuthorityColaborador_whenGetUsuarios_thenForbidden() throws Exception {
        // Gerar token falso
        Usuario usuarioLogado = new Usuario(
                0L,
                "Usuario Colaborador",
                "colaborador@email.com",
                "colaborador",
                "senha",
                LocalDate.now(),
                NivelAcesso.COLABORADOR
        );
        given(repository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        mvc.perform(get("/usuarios").header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenUserWithAuthorityAdministrador_whenGetUsuarios_thenReturnJsonArray() throws Exception {
        // Gerar token falso
        Usuario usuarioLogado = new Usuario(
                0L,
                "Usuario Administrador",
                "administrador@email.com",
                "administrador",
                "senha",
                LocalDate.now(),
                NivelAcesso.ADMINISTRADOR
        );
        given(repository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        Usuario usuario = new Usuario(
                0L,
                "Usuario",
                "usuario@email.com",
                "usuario",
                "senha",
                LocalDate.now(),
                NivelAcesso.COLABORADOR
        );

        List<Usuario> listaUsuarios = asList(usuario);
        given(repository.findAll()).willReturn(listaUsuarios);

        mvc.perform(get("/usuarios").header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(usuario.getId().intValue())))
                .andExpect(jsonPath("$[0].nome", is(usuario.getNome())))
                .andExpect(jsonPath("$[0].email", is(usuario.getEmail())))
                .andExpect(jsonPath("$[0].login", is(usuario.getLogin())))
                .andExpect(jsonPath("$[0].senha", is(usuario.getSenha())))
                .andExpect(jsonPath("$[0].data_cadastro", is(usuario.getData_cadastro().toString())))
                .andExpect(jsonPath("$[0].nivel_acesso", is(usuario.getNivel_acesso().toString())));
    }

    @Test
    public void givenAnonymousUser_whenPostUsuarios_thenUnauthorized() throws Exception {
        mvc.perform(post("/usuarios")).andExpect(status().isUnauthorized());
    }

    @Test
    public void givenUserWithAuthorityColaborador_whenPostUsuarios_thenForbidden() throws Exception {
        // Gerar token falso
        Usuario usuarioLogado = new Usuario(
                0L,
                "Usuario Colaborador",
                "colaborador@email.com",
                "colaborador",
                "senha",
                LocalDate.now(),
                NivelAcesso.COLABORADOR
        );
        given(repository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        mvc.perform(post("/usuarios").header("Authorization", token)).andExpect(status().isForbidden());
    }

    @Test
    public void givenUserWithAuthorityAdministrador_whenPostUsuarios_thenReturnJson() throws Exception {
        // Gerar token falso
        Usuario usuarioLogado = new Usuario(
                0L,
                "Usuario Administrador",
                "administrador@email.com",
                "administrador",
                "senha",
                LocalDate.now(),
                NivelAcesso.ADMINISTRADOR
        );
        given(repository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        DTOCadastroUsuario dtoCadastroUsuario = new DTOCadastroUsuario(
                "Usuario",
                "usuario@email.com",
                "usuario",
                "senha",
                LocalDate.now(),
                NivelAcesso.COLABORADOR
        );

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
        String senhaEnc = encoder.encode(dtoCadastroUsuario.senha());
        Usuario usuario = new Usuario(
                0L,
                "Usuario",
                "usuario@email.com",
                "usuario",
                senhaEnc,
                LocalDate.now(),
                NivelAcesso.COLABORADOR
        );

        DTODBUsuario dtoDbUsuario = new DTODBUsuario(dtoCadastroUsuario, senhaEnc);
        given(repository.save(ArgumentMatchers.<Usuario>any())).willReturn(usuario);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String dadosJson = ow.writeValueAsString(dtoCadastroUsuario);

        mvc.perform(post("/usuarios").header("Authorization", token).contentType(MediaType.APPLICATION_JSON).content(dadosJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(usuario.getId().intValue())))
                .andExpect(jsonPath("$.nome", is(usuario.getNome())))
                .andExpect(jsonPath("$.email", is(usuario.getEmail())))
                .andExpect(jsonPath("$.login", is(usuario.getLogin())))
                .andExpect(jsonPath("$.senha", is(usuario.getSenha())))
                .andExpect(jsonPath("$.data_cadastro", is(usuario.getData_cadastro().toString())))
                .andExpect(jsonPath("$.nivel_acesso", is(usuario.getNivel_acesso().toString())));
    }

    @Test
    public void givenAnonymousUser_whenPutUsuarios_thenUnauthorized() throws Exception {
        mvc.perform(put("/usuarios/0")).andExpect(status().isUnauthorized());
    }

    @Test
    public void givenUserWithAuthorityColaborador_whenPutUsuarios_thenForbidden() throws Exception {
        // Gerar token falso
        Usuario usuarioLogado = new Usuario(
                0L,
                "Usuario Colaborador",
                "colaborador@email.com",
                "colaborador",
                "senha",
                LocalDate.now(),
                NivelAcesso.COLABORADOR
        );
        given(repository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        mvc.perform(put("/usuarios/0").header("Authorization", token)).andExpect(status().isForbidden());
    }

    @Test
    public void givenUserWithAuthorityAdministrador_whenPutUsuarios_thenReturnJson() throws Exception {
        // Gerar token falso
        Usuario usuarioLogado = new Usuario(
                0L,
                "Usuario Administrador",
                "administrador@email.com",
                "administrador",
                "senha",
                LocalDate.now(),
                NivelAcesso.ADMINISTRADOR
        );
        given(repository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        Usuario usuario = new Usuario(
                0L,
                "Usuario",
                "usuario@email.com",
                "usuario",
                "senha",
                LocalDate.now(),
                NivelAcesso.COLABORADOR
        );

        DTOCadastroUsuario dtoCadastroUsuario = new DTOCadastroUsuario(usuario);
        given(repository.getReferenceById(usuario.getId())).willReturn(usuario);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String dadosJson = ow.writeValueAsString(dtoCadastroUsuario);

        mvc.perform(put("/usuarios/0").header("Authorization", token).contentType(MediaType.APPLICATION_JSON).content(dadosJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(usuario.getId().intValue())))
                .andExpect(jsonPath("$.nome", is(usuario.getNome())))
                .andExpect(jsonPath("$.email", is(usuario.getEmail())))
                .andExpect(jsonPath("$.login", is(usuario.getLogin())))
                .andExpect(jsonPath("$.senha", is(usuario.getSenha())))
                .andExpect(jsonPath("$.data_cadastro", is(usuario.getData_cadastro().toString())))
                .andExpect(jsonPath("$.nivel_acesso", is(usuario.getNivel_acesso().toString())));
    }

    @Test
    public void givenAnonymousUser_whenDeleteUsuarios_thenUnauthorized() throws Exception {
        mvc.perform(delete("/usuarios/0")).andExpect(status().isUnauthorized());
    }

    @Test
    public void givenUserWithAuthorityColaborador_whenDeleteUsuarios_thenForbidden() throws Exception {
        // Gerar token falso
        Usuario usuarioLogado = new Usuario(
                0L,
                "Usuario Colaborador",
                "colaborador@email.com",
                "colaborador",
                "senha",
                LocalDate.now(),
                NivelAcesso.COLABORADOR
        );
        given(repository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        mvc.perform(delete("/usuarios/0").header("Authorization", token)).andExpect(status().isForbidden());
    }

    @Test
    public void givenUserWithAuthorityAdministrador_whenDeleteUsuarios_thenReturnNothing() throws Exception {
        // Gerar token falso
        Usuario usuarioLogado = new Usuario(
                0L,
                "Usuario Administrador",
                "administrador@email.com",
                "administrador",
                "senha",
                LocalDate.now(),
                NivelAcesso.ADMINISTRADOR
        );
        given(repository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        doNothing().when(repository).deleteById(0L);

        mvc.perform(delete("/usuarios/0").header("Authorization", token))
                .andExpect(status().isNoContent());
    }

}