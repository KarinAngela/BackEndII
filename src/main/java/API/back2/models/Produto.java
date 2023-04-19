package API.back2.models;

import API.back2.enums.Categoria;
import API.back2.dto.DTOCadastroProduto;
import API.back2.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;

@Table(name = "produtos")
@Entity(name =  "Produto")

@Getter //Gerando os metodos getters

@NoArgsConstructor //Gerando os constructor nas entidades

@AllArgsConstructor //Recebendo todos os campo

@EqualsAndHashCode //gerar automaticamente os métodos equals() e hashCode() para uma class,equals() compara se dois objetos são iguais e hashCode() é usado para calcular um valor hash do objeto

public class Produto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String preco_unitario;
    @Enumerated(EnumType.STRING)
    private Categoria categoria;
    private String descricao;
    @Enumerated(EnumType.STRING)
    private Status status;
    
    public Produto(DTOCadastroProduto dados) {
        this.nome = dados.nome();
        this.preco_unitario = dados.precoUnitario();
        this.categoria = dados.categoria();
        this.descricao = dados.descricao();
        this.status = dados.status();
    }

    @PutMapping
    public void atualizandoInformacoes(DTOCadastroProduto dados) {

        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.precoUnitario() != null){
            this.preco_unitario = dados.precoUnitario();
        }
        if (dados.categoria() != null){
            this.categoria = dados.categoria();
        }
        if (dados.descricao() != null){
            this.descricao = dados.descricao();
        }
        if (dados.status() != null){
            this.status = dados.status();
        }
    }
}
