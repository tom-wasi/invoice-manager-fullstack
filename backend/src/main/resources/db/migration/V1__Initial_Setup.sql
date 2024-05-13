CREATE TABLE app_user (
    user_id VARCHAR(255) PRIMARY KEY,
    username TEXT NOT NULL,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    is_enabled BOOLEAN
);

CREATE TABLE company(
    company_id varchar(255) PRIMARY KEY,
    company_name TEXT NOT NULL,
    accountant_email TEXT,
    user_id VARCHAR(255)
);

CREATE TABLE invoice(
    id BIGSERIAL PRIMARY KEY,
    invoice_file_id VARCHAR(255),
    invoice_description VARCHAR(255),
    is_pending BOOLEAN,
    uploaded DATE,
    company_id INTEGER
);

CREATE TABLE confirmation_token(
    token_id BIGSERIAL PRIMARY KEY,
    created_date TIMESTAMP(6),
    user_id VARCHAR(255),
    confirmation_token varchar(255)
);