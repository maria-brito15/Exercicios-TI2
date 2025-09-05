package com.sparkjava.sparkcore.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sparkjava.sparkcore.model.Produto;
import com.sparkjava.sparkcore.service.ProdutoService;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

import static spark.Spark.*;

public class ProdutoController {
    
    private static final Logger LOGGER = Logger.getLogger(ProdutoController.class.getName());
    private static final ProdutoService produtoService = new ProdutoService();
    private static final Gson gson = new Gson();
    
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String ENCODING_UTF8 = "UTF-8";
    
    public static void rotas() {
        configurarCORS();
        configurarMiddleware();
        definirRotasProdutos();
        configurarTratamentoErros();
    }
    
    private static void configurarCORS() {
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type,Authorization");
            res.header("Content-Type", CONTENT_TYPE_JSON + ";charset=" + ENCODING_UTF8);
        });
        
        options("/*", (req, res) -> {
            res.status(200);
            return "";
        });
    }
    
    private static void configurarMiddleware() {
        before("/produtos/*", (req, res) -> {
            LOGGER.info("Requisição recebida: " + req.requestMethod() + " " + req.pathInfo());
            
            String clientIP = req.ip();
            LOGGER.info("Cliente IP: " + clientIP);
        });
        
        after("/produtos/*", (req, res) -> {
            LOGGER.info("Resposta enviada: " + res.status() + " para " + req.pathInfo());
        });
    }
    
    private static void definirRotasProdutos() {
        get("/produtos", ProdutoController::listarProdutos);
        
        get("/produtos/search", ProdutoController::buscarProdutosPorNome);
        
        get("/produtos/:id", ProdutoController::buscarProdutoPorId);
        
        post("/produtos", ProdutoController::criarProduto);
        
        put("/produtos/:id", ProdutoController::atualizarProduto);
        
        patch("/produtos/:id/estoque", ProdutoController::atualizarEstoque);
        
        delete("/produtos/:id", ProdutoController::deletarProduto);
    }
    
    private static String listarProdutos(Request req, Response res) {
        try {
            List<Produto> produtos = produtoService.getAllProdutos();
            res.status(200);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", produtos);
            response.put("total", produtos.size());
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            return tratarErro(res, e, "Erro ao listar produtos");
        }
    }
    
    private static String buscarProdutosPorNome(Request req, Response res) {
        try {
            String nome = req.queryParams("nome");
            
            if (nome == null || nome.trim().isEmpty()) {
                return criarErroValidacao(res, "Parâmetro 'nome' é obrigatório");
            }
            
            List<Produto> produtos = produtoService.getProdutosByNome(nome);
            res.status(200);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", produtos);
            response.put("total", produtos.size());
            response.put("searchTerm", nome);
            
            return gson.toJson(response);
            
        } catch (IllegalArgumentException e) {
            return criarErroValidacao(res, e.getMessage());
        } catch (Exception e) {
            return tratarErro(res, e, "Erro ao buscar produtos por nome");
        }
    }
    
    private static String buscarProdutoPorId(Request req, Response res) {
        try {
            int id = validarEConverterParametroId(req.params("id"));
            
            Produto produto = produtoService.getProdutoById(id);
            res.status(200);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", produto);
            
            return gson.toJson(response);
            
        } catch (NumberFormatException e) {
            return criarErroValidacao(res, "ID deve ser um número válido");
        } catch (IllegalArgumentException e) {
            return criarErroNaoEncontrado(res, e.getMessage());
        } catch (Exception e) {
            return tratarErro(res, e, "Erro ao buscar produto");
        }
    }
    
    private static String criarProduto(Request req, Response res) {
        try {
            Produto produto = validarEConverterCorpoRequisicao(req.body());
            
            produtoService.addProduto(produto);
            res.status(201);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Produto criado com sucesso");
            response.put("data", produto);
            
            return gson.toJson(response);
            
        } catch (JsonSyntaxException e) {
            return criarErroValidacao(res, "JSON inválido na requisição");
        } catch (IllegalArgumentException e) {
            return criarErroValidacao(res, e.getMessage());
        } catch (Exception e) {
            return tratarErro(res, e, "Erro ao criar produto");
        }
    }
    
    private static String atualizarProduto(Request req, Response res) {
        try {
            int id = validarEConverterParametroId(req.params("id"));
            Produto produto = validarEConverterCorpoRequisicao(req.body());
            produto.setId(id);
            
            produtoService.updateProduto(produto);
            res.status(200);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Produto atualizado com sucesso");
            response.put("data", produto);
            
            return gson.toJson(response);
            
        } catch (NumberFormatException e) {
            return criarErroValidacao(res, "ID deve ser um número válido");
        } catch (JsonSyntaxException e) {
            return criarErroValidacao(res, "JSON inválido na requisição");
        } catch (IllegalArgumentException e) {
            return criarErroValidacao(res, e.getMessage());
        } catch (Exception e) {
            return tratarErro(res, e, "Erro ao atualizar produto");
        }
    }
    
    private static String atualizarEstoque(Request req, Response res) {
        try {
            int id = validarEConverterParametroId(req.params("id"));
            
            Map<String, Object> requestData = gson.fromJson(req.body(), Map.class);
            
            if (!requestData.containsKey("quantidade")) {
                return criarErroValidacao(res, "Campo 'quantidade' é obrigatório");
            }
            
            int novaQuantidade = ((Double) requestData.get("quantidade")).intValue();
            
            produtoService.updateEstoque(id, novaQuantidade);
            res.status(200);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estoque atualizado com sucesso");
            response.put("novaQuantidade", novaQuantidade);
            
            return gson.toJson(response);
            
        } catch (NumberFormatException e) {
            return criarErroValidacao(res, "ID deve ser um número válido");
        } catch (JsonSyntaxException e) {
            return criarErroValidacao(res, "JSON inválido na requisição");
        } catch (IllegalArgumentException e) {
            return criarErroValidacao(res, e.getMessage());
        } catch (Exception e) {
            return tratarErro(res, e, "Erro ao atualizar estoque");
        }
    }
    
    private static String deletarProduto(Request req, Response res) {
        try {
            int id = validarEConverterParametroId(req.params("id"));
            
            produtoService.deleteProduto(id);
            res.status(200);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Produto deletado com sucesso");
            
            return gson.toJson(response);
            
        } catch (NumberFormatException e) {
            return criarErroValidacao(res, "ID deve ser um número válido");
        } catch (IllegalArgumentException e) {
            return criarErroNaoEncontrado(res, e.getMessage());
        } catch (IllegalStateException e) {
            return criarErroConflito(res, e.getMessage());
        } catch (Exception e) {
            return tratarErro(res, e, "Erro ao deletar produto");
        }
    }
    
    private static void configurarTratamentoErros() {
        notFound((req, res) -> {
            res.type(CONTENT_TYPE_JSON);
            res.status(404);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Endpoint não encontrado");
            error.put("path", req.pathInfo());
            error.put("method", req.requestMethod());
            
            return gson.toJson(error);
        });
        
        internalServerError((req, res) -> {
            res.type(CONTENT_TYPE_JSON);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Erro interno do servidor");
            error.put("message", "Por favor, tente novamente mais tarde");
            
            return gson.toJson(error);
        });
    }
    
    // ===== MÉTODOS UTILITÁRIOS =====
    
    private static int validarEConverterParametroId(String idParam) {
        if (idParam == null || idParam.trim().isEmpty()) {
            throw new IllegalArgumentException("ID é obrigatório");
        }
        
        try {
            int id = Integer.parseInt(idParam.trim());
            if (id <= 0) {
                throw new IllegalArgumentException("ID deve ser um número positivo");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("ID deve ser um número válido");
        }
    }
    
    private static Produto validarEConverterCorpoRequisicao(String body) {
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Corpo da requisição não pode estar vazio");
        }
        
        try {
            Produto produto = gson.fromJson(body, Produto.class);
            if (produto == null) {
                throw new IllegalArgumentException("Dados do produto inválidos");
            }
            return produto;
        } catch (JsonSyntaxException e) {
            throw new JsonSyntaxException("JSON inválido na requisição");
        }
    }
    
    private static String criarErroValidacao(Response res, String mensagem) {
        res.status(400);
        
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Erro de validação");
        error.put("message", mensagem);
        error.put("timestamp", System.currentTimeMillis());
        
        return gson.toJson(error);
    }
    

    private static String criarErroNaoEncontrado(Response res, String mensagem) {
        res.status(404);
        
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Recurso não encontrado");
        error.put("message", mensagem);
        error.put("timestamp", System.currentTimeMillis());
        
        return gson.toJson(error);
    }

    
    private static String criarErroConflito(Response res, String mensagem) {
        res.status(409);
        
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Conflito");
        error.put("message", mensagem);
        error.put("timestamp", System.currentTimeMillis());
        
        return gson.toJson(error);
    }
    
    private static String tratarErro(Response res, Exception e, String contexto) {
        LOGGER.log(Level.SEVERE, contexto, e);
        res.status(500);
        
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Erro interno do servidor");
        error.put("message", "Ocorreu um erro interno. Tente novamente mais tarde.");
        error.put("timestamp", System.currentTimeMillis());
        
        return gson.toJson(error);
    }
}
