package com.sparkjava.sparkcore.app;

import static spark.Spark.*;

import com.sparkjava.sparkcore.controller.ProdutoController;

public class App {
	public static void main(String[] args) {
	    try {
	        port(8080);
	        
	        staticFiles.location("/public");
	        
	        get("/test", (req, res) -> "Servidor funcionando!");
	        
	        get("/", (req, res) -> {
	            res.redirect("/index.html");
	            return null;
	        });
	        
	        ProdutoController.rotas();
	        
	        System.out.println("Servidor rodando em http://localhost:8080");
	        
	    } catch (Exception e) {
	        System.out.println("Erro ao iniciar servidor: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
}
