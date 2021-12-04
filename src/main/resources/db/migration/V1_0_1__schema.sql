CREATE SCHEMA IF NOT EXISTS hotp;

-- CREATE product category
CREATE TABLE hotp.product_category
(
    uuid   UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    name   TEXT             NOT NULL,
    parent UUID,
    CONSTRAINT fk_parent_product_category FOREIGN KEY (parent) REFERENCES hotp.product_category (uuid)
);

CREATE UNIQUE INDEX unique_product_category_name_idx ON hotp.product_category (lower(name));
CREATE INDEX product_category_parent_idx ON hotp.product_category (parent);

-- CREATE product menu
CREATE TYPE hotp.amount_unit AS ENUM ('g', 'ml');
CREATE TABLE hotp.product
(
    uuid        uuid primary key not null default gen_random_uuid(),
    name        text             not null,
    description text,
    category    uuid             not null,
    price       decimal          not null,
    amount      int              not null,
    amount_unit text,
    CONSTRAINT fk_category_product FOREIGN KEY (category) REFERENCES hotp.product_category (uuid),
    CONSTRAINT product_amount_unit_check CHECK ( cast(amount_unit as hotp.amount_unit) IS NOT NULL)
);

CREATE UNIQUE INDEX unique_product_name_idx ON hotp.product (lower(name));

-- CREATE user
CREATE TABLE hotp.client_user
(
    uuid  uuid primary key   not null default gen_random_uuid(),
    name  text               not null,
    phone varchar(16) unique not null
);

-- CREATE address
CREATE TABLE hotp.address
(
    uuid        uuid primary key not null default gen_random_uuid(),
    client_user uuid             not null,
    address     text             not null,
    CONSTRAINT user_address_unique UNIQUE (client_user, address),
    CONSTRAINT fk_client_user_address FOREIGN KEY (client_user) REFERENCES hotp.client_user (uuid)
);

-- CREATE order
CREATE TYPE hotp.order_state AS ENUM ('NEW', 'ACCEPTED', 'DELIVERY', 'COMPLETED', 'CANCELED');
CREATE TABLE hotp.client_order
(
    uuid              uuid primary key not null default gen_random_uuid(),
    client            uuid             not null,
    number            bigserial,
    state             text not null default 'NEW',
    creation_date     timestamp        not null default now(),
    last_updated_date timestamp        not null default now(),
    client_comment    text,
    service_comment   text,
    CONSTRAINT fk_client_order_client_user FOREIGN KEY (client) REFERENCES hotp.client_user (uuid),
    CONSTRAINT product_amount_unit_check CHECK ( cast(state as hotp.order_state) IS NOT NULL)
);
CREATE UNIQUE INDEX unique_order_number_idx ON hotp.client_order (number);
CREATE INDEX order_state_idx ON hotp.client_order (state);

CREATE TABLE hotp.order_product
(
    client_order uuid not null,
    product      uuid not null,
    quantity     int  not null,

    PRIMARY KEY (client_order, product),
    CONSTRAINT fk_client_order_order_products FOREIGN KEY (client_order) REFERENCES hotp.client_order (uuid),
    CONSTRAINT fk_product_order_products FOREIGN KEY (product) REFERENCES hotp.product (uuid)
)
