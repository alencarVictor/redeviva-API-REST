CREATE TABLE tb_loja (
    cd_loja       NUMBER(10)    NOT NULL,
    nm_loja       VARCHAR2(100) NOT NULL,
    ds_endereco   VARCHAR2(200) NOT NULL,
    nr_latitude   NUMBER(9,6)   NOT NULL,
    nr_longitude  NUMBER(9,6)   NOT NULL,
    ds_email      VARCHAR2(120) NOT NULL,
    CONSTRAINT pk_tb_loja        PRIMARY KEY (cd_loja),
    CONSTRAINT uq_tb_loja_email  UNIQUE (ds_email),
    CONSTRAINT ck_tb_loja_lat    CHECK (nr_latitude  BETWEEN -90  AND 90),
    CONSTRAINT ck_tb_loja_lon    CHECK (nr_longitude BETWEEN -180 AND 180)
);

CREATE TABLE tb_cliente (
    cd_cliente            NUMBER(10)    NOT NULL,
    nm_cliente            VARCHAR2(100) NOT NULL,
    nr_cpf                CHAR(11)      NOT NULL,
    nr_cep                CHAR(8)       NOT NULL,
    nr_latitude           NUMBER(9,6)   NOT NULL,
    nr_longitude          NUMBER(9,6)   NOT NULL,
    ds_origem             VARCHAR2(10)  NOT NULL,
    st_validacao          VARCHAR2(10)  DEFAULT 'PENDENTE' NOT NULL,
    dt_validade_cadastro  DATE,
    CONSTRAINT pk_tb_cliente         PRIMARY KEY (cd_cliente),
    CONSTRAINT uq_tb_cliente_cpf     UNIQUE (nr_cpf),
    CONSTRAINT ck_tb_cliente_cpf     CHECK (REGEXP_LIKE(nr_cpf, '^[0-9]{11}$')),
    CONSTRAINT ck_tb_cliente_cep     CHECK (REGEXP_LIKE(nr_cep, '^[0-9]{8}$')),
    CONSTRAINT ck_tb_cliente_origem  CHECK (ds_origem IN ('CADUNICO', 'CONVITE')),
    CONSTRAINT ck_tb_cliente_status  CHECK (st_validacao IN ('PENDENTE', 'VALIDADO', 'RECUSADO')),
    CONSTRAINT ck_tb_cliente_validad CHECK (st_validacao <> 'VALIDADO' OR dt_validade_cadastro IS NOT NULL)
);

CREATE TABLE tb_produto (
    cd_produto    NUMBER(10)    NOT NULL,
    cd_loja       NUMBER(10)    NOT NULL,
    cd_barras     VARCHAR2(14)  NOT NULL,
    nm_produto    VARCHAR2(100) NOT NULL,
    ds_categoria  VARCHAR2(20)  NOT NULL,
    nr_peso_kg    NUMBER(6,3)   NOT NULL,
    CONSTRAINT pk_tb_produto       PRIMARY KEY (cd_produto),
    CONSTRAINT fk_tb_produto_loja  FOREIGN KEY (cd_loja) REFERENCES tb_loja (cd_loja),
    CONSTRAINT uq_tb_produto_barra UNIQUE (cd_loja, cd_barras),
    CONSTRAINT ck_tb_produto_cat   CHECK (ds_categoria IN
        ('HORTIFRUTI', 'PADARIA', 'LATICINIOS', 'CARNES', 'FRIOS', 'MERCEARIA', 'CONGELADOS')),
    CONSTRAINT ck_tb_produto_peso  CHECK (nr_peso_kg > 0)
);

CREATE TABLE tb_oferta (
    cd_oferta         NUMBER(10)   NOT NULL,
    cd_loja           NUMBER(10)   NOT NULL,
    cd_produto        NUMBER(10)   NOT NULL,
    qt_disponivel     NUMBER(5)    NOT NULL,
    vl_preco_sistema  NUMBER(8,2)  NOT NULL,
    vl_preco_rede     NUMBER(8,2)  NOT NULL,
    dt_validade       DATE         NOT NULL,
    st_oferta         VARCHAR2(10) DEFAULT 'ATIVA' NOT NULL,
    CONSTRAINT pk_tb_oferta          PRIMARY KEY (cd_oferta),
    CONSTRAINT fk_tb_oferta_loja     FOREIGN KEY (cd_loja)    REFERENCES tb_loja (cd_loja),
    CONSTRAINT fk_tb_oferta_produto  FOREIGN KEY (cd_produto) REFERENCES tb_produto (cd_produto),
    CONSTRAINT ck_tb_oferta_qtd      CHECK (qt_disponivel >= 0),
    CONSTRAINT ck_tb_oferta_preco_si CHECK (vl_preco_sistema > 0),
    CONSTRAINT ck_tb_oferta_preco_re CHECK (vl_preco_rede > 0),
    CONSTRAINT ck_tb_oferta_menor    CHECK (vl_preco_rede < vl_preco_sistema),
    CONSTRAINT ck_tb_oferta_status   CHECK (st_oferta IN ('ATIVA', 'ESGOTADA', 'ENCERRADA'))
);

CREATE TABLE tb_reserva (
    cd_reserva          NUMBER(10)   NOT NULL,
    cd_cliente          NUMBER(10)   NOT NULL,
    cd_loja             NUMBER(10)   NOT NULL,
    cd_retirada         VARCHAR2(8)  NOT NULL,
    dt_reserva          DATE         DEFAULT SYSDATE NOT NULL,
    dt_expiracao        DATE         NOT NULL,
    st_reserva          VARCHAR2(10) DEFAULT 'ATIVA' NOT NULL,
    ds_forma_pagamento  VARCHAR2(20) NOT NULL,
    CONSTRAINT pk_tb_reserva          PRIMARY KEY (cd_reserva),
    CONSTRAINT fk_tb_reserva_cliente  FOREIGN KEY (cd_cliente) REFERENCES tb_cliente (cd_cliente),
    CONSTRAINT fk_tb_reserva_loja     FOREIGN KEY (cd_loja)    REFERENCES tb_loja (cd_loja),
    CONSTRAINT uq_tb_reserva_retirada UNIQUE (cd_retirada),
    CONSTRAINT ck_tb_reserva_24h      CHECK (dt_expiracao = dt_reserva + 1),
    CONSTRAINT ck_tb_reserva_status   CHECK (st_reserva IN ('ATIVA', 'RETIRADA', 'EXPIRADA', 'CANCELADA')),
    CONSTRAINT ck_tb_reserva_pgto     CHECK (ds_forma_pagamento IN ('PAGAR_NA_RETIRADA', 'PIX_APP'))
);

CREATE TABLE tb_item_reserva (
    cd_item      NUMBER(10)  NOT NULL,
    cd_reserva   NUMBER(10)  NOT NULL,
    cd_oferta    NUMBER(10)  NOT NULL,
    qt_item      NUMBER(5)   NOT NULL,
    vl_unitario  NUMBER(8,2) NOT NULL,
    CONSTRAINT pk_tb_item_reserva         PRIMARY KEY (cd_item),
    CONSTRAINT fk_tb_item_reserva_reserva FOREIGN KEY (cd_reserva) REFERENCES tb_reserva (cd_reserva),
    CONSTRAINT fk_tb_item_reserva_oferta  FOREIGN KEY (cd_oferta)  REFERENCES tb_oferta (cd_oferta),
    CONSTRAINT uq_tb_item_reserva_res_ofe UNIQUE (cd_reserva, cd_oferta),
    CONSTRAINT ck_tb_item_reserva_qtd     CHECK (qt_item > 0),
    CONSTRAINT ck_tb_item_reserva_valor   CHECK (vl_unitario > 0)
);

CREATE TABLE tb_confirmacao_retirada (
    cd_confirmacao        NUMBER(10)    NOT NULL,
    cd_reserva            NUMBER(10)    NOT NULL,
    dt_retirada           DATE          DEFAULT SYSDATE NOT NULL,
    nm_atendente          VARCHAR2(60)  NOT NULL,
    vl_total_caixa        NUMBER(10,2)  NOT NULL,
    vl_desconto_aplicado  NUMBER(10,2)  NOT NULL,
    CONSTRAINT pk_tb_confirmacao_retirada PRIMARY KEY (cd_confirmacao),
    CONSTRAINT fk_tb_confirmacao_reserva  FOREIGN KEY (cd_reserva) REFERENCES tb_reserva (cd_reserva),
    CONSTRAINT uq_tb_confirmacao_reserva  UNIQUE (cd_reserva),
    CONSTRAINT ck_tb_confirmacao_total    CHECK (vl_total_caixa > 0),
    CONSTRAINT ck_tb_confirmacao_desc     CHECK (vl_desconto_aplicado >= 0 AND vl_desconto_aplicado < vl_total_caixa)
);

CREATE SEQUENCE sq_tb_loja         START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE sq_tb_cliente      START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE sq_tb_produto      START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE sq_tb_oferta       START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE sq_tb_reserva      START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE sq_tb_item_reserva START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE sq_tb_confirmacao_retirada START WITH 100 INCREMENT BY 1 NOCACHE;

