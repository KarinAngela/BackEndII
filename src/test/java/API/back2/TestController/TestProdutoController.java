package API.back2.TestController;

import API.back2.dto.DTOCadastroProduto;
import API.back2.enums.Categoria;
import API.back2.enums.NivelAcesso;
import API.back2.enums.Status;
import API.back2.models.Produto;

import API.back2.models.Usuario;
import API.back2.repositories.ProdutoRepository;
import API.back2.repositories.UsuarioRepository;
import API.back2.services.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class) //anotação usada para informar o JUnit que a execução dos testes será feita pelo Spring Runner
//@WebMvcTest(ProdutoController.class) //anotação usada para configurar o teste de unidade para um controlador específico
// Para usar o carregamento customizado do MVC (função setup), não se pode usar
// o WebMvcTest, mas sim o SpringBootTest
// O que tá dentro do parenteses eu não faço ideia do que seja :)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestProdutoController {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc; //Simular testes sem acessar o banco original

    @MockBean
    private ProdutoRepository repository;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    // Essa função é executada antes dos testes e serve
    // para forçar o carregamento da API como um todo
    // (checa as migrations, conecta com o banco, carrega o
    // Spring Security, etc)
    // Deixa os testes bem mais lentos de executar, porém
    // resolve o problema do TokenService :)
    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void givenAnonymousUser_whenGetProduto_thenUnauthorized() throws Exception {
        mvc.perform(get("/produtos")).andExpect(status().isUnauthorized());
    }

    @Test
    public void givenUserWithAuthorityColaborador_whenGetProduto_thenReturnJsonArray() throws Exception {
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
        given(usuarioRepository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        Produto produto = new Produto(
                0L,
                "uniport3030",
                "1200",
                Categoria.COMBUSTIVEL,
                "para pequenos agro",
                Status.DISPONIVEL
        );

        List<Produto> listaProduto = asList(produto);
        given(repository.findAll()).willReturn(listaProduto);

        mvc.perform(get("/produtos").header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(produto.getId().intValue())))
                .andExpect(jsonPath("$[0].nome", is(produto.getNome())))
                .andExpect(jsonPath("$[0].preco_unitario", is(produto.getPreco_unitario())))
                .andExpect(jsonPath("$[0].categoria", is(produto.getCategoria().toString())))
                .andExpect(jsonPath("$[0].descricao", is(produto.getDescricao())))
                .andExpect(jsonPath("$[0].status", is(produto.getStatus().toString())));
    }

    @Test
    public void givenUserWithAuthorityAdministrador_whenGetProduto_thenReturnJsonArray() throws Exception {
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
        given(usuarioRepository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        Produto produto = new Produto(
                0L,
                "uniport3030",
                "1200",
                Categoria.COMBUSTIVEL,
                "para pequenos agro",
                Status.DISPONIVEL
        );

        List<Produto> listaProduto = asList(produto);
        given(repository.findAll()).willReturn(listaProduto);

        mvc.perform(get("/produtos").header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(produto.getId().intValue())))
                .andExpect(jsonPath("$[0].nome", is(produto.getNome())))
                .andExpect(jsonPath("$[0].preco_unitario", is(produto.getPreco_unitario())))
                .andExpect(jsonPath("$[0].categoria", is(produto.getCategoria().toString())))
                .andExpect(jsonPath("$[0].descricao", is(produto.getDescricao())))
                .andExpect(jsonPath("$[0].status", is(produto.getStatus().toString())));
    }

    @Test
    public void givenAnonymousUser_whenPostProdutos_thenUnauthorized() throws Exception {
        mvc.perform(post("/produtos")).andExpect(status().isUnauthorized());
    }

    @Test
    public void givenUserWithAuthorityColaborador_whenPostProdutos_thenReturnJson() throws Exception {
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
        given(usuarioRepository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        Produto produto = new Produto(
                0L,
                "costal",
                "12330",
                Categoria.COMBUSTIVEL,
                "usado nas costa",
                Status.DISPONIVEL
        );

        // Reparei que o metodo given só aceita o método exato como é executado
        // no Controller, então quando for fazer mais testes, coloque exatamente
        // como está no Controller associado ao teste
        DTOCadastroProduto dtoCadastroProduto = new DTOCadastroProduto(produto);
        given(repository.save(new Produto(dtoCadastroProduto))).willReturn(produto);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String dadosJson = ow.writeValueAsString(dtoCadastroProduto);

        mvc.perform(post("/produtos").header("Authorization", token).contentType(MediaType.APPLICATION_JSON).content(dadosJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(produto.getId().intValue())))
                .andExpect(jsonPath("$.nome", is(produto.getNome())))
                .andExpect(jsonPath("$.preco_unitario", is(produto.getPreco_unitario())))
                .andExpect(jsonPath("$.categoria", is(produto.getCategoria().toString())))
                .andExpect(jsonPath("$.descricao", is(produto.getDescricao())))
                .andExpect(jsonPath("$.status", is(produto.getStatus().toString())));
    }

    @Test
    public void givenUserWithAuthorityAdministrador_whenPostProdutos_thenReturnJson() throws Exception {
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
        given(usuarioRepository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        Produto produto = new Produto(
                0L,
                "costal",
                "12330",
                Categoria.COMBUSTIVEL,
                "usado nas costa",
                Status.DISPONIVEL
        );

        DTOCadastroProduto dtoCadastroProduto = new DTOCadastroProduto(produto);
        given(repository.save(new Produto(dtoCadastroProduto))).willReturn(produto);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String dadosJson = ow.writeValueAsString(dtoCadastroProduto);

        mvc.perform(post("/produtos").header("Authorization", token).contentType(MediaType.APPLICATION_JSON).content(dadosJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(produto.getId().intValue())))
                .andExpect(jsonPath("$.nome", is(produto.getNome())))
                .andExpect(jsonPath("$.preco_unitario", is(produto.getPreco_unitario())))
                .andExpect(jsonPath("$.categoria", is(produto.getCategoria().toString())))
                .andExpect(jsonPath("$.descricao", is(produto.getDescricao())))
                .andExpect(jsonPath("$.status", is(produto.getStatus().toString())));
    }

    @Test
    public void givenAnonymousUser_whenPutProduto_thenUnauthorized() throws Exception {
        mvc.perform(put("/produtos/1")).andExpect(status().isUnauthorized());
    }

    @Test
    public void givenUserWithAuthorityColaborador_whenPutProduto_thenReturnJson() throws Exception {
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
        given(usuarioRepository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        Produto produto = new Produto(
                0L,
                "NPK",
                "12345",
                Categoria.COMBUSTIVEL,
                "usado para agro",
                Status.DISPONIVEL
        );

        given(this.repository.getReferenceById(produto.getId())).willReturn(produto);
        DTOCadastroProduto dtoCadastroProduto = new DTOCadastroProduto(produto);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String dadosJson = ow.writeValueAsString(dtoCadastroProduto);

        mvc.perform(put("/produtos/0").header("Authorization", token).contentType(MediaType.APPLICATION_JSON).content(dadosJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(produto.getId().intValue())))
                .andExpect(jsonPath("$.nome", is(produto.getNome())))
                .andExpect(jsonPath("$.preco_unitario", is (produto.getPreco_unitario())))
                .andExpect(jsonPath("$.categoria", is (produto.getCategoria().toString())))
                .andExpect(jsonPath("$.descricao", is (produto.getDescricao())))
                .andExpect(jsonPath("$.status", is (produto.getStatus().toString())));
}

    @Test
    public void givenUserWithAuthorityAdministrador_whenPutProduto_thenReturnJson() throws Exception {
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
        given(usuarioRepository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        Produto produto = new Produto(
                0L,
                "NPK",
                "12345",
                Categoria.COMBUSTIVEL,
                "usado para agro",
                Status.DISPONIVEL
        );

        given(this.repository.getReferenceById(produto.getId())).willReturn(produto);
        DTOCadastroProduto dtoCadastroProduto = new DTOCadastroProduto(produto);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String dadosJson = ow.writeValueAsString(dtoCadastroProduto);

        mvc.perform(put("/produtos/0").header("Authorization", token).contentType(MediaType.APPLICATION_JSON).content(dadosJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(produto.getId().intValue())))
                .andExpect(jsonPath("$.nome", is(produto.getNome())))
                .andExpect(jsonPath("$.preco_unitario", is (produto.getPreco_unitario())))
                .andExpect(jsonPath("$.categoria", is (produto.getCategoria().toString())))
                .andExpect(jsonPath("$.descricao", is (produto.getDescricao())))
                .andExpect(jsonPath("$.status", is (produto.getStatus().toString())));
    }

    @Test
    public void givenAnonymousUser_whenDeleteProduto_thenUnauthorized() throws Exception {
        mvc.perform(delete("/produtos/0")).andExpect(status().isUnauthorized());
    }

    @Test
    public void givenUsuarioWithAuthorityColaborador_whenDeleteProduto_thenReturnNothing() throws Exception {
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
        given(usuarioRepository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        doNothing().when(repository).deleteById(0L);

        mvc.perform(delete("/produtos/0").header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenUsuarioWithAuthorityAdministrador_whenDeleteProduto_thenReturnNothing() throws Exception {
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
        given(usuarioRepository.findByLogin(usuarioLogado.getUsername())).willReturn(usuarioLogado);
        String token = tokenService.gerarToken(usuarioLogado);

        doNothing().when(repository).deleteById(0L);

        mvc.perform(delete("/produtos/0").header("Authorization", token))
                .andExpect(status().isNoContent());
    }
}
