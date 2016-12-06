SELECT nick, MAX(score) as sc FROM games WHERE map_id=? GROUP BY nick ORDER BY sc DESC LIMIT ?;
