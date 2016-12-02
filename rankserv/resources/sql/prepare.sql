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
                                  PRIMARY KEY(id));

CREATE TABLE IF NOT EXISTS user_auth( user_id INTEGER,
                                      type INTEGER,
                                      data VARCHAR(64),
                                      FOREIGN KEY(user_id) REFERENCES users(id));
