CREATE TABLE recebimentos (

    id INT NOT NULL AUTO_INCREMENT,
    conta_receber_id INT NOT NULL,
    valor DECIMAL(15,2) NOT NULL,
    forma_pagamento VARCHAR(30) NOT NULL,
    data_pagamento DATE NOT NULL,
    observacoes TEXT,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT fk_recebimentos_conta
        FOREIGN KEY (conta_receber_id)
        REFERENCES contas_receber(id),

    CONSTRAINT chk_recebimentos_valor
        CHECK (valor > 0),

    CONSTRAINT chk_recebimentos_forma
        CHECK (
            forma_pagamento IN (
                'DINHEIRO',
                'PIX',
                'CARTAO_CREDITO',
                'CARTAO_DEBITO',
                'BOLETO',
                'TRANSFERENCIA',
                'OUTRO'
            )
        )
);