-- engine domain
CREATE TABLE engine_artist_of_the_day (
  id BIGINT PRIMARY KEY,
  artist_id BIGINT,
  date DATE NOT NULL
);

CREATE INDEX engine_artist_of_the_day_date_idx ON engine_artist_of_the_day (date);