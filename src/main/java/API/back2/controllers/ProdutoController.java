package API.back2.controllers;

import API.back2.repositories.ProdutoRepository;
import API.back2.dto.DTOCadastroProduto;
import API.back2.models.Produto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoRepository repository;

    @GetMapping
    @Transactional //realiza transações entre bancos
    public List<Produto> listar() {
        return this.repository.findAll();
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Produto> cadastrar(@RequestBody @Valid DTOCadastroProduto dados){
        Produto produto = this.repository.save(new Produto(dados));

        return new ResponseEntity<Produto> (
                produto,
                HttpStatus.CREATED
        );
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(
            @PathVariable("id") Long id,
            @RequestBody @Valid DTOCadastroProduto dados
    ) {
        var produto = this.repository.getReferenceById(id);
        produto.atualizandoInformacoes(dados);

        return new ResponseEntity<Produto>(
                produto,
                HttpStatus.OK
        );
    }

    //Criando o delete e utilizando o id para usar para deletar
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deletar (@PathVariable Long id){
        repository.deleteById(id);

        return new ResponseEntity<Void> (
                HttpStatus.NO_CONTENT
        );
    }
}
