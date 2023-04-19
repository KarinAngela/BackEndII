package API.back2.controllers;

import API.back2.dto.DTOCadastroUsuario;
import API.back2.dto.DTODBUsuario;
import API.back2.models.Usuario;
import API.back2.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;


    @GetMapping
    @Transactional
    public List<Usuario> listar(){
        return this.repository.findAll();
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Usuario> cadastrar (@RequestBody DTOCadastroUsuario dados){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
        String senhaEnc = encoder.encode(dados.senha());
        DTODBUsuario dadosDB = new DTODBUsuario(dados, senhaEnc);

        Usuario usuario = repository.save(new Usuario(dadosDB));
        return new ResponseEntity<Usuario>(
                usuario,
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Usuario> atualizar(
            @PathVariable("id") Long id,
            @RequestBody @Valid DTOCadastroUsuario dados
    ) {
        var usuario = this.repository.getReferenceById(id);
        usuario.atualizarInf(dados);

        return new ResponseEntity<Usuario>(
                usuario,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}") //Delete metodo id para ser  usado para excluir
    @Transactional
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        repository.deleteById(id);

        return new ResponseEntity<Void> (
                HttpStatus.NO_CONTENT
        );
    }
}
