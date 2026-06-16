package java_mongodb;

import java.util.Arrays;
import java.util.Scanner;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.logging.Level;
import java.util.logging.Logger;



import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class Utils {
	
	static Scanner teclado = new Scanner(System.in);
	
	public static MongoCollection<Document> conectar() {
		Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
		try{
			MongoClient conn = MongoClients.create(
					MongoClientSettings.builder().applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress("localhost", 27017)))).build()
					);
			MongoDatabase db = conn.getDatabase("pmongo");
			MongoCollection<Document> produtos = db.getCollection("produtos");
			return produtos;
			
		}catch(Exception e) {
			e.printStackTrace();
			System.err.println("Não foi possível conectar ao servidor.");
			System.exit(-42);
			return null;
		}
	}


	public static void listar() {
		MongoCollection<Document> produtos = conectar();
		
		if(produtos.countDocuments() > 0) {
			MongoCursor<Document> cursor = produtos.find().iterator();
			
			try {
				System.out.println("Listando Produtos...");
				System.out.println("--------------------");
				while(cursor.hasNext()) {
					String json = cursor.next().toJson();
					
					JSONObject obj= new JSONObject(json);
					JSONObject id = obj.getJSONObject("_id");
				
					System.out.println("ID: " + id.get("$oid"));
					System.out.println("Produto: " + obj.get("nome"));
					System.out.println("Preço: " + obj.get("preco"));
					System.out.println("Estoque: " + obj.get("estoque"));
					System.out.println("--------------------");
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally {
				cursor.close();
			}
			
		}else {
			System.out.println("Não existem produtos cadastrados.");
		}
	}
	
	public static void inserir() {
		MongoCollection<Document> produtos = conectar();
		
		System.out.println("Inserindo produtos...");
		System.out.println("Informe o nome do produto:");
		String nome = teclado.nextLine();
		System.out.println("Informe o preço: ");
		float preco = teclado.nextFloat();
		System.out.println("Informe a quantidade em estoque:");
		int estoque = teclado.nextInt();
		
		JSONObject novo_produto = new JSONObject();
		novo_produto.put("nome", nome);
		novo_produto.put("preco", preco);
		novo_produto.put("estoque", estoque);
		
		produtos.insertOne(Document.parse(novo_produto.toString()));
		
		System.out.println("O produto " + nome + " foi inserido com sucesso.");
	}
	
	public static void atualizar() {
		MongoCollection<Document> produtos = conectar();
		
		System.out.println("Informe o ID do produto: ");
		String _id = teclado.nextLine();
		try {
		ObjectId objectId = new ObjectId(_id);
		
		long count = produtos.countDocuments(
		        Filters.eq("_id", objectId), 
		        new com.mongodb.client.model.CountOptions().limit(1)
		    );
		
		if(count > 0) {
			
			System.out.println("Informe o novo nome do produto: ");
			String nome = teclado.nextLine();
			
			System.out.println("Informe o novo preço do produto: ");
			float preco = teclado.nextFloat();
			
			System.out.println("Informe A quantidade em estoque: ");
			int estoque = teclado.nextInt();
			
			Bson query = combine(set("nome", nome), set("preco", preco), set("estoque", estoque));
			
			UpdateResult res = produtos.updateOne(new Document("_id", new ObjectId(_id)), query);
		
			if(res.getModifiedCount() == 1) {
				System.out.println("O produto " + nome + " foi atualizado com sucesso.");
			}else {
				System.out.println("O produto não pode ser atualizado.");
			}
		}else {
			System.out.println("Não existe um produto com esse id.");
		}
		}catch(Exception e) {
			System.out.println("ID inválido.");
		}
	}
	
	public static void deletar() {
		MongoCollection<Document> produtos = conectar();
		
		System.out.println("Insira o ID do produto: ");
		String _id = teclado.nextLine();
		
		try {
			ObjectId objectId = new ObjectId(_id);
			
			long count = produtos.countDocuments(
			        Filters.eq("_id", objectId), 
			        new com.mongodb.client.model.CountOptions().limit(1)
			    );
			
			if(count > 0) {
				DeleteResult res = produtos.deleteOne(new Document("_id", new ObjectId(_id)));
				
				if(res.getDeletedCount() == 1) {
					System.out.println("Produto deletado com sucesso.");
				}else {
					System.out.println("Não foi possível deletar o produto");
				}
				}
			}catch(Exception e) {
				System.out.println("ID inválido.");
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
