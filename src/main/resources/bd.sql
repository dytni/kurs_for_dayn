-- Убедитесь, что расширение для UUID включено
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create the Users table
CREATE TABLE Users (
    uuid UUID PRIMARY KEY DEFAULT uuid_generate_v4(), -- Авто-генерация UUID
    login VARCHAR(255) NOT NULL,                      -- Login field
    passw VARCHAR(255) NOT NULL,                     -- Password field
    role VARCHAR(50) NOT NULL DEFAULT 'client'       -- Role field
);

-- Create the Clients table
CREATE TABLE Clients (
    uuid UUID PRIMARY KEY DEFAULT uuid_generate_v4(), -- Авто-генерация UUID
    total_hours INT,                                  -- Total hours field
    FOREIGN KEY (uuid) REFERENCES Users(uuid)         -- Foreign key referencing Users
);

-- Create the Admins table
CREATE TABLE Admins (
    uuid UUID PRIMARY KEY DEFAULT uuid_generate_v4(), -- Авто-генерация UUID
    FOREIGN KEY (uuid) REFERENCES Users(uuid)         -- Foreign key referencing Users
);

-- Create the JobPosition table
CREATE TABLE JobPosition (
    id SERIAL PRIMARY KEY,                            -- Primary key for the job position
    position_name VARCHAR(255) NOT NULL,             -- Name of the job position
    hourly_rate NUMERIC(10, 2) NOT NULL              -- Hourly pay rate
);

-- Create a table linking Clients and JobPosition
CREATE TABLE ClientJob (
    client_uuid UUID REFERENCES Clients(uuid) ON DELETE CASCADE, -- Reference to the Clients table
    job_id INT REFERENCES JobPosition(id) ON DELETE CASCADE,     -- Reference to the JobPosition table
    PRIMARY KEY (client_uuid, job_id)                            -- Composite primary key
);

-- Добавление администратора
DO $$
DECLARE
    new_admin_uuid UUID; -- Переменная для хранения сгенерированного UUID
BEGIN
    -- Добавление записи в таблицу Users
    INSERT INTO Users (login, passw, role)
    VALUES ('2', 'd4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35', 'admin')
    RETURNING uuid INTO new_admin_uuid;

    -- Добавление записи в таблицу Admins, используя сгенерированный UUID
    INSERT INTO Admins (uuid)
    VALUES (new_admin_uuid);

    RAISE NOTICE 'Администратор добавлен с UUID: %', new_admin_uuid;
END $$;
