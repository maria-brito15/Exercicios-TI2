package com.sparkjava.sparkcore.app;

import static spark.Spark.*;
import com.sparkjava.sparkcore.controller.ProdutoController;

import java.util.logging.Logger;
import java.util.logging.Level;

public class App {
    
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    
    public static void main(String[] args) {
        try {
            LOGGER.info("Iniciando servidor...");
            
            port(8080);
            staticFiles.location("/public");
            
            get("/test", (req, res) -> "Servidor funcionando!");
            
            get("/", (req, res) -> {
                res.redirect("/index.html");
                return null;
            });
            
            ProdutoController.rotas();
            
            awaitInitialization();
            LOGGER.info("Servidor rodando em http://localhost:8080");
            System.out.println("Servidor rodando em http://localhost:8080");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao iniciar servidor", e);
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }
}
