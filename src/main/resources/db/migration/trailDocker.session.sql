-- SELECT current_database();
-- SELECT version();
-- SELECT * FROM email_verification_tokens 
-- ORDER BY token_id DESC;

-- ALTER TABLE users
-- ADD COLUMN preferred_name VARCHAR(100),
-- ADD COLUMN onboarding_completed BOOLEAN DEFAULT FALSE;
SELECT * FROM users;
-- CREATE TABLE user_profile (
--     user_id INTEGER PRIMARY KEY REFERENCES users(user_id),
--     home_type VARCHAR(20), -- RENT / OWN / OTHER
--     created_at TIMESTAMP DEFAULT now(),
--     updated_at TIMESTAMP DEFAULT now()
-- );
-- CREATE TABLE user_onboarding_source (
--     user_id INTEGER PRIMARY KEY REFERENCES users(user_id),
--     source VARCHAR(50),          -- GOOGLE / FRIEND / SOCIAL / AD / OTHER
--     source_other VARCHAR(255)
-- );
-- CREATE TABLE user_onboarding_selections (
--     id SERIAL PRIMARY KEY,
--     user_id INTEGER REFERENCES users(user_id),
--     category VARCHAR(50),   -- GOAL, DEBT, TRANSPORT, SPENDING, DREAM, LIFESTYLE
--     value VARCHAR(100),     -- CREDIT_CARD, RENT, GROCERY, etc
--     frequency VARCHAR(20),  -- MONTHLY, YEARLY, NULL
--     created_at TIMESTAMP DEFAULT now()
-- );
-- CREATE TABLE user_household (
--     id SERIAL PRIMARY KEY,
--     user_id INTEGER REFERENCES users(user_id),
--     member_type VARCHAR(30), -- PARTNER, KIDS, PETS, etc
--     count INTEGER DEFAULT 1
-- );
-- UPDATE users
-- SET onboarding_completed = false
-- WHERE email = 'faiyaj.jarif01@gmail.com';

-- UPDATE users
-- SET onboarding_completed = false
-- WHERE email = 'faiyaz.jarif@gmail.com';
-- DELETE FROM user_onboarding_selections;
-- UPDATE users SET onboarding_completed = false;

SELECT * FROM users;

-- TRUNCATE TABLE user_onboarding_selections RESTART IDENTITY;
-- ALTER TABLE categories
-- ADD COLUMN is_goal BOOLEAN DEFAULT FALSE,
-- ADD COLUMN goal_amount NUMERIC,
-- ADD COLUMN goal_frequency VARCHAR(20);

SELECT * FROM categories;
SELECT * FROM category_goals;


SELECT * FROM user_onboarding_selections;

