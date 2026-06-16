package java_couchdb;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Base64;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;

public class Utils {
	
	static Scanner teclado = new Scanner(System.in);
	
	public static HttpClient conectar() {
		HttpClient.newHttpClient();
		HttpClient conn = HttpClient.newBuilder().build();
		
		return conn;
	}


	public static void listar() {
		HttpClient conn = conectar();
		
		Dotenv dotenv = Dotenv.load();
		
		String link = dotenv.get("COUCH_LINK") + "_all_docs?include_docs=true";
		
		 String user = dotenv.get("COUCH_USER");
         String pass = dotenv.get("COUCH_PASSWORD");
	     String credentials = user + ":" + pass;
	     String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
	        
		HttpRequest requisicao = HttpRequest.newBuilder()
				.uri(URI.create(link))
				.header("Authorization", "Basic " + base64Credentials)
				.build();
		
		try {
			HttpResponse<String> resposta = conn.send(requisicao, BodyHandlers.ofString());
			
			JSONObject obj = new JSONObject(resposta.body());
			
			
			if((int)obj.get("total_rows") > 0) {
				JSONArray produtos = (JSONArray)obj.get("rows");
				
				System.out.println("Listando produtos...");
				System.out.println("====================");
				
				for(Object produto : produtos) {
					JSONObject doc = (JSONObject) produto;
					JSONObject prod = (JSONObject) doc.get("doc");
					
					
					System.out.println("ID: " + prod.get("_id"));
					System.out.println("Rev: " + prod.get("_rev"));
					System.out.println("Produto: " + prod.get("nome"));
					System.out.println("Preço: " + prod.get("preco"));
					System.out.println("Estoque: " + prod.get("estoque"));
					System.out.println("--------------------");
				}
			}else {
				System.out.println("Não existem produtos cadastrados.");
			}
		}catch(IOException e){
			System.out.println("Ocorreu um erro durante a conexão.");
			e.printStackTrace();
		}catch(InterruptedException e) {
			System.out.println("Ocorreu um erro durante a conexão.");
			e.printStackTrace();
		}
	}
	
	public static void inserir() {
		HttpClient conn = conectar();
		
		Dotenv dotenv = Dotenv.load();
		
		String link = dotenv.get("COUCH_LINK");
		
		System.out.println("Informe o nome do produto: ");
		String nome = teclado.nextLine();
		
		System.out.println("Informe o preço: ");
		float preco = teclado.nextFloat();
		
		System.out.println("Informe a Quantidade em estoque: ");
		int estoque = teclado.nextInt();
		
		JSONObject novo_produto = new JSONObject();
		
		novo_produto.put("nome", nome);
		novo_produto.put("preco", preco);
		novo_produto.put("estoque", estoque);
		
		
		String user = dotenv.get("COUCH_USER");
        String pass = dotenv.get("COUCH_PASSWORD");
	    String credentials = user + ":" + pass;
	    String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
	     
	     
		HttpRequest requisicao = HttpRequest.newBuilder().uri(URI.create(link))
				.POST(BodyPublishers.ofString(novo_produto.toString()))
				.header("Content-Type", "application/json")
				.header("Authorization", "Basic " + base64Credentials)
				.build();
		
		try {
			HttpResponse<String> resposta = conn.send(requisicao, BodyHandlers.ofString());
			
			JSONObject obj = new JSONObject(resposta.body());
			
			if(resposta.statusCode() == 201) {
				System.out.println("O produto foi cadastrado com sucesso.");
			}else {
				System.out.println("Não foi possível inserir o produto.");
				System.out.println(obj);
				System.out.println(resposta.statusCode());
			}
		}catch(IOException e){
			System.out.println("Ocorreu um erro durante a conexão.");
			e.printStackTrace();
		}catch(InterruptedException e) {
			System.out.println("Ocorreu um erro durante a conexão.");
			e.printStackTrace();
		}
	}
	
	public static void atualizar() {
		HttpClient conn = conectar();
		
		Dotenv dotenv = Dotenv.load();
		
		System.out.println("Informe o ID do produto: ");
		String _id = teclado.nextLine();
		
		System.out.println("Informe a rev do produto: ");
		String _rev = teclado.nextLine();
		
		System.out.println("Informe o nome do produto: ");
		String nome = teclado.nextLine();
		
		System.out.println("Informe o preço: ");
		float preco = teclado.nextFloat();
		
		System.out.println("Informe a Quantidade em estoque: ");
		int estoque = teclado.nextInt();
		
		String link = dotenv.get("COUCH_LINK") + _id + "/?rev=" + _rev;
		
		JSONObject novo_produto = new JSONObject();
		
		novo_produto.put("nome", nome);
		novo_produto.put("preco", preco);
		novo_produto.put("estoque", estoque);
		
		String user = dotenv.get("COUCH_USER");
        String pass = dotenv.get("COUCH_PASSWORD");
	    String credentials = user + ":" + pass;
	    String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
		
		HttpRequest requisicao = HttpRequest.newBuilder()
				.uri(URI.create(link))
				.PUT(BodyPublishers.ofString(novo_produto.toString()))
				.header("Content-Type", "application/json")
				.header("Authorization", "Basic " + base64Credentials)
				.build();
		
		try {
			HttpResponse<String> resposta = conn.send(requisicao, BodyHandlers.ofString());
			
			JSONObject obj = new JSONObject(resposta.body());
			
			if(resposta.statusCode() == 201) {
				System.out.println("O produto foi atualizado com sucesso.");
			}else {
				System.out.println("Não foi possível atualizar o produto.");
				System.out.println(obj);
				System.out.println(resposta.statusCode());
			}
		}catch(IOException e){
			System.out.println("Ocorreu um erro durante a conexão.");
			e.printStackTrace();
		}catch(InterruptedException e) {
			System.out.println("Ocorreu um erro durante a conexão.");
			e.printStackTrace();
		}
	}
	
	
	public static void deletar() {
		HttpClient conn = conectar();
		
		Dotenv dotenv = Dotenv.load();
		
		System.out.println("Informe o ID do produto: ");
		String _id = teclado.nextLine();
		
		System.out.println("Informe a rev do produto: ");
		String _rev = teclado.nextLine();
		
		String link = dotenv.get("COUCH_LINK") + _id + "/?rev=" + _rev;
		
		String user = dotenv.get("COUCH_USER");
        String pass = dotenv.get("COUCH_PASSWORD");
	    String credentials = user + ":" + pass;
	    String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
		
		HttpRequest requisicao = HttpRequest.newBuilder()
				.uri(URI.create(link))
				.header("Authorization", "Basic " + base64Credentials)
				.DELETE()
				.build();	
		
		try {
			HttpResponse<String> resposta = conn.send(requisicao, BodyHandlers.ofString());
			
			JSONObject obj = new JSONObject(resposta.body());
			
			if(resposta.statusCode() == 200) {
				System.out.println("O produto foi deletado com sucesso.");
			}else {
				System.out.println("Não foi possível deletar o produto.");
				System.out.println(obj);
				System.out.println(resposta.statusCode());
			}
		}catch(IOException e){
			System.out.println("Ocorreu um erro durante a conexão.");
			e.printStackTrace();
		}catch(InterruptedException e) {
			System.out.println("Ocorreu um erro durante a conexão.");
			e.printStackTrace();
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
