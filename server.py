import socket
import json
from game import Game

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_address = ('127.0.0.1', 3000)
sock.bind(server_address)
sock.listen(1)

def verifyWinner(flag):
	if flag != 'n':
		if flag == 'x':
			return 'x'
		elif flag == 'o':
			return 'o'
		elif flag == 'e':
			return 'e'
	else:
		return 'n'


while True:
	print('Aguardando conexão com o jogador...')
	connection, client_adrress = sock.accept()

	try:
		print('Jogador conectado! :)')
		board = Game()

		board.moveRandom('o')
		print('Servidor jogou: ')
		board.print()

		x = board.save()
		x += "\r\n"
		
		connection.sendall(x.encode())
		while True:
				data = connection.recv(1024)
				if not data:
					print('Jogador saiu. :(')
					break
				else:
					board.restore(data.decode())
					print('Jogador jogou: ')
					board.print()
					aux = board.save()

					if verifyWinner(board.checkWinner()) != 'n':
						aux = json.loads(aux)
						aux['flag'] = verifyWinner(board.checkWinner())
						connection.sendall(json.dumps(aux).encode())
						break
					else:
						board.moveRandom('o')
						print('Servidor jogou: ')
						board.print()
						x = board.save()

						if verifyWinner(board.checkWinner()) != 'n':
							x = json.loads(x)
							x['flag'] = verifyWinner(board.checkWinner())
							connection.sendall(json.dumps(x).encode())
							break
						else:
							x += "\r\n"
							connection.sendall(x.encode())
	finally:
		connection.close()



