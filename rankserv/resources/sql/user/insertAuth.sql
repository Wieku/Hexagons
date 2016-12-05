WITH U AS (INSERT INTO users (nick) VALUES (CONCAT('u', lastval())) RETURNING id)
	INSERT INTO user_auth VALUES((SELECT id FROM U), ?, ?) RETURNING user_id
