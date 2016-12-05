SELECT users.id, users.nick FROM user_auth
    JOIN users ON user_auth.user_id = users.id
    WHERE user_auth.type=? AND user_auth.data=? LIMIT 1;
