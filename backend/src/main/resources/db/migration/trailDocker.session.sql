-- SELECT current_database();
-- SELECT version();
-- SELECT * FROM email_verification_tokens 
-- ORDER BY token_id DESC;

-- ALTER TABLE users
-- ADD COLUMN preferred_name VARCHAR(100),
-- ADD COLUMN onboarding_completed BOOLEAN DEFAULT FALSE;
SELECT * FROM users;
SELECT * FROM budget_periods
WHERE user_id = 3
AND month = 1 AND year = 2026;

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

SELECT * FROM budget_periods;
-- DROP TABLE IF EXISTS recurring_bills;
-- CREATE TABLE recurring_bills (
--     id SERIAL PRIMARY KEY,
--     user_id INTEGER NOT NULL,
--     category_id INTEGER NOT NULL,
--     name VARCHAR(255) NOT NULL,
--     amount NUMERIC(14,2) NOT NULL,
--     frequency VARCHAR(20) NOT NULL,
--     active BOOLEAN NOT NULL DEFAULT TRUE,
--     next_due_date DATE NOT NULL
-- );

SELECT * FROM recurring_bills;
select * from family_groups;
select * from family_members;
SELECT * FROM family_categories;
SELECT * FROM family_invitations;
-- ALTER TABLE categories
-- ADD COLUMN rollover_enabled BOOLEAN NOT NULL DEFAULT TRUE;
SELECT category_id, name, rollover_enabled
FROM categories
WHERE user_id = 3;
SELECT * FROM budget_periods
WHERE user_id = 3 AND month = 1 AND year = 2026;
SELECT * FROM transactions ORDER BY transaction_id DESC;
SELECT actual_amount FROM budget_items;


-- create table if not exists family_invitations (
--     id bigserial primary key,
--     family_id int not null,
--     invited_user_id int null,
--     invited_email varchar(255) not null,
--     role varchar(50) not null,
--     invited_by_user_id int not null,
--     token varchar(100) not null unique,
--     status varchar(50) not null,
--     created_at timestamptz not null,
--     expires_at timestamptz not null,
--     responded_at timestamptz null
-- );

-- create index if not exists idx_family_invitations_email
--     on family_invitations(invited_email);

-- create index if not exists idx_family_invitations_family
--     on family_invitations(family_id);

-- create index if not exists idx_family_invitations_status
--     on family_invitations(status);



-- ALTER TABLE budget_periods
-- ADD COLUMN income NUMERIC(12,2) NOT NULL DEFAULT 0;

-- ALTER TABLE budget_periods
-- ADD COLUMN total_assigned NUMERIC(12,2) NOT NULL DEFAULT 0;


