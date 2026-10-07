CREATE TABLE maquinas_os (

    id INT NOT NULL AUTO_INCREMENT,

    ordem_servico_id INT NOT NULL,

    maquina_id INT NOT NULL,

    valor DECIMAL(15,2) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_maquinas_os_ordem
        FOREIGN KEY (ordem_servico_id)
        REFERENCES ordens_servico(id),

    CONSTRAINT fk_maquinas_os_maquina
        FOREIGN KEY (maquina_id)
        REFERENCES maquinas(id),

    CONSTRAINT uk_maquinas_os_ordem_maquina
        UNIQUE (
            ordem_servico_id,
            maquina_id
        ),

    CONSTRAINT chk_maquinas_os_valor
        CHECK (valor > 0)
);