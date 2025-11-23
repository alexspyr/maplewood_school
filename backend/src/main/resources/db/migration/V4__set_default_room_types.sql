-- Set default room types for specializations that have NULL room_type_id
-- This ensures all specializations have a valid room type for scheduling

-- Mathematics -> classroom (general)
UPDATE specializations SET room_type_id = 1 WHERE id = 1 AND room_type_id IS NULL;

-- English -> classroom (general)
UPDATE specializations SET room_type_id = 1 WHERE id = 2 AND room_type_id IS NULL;

-- Science -> science_lab
UPDATE specializations SET room_type_id = 2 WHERE id = 3 AND room_type_id IS NULL;

-- Social_Studies -> classroom (general)
UPDATE specializations SET room_type_id = 1 WHERE id = 4 AND room_type_id IS NULL;

-- Arts -> art_studio
UPDATE specializations SET room_type_id = 3 WHERE id = 5 AND room_type_id IS NULL;

-- Music -> music_room
UPDATE specializations SET room_type_id = 6 WHERE id = 6 AND room_type_id IS NULL;

-- Physical_Education -> gym
UPDATE specializations SET room_type_id = 4 WHERE id = 7 AND room_type_id IS NULL;

-- Computer_Science -> computer_lab
UPDATE specializations SET room_type_id = 5 WHERE id = 8 AND room_type_id IS NULL;

-- Foreign_Language -> classroom (general)
UPDATE specializations SET room_type_id = 1 WHERE id = 9 AND room_type_id IS NULL;

