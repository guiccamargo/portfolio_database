import redis


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


def gera_id():
    """
    Gera o próximo id do banco.
    :return: Valor da próxima da chave
    """
    try:
        conn = conectar()

        chave = conn.get('chave')

        if chave:
            chave = conn.incr('chave')
            return chave
        else:
            conn.set('chave', 1)
            return 1
    except redis.exceptions.ConnectionError as e:
        print(f'Não foi possível gerar a chave: {e}')


def conectar() -> redis.Redis:
    """
    Função para conectar ao servidor.
    """
    conn = redis.Redis(host='localhost', port=6379)
    return conn


def desconectar(conexao: redis.Redis):
    """
    Função para desconectar do servidor.
    :param conexao: Conexão ao banco de dados
    """
    conexao.connection_pool.disconnect()


def listar():
    """
    Função para listar os produtos
    """
    conn = conectar()

    try:
        dados = conn.keys(pattern='produtos:*')

        if dados:
            print('Listando produtos:')
            print('-----------------')
            for chave in dados:
                produto = conn.hgetall(chave)
                print(f"ID: {str(chave, 'utf-8', 'ignore').replace('produtos:', '')}")
                print(f"Produto: {str(produto[b'nome'], 'utf-8', 'ignore')}")
                print(f"Preço: {str(produto[b'preco'], 'utf-8', 'ignore')}")
                print(f"Estoque: {str(produto[b'estoque'], 'utf-8', 'ignore')}")
                print('-----------------')
        else:
            print('Não existem produtos cadastrados.')
    except redis.exceptions.ConnectionError as e:
        print(f'Não foi possível listar os produtos. {e}')
    finally:
        desconectar(conn)


def inserir():
    """
    Função para inserir um produto
    """
    conn = conectar()

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
    chave = f'produtos:{gera_id()}'

    try:
        res = conn.hmset(chave, produto)
        if res:
            print(f'Produto {nome} inserido com sucesso.')
        else:
            print('Não foi possível inserir o produto.')

    except redis.exceptions.ConnectionError as e:
        print(f'Falha ao inserir produot: {e}')


def atualizar():
    """
    Função para atualizar um produto
    """
    conn = conectar()

    chave_atual = conn.get('chave')

    # Repetir até o usuário informar um id que exista no banco
    while True:
        codigo_produto = input('Insira o ID do produto: ')

        # Repetir até que um dado válido para o ID seja inserido
        while not validar(int, codigo_produto):
            print('Esse não é um ID válido, tente novamente.\n')
            codigo_produto = input('Insira o ID do produto: ')

        codigo_produto = validar(int, codigo_produto)

        if codigo_produto > int(chave_atual):
            print('Não existe produto relacionado ao código inserido.')
        else:
            break
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

    produto = {'nome': nome, 'preco': preco, 'estoque': estoque}

    try:
        res = conn.hmset(f'produtos:{codigo_produto}', produto)
        if res:
            print(f'O produto {codigo_produto} foi atulizado com sucesso.')
    except redis.exceptions.ConnectionError as e:
        print(f'Erro ao atualizar o produto: {e}')
    finally:
        desconectar(conn)


def deletar():
    """
    Função para deletar um produto
    """
    conn = conectar()

    chave_atual = conn.get('chave')

    # Repetir até o usuário informar um id que exista no banco
    while True:
        codigo_produto = input('Insira o ID do produto: ')

        # Repetir até que um dado válido para o ID seja inserido
        while not validar(int, codigo_produto):
            print('Esse não é um ID válido, tente novamente.\n')
            codigo_produto = input('Insira o ID do produto: ')

        codigo_produto = validar(int, codigo_produto)

        if codigo_produto > int(chave_atual):
            print('Não existe produto relacionado ao código inserido.')
        else:
            break

    try:
        print(f'produtos:{codigo_produto}')
        res = conn.delete(f'produtos:{codigo_produto}')
        if res:
            print('Produto deletado com sucesso.')
    except redis.exceptions.ConnectionError as e:
        print(f'Não foi possível deletar o produto: {e}')
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
