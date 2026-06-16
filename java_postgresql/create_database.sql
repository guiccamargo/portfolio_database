CREATE TABLE IF NOT EXISTS produtos
(
    id serial NOT NULL,
    nome character varying(50) NOT NULL,
    preco decimal NOT NULL,
    estoque integer NOT NULL,
    CONSTRAINT produtos_pkey PRIMARY KEY (id)
)
