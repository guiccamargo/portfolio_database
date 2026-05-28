import os
import socket

import couchdb
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
    Função para conectar ao servidor.
    """
    user = os.getenv('usuario')
    passoword = os.getenv('senha')

    conn = couchdb.Server(f'http://{user}:{passoword}@localhost:5984')

    banco = 'pcouch'

    if banco in conn:
        return conn[banco]
    else:
        try:
            return conn.create(banco)

        except socket.gaierror as e:
            print(f'Erro ao conectar ao servidor: {e}')
        except couchdb.http.Unauthorized as f:
            print(f'Você não tem permissão para acessar esse banco de dados: {f}')
        except ConnectionRefusedError as g:
            print(f'Não foi prossível conectar ao servidor: {g}')


def listar():
    """
    Função para listar os produtos.
    """
    db = conectar()

    if db:
        if db.info()['doc_count']:
            print('Listando documentos:')
            print('--------------------')
            for doc in db:
                print(f'ID: {db[doc]['_id']}')
                print(f'Rev: {db[doc]['_rev']}')
                print(f'Produto: {db[doc]['nome']}')
                print(f'Preço: {db[doc]['preco']}')
                print(f'Estoque: {db[doc]['estoque']}')
                print('=' * 48)
        else:
            print('Não existem produtos cadastrados.')
    else:
        print('Não foi possível conectar ao banco de dados.')


def inserir():
    """
    Função para inserir um produto.
    """
    db = conectar()
    if db:
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

        res = db.save(produto)

        if res:
            print(f'O produto {nome} foi inserido com sucesso.')
        else:
            print('Não foi possível inserir o produto.')
    else:
        print('Nãp foi possível conectar ao banco de dados.')


def atualizar():
    """
    Função para atualizar um produto.
    """
    db = conectar()

    if db:
        chave = input('Informe o id do produto: ')

        try:
            doc = db[chave]

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

            doc['nome'] = nome
            doc['preco'] = preco
            doc['estoque'] = estoque

            db[doc.id] = doc
            print(f'O produto {nome} foi atualizado com sucesso.')
        except couchdb.http.ResourceNotFound as e:
            print(f'Produto não encontrado: {e}')
    else:
        print('Não foi possível conectar ao .')


def deletar():
    """
    Função para deletar um produto.
    """
    db = conectar()

    if db:
        chave = input('Informe o ID do produto: ')
        try:
            db.delete(db[chave])
            print('Produto deletado com sueceso.')
        except couchdb.http.ResourceNotFound as e:
            print(f'Não foi possível deletar o produto: {e}')
    else:
        print('Nãp foi possível conectar ao banco de dados.')


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
