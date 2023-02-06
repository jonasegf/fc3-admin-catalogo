CREATE TABLE category (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name varchar(255) NOT NULL,
    description varchar(4000),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) not null,
    updated_at DATETIME(6) not null,
    deleted_at DATETIME(6)
)