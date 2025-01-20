CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    name  VARCHAR(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(2000) NOT NULL,
    description        VARCHAR(7000) NOT NULL,
    event_date         TIMESTAMP,
    lat                DOUBLE PRECISION,
    lon                DOUBLE PRECISION,
    paid               BOOLEAN NOT NULL,
    participant_limit  INTEGER NOT NULL,
    request_moderation BOOLEAN NOT NULL,
    state              VARCHAR(20) NOT NULL,
    title              VARCHAR(120) NOT NULL,
    created_on         TIMESTAMP NOT NULL,
    published_on       TIMESTAMP,
    initiator_id       BIGINT NOT NULL,
    category_id        BIGINT,

    CONSTRAINT fk_initiator FOREIGN KEY (initiator_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_category FOREIGN KEY (category_id)
        REFERENCES categories (id)
);
CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created      TIMESTAMP NOT NULL,
    event_id     BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status       VARCHAR(20) NOT NULL,

    CONSTRAINT fk_event FOREIGN KEY (event_id)
        REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_requester FOREIGN KEY (requester_id)
        REFERENCES users (id) ON DELETE CASCADE
);