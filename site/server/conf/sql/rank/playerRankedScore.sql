SELECT SUM(maxScore) as totalScore FROM (SELECT MAX(score) AS maxScore FROM games WHERE user_id=? GROUP BY map_id) AS subTable;
