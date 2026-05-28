import bson.errors
from bson import ObjectId
from pymongo import MongoClient, errors


def validar(tipo, valor):
    """
    Função para verificar se o valor inserido é pode ser convertido.
    :param tipo: Tipo de dado esperado
    :param valor: Valor inserido
    :return: Valor convertido ou None
    """
    try:
        return tipo(valor)
    except ValueError:
        return None


def conectar():
    """
    Função para conectar ao servidor
    """
    conn = MongoClient('localhost', 27017)
    return conn


def desconectar(conexao: MongoClient):
    """ 
    Função para desconectar do servidor.
    :param conexao: Conexão com o servidor
    """
    if conexao:
        conexao.close()


def listar():
    """
    Função para listar os produtos
    """
    conn = conectar()
    db = conn.pmongo

    try:
        if db.produtos.count_documents({}):
            produtos = db.produtos.find()
            print('Listando produtos')
            print('-----------------')
            for produto in produtos:
                print(f"ID: {produto['_id']}")
                print(f"Produto: {produto['nome']}")
                print(f"Preço: {produto['preco']}")
                print(f"Estoque: {produto['estoque']}")
                print('-----------------')
        else:
            print('Não existem produtos cadastrados.')
    except errors.PyMongoError as e:
        print(f'Erro ao acessar o banco de dados: {e}')
    finally:
        conn.close()


def inserir():
    """
    Função para inserir um produto
    """
    conn = conectar()
    db = conn.pmongo
    nome = input('Informe o nome do produto: ')

    preco = input('Informe o preço do produto: ')

    # Repetir até que um dado válido para o preço seja inserido
    while not validar(float, preco):
        print('Esse não é um preço válido, tente novamente.\n')
        preco = input('Informe o preço do produto: ')

    preco = validar(float, preco)

    estoque = input('Informe a quantidade em estoque: ')

    # Repetir até que um dado válido para o estoque seja inserido
    while not validar(int, estoque):
        print('Esse não é um número válido, tente novamente.\n')
        estoque = input('Informe a quantidade em estoque: ')

    estoque = validar(int, estoque)

    try:
        db.produtos.insert_one(
            {
                'nome': nome,
                'preco': preco,
                'estoque': estoque
            }
        )
        print(f'O produto {nome} fois inserido com sucesso.')
    except errors.PyMongoError as e:
        print(f'Não foi possível inserir o produto. {e}')
    finally:
        desconectar(conn)


def atualizar():
    """
    Função para atualizar um produto
    """
    conn = conectar()
    db = conn.pmongo

    codigo_produto = input('Insira o ID do produto: ')

    nome = input('Insira o novo nome do produto: ')

    preco = input('Insira o novo preço do produto: ')

    # Repetir até que um dado válido para o preço seja inserido
    while not validar(float, preco):
        print('Esse não é um preço válido, tente novamente.\n')
        preco = input('Insira o novo preço do produto: ')

    preco = validar(float, preco)

    estoque = input('Informe a quantidade em estoque: ')

    # Repetir até que um dado válido para o estoque seja inserido
    while not validar(int, estoque):
        print('Esse não é um número válido, tente novamente.\n')
        estoque = input('Informe a quantidade em estoque: ')

    estoque = validar(int, estoque)

    try:
        if db.produtos.count_documents({}):
            res = db.produtos.update_one(
                {'_id': ObjectId(codigo_produto)},
                {
                    '$set':
                        {'nome': nome,
                         'preco': preco,
                         'estoque': estoque
                         }
                }
            )
            if res.modified_count == 1:
                print(f'O produto {nome} foi atualizado com sucesso.')
            else:
                print('Não foi possível atualizar o produto.')
        else:
            print('Não existem produtos para serem atualizados.')
    except errors.PyMongoError as e:
        print(f'Não foi possível inserir o produto. {e}')
    except bson.errors.InvalidId as f:
        print(f'Id inválido: {f}')
    finally:
        desconectar(conn)


def deletar():
    """
    Função para deletar um produto
    """
    conn = conectar()
    db = conn.pmongo

    codigo_produto = input('Insira o ID do produto: ')

    try:
        if db.produtos.count_documents({}):
            res = db.produtos.delete_one(
                {'_id': ObjectId(codigo_produto)}
            )
            if res.deleted_count > 0:
                print('Produto deletado com sucesso.')
            else:
                print('Não foi possivel deletar o produto.')
        else:
            print('Não existem produtos para serem deletados')

    except errors.PyMongoError as e:
        print(f'Não foi possível inserir o produto. {e}')
    except bson.errors.InvalidId as f:
        print(f'Id inválido: {f}')
    finally:
        desconectar(conn)


def menu():
    """
    Função para gerar o menu inicial.
    """
    # Rodar o menu até o usuário saia da aplicação
    while True:
        print('=========Gerenciamento de Produtos==============')
        print('Selecione uma opção: ')
        print('1 - Listar produtos.')
        print('2 - Inserir produtos.')
        print('3 - Atualizar produto.')
        print('4 - Deletar produto.')
        print('5 - Sair.')

        opcao = input()
        if opcao.isdigit() and opcao in ['1', '2', '3', '4', '5']:
            if opcao == '1':
                listar()
            elif opcao == '2':
                inserir()
            elif opcao == '3':
                atualizar()
            elif opcao == '4':
                deletar()
            elif opcao == '5':
                break  # Finalizar loop
        else:
            print('Opção inválida')
        print('=' * 48)
