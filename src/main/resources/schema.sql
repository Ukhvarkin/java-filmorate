CREATE TABLE IF NOT EXISTS mpa(
mpa_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
name varchar (64),
description varchar (255)
);

CREATE TABLE IF NOT EXISTS genres(
genre_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL ,
name varchar (64)
);

CREATE TABLE IF NOT EXISTS films(
film_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL ,
name varchar (64),
description varchar (255),
release_date timestamp,
duration int,
mpa_id int REFERENCES mpa(mpa_id)
);

CREATE TABLE IF NOT EXISTS film_genres(
film_id int REFERENCES films (film_id) NOT NULL,
genre_id int REFERENCES genres (genre_id) NOT NULL,
PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users(
user_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
email varchar (64) NOT NULL ,
login varchar (64) NOT NULL,
name varchar (64),
birthday timestamp
);

CREATE TABLE IF NOT EXISTS user_friends(
user_id int REFERENCES users (user_id),
friend_id int REFERENCES users (user_id),
friendship_status_id int,
PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS likes(
user_id int REFERENCES users(user_id),
film_id int REFERENCES films (film_id),
PRIMARY KEY (user_id, film_id)
);