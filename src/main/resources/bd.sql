-- Create the User table
CREATE TABLE User (
    uuid UUID PRIMARY KEY,          -- Primary key for User
    login VARCHAR(255) NOT NULL,    -- Login field
    passw VARCHAR(255) NOT NULL     -- Password field
);

-- Create the Client table
CREATE TABLE Client (
    uuid UUID PRIMARY KEY REFERENCES User(uuid), -- Foreign key referencing User
    total_hours INT                              -- Total hours field
);

-- Create the Admin table
CREATE TABLE Admin (
    uuid UUID PRIMARY KEY REFERENCES User(uuid)  -- Foreign key referencing User
);

-- Create the JobPosition table
CREATE TABLE JobPosition (
    id SERIAL PRIMARY KEY,         -- Primary key for the job position
    position_name VARCHAR(255) NOT NULL, -- Name of the job position
    hourly_rate NUMERIC(10, 2) NOT NULL -- Hourly pay rate
);

-- Create a table linking Client and JobPosition
CREATE TABLE ClientJob (
    client_uuid UUID REFERENCES Client(uuid) ON DELETE CASCADE, -- Reference to the Client table
    job_id INT REFERENCES JobPosition(id) ON DELETE CASCADE,   -- Reference to the JobPosition table
    PRIMARY KEY (client_uuid, job_id)                          -- Composite primary key
);
