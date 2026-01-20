-- Inserir clientes
INSERT INTO clientes (nome, email, telefone, endereco, data_cadastro, ativo) VALUES
('João Silva', 'joao@email.com', '(11) 99999-1111', 'Rua A, 123 - São Paulo/SP', NOW(), true),
('Maria Santos', 'maria@email.com', '(11) 99999-2222', 'Rua B, 456 - São Paulo/SP', NOW(), true),
('Pedro Oliveira', 'pedro@email.com', '(11) 99999-3333', 'Rua C, 789 - São Paulo/SP', NOW(), true);

-- Inserir restaurantes
INSERT INTO restaurantes (nome, categoria, endereco, telefone, taxa_entrega, avaliacao, ativo) VALUES
('Pizzaria Bella', 'Italiana', 'Av. Paulista, 1000 - São Paulo/SP', '(11) 3333-1111', 5.00, 4.5, true),
('Burger House', 'Hamburgueria', 'Rua Augusta, 500 - São Paulo/SP', '(11) 3333-2222', 3.50, 4.2, true),
('Sushi Master', 'Japonesa', 'Rua Liberdade, 200 - São Paulo/SP', '(11) 3333-3333', 8.00, 4.8, true);

-- Inserir produtos
-- Pizzaria Bella (ID 1)
INSERT INTO produtos (nome, descricao, preco, categoria, disponivel, restaurante_id) VALUES
('Pizza Margherita', 'Molho de tomate, mussarela e manjericão', 35.90, 'Pizza', true, 1),
('Pizza Calabresa', 'Molho de tomate, mussarela e calabresa', 38.90, 'Pizza', true, 1),
('Lasanha Bolonhesa', 'Lasanha tradicional com molho bolonhesa', 28.90, 'Massa', true, 1);

-- Burger House (ID 2)
INSERT INTO produtos (nome, descricao, preco, categoria, disponivel, restaurante_id) VALUES
('X-Burger', 'Hambúrguer, queijo, alface e tomate', 18.90, 'Hambúrguer', true, 2),
('X-Bacon', 'Hambúrguer, queijo, bacon, alface e tomate', 22.90, 'Hambúrguer', true, 2),
('Batata Frita', 'Porção de batata frita crocante', 12.90, 'Acompanhamento', true, 2);

-- Sushi Master (ID 3)
INSERT INTO produtos (nome, descricao, preco, categoria, disponivel, restaurante_id) VALUES
('Combo Sashimi', '15 peças de sashimi variado', 45.90, 'Sashimi', true, 3),
('Hot Roll Salmão', '8 peças de hot roll de salmão', 32.90, 'Hot Roll', true, 3),
('Temaki Atum', 'Temaki de atum com cream cheese', 15.90, 'Temaki', true, 3);

-- Inserir pedidos de exemplo
-- Nota: numero_pedido é String conforme sua classe Java
INSERT INTO pedidos (numero_Pedido, data_pedido, status, taxa_entrega, valor_total, endereco_entrega, cliente_id, restaurante_id) VALUES
('PED1234567890', NOW(), 'PENDENTE', 5.00, 40.90, 'Rua A, 123 - São Paulo/SP', 1, 1),
('PED1234567891', NOW(), 'CONFIRMADO', 3.50, 22.40, 'Rua B, 456 - São Paulo/SP', 2, 2),
('PED1234567892', NOW(), 'ENTREGUE', 8.00, 53.90, 'Rua C, 789 - São Paulo/SP', 3, 3);

-- Inserir itens dos pedidos
-- Pedido 1 (João na Pizzaria Bella) - 1 Pizza Margherita
INSERT INTO itens_pedido (quantidade, preco_unitario, subtotal, pedido_id, produto_id) VALUES
(1, 35.90, 35.90, 1, 1);

-- Pedido 2 (Maria na Burger House) - 1 X-Burger
INSERT INTO itens_pedido (quantidade, preco_unitario, subtotal, pedido_id, produto_id) VALUES
(1, 18.90, 18.90, 2, 4);

-- Pedido 3 (Pedro na Sushi Master) - 1 Combo Sashimi
INSERT INTO itens_pedido (quantidade, preco_unitario, subtotal, pedido_id, produto_id) VALUES
(1, 45.90, 45.90, 3, 7);