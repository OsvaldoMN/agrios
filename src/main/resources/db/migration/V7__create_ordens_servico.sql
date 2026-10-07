CREATE TABLE ordens_servico (

    id INT NOT NULL AUTO_INCREMENT,

    cliente_id INT NOT NULL,

    fazenda_id INT NOT NULL,

    servico_id INT NOT NULL,

    produto_id INT,

    data_inicio DATE NOT NULL,

    data_fim DATE,

    status VARCHAR(20) NOT NULL DEFAULT 'ABERTA',

    hectares DECIMAL(12,2),

    valor_total DECIMAL(15,2) NOT NULL DEFAULT 0.00,

    observacoes TEXT,

    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT fk_os_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id),

    CONSTRAINT fk_os_fazenda
        FOREIGN KEY (fazenda_id)
        REFERENCES fazendas(id),

    CONSTRAINT fk_os_servico
        FOREIGN KEY (servico_id)
        REFERENCES servicos(id),

    CONSTRAINT fk_os_produto
        FOREIGN KEY (produto_id)
        REFERENCES produtos(id),

    CONSTRAINT chk_os_valor_total
        CHECK (valor_total >= 0),

    CONSTRAINT chk_os_status_datas CHECK (
        status IN ('ABERTA','FINALIZADA','CANCELADA')
            AND
            (data_fim IS NULL OR data_fim >= data_inicio)
            AND
            (status <> 'FINALIZADA' OR data_fim IS NOT NULL)
        )
);