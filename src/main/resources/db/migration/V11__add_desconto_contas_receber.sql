ALTER TABLE contas_receber
    ADD COLUMN valor_desconto DECIMAL(15,2) NOT NULL DEFAULT 0.00,


    ADD CONSTRAINT chk_contas_receber_desconto CHECK (valor_desconto >= 0);