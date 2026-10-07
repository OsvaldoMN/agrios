CREATE TABLE maquinas (

    id INT NOT NULL AUTO_INCREMENT,

    nome VARCHAR(150) NOT NULL,

    identificacao VARCHAR(80),

    descricao TEXT,

    marca VARCHAR(100),

    modelo VARCHAR(100),

    ano_fabricacao INT,

    placa VARCHAR(10),

    numero_serie VARCHAR(100),

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT uk_maquinas_identificacao
        UNIQUE (identificacao)
);