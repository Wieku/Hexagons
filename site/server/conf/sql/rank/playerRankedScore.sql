SELECT SUM(maxScore) as totalScore FROM (SELECT MAX(score) AS maxScore FROM games WHERE nick=? GROUP BY map_id) AS subTable;
