import os

import empyrebase
from dotenv import load_dotenv

load_dotenv()


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
    config = {
        'apiKey': os.getenv('apikey'),
        'authDomain': os.getenv('url'),
        'databaseURL': os.getenv('url'),
        'storageBucket': os.getenv('bucket'),
        'projectId': os.getenv('project_id')
    }
    conn = empyrebase.initialize_app(config)

    return conn.database()


def listar():
    """
    Função para listar os produtos
    """
    db = conectar()

    produtos = db.child('produtos').get()

    if produtos.val():
        print('Listando produtos:')
        print('------------------')
        for produto in produtos.each():
            print(f'ID: {produto.key()}')
            print(f'Produto: {produto.val()['nome']}')
            print(f'Preço: {produto.val()['preco']}')
            print(f'Estoque: {produto.val()['estoque']}')
            print('=' * 48)
    else:
        print('Não existem produtos cadastrados.')


def inserir():
    """
    Função para inserir um produto
    """
    db = conectar()

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

    produto = {'nome': nome, 'preco': preco, 'estoque': estoque}

    res = db.child('produtos').push(produto)

    if 'name' in res:
        print(f'O produto {nome} foi cadastrado com sucesso.')
    else:
        print('Não foi possível cadastrar o produto.')


def atualizar():
    """
    Função para atualizar um produto
    """
    db = conectar()

    chave = input('Informe o ID do produto: ')

    produto = db.child('produtos').child(chave).get()

    if produto.val():
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

        novo_produto = {'nome': nome, 'preco': preco, 'estoque': estoque}

        db.child('produtos').child(chave).update(novo_produto)

        print(f'O produto {nome} foi atualizado com sucesso.')
    else:
        print('Não existe produto com o ID informado.')


def deletar():
    """
    Função para deletar um produto
    """
    db = conectar()

    chave = input('Informe o ID do produto: ')

    produto = db.child('produtos').child(chave).get()

    if produto.val():
        db.child('produtos').child(chave).remove()
        print('O produto foi deletado com sucesso.')
    else:
        print('Não existe produto com o ID informado.')


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
