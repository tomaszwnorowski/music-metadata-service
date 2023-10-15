-- track domain
CREATE TABLE track_genre (id BIGINT PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE);

CREATE TABLE track_track (
  id BIGINT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  length INTERVAL NOT NULL,
  artist_id BIGINT NOT NULL,
  genre_id BIGINT REFERENCES track_genre (id)
);

INSERT INTO
  track_genre (id, name)
VALUES
  (500933453575241972, 'POP'),
  (500933453583630585, 'ROCK');

CREATE INDEX track_track_title_idx ON track_track(title);

CREATE INDEX track_genre_name_idx ON track_genre(name);

CREATE UNIQUE INDEX track_track_artist_id_title_key ON track_track(artist_id, title);

CREATE INDEX track_track_artist_id_track_id_idx ON track_track(artist_id, id);