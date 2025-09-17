CREATE TABLE IF NOT EXISTS public.users (
        id BIGSERIAL PRIMARY KEY,
        first_name VARCHAR(255),
        last_name VARCHAR(255),
        document VARCHAR(255) UNIQUE,
        email VARCHAR(255) UNIQUE,
        password VARCHAR(255),
        balance DECIMAL(19,2),
        user_type VARCHAR(50)
);