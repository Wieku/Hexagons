SELECT COUNT(DISTINCT nick) as rank FROM games WHERE map_id=?
  AND score > (SELECT MAX(score) as sc FROM games WHERE nick=?
                 AND map_id=?)
