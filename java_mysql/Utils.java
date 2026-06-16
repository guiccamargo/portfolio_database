package java_mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import io.github.cdimascio.dotenv.Dotenv;

public class Utils {
	
	static Scanner teclado = new Scanner(System.in);
	
	public static Connection conectar() {
		
		Dotenv dotenv = Dotenv.load();
		
		String USUARIO = dotenv.get("MYSQL_USER");
		String SENHA = dotenv.get("MYSQL_PASSWORD");
		String URL_SERVIDOR = dotenv.get("MYSQL_URL");
		
		try {
			return DriverManager.getConnection(URL_SERVIDOR+ "user=" + USUARIO + "&password="+ SENHA);
		}catch(Exception e) {
			if(e instanceof ClassNotFoundException) {
				System.out.println("Verifique o driver de conexão.");
			}else {
				System.out.println("Verifique se o servidor está ativo");
			}
			System.exit(-42);
			return null;
		}
	}

	public static void desconectar(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			}catch (SQLException e) {
				System.out.println("Não foi possível fechar a conexão.");
				e.printStackTrace();
			}
		}
	}

	public static void listar() {
		String BUSCAR_TODOS = "SELECT * FROM produtos;";
		
		try {
			Connection conn= conectar();
			
	
			PreparedStatement produtos = conn.prepareStatement(BUSCAR_TODOS);
			ResultSet res = produtos.executeQuery();
			
		
			if (res.next()) {
				System.out.println("Listando Produtos...");
				System.out.println("--------------------");
				
				do {

						System.out.println("ID: " + res.getInt(1));
						System.out.println("Produto: " + res.getString(2));
						System.out.println("Preço: " + res.getFloat(3));
						System.out.println("Estoque: " + res.getInt(4));
						System.out.println("--------------------");
				} while(res.next());
			}
			else {
				System.out.println("Não existem produtos cadastrados.");
			}
			
			
			produtos.close();
			desconectar(conn);
			
		}catch(Exception e) {
			e.printStackTrace();
			System.err.println("Erro ao buscar produtos.");
			System.exit(-42);
		}
	}
	
	public static void inserir() {
		System.out.println("Informe o nome do produto:");
		String nome = teclado.nextLine();
		System.out.println("Informe o preço: ");
		float preco = teclado.nextFloat();
		System.out.println("Informe a quantidade em estoque:");
		int estoque = teclado.nextInt();
		
		String INSERIR_PRODUTO = "INSERT INTO PRODUTOS (nome, preco, estoque) VALUES (?, ?, ?)";
		
		try {
			Connection conn = conectar();
			PreparedStatement salvar = conn.prepareStatement(INSERIR_PRODUTO);
			salvar.setString(1, nome);
			salvar.setFloat(2, preco);
			salvar.setInt(3, estoque);
			
			salvar.executeUpdate();
			salvar.close();
			System.out.println("O Produto " + nome + " foi inserido com sucesso.");
			
			desconectar(conn);
			
		}catch(Exception e) {
			e.printStackTrace();
			System.err.println("Erro ao inserir produto.");
			System.exit(-42);
		}
	}
	
	
	public static void atualizar() {
		System.out.println("Insira o ID do produto:");
		int id = Integer.parseInt(teclado.nextLine());
		
		String BUSCAR_POR_ID = "SELECT * FROM produtos WHERE id=?";
		
		try {
			Connection conn = conectar();
			PreparedStatement produto = conn.prepareStatement(BUSCAR_POR_ID);
			produto.setInt(1, id);
			
			ResultSet res = produto.executeQuery();
			
			if(res.next()) {
				do {
					System.out.println("Informe o novo nome do produto:");
					String nome = teclado.nextLine();
					System.out.println("Informe o novo preço: ");
					float preco = teclado.nextFloat();
					System.out.println("Informe a quantidade em estoque:");
					int estoque = teclado.nextInt();
					
					String ATUALIZAR_PRODUTO = "UPDATE produtos SET nome=?, preco=?, estoque=? WHERE id=?";
					
					try {
						PreparedStatement atualizar_dados = conn.prepareStatement(ATUALIZAR_PRODUTO);
						atualizar_dados.setString(1, nome);
						atualizar_dados.setFloat(2, preco);
						atualizar_dados.setInt(3, estoque);
						atualizar_dados.setInt(4, id);
						
						atualizar_dados.execute();
						System.out.println("Produto " + nome + " atualizado com sucesso.");
					}catch(Exception e) {
						e.printStackTrace();
						System.err.println("Erro ao atualizar o produto.");
						System.exit(-42);
					}
					}while(res.next());}
			else {
				System.out.println("Não existe um produto com esse ID.");
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.err.println("Erro ao atualizar o produto.");
			System.exit(-42);
		}
		
		
	}
			
	
	public static void deletar() {
		System.out.println("Insira o ID do produto:");
		int id = Integer.parseInt(teclado.nextLine());
		
		String BUSCAR_POR_ID = "SELECT * FROM produtos WHERE id=?";
		
		try {
			Connection conn = conectar();
			PreparedStatement produto = conn.prepareStatement(BUSCAR_POR_ID);
			produto.setInt(1, id);
			
			ResultSet res = produto.executeQuery();
			
			if(res.next()) {
				do {
		
					String DELETAR_PRODUTO = "DELETE FROM produtos WHERE id=?;";
		
				try {
					PreparedStatement excluir = conn.prepareStatement(DELETAR_PRODUTO);
					excluir.setInt(1, id);
					excluir.execute();
					System.out.println("O produto " + id + " foi excluido com sucesso.");
					excluir.close();
					
				}catch(Exception e) {
					e.printStackTrace();
					System.err.println("Erro ao deletar produto.");
					System.exit(-42);
				}} while (res.next());
			}else {
				System.out.println("Não existe um produto com esse ID.");	
			}
			desconectar(conn);
		}
			catch(Exception e) {
				e.printStackTrace();
				System.err.println("Erro ao deletar o produto.");
				System.exit(-42);
			}
				
	}
	
	public static void menu() {
		System.out.println("==================Gerenciamento de Produtos===============");
		System.out.println("Selecione uma opção: ");
		System.out.println("1 - Listar produtos.");
		System.out.println("2 - Inserir produtos.");
		System.out.println("3 - Atualizar produtos.");
		System.out.println("4 - Deletar produtos.");
		
		int opcao = Integer.parseInt(teclado.nextLine());
		if(opcao == 1) {
			listar();
		}else if(opcao == 2) {
			inserir();
		}else if(opcao == 3) {
			atualizar();
		}else if(opcao == 4) {
			deletar();
		}else {
			System.out.println("Opção inválida.");
		}
	}
}
