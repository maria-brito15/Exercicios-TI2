package com.sparkjava.sparkcore.service;

import com.sparkjava.sparkcore.dao.ProdutoDAO;
import com.sparkjava.sparkcore.model.Produto;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ProdutoService {
    
    private static final Logger LOGGER = Logger.getLogger(ProdutoService.class.getName());
    private final ProdutoDAO produtoDAO;
    
    public ProdutoService() {
        this.produtoDAO = new ProdutoDAO();
    }
    
    public ProdutoService(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }
    
    public void addProduto(Produto produto) {
        LOGGER.info("Tentando adicionar produto: " + produto.getNome());
        
        try {
            validateProduto(produto);
            
            if (isProdutoNomeExists(produto.getNome())) {
                throw new IllegalArgumentException("Já existe um produto com o nome: " + produto.getNome());
            }
            
            produtoDAO.create(produto);
            LOGGER.info("Produto adicionado com sucesso: " + produto.getNome());
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao adicionar produto: " + produto.getNome(), e);
            throw new RuntimeException("Erro ao adicionar produto: " + e.getMessage(), e);
        }
    }
    
    public List<Produto> getAllProdutos() {
        LOGGER.info("Buscando todos os produtos");
        
        try {
            List<Produto> produtos = produtoDAO.findAll();
            LOGGER.info("Encontrados " + produtos.size() + " produtos");
            return produtos;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todos os produtos", e);
            throw new RuntimeException("Erro ao buscar produtos: " + e.getMessage(), e);
        }
    }
    
    public Produto getProdutoById(int id) {
        LOGGER.info("Buscando produto com ID: " + id);
        
        if (id <= 0) {
            throw new IllegalArgumentException("ID deve ser um número positivo");
        }
        
        try {
            Produto produto = produtoDAO.findById(id);
            
            if (produto == null) {
                LOGGER.warning("Produto não encontrado com ID: " + id);
                throw new IllegalArgumentException("Produto não encontrado com ID: " + id);
            }
            
            LOGGER.info("Produto encontrado: " + produto.getNome());
            return produto;
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar produto com ID: " + id, e);
            throw new RuntimeException("Erro ao buscar produto: " + e.getMessage(), e);
        }
    }
    
    public void updateProduto(Produto produto) {
        LOGGER.info("Tentando atualizar produto ID: " + produto.getId());
        
        try {
            validateProduto(produto);
            validateProdutoId(produto.getId());
            
            Produto existingProduto = produtoDAO.findById(produto.getId());
            if (existingProduto == null) {
                throw new IllegalArgumentException("Produto não encontrado para atualização com ID: " + produto.getId());
            }
            
            if (!existingProduto.getNome().equals(produto.getNome()) && 
                isProdutoNomeExists(produto.getNome())) {
                throw new IllegalArgumentException("Já existe outro produto com o nome: " + produto.getNome());
            }
            
            produtoDAO.update(produto);
            LOGGER.info("Produto atualizado com sucesso: " + produto.getNome());
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar produto ID: " + produto.getId(), e);
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }
    
    public void deleteProduto(int id) {
        LOGGER.info("Tentando deletar produto ID: " + id);
        
        try {
            validateProdutoId(id);
            
            Produto produto = produtoDAO.findById(id);
            if (produto == null) {
                throw new IllegalArgumentException("Produto não encontrado para exclusão com ID: " + id);
            }
            
            if (produto.getQuantidade() > 0) {
                throw new IllegalStateException("Não é possível deletar produto com estoque. Produto: " + produto.getNome() + ", Quantidade: " + produto.getQuantidade());
            }
            
            produtoDAO.delete(id);
            LOGGER.info("Produto deletado com sucesso: " + produto.getNome());
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar produto ID: " + id, e);
            throw new RuntimeException("Erro ao deletar produto: " + e.getMessage(), e);
        }
    }
    
    public List<Produto> getProdutosByNome(String nome) {
        LOGGER.info("Buscando produtos por nome: " + nome);
        
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        
        try {
            return produtoDAO.findAll().stream()
                    .filter(p -> p.getNome().toLowerCase().contains(nome.toLowerCase().trim()))
                    .toList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar produtos por nome: " + nome, e);
            throw new RuntimeException("Erro ao buscar produtos por nome: " + e.getMessage(), e);
        }
    }
    
    public void updateEstoque(int produtoId, int novaQuantidade) {
        LOGGER.info("Atualizando estoque do produto ID: " + produtoId);
        
        if (novaQuantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }
        
        try {
            Produto produto = getProdutoById(produtoId);
            produto.setQuantidade(novaQuantidade);
            produtoDAO.update(produto);
            
            LOGGER.info("Estoque atualizado para produto: " + produto.getNome() + ", nova quantidade: " + novaQuantidade);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar estoque do produto ID: " + produtoId, e);
            throw new RuntimeException("Erro ao atualizar estoque: " + e.getMessage(), e);
        }
    }
    
    private boolean isProdutoNomeExists(String nome) {
        try {
            return produtoDAO.findAll().stream()
                    .anyMatch(p -> p.getNome().equalsIgnoreCase(nome.trim()));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao verificar existência do nome: " + nome, e);
            return false;
        }
    }
    
    private void validateProduto(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }
        
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        
        if (produto.getNome().trim().length() > 255) {
            throw new IllegalArgumentException("Nome do produto não pode ter mais de 255 caracteres");
        }
        
        if (produto.getDescricao() != null && produto.getDescricao().length() > 1000) {
            throw new IllegalArgumentException("Descrição não pode ter mais de 1000 caracteres");
        }
        
        if (produto.getPreco() < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
        
        if (produto.getQuantidade() < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }
        
        produto.setNome(produto.getNome().trim());
        if (produto.getDescricao() != null) {
            produto.setDescricao(produto.getDescricao().trim());
        }
    }
    
    private void validateProdutoId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do produto deve ser um número positivo");
        }
    }
}
