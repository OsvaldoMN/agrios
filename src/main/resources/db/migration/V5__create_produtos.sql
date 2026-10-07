CREATE TABLE produtos (

    id INT NOT NULL AUTO_INCREMENT,

    nome VARCHAR(150) NOT NULL,

    descricao TEXT,

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT uk_produtos_nome
        UNIQUE (nome)
);