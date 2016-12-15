WITH query_ranks AS (SELECT nick, SUM(maxScore) AS bestSum FROM
    (SELECT nick, map_id, MAX(score) AS maxScore FROM games GROUP BY map_id, nick) AS subTable GROUP BY nick)
    SELECT COUNT(DISTINCT nick)+1 as rank FROM query_ranks WHERE bestSum > (SELECT bestSum FROM query_ranks WHERE nick=?);