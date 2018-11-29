import json
import numpy as np
from random import *

class Game():
	def __init__(self):
		self.board = [[''] * 3 for i in range(3)]
		self.numPlays = 0

	def save(self):
		if type(self.board) == list:
			pass
		else:
			self.board = self.board.tolist()
		dic = {}
		for i in range(3):
			dic[i + 1] = self.board[i]
		return json.dumps(dic)

	def restore(self, data):
		data = json.loads(data)
		lista = [i.replace("[", "").replace(",", "").replace("]", "").replace(" ", "") for i in data[str(1)].split(',')]
		lista1 = [i.replace("[", "").replace(",", "").replace("]", "").replace(" ", "") for i in data[str(2)].split(',')]
		lista2 = [i.replace("[", "").replace(",", "").replace("]", "").replace(" ", "") for i in data[str(3)].split(',')]
		data = lista + lista1 + lista2
		self.numPlays = self.numPlays + 1
		self.board = np.reshape(data, (3, 3))

	def print(self):
		print("+---+---+---+")
		for row in self.board:
			print('|{}|{}|{}|'.format(row[0].center(3, ' '), row[1].center(3, ' '), row[2].center(3, ' ')))
			print("+---+---+---+")

	def move(self, row, column, piece):
		if row < 0 or row > 2:
			raise RuntimeError('Número de linha inválido: {}'.format(row))

		if column < 0 or column > 2:
			raise RuntimeError('Número da coluna inválido: {}'.format(column))

		if piece.lower() != 'x' and piece.lower() != 'o':
			raise RuntimeError('Peça inválida: {}'.format(piece))

		if self.board[row][column] != '':
			raise RuntimeError('Posição do tabuleiro já preenchida: {}x{}'.format(row, column))
		
		self.board[row][column] = piece.lower()
		self.numPlays = self.numPlays + 1

	def moveRandom(self, piece):
		options_list = []
		for row in range(3):
			for col in range(3):
				if self.board[row][col] == '':
					options_list.append((row, col))

		shuffle(options_list)
		if len(options_list) > 0:
			row = options_list[0][0]
			col = options_list[0][1]
			self.move(row, col, piece)

	def checkWinner(self):
		if((self.board[0][0] == self.board[1][1] and self.board[0][0] == self.board[2][2]) or (self.board[0][2] == self.board[1][1] and self.board[0][2] == self.board[2][0])) and (self.board[0][0] != '' and self.board[1][1] != '' and self.board[2][2] != '' and self.board[0][2] != '' and self.board[2][0] != ''):
			if self.board[1][1] == 'x':
				return 'x'
			else:
				return 'o'

		else:
			for row in range(3):
				if (self.board[row][0] == self.board[row][1] and self.board[row][0] == self.board[row][2]) and (self.board[row][0] != '' and self.board[row][1] != '' and self.board[row][2] != ''):
					if self.board[row][0] == 'x':
						return 'x'
					else:
						return 'o'

				if (self.board[0][row] == self.board[1][row] and self.board[0][row] == self.board[2][row]) and (self.board[0][row] != '' and self.board[1][row] != '' and self.board[2][row] != ''):
					if self.board[0][row] == 'x':
						return 'x'
					else:
						return 'o'

			if self.numPlays == 9:
				return 'e'
			else:
				return 'n'
