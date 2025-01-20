--DROP TABLE if EXISTS categories;

CREATE TABLE if not EXISTS categories(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    name  VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS events
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS compilations
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title    VARCHAR(255) NOT NULL,
    pinned   BOOLEAN      NOT NULL,
    event_id BIGINT,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
)