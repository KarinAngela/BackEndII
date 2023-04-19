create table produtos (
    id int auto_increment not null,
    nome varchar(100) not null,
    preco_unitario real not null,
    categoria enum('COMBUSTIVEL', 'MANUAL', 'ELETRICO') not null,
    descricao varchar(10000) not  null,
    status varchar(150) not null,
    primary key(id)
);