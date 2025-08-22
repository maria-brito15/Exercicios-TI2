package com.exercicio02;

import com.exercicio02.UsuarioDAO;
import com.exercicio02.Usuario;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UsuarioDAO dao = new UsuarioDAO();
        
        Scanner sc = new Scanner(System.in);
        
        boolean conectado = dao.conectar();

        if (!conectado) {
            System.out.println("Erro ao conectar ao banco. Encerrando...");
            
            sc.close();
            return;
        }

        int opcao;

        do {
            System.out.println("\n=== MENU CRUD USUÁRIO ===");
            System.out.println("1 - Listar usuários");
            System.out.println("2 - Inserir usuário");
            System.out.println("3 - Atualizar usuário");
            System.out.println("4 - Excluir usuário");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");
            
            opcao = sc.nextInt();
            sc.nextLine(); 

            switch (opcao) {
                case 1:
                    List<Usuario> lista = dao.listarUsuarios();
                    
                    for (Usuario u : lista) {
                        System.out.println(u);
                    }
                    
                    break;

                case 2:
                    System.out.print("Código: ");
                    int codIns = sc.nextInt();
                    sc.nextLine();
                    
                    System.out.print("Login: ");
                    String loginIns = sc.nextLine();
                    
                    System.out.print("Senha: ");
                    String senhaIns = sc.nextLine();
                    
                    System.out.print("Sexo: ");
                    String sexoIns = sc.nextLine();
                    
                    Usuario novo = new Usuario(codIns, loginIns, senhaIns, sexoIns);
                    dao.inserirUsuario(novo);
                    
                    System.out.println("Usuário inserido.");
                    break;

                case 3:
                    System.out.print("Código do usuário a atualizar: ");
                    int codAlt = sc.nextInt();
                    sc.nextLine();
                    
                    System.out.print("Novo login: ");
                    String loginAlt = sc.nextLine();
                    
                    System.out.print("Nova senha: ");
                    String senhaAlt = sc.nextLine();
                    
                    System.out.print("Novo sexo: ");
                    String sexoAlt = sc.nextLine();
                    
                    Usuario atualizado = new Usuario(codAlt, loginAlt, senhaAlt, sexoAlt);
                    dao.atualizarUsuario(atualizado);
                    
                    System.out.println("Usuário atualizado.");
                    break;

                case 4:
                    System.out.print("Código do usuário a excluir: ");
                    int codExc = sc.nextInt();
                    dao.excluirUsuario(codExc);
                    System.out.println("Usuário excluído.");
                    break;

                case 0:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }

        } while (opcao != 0);

        dao.close();
        sc.close();
    }
}