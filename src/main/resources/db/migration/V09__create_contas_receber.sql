CREATE TABLE contas_receber (

    id INT NOT NULL AUTO_INCREMENT,
    cliente_id INT NOT NULL,
    ordem_servico_id INT,
    origem VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ABERTA',
    descricao VARCHAR(255) NOT NULL,
    valor DECIMAL(15,2) NOT NULL,
    data_emissao DATE NOT NULL,
    data_vencimento DATE NOT NULL,
    observacoes TEXT,
    criado_em DATETIME(6)NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT fk_contas_receber_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id),

    CONSTRAINT fk_contas_receber_os
        FOREIGN KEY (ordem_servico_id)
        REFERENCES ordens_servico(id),

    CONSTRAINT uk_contas_receber_os
        UNIQUE (ordem_servico_id),

    CONSTRAINT chk_contas_receber_valor
        CHECK (valor > 0),

    CONSTRAINT chk_contas_receber_origem
        CHECK (
            origem IN (
                'ORDEM_SERVICO',
                'MANUAL'
            )
        ),

    CONSTRAINT chk_contas_receber_status
        CHECK (
            status IN (
                'ABERTA',
                'PARCIAL',
                'ATRASADA',
                'QUITADA',
                'CANCELADA'
            )
        )
);