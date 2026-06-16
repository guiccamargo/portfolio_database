package java_firebase;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;

public class Utils {
	
	static Scanner teclado = new Scanner(System.in);
	
	public static HttpClient conectar() {
		HttpClient conn = HttpClient.newBuilder().build();
		return conn;
	}

	public static void listar() {
		HttpClient conn = conectar();
		
		Dotenv dotenv = Dotenv.load();
		
		String link = dotenv.get("FIREBASE_LINK_BASE") + ".json";
	
		HttpRequest requisicao = HttpRequest.newBuilder()
				.uri(URI.create(link)).build();
		
		try{
			HttpResponse<String> resposta = conn.send(requisicao, BodyHandlers.ofString());
			
			if(resposta.body().equals("null")) {
				System.out.println("Não existem produtos cadastrados;");
			}else {
				JSONObject obj = new JSONObject(resposta.body());
				
				System.out.println("Listando produtos...");
				System.out.println("====================");
				for(int i = 0; i < obj.length(); i++) {
					JSONObject prod = (JSONObject) obj.get(obj.names().getString(i));
					System.out.println("ID: " + obj.names().getString(i));
					System.out.println("Produto: " + prod.get("nome"));
					System.out.println("Preço: " + prod.get("preco"));
					System.out.println("Estoque: " + prod.get("estoque"));
					System.out.println("--------------------");
				}
				
			}
		}catch(IOException e){
			System.out.println("Houve um erro na conexão: " + e);
		}catch(InterruptedException e) {
			System.out.println("Houve um erro na conexão: " + e);
		}
	}
	
	public static void inserir() {
		HttpClient conn = conectar();
		
		Dotenv dotenv = Dotenv.load();
		
		String link = dotenv.get("FIREBASE_LINK_BASE") + ".json";
		
		System.out.println("Informe o nome do produto: ");
		String nome = teclado.nextLine();
		
		System.out.println("Informe o preço do produto: ");
		float preco = teclado.nextFloat();
		
		System.out.println("Informe a quandidade em estoque: ");
		int estoque = teclado.nextInt();
		
		JSONObject novo_produto= new JSONObject();
		novo_produto.put("nome", nome);
		novo_produto.put("preco", preco);
		novo_produto.put("estoque", estoque);
		
		HttpRequest requisicao = HttpRequest.newBuilder()
				.uri(URI.create(link))
				.POST(BodyPublishers.ofString(novo_produto.toString()))
				.header("Content-Type", "applcation/json")
				.build();
		try{
			HttpResponse<String> resposta = conn.send(requisicao, BodyHandlers.ofString());
			
				JSONObject obj = new JSONObject(resposta.body());
				
				if(resposta.statusCode() == 200) {
					System.out.println("O produto " + nome + " foi inserido com sucesso.");
				}else {
					System.out.println("Não foi possível inserir o produto.");
					System.out.println(obj);
					System.out.println("Status: " + resposta.statusCode());
				}					
		}catch(IOException e){
			System.out.println("Houve um erro na conexão: " + e);
		}catch(InterruptedException e) {
			System.out.println("Houve um erro na conexão: " + e);
		}
		
	}
	
	public static void atualizar() {
		HttpClient conn = conectar();
		
		Dotenv dotenv = Dotenv.load();
		
		System.out.println("Informe o ID: ");
		String _id = teclado.nextLine();	
			
		
		String link_busca = dotenv.get("FIREBASE_LINK_BASE") + ".json";
		
		HttpRequest requisicao_busca = HttpRequest.newBuilder()
				.uri(URI.create(link_busca)).build();
		
		try{
			HttpResponse<String> resposta_busca = conn.send(requisicao_busca, BodyHandlers.ofString());
			

			JSONObject obj = new JSONObject(resposta_busca.body());
			if(obj.names().toString().contains(_id)) {
				
				String link = dotenv.get("FIREBASE_LINK_BASE") + "/" + _id + ".json";
				
				System.out.println("Informe o nome do produto: ");
				String nome = teclado.nextLine();
				
				System.out.println("Informe o preço do produto: ");
				float preco = teclado.nextFloat();
				
				System.out.println("Informe a quandidade em estoque: ");
				int estoque = teclado.nextInt();
			
				
				JSONObject novo_produto= new JSONObject();
				novo_produto.put("nome", nome);
				novo_produto.put("preco", preco);
				novo_produto.put("estoque", estoque);
				
				HttpRequest requisicao = HttpRequest.newBuilder()
						.uri(URI.create(link))
						.PUT(BodyPublishers.ofString(novo_produto.toString()))
						.header("Content-Type", "applcation/json")
						.build();
				
				try{
					HttpResponse<String> resposta = conn.send(requisicao, BodyHandlers.ofString());
					
						JSONObject res = new JSONObject(resposta.body());
						
						if(resposta.statusCode() == 200) {
							System.out.println("O produto " + nome + " foi atualizado com sucesso.");
						}else {
							System.out.println("Não foi possível atualizar o produto.");
							System.out.println(res);
							System.out.println("Status: " + resposta.statusCode());
						}					
				}catch(IOException e){
					System.out.println("Houve um erro na conexão: " + e);
				}catch(InterruptedException e) {
					System.out.println("Houve um erro na conexão: " + e);
				}
			}else {
				System.out.println("Não existem produtos com o ID informado.");
			}
		
		}catch(IOException e){
			System.out.println("Houve um erro na conexão: " + e);
		}catch(InterruptedException e) {
			System.out.println("Houve um erro na conexão: " + e);
		}catch(JSONException e) {
			System.out.println("Não existe nenhum produto cadastrado.");
		}
		
	}
	
	
	public static void deletar() {
		HttpClient conn = conectar();
		
		System.out.println("Informe o ID: ");
		String _id = teclado.nextLine();	
		
		Dotenv dotenv = Dotenv.load();
		
		String link_busca = dotenv.get("FIREBASE_LINK_BASE") + ".json";
				
		HttpRequest requisicao_busca = HttpRequest.newBuilder()
				.uri(URI.create(link_busca)).build();
		
		try{
			HttpResponse<String> resposta_busca = conn.send(requisicao_busca, BodyHandlers.ofString());
			
		
				JSONObject obj = new JSONObject(resposta_busca.body());

				if(obj.names().toString().contains(_id)) {
					
					String link = dotenv.get("FIREBASE_LINK_BASE") + "/" + _id + ".json";
					
					HttpRequest requisicao = HttpRequest.newBuilder()
							.uri(URI.create(link))
							.DELETE()
							.header("Content-Type", "applcation/json")
							.build();
					try{
						HttpResponse<String> resposta = conn.send(requisicao, BodyHandlers.ofString());
							if(resposta.statusCode() == 200 && resposta.body() != null) {
								System.out.println("O produto " + _id + " foi deletado com sucesso.");
							}else {
								System.out.println("Erro ao deletar produto.");
							}					
					}catch(IOException e){
						System.out.println("Houve um erro na conexão: " + e);
					}catch(InterruptedException e) {
						System.out.println("Houve um erro na conexão: " + e);
					}
				}else {
					System.out.println("Não existem produtos com o ID informado.");
				}
			}catch(IOException e){
				System.out.println("Houve um erro na conexão: " + e);
			}catch(InterruptedException e) {
				System.out.println("Houve um erro na conexão: " + e);
			}catch(JSONException e) {
				System.out.println("Não existe nenhum produto cadastrado.");
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
