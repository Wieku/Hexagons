SELECT maps.uuid, ranked_hash FROM ranked_maps
  JOIN maps ON maps.id = ranked_maps.map_id WHERE ranked_maps.id = ?
