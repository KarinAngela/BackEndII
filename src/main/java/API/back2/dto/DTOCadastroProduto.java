package API.back2.dto;


import API.back2.enums.Categoria;
import API.back2.enums.Status;
import API.back2.models.Produto;

public record DTOCadastroProduto(

        Long id,

        String nome,

        String precoUnitario,

        Categoria categoria,

        String descricao,

        Status status)
{
        public DTOCadastroProduto(Produto produto){
            this(produto.getId(),
                    produto.getNome(),
                    produto.getPreco_unitario(),
                    produto.getCategoria(),
                    produto.getDescricao(),
                    produto.getStatus()
            );
    }
}
