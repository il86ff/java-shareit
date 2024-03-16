--DROP TABLE users CASCADE;
--DROP TABLE items CASCADE;
--DROP TABLE booking CASCADE;
--DROP TABLE comment CASCADE;
--DROP TABLE request CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email varchar(320) UNIQUE NOT NULL,
    name_user varchar(100) NOT NULL);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name_item varchar(100),
    description varchar(100),
    available BOOLEAN,
    user_id BIGINT,
    request_id BIGINT,
    booking_id BIGINT);

CREATE TABLE IF NOT EXISTS booking (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id BIGINT,
    booker_id BIGINT,
    booking_status varchar(100));

CREATE TABLE IF NOT EXISTS comment (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text varchar(100),
    item_id BIGINT,
    author_name varchar(100),
    created TIMESTAMP WITHOUT TIME ZONE);

CREATE TABLE IF NOT EXISTS request (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description varchar(100),
    requester_id BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE);