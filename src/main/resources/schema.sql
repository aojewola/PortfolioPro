CREATE TABLE IF NOT EXISTS stock (
    id VARCHAR(36) PRIMARY KEY,      -- Primary key
    stock_name VARCHAR(255) NOT NULL,          -- Name of the stock
    ticker VARCHAR(50) NOT NULL,               -- Stock ticker symbol
    shares INT NOT NULL,                       -- Number of shares
    price DOUBLE PRECISION NOT NULL,           -- Price of each share
    total_cost DOUBLE PRECISION NOT NULL,      -- Total amount cost 
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE, -- Soft delete flag
    createdBy VARCHAR(255),                    -- the user who created the record
    lastUpdatedBy VARCHAR(255),                -- updated by which user
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Creation timestamp
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- Update timestamp
);