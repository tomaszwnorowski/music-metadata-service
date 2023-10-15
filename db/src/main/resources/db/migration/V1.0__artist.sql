-- artist domain
CREATE TABLE artist_artist (id BIGINT PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE);

CREATE TABLE artist_alias (
  id BIGINT PRIMARY KEY,
  alias VARCHAR(255) NOT NULL UNIQUE,
  artist_id BIGINT REFERENCES artist_artist (id)
);

CREATE INDEX artist_artist_name_idx ON artist_artist(name);

CREATE INDEX artist_alias_alias_idx ON artist_alias(alias);