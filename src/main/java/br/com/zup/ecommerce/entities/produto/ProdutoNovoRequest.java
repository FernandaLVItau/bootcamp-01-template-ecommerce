package br.com.zup.ecommerce.entities.produto;

import br.com.zup.ecommerce.entities.categoria.Categoria;
import br.com.zup.ecommerce.entities.produto.caracteristica.CaracteristicasProduto;
import br.com.zup.ecommerce.entities.produto.caracteristica.CaracteristicasProdutoNovoRequest;
import br.com.zup.ecommerce.entities.usuario.Usuario;
import br.com.zup.ecommerce.validations.existeId.ExisteId;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Contagem de carga intrínseca da classe: 9
 */

public class ProdutoNovoRequest {

    @NotBlank
    private String nome;

    @NotNull
    @Positive
    private BigDecimal valor;

    @NotNull
    @Min(0)
    private int qtdDisponivel;

    @Size(min = 3, message = "deve ter tamanho igual ou maior que 3")
    //1
    private List<CaracteristicasProdutoNovoRequest> caracteristicas;

    @NotBlank
    @Size(max=1000)
    private String descricao;

    @NotNull
    //2
    @ExisteId(dominioClasse = Categoria.class, nomeCampo = "id")
    private Long categoriaId;

    public String getNome() {
        return nome;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public int getQtdDisponivel() {
        return qtdDisponivel;
    }

    public List<CaracteristicasProdutoNovoRequest> getCaracteristicas() {
        return caracteristicas;
    }

    public String getDescricao() {
        return descricao;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    //2
    public Produto toModel(EntityManager manager, Usuario dono){

        Categoria categoria = manager.find(Categoria.class, this.categoriaId);
        Assert.notNull(categoria, "Categoria não encontrada");

        Produto produto = new Produto(this.nome, this.valor, this.qtdDisponivel,
                this.descricao, categoria, dono);

        //2
        Set<CaracteristicasProduto> caracteristicasProdutos = this.caracteristicas
                .stream()
                .map(cp -> cp.toModel(produto))
                .collect(Collectors.toSet());

        produto.getCaracteristicasProduto().addAll(caracteristicasProdutos);

        return produto;
    }

    public boolean caracteristicaRepetida() {
        Set<String> listaNome = new HashSet<>();

        //1
        for (CaracteristicasProdutoNovoRequest caracteristica : this.caracteristicas) {
            //1
            if (!listaNome.add(caracteristica.getNome())) {
                return true;
            }
        }

        return false;
    }
}
