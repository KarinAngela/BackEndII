create table usuarios (
    id int auto_increment not null,
    nome varchar(255) not null,
    email varchar(255) not null unique,
    login varchar(255) not null unique,
    senha varchar(255) not null,
    data_cadastro date default (current_date) not null,
    nivel_acesso enum('ADMINISTRADOR', 'COLABORADOR') not null,
    primary key(id)
);
