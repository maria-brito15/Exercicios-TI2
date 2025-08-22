package com.exercicio02;

import com.exercicio02.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private Connection conexao;

    public boolean conectar() {
        String url = "jdbc:postgresql://localhost:5432/exercicio02crud";
        String user = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        try {
            Class.forName("org.postgresql.Driver");
            conexao = DriverManager.getConnection(url, user, password);
            
            return true;
        } catch (Exception e) {
            System.err.println("Erro na conexão: " + e.getMessage());
            
            return false;
        }
    }

    public boolean close() {
        try {
            conexao.close();
            
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean inserirUsuario(Usuario usuario) {
        try {
            Statement st = conexao.createStatement();
            
            st.executeUpdate("INSERT INTO usuario (codigo, login, senha, sexo) VALUES (" +
                    usuario.getCodigo() + ", '" +
                    usuario.getLogin() + "', '" +
                    usuario.getSenha() + "', '" +
                    usuario.getSexo() + "');");
            st.close();
            
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean atualizarUsuario(Usuario usuario) {
        try {
            Statement st = conexao.createStatement();
            
            String sql = "UPDATE usuario SET login = '" + usuario.getLogin() +
                    "', senha = '" + usuario.getSenha() +
                    "', sexo = '" + usuario.getSexo() +
                    "' WHERE codigo = " + usuario.getCodigo();
            
            st.executeUpdate(sql);
            st.close();
            
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean excluirUsuario(int codigo) {
        try {
            Statement st = conexao.createStatement();
            
            st.executeUpdate("DELETE FROM usuario WHERE codigo = " + codigo);
            st.close();
            
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        
        try {
            Statement st = conexao.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM usuario");
            
            while (rs.next()) {
                Usuario u = new Usuario(
                        rs.getInt("codigo"),
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getString("sexo")
                );
                
                lista.add(u);
            }
            
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return lista;
    }
}