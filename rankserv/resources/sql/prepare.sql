CREATE TABLE IF NOT EXISTS config(key varchar(36) PRIMARY KEY,
                                  value TEXT);

CREATE TABLE IF NOT EXISTS users( id SERIAL,
                                  nick VARCHAR(64),
                                  PRIMARY KEY(id),
                                  UNIQUE (nick));

CREATE TABLE IF NOT EXISTS games( id SERIAL,
                                  map_id UUID NOT NULL,
                                  score BIGINT NOT NULL,
                                  user_id INTEGER,
                                  at BIGINT NOT NULL,
                                  PRIMARY KEY(id),
                                  FOREIGN KEY(user_id) REFERENCES users(id));

CREATE TABLE IF NOT EXISTS user_auth( user_id INTEGER,
                                      type INTEGER,
                                      data VARCHAR(64),
                                      FOREIGN KEY(user_id) REFERENCES users(id));

CREATE TABLE IF NOT EXISTS maps( id SERIAL,
                                 uuid UUID,
                                 name VARCHAR(128));

CREATE MATERIALIZED VIEW IF NOT EXISTS top_scores AS
                                      SELECT user_id, map_id, MAX(score) AS sc FROM games GROUP BY map_id, user_id;

CREATE UNIQUE INDEX IF NOT EXISTS top_scores_key on top_scores (user_id, map_id);

CREATE OR REPLACE FUNCTION update_scores()
  RETURNS trigger AS
  $$
    BEGIN
      REFRESH MATERIALIZED VIEW CONCURRENTLY top_scores;
      RETURN null;
    END;
  $$
  LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS update_cached ON games;
CREATE TRIGGER update_cached
  AFTER INSERT
    ON games
  FOR EACH STATEMENT
  EXECUTE PROCEDURE update_scores();
