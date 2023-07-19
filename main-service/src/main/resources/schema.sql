DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS category CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilations_events CASCADE;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(250) NOT NULL,
  email VARCHAR(254) NOT NULL,
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS category (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  CONSTRAINT UQ_CATEGORY_NAME UNIQUE (name)

);

CREATE TABLE IF NOT EXISTS events (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  title VARCHAR(120) NOT NULL,
  annotation VARCHAR(2000) NOT NULL,
  description VARCHAR(7000) NOT NULL,
  cat_id BIGINT REFERENCES category(id) ON DELETE RESTRICT,
  user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
  event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  published_on TIMESTAMP,
  location_lat FLOAT NOT NULL,
  location_lon FLOAT NOT NULL,
  paid boolean NOT NULL,
  participant_limit INT NOT NULL,
  request_moderation boolean NOT NULL,
  confirmed_requests INT,
  state VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  requestor_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
  event_id BIGINT REFERENCES events(id) ON DELETE CASCADE,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  status VARCHAR NOT NULL,
  UNIQUE (requestor_id, event_id)
);

CREATE TABLE IF NOT EXISTS compilations (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  title VARCHAR(50) NOT NULL,
  pinned boolean
);

CREATE TABLE IF NOT EXISTS compilations_events (
  event_id BIGINT REFERENCES events(id) ON DELETE CASCADE,
  compilation_id BIGINT REFERENCES compilations(id) ON DELETE CASCADE,
  PRIMARY KEY (event_id, compilation_id)
);