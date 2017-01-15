SELECT users.nick, MAX(score) AS sc FROM games
  JOIN users ON games.user_id = users.id
  WHERE map_id=?::uuid GROUP BY users.nick ORDER BY sc DESC LIMIT ?;
