WITH query_ranks AS (SELECT user_id, SUM(maxScore) AS bestSum FROM
    (SELECT user_id, map_id, MAX(score) AS maxScore FROM games GROUP BY map_id, user_id) AS subTable GROUP BY user_id)
    SELECT COUNT(DISTINCT user_id)+1 as rank FROM query_ranks WHERE bestSum > (SELECT bestSum FROM query_ranks WHERE user_id=?);