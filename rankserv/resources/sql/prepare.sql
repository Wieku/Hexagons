CREATE TABLE IF NOT EXISTS games( id SERIAL,
                                  map_id varchar(36) NOT NULL,
                                  score bigint NOT NULL,
                                  nick varchar(24) NOT NULL,
                                  at bigint NOT NULL,
                                  PRIMARY KEY(id));

CREATE TABLE IF NOT EXISTS config(key varchar(36) PRIMARY KEY,
                                  value TEXT);

CREATE TABLE IF NOT EXISTS users( id SERIAL,
                                  nick VARCHAR(64),
                                  PRIMARY KEY(id),
                                  UNIQUE (nick));

CREATE TABLE IF NOT EXISTS user_auth( user_id INTEGER,
                                      type INTEGER,
                                      data VARCHAR(64),
                                      FOREIGN KEY(user_id) REFERENCES users(id));

CREATE MATERIALIZED VIEW IF NOT EXISTS top_scores AS
                                      SELECT nick, map_id, MAX(score) AS sc FROM games GROUP BY map_id, nick;

CREATE UNIQUE INDEX IF NOT EXISTS top_scores_key on top_scores (nick, map_id);

CREATE OR REPLACE FUNCTION update_scores()
  RETURNS trigger AS
  $$
    BEGIN
      REFRESH MATERIALIZED VIEW CONCURRENTLY top_scores;
      RETURN null;
    END;
  $$
  LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS test_trigger ON games;
CREATE TRIGGER test_trigger
  AFTER INSERT
    ON games
  FOR EACH STATEMENT
  EXECUTE PROCEDURE update_scores();
