import os

import MySQLdb
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


def conectar() -> MySQLdb.connections.Connection:
    """
    Função para conectar ao servidor.
    :return: Conexão estabelecida.
    """
    try:
        return MySQLdb.connect(
            database='pmysql',
            host='localhost',
            user=os.getenv('usuario'),
            passwd=os.getenv('senha')
        )
    except MySQLdb.Error as e:
        print(f'Erro na connexão ao MySQL Server: {e}')


def desconectar(conexao: MySQLdb.connections.Connection):
    """
    Função para desconectar do servidor.
    :param conexao: Conexão com o servidor
    """
    if conexao:
        conexao.close()


def listar(conexao: MySQLdb.connections.Connection):
    """
    Função para listar os produtos.
    :param conexao: Conexão com o servidor
    """
    cursor = conexao.cursor()
    cursor.execute('SELECT * FROM produtos;')
    produtos = cursor.fetchall()

    if produtos:
        print('Listando produtos...')
        print('--------------------')
        for produto in produtos:
            print(f'ID: {produto[0]}')
            print(f'Nome: {produto[1]}')
            print(f'Preço: {produto[2]}')
            print(f'Estoque: {produto[3]}')
            print('--------------------')
    else:
        print('Não existem produtos cadastrados.')


def inserir(conexao: MySQLdb.connections.Connection):
    """
    Função para inserir um produto
    :param conexao: Conexão com o servidor
    """
    cursor = conexao.cursor()

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

    # Executar query no banco de dados
    cursor.execute(f"INSERT INTO produtos (nome, preco, estoque) VALUES ('{nome}', {preco}, {estoque});")
    conexao.commit()

    # Checar se a ação foi bem sucedida
    if cursor.rowcount == 1:
        print(f'O produto {nome} foi inserido com sucesso.')
    else:
        print('Falha ao inserir produto no banco de dados.')


def atualizar(conexao: MySQLdb.connections.Connection):
    """
    Função para atualizar um produto.
    :param conexao: Conexão com o servidor
    """
    cursor = conexao.cursor()

    codigo_produto = input('Insira o ID do produto: ')

    # Repetir até que um dado válido para o ID seja inserido
    while not validar(int, codigo_produto):
        print('Esse não é um ID válido, tente novamente.\n')
        codigo_produto = input('Insira o ID do produto: ')

    codigo_produto = validar(int, codigo_produto)

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

    # Executar query no banco de dados
    cursor.execute(f"UPDATE produtos SET nome='{nome}', preco={preco}, estoque={estoque} WHERE id={codigo_produto};")
    conexao.commit()

    # Checar se a ação foi bem sucedida
    if cursor.rowcount == 1:
        print(f'Produto {codigo_produto} atualizado com sucesso.')
    else:
        print(f'Falha ao atualizar o produto {codigo_produto}.')


def deletar(conexao: MySQLdb.connections.Connection):
    """
    Função para deletar um produto.
    :param conexao: Conexão com o servidor
    """
    cursor = conexao.cursor()

    codigo_produto = input('Insira o ID do produto: ')

    # Repetir até que um dado válido para o ID seja inserido
    while not validar(int, codigo_produto):
        print('Esse não é um ID válido, tente novamente.\n')
        codigo_produto = input('Insira o ID do produto: ')

    codigo_produto = validar(int, codigo_produto)

    # Executar query no banco de dados
    cursor.execute(f'DELETE FROM produtos WHERE id={codigo_produto};')
    conexao.commit()

    # Checar se a ação foi bem sucedida
    if cursor.rowcount == 1:
        print(f'O produto {codigo_produto} foi deletado com sucesso.')
    else:
        print('Não foi possível deletar o produto.')


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

        conn = conectar()
        opcao = input()
        if opcao.isdigit() and opcao in ['1', '2', '3', '4', '5']:
            if opcao == '1':
                listar(conn)
            elif opcao == '2':
                inserir(conn)
            elif opcao == '3':
                atualizar(conn)
            elif opcao == '4':
                deletar(conn)
            elif opcao == '5':
                desconectar(conn)
                break  # Finalizar loop
        else:
            print('Opção inválida')
        print('=' * 48)
