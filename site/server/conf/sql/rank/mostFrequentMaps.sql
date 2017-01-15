SELECT maps.name, COUNT(map_id) FROM games
  JOIN maps ON games.map_id=maps.uuid
  WHERE user_id=? GROUP BY maps.name ORDER BY COUNT(map_id) DESC LIMIT ?
