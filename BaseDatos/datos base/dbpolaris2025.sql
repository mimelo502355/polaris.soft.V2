CREATE DATABASE dbPolaris2025;
USE dbPolaris2025;

CREATE TABLE empleado (
    idEmpleado CHAR(36) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL
);

CREATE TABLE categoria (
    idCategoria CHAR(36) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL
);

CREATE TABLE producto (
    idProducto CHAR(36) PRIMARY KEY,
    idCategoria CHAR(36) NOT NULL,
    disponible BOOLEAN NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    precioBase DECIMAL(10,2) NOT NULL,
    descripcion TEXT,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,
    FOREIGN KEY (idCategoria) REFERENCES categoria(idCategoria)
);

CREATE TABLE acompanamiento (
    idAcompanamiento CHAR(36) PRIMARY KEY,
    nombreAcompanamiento VARCHAR(100) NOT NULL,
    tipoAcompanamiento ENUM('SALSA','TOPPING') NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL
);

CREATE TABLE productoAcompanamiento (
    idProdAcomp CHAR(36) PRIMARY KEY,
    idProducto CHAR(36) NOT NULL,
    idAcompanamiento CHAR(36) NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,
    FOREIGN KEY (idProducto) REFERENCES producto(idProducto),
    FOREIGN KEY (idAcompanamiento) REFERENCES acompanamiento(idAcompanamiento)
);

CREATE TABLE pedido (
    idPedido CHAR(36) PRIMARY KEY,
    idEmpleado CHAR(36) NOT NULL,
    nombreCliente VARCHAR(100) NOT NULL,
    estado ENUM('ESPERA','PREPARACION','LISTO') NOT NULL,
    totalPagar DECIMAL(10,2) NOT NULL,
    tipoPedido ENUM('MESA','LLEVAR') NOT NULL,
    metodoPago ENUM('EFECTIVO','YAPE') NOT NULL,
    mesa INT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,
    FOREIGN KEY (idEmpleado) REFERENCES empleado(idEmpleado)
);

CREATE TABLE pedidoItem (
    idItem CHAR(36) PRIMARY KEY,
    idProducto CHAR(36) NOT NULL,
    idPedido CHAR(36) NOT NULL,
    cantidad INT NOT NULL,
    precioUnitarioFinal DECIMAL(10,2) NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,
    FOREIGN KEY (idProducto) REFERENCES producto(idProducto),
    FOREIGN KEY (idPedido) REFERENCES pedido(idPedido)
);

INSERT INTO empleado VALUES
('11111111-1111-1111-1111-111111111111','Pedro','123456',NOW(),NOW());

INSERT INTO categoria VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa','Jugos, Ensaladas y Sándwiches',NOW(),NOW()),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','Cafés',NOW(),NOW()),
('cccccccc-cccc-cccc-cccc-cccccccccccc','Frappes',NOW(),NOW()),
('dddddddd-dddd-dddd-dddd-dddddddddddd','Waffles',NOW(),NOW());

INSERT INTO acompanamiento VALUES
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee','Chocolate','SALSA',NOW(),NOW()),
('ffffffff-ffff-ffff-ffff-ffffffffffff','Manjar','SALSA',NOW(),NOW()),
('12121212-1212-1212-1212-121212121212','Miel','SALSA',NOW(),NOW()),
('13131313-1313-1313-1313-131313131313','Fresa','TOPPING',NOW(),NOW()),
('14141414-1414-1414-1414-141414141414','Plátano','TOPPING',NOW(),NOW()),
('15151515-1515-1515-1515-151515151515','Mango','TOPPING',NOW(),NOW()),
('16161616-1616-1616-1616-161616161616','Piña','TOPPING',NOW(),NOW()),
('17171717-1717-1717-1717-171717171717','Uva','TOPPING',NOW(),NOW()),
('18181818-1818-1818-1818-181818181818','Chantilly','TOPPING',NOW(),NOW()),
('19191919-1919-1919-1919-191919191919','Helado','TOPPING',NOW(),NOW()),
('20202020-2020-2020-2020-202020202020','Oreo','TOPPING',NOW(),NOW());

INSERT INTO producto VALUES
('p0000001-0000-0000-0000-000000000001','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',1,'Jugo de Fresa + Leche',6.00,NULL,NOW(),NOW()),
('p0000002-0000-0000-0000-000000000002','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',1,'Jugo de Mango + Leche',5.00,NULL,NOW(),NOW()),
('p0000003-0000-0000-0000-000000000003','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',1,'Jugo de Plátano + Leche',4.00,NULL,NOW(),NOW()),
('p0000004-0000-0000-0000-000000000004','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',1,'Jugo de Papaya + Leche',4.00,NULL,NOW(),NOW()),
('p0000005-0000-0000-0000-000000000005','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',1,'Jugo Surtido',7.00,NULL,NOW(),NOW()),
('p0000006-0000-0000-0000-000000000006','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',1,'Jugo Especial',8.00,NULL,NOW(),NOW()),
('p0000007-0000-0000-0000-000000000007','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',1,'Piña Colada',4.00,NULL,NOW(),NOW()),
('p0000008-0000-0000-0000-000000000008','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',1,'Limonada Frozen 1 L',5.00,NULL,NOW(),NOW()),
('p0000009-0000-0000-0000-000000000009','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',1,'Infusiones',2.00,NULL,NOW(),NOW()),
('p0000010-0000-0000-0000-000000000010','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',1,'Café Instantáneo',2.00,NULL,NOW(),NOW()),
('p0000011-0000-0000-0000-000000000011','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',1,'Ensalada FrutiMix',4.00,'Fresa, Uva, Plátano, Papaya, Yogur, Cereal',NOW(),NOW()),
('p0000012-0000-0000-0000-000000000012','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',1,'Ensalada Gourmet',6.00,'Fresa, Arándano, Plátano, Mango, Yogur Griego, Cereal',NOW(),NOW()),
('p0000054-0000-0000-0000-000000000054','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',1,'Sandwich Clásico',2.50,'Jamón y Queso.',NOW(),NOW()),
('p0000055-0000-0000-0000-000000000055','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',1,'Súper Sándwich',4.50,'Pollo desmenuzado, Lechuga y Tomate.',NOW(),NOW()),

('p0000020-0000-0000-0000-000000000020','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',1,'Café Americano',5.00,'Café espresso + agua caliente. Sabor suave.',NOW(),NOW()),
('p0000021-0000-0000-0000-000000000021','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',1,'Café Expresso',4.00,'Concentrado, intenso y con poca cantidad.',NOW(),NOW()),
('p0000022-0000-0000-0000-000000000022','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',1,'Cappuccino',8.00,'Café espresso + leche vaporizada + espuma de leche.',NOW(),NOW()),
('p0000023-0000-0000-0000-000000000023','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',1,'Afogato',6.00,'Café espresso caliente + bola de helado.',NOW(),NOW()),
('p0000024-0000-0000-0000-000000000024','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',1,'Mocaccino',9.00,'Café espresso + leche vaporizada + chocolate + crema.',NOW(),NOW()),
('p0000025-0000-0000-0000-000000000025','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',1,'Latte Macchiato',8.00,'Leche vaporizada + espuma de leche + toque de espresso.',NOW(),NOW()),
('p0000026-0000-0000-0000-000000000026','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',1,'Café Bombón',6.00,'Café espresso + leche condensada + Topping.',NOW(),NOW()),

('p0000030-0000-0000-0000-000000000030','cccccccc-cccc-cccc-cccc-cccccccccccc',1,'Frappuchino',8.00,'Café instantáneo, hielo, leche, azúcar, esencia vainilla y chantillí.',NOW(),NOW()),
('p0000031-0000-0000-0000-000000000031','cccccccc-cccc-cccc-cccc-cccccccccccc',1,'Frappé de Vainilla',8.00,'Hielo, leche, esencia de vainilla, azúcar y chantillí.',NOW(),NOW()),
('p0000032-0000-0000-0000-000000000032','cccccccc-cccc-cccc-cccc-cccccccccccc',1,'Frappe de Caramelo',8.00,'Hielo, leche, jarabe de caramelo, azúcar y chantillí.',NOW(),NOW()),
('p0000033-0000-0000-0000-000000000033','cccccccc-cccc-cccc-cccc-cccccccccccc',1,'Frappe de Moca',8.00,'Café instantáneo, leche, hielo, sirope de chocolate, azúcar y chantillí.',NOW(),NOW()),
('p0000034-0000-0000-0000-000000000034','cccccccc-cccc-cccc-cccc-cccccccccccc',1,'Frappe de Oreo',9.00,'Hielo, leche, galletas Oreo, esencia de vainilla, chocolate, azúcar y chantillí.',NOW(),NOW()),
('p0000035-0000-0000-0000-000000000035','cccccccc-cccc-cccc-cccc-cccccccccccc',1,'Frappe de Fresa',9.00,'Hielo, leche, leche condensada, pulpa de fresa, azúcar y chantillí.',NOW(),NOW()),
('p0000036-0000-0000-0000-000000000036','cccccccc-cccc-cccc-cccc-cccccccccccc',1,'Frappe de Arándano',10.00,'Hielo, leche, leche condensada, pulpa de arándano, azúcar y chantillí.',NOW(),NOW()),
('p0000037-0000-0000-0000-000000000037','cccccccc-cccc-cccc-cccc-cccccccccccc',1,'Frappé de Maracuyá',9.00,'Hielo, leche, leche condensada, pulpa de maracuyá, azúcar y chantillí.',NOW(),NOW()),
('p0000038-0000-0000-0000-000000000038','cccccccc-cccc-cccc-cccc-cccccccccccc',1,'Frappé de Durazno',10.00,'Hielo, leche, leche condensada, pulpa de durazno, azúcar y chantillí.',NOW(),NOW()),
('p0000039-0000-0000-0000-000000000039','cccccccc-cccc-cccc-cccc-cccccccccccc',1,'Frappé de Mango',9.00,'Hielo, leche, leche condensada, pulpa de mango, azúcar y chantillí.',NOW(),NOW()),
('p0000040-0000-0000-0000-000000000040','cccccccc-cccc-cccc-cccc-cccccccccccc',1,'Frappé de Taro',10.00,'Hielo, leche, polvo de taro, azúcar y chantillí.',NOW(),NOW()),
('p0000041-0000-0000-0000-000000000041','cccccccc-cccc-cccc-cccc-cccccccccccc',1,'Frappé de Matcha',10.00,'Hielo, leche, polvo de té verde matcha, azúcar y chantillí.',NOW(),NOW()),

('p0000050-0000-0000-0000-000000000050','dddddddd-dddd-dddd-dddd-dddddddddddd',1,'Waffle',8.00,'Incluye 1 salsa y 1 topping.',NOW(),NOW()),
('p0000051-0000-0000-0000-000000000051','dddddddd-dddd-dddd-dddd-dddddddddddd',1,'Waffle Sencillo',8.00,'Incluye 1 salsa y 2 toppings.',NOW(),NOW()),
('p0000052-0000-0000-0000-000000000052','dddddddd-dddd-dddd-dddd-dddddddddddd',1,'Waffle Especial',9.00,'Incluye 2 salsas y 3 toppings.',NOW(),NOW()),
('p0000053-0000-0000-0000-000000000053','dddddddd-dddd-dddd-dddd-dddddddddddd',1,'Waffle Supremo',10.00,'Incluye 3 salsas y 4 toppings.',NOW(),NOW());

INSERT INTO productoAcompanamiento
SELECT UUID(), p.idProducto, a.idAcompanamiento, NOW(), NOW()
FROM producto p
JOIN acompanamiento a
WHERE p.nombre LIKE 'Waffle%'
AND a.tipoAcompanamiento IN ('SALSA','TOPPING');