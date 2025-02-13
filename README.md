# senior_backend database

# User database

-- Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE
);

-- User Profiles Table
CREATE TABLE user_profiles (
    id SERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,  -- One-to-one relationship with users
    username VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    bio TEXT,
    link VARCHAR(2083),
    location VARCHAR(255),
    profile_image VARCHAR(2083),
    CONSTRAINT fk_user_profiles FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tokens Table
CREATE TABLE tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    UNIQUE (token, user_id),
    CONSTRAINT fk_user_tokens FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- User Follows Table
CREATE TABLE user_follows (
    follower_id BIGINT NOT NULL,
    followed_id BIGINT NOT NULL,
    date_followed TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, followed_id),
    CONSTRAINT fk_follower FOREIGN KEY (follower_id) REFERENCES user_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_followed FOREIGN KEY (followed_id) REFERENCES user_profiles(id) ON DELETE CASCADE
);




# Interaction database

CREATE TABLE review (
    id BIGSERIAL PRIMARY KEY, 
    user_id BIGINT NOT NULL,  
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5), 
    comment TEXT,            
    spotify_id VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_review UNIQUE (user_id, spotify_id)
);


CREATE TABLE likes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    spotify_id VARCHAR(64) NOT NULL,
    type VARCHAR(50) NOT NULL,  -- New column for type
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE review_likes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    review_id BIGINT NOT NULL,  -- Changed from VARCHAR(64) to BIGINT
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review FOREIGN KEY (review_id) REFERENCES review(id) ON DELETE CASCADE
);


CREATE TABLE favorite (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    spotify_id VARCHAR(64) NOT NULL,
    type VARCHAR(50) NOT NULL,  -- New column for type
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE review_comments (
    id BIGSERIAL PRIMARY KEY,
    review_id BIGINT NOT NULL REFERENCES review (id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL,
    comment TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- Create the conversations table
CREATE TABLE conversations (
    id BIGSERIAL PRIMARY KEY,
    user1 BIGINT NOT NULL,
    user2 BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_pair UNIQUE (user1, user2) -- Unique constraint on ordered pairs
);

-- Create the function to swap user1 and user2
CREATE OR REPLACE FUNCTION enforce_user_order()
RETURNS TRIGGER AS $$
DECLARE
    temp BIGINT;  -- Declare temp variable BEFORE BEGIN
BEGIN
    IF NEW.user1 > NEW.user2 THEN
        -- Swap user1 and user2 to ensure order
        temp := NEW.user1;
        NEW.user1 := NEW.user2;
        NEW.user2 := temp;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the trigger that calls the function before insert
CREATE TRIGGER before_insert_conversations
BEFORE INSERT ON conversations
FOR EACH ROW
EXECUTE FUNCTION enforce_user_order();

# Cassandra

CREATE KEYSPACE IF NOT EXISTS chat_system WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};

use chat_system;

CREATE TABLE chat_messages (
    conversation_id BIGINT,        
    message_id TIMEUUID,           
    sender_id BIGINT,             
    receiver_id BIGINT,          
    message TEXT,               
    sent_at TIMESTAMP,            
    PRIMARY KEY (conversation_id, message_id)
) WITH CLUSTERING ORDER BY (message_id DESC);

