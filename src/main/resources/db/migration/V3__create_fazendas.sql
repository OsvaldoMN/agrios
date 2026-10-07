CREATE TABLE fazendas (

    id INT NOT NULL AUTO_INCREMENT,

    cliente_id INT NOT NULL,

    nome VARCHAR(200) NOT NULL,

    inscricao_estadual VARCHAR(30),

    indicador_ie VARCHAR(30) NOT NULL,

    cep VARCHAR(8),

    estado VARCHAR(2),

    cidade VARCHAR(150),

    bairro VARCHAR(150),

    logradouro VARCHAR(200),

    numero VARCHAR(30),

    complemento VARCHAR(200),

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    observacoes TEXT,

    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_fazendas_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id),

    CONSTRAINT uk_fazenda_ie_estado
        UNIQUE (
            estado,
            inscricao_estadual
        )
);


CREATE INDEX idx_fazendas_cliente
    ON fazendas(cliente_id);