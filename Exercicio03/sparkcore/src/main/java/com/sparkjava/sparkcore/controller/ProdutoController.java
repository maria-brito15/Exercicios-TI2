package com.sparkjava.sparkcore.controller;

import com.google.gson.Gson;
import com.sparkjava.sparkcore.model.Produto;
import com.sparkjava.sparkcore.service.ProdutoService;

import static spark.Spark.*;

public class ProdutoController {

    private static final ProdutoService produtoService = new ProdutoService();
    private static final Gson gson = new Gson();

    public static void rotas() {
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type");
        });

        options("/*", (req, res) -> "");

        get("/produtos", (req, res) -> {
            res.type("application/json");
            return gson.toJson(produtoService.getAllProdutos());
        });

        get("/produtos/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            Produto produto = produtoService.getProdutoById(id);
            
            return (produto != null) ? (gson.toJson(produto)) : "{}";
        });

        post("/produtos", (req, res) -> {
            Produto produto = gson.fromJson(req.body(), Produto.class);
            produtoService.addProduto(produto);
            res.status(201);
            
            return "Criado com sucesso";
        });

        put("/produtos/:id", (req, res) -> {
            Produto produto = gson.fromJson(req.body(), Produto.class);
            produto.setId(Integer.parseInt(req.params("id")));
            produtoService.updateProduto(produto);
            
            return "Atualizado com sucesso";
        });

        delete("/produtos/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            produtoService.deleteProduto(id);
            
            return "Deletado com sucesso";
        });
    }
}
