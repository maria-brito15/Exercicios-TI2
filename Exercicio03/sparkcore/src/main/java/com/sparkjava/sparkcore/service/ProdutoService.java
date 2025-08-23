package com.sparkjava.sparkcore.service;

import com.sparkjava.sparkcore.model.Produto;
import com.sparkjava.sparkcore.dao.ProdutoDAO;

import java.util.List;

public class ProdutoService {

    private ProdutoDAO produtoDAO = new ProdutoDAO();

    public void addProduto(Produto produto) {
        produtoDAO.create(produto);
    }

    public List<Produto> getAllProdutos() {
        return produtoDAO.findAll();
    }

    public Produto getProdutoById(int id) {
        return produtoDAO.findById(id);
    }

    public void updateProduto(Produto produto) {
        produtoDAO.update(produto);
    }

    public void deleteProduto(int id) {
        produtoDAO.delete(id);
    }
}
