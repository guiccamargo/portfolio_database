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
    print('Conectando ao servidor...')


def desconectar():
    """ 
    Função para desconectar do servidor.
    """
    print('Desconectando do servidor...')


def listar():
    """
    Função para listar os produtos
    """
    print('Listando produtos...')


def inserir():
    """
    Função para inserir um produto
    """
    print('Inserindo produto...')


def atualizar():
    """
    Função para atualizar um produto
    """
    print('Atualizando produto...')


def deletar():
    """
    Função para deletar um produto
    """
    print('Deletando produto...')


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
