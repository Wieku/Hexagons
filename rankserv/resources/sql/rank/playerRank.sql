SELECT COUNT(DISTINCT user_id) as rank FROM games WHERE map_id=?::uuid
  AND score > (SELECT MAX(score) as sc FROM games WHERE user_id=?
                 AND map_id=?::uuid)
