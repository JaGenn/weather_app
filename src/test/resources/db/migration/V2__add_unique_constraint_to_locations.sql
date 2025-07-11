ALTER TABLE locations
ADD CONSTRAINT unique_user_location
UNIQUE (user_id, latitude, longitude);