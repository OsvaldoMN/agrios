CREATE TABLE clientes (

    id INT NOT NULL AUTO_INCREMENT,

    tipo_pessoa VARCHAR(20) NOT NULL,

    nome VARCHAR(150),

    razao_social VARCHAR(200),

    nome_fantasia VARCHAR(200),

    documento VARCHAR(14) NOT NULL,

    data_nascimento DATE,

    email VARCHAR(150),

    contato VARCHAR(11),

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    observacoes TEXT,

    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uk_clientes_documento
        UNIQUE (documento),

    CONSTRAINT uk_clientes_email
        UNIQUE (email)
);
