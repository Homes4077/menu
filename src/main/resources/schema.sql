-- ==========================================
-- DIGITAL MENU PLATFORM - DATABASE SCHEMA
-- ==========================================

-- Drop tables if they already exist (Clean reset for development)
DROP TABLE IF EXISTS reservations CASCADE;
DROP TABLE IF EXISTS menu_items CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS businesses CASCADE;

-- ------------------------------------------
-- 1. BUSINESSES (TENANTS)
-- ------------------------------------------
CREATE TABLE businesses (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            slug VARCHAR(100) UNIQUE NOT NULL,
                            description TEXT,
                            logo_url TEXT,
                            cover_image_url TEXT,
                            phone VARCHAR(20),
                            whatsapp_number VARCHAR(20) NOT NULL,
                            email VARCHAR(255),
                            location VARCHAR(255),
                            opening_hours VARCHAR(255),
                            theme VARCHAR(50) DEFAULT 'LUXURY',
                            active BOOLEAN DEFAULT TRUE,
                            created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index for fast lookup when customers scan QR code (e.g. /menu/paleo-hotel)
CREATE INDEX idx_businesses_slug ON businesses(slug);

-- ------------------------------------------
-- 2. USERS (RESTAURANT OWNERS / ADMINS)
-- ------------------------------------------
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       business_id BIGINT NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) DEFAULT 'OWNER', -- 'SUPER_ADMIN', 'OWNER', 'STAFF'
                       active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------
-- 3. CATEGORIES
-- ------------------------------------------
CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            business_id BIGINT NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
                            name VARCHAR(100) NOT NULL,
                            description TEXT,
                            display_order INT DEFAULT 0,
                            active BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_categories_business ON categories(business_id);

-- ------------------------------------------
-- 4. MENU ITEMS
-- ------------------------------------------
CREATE TABLE menu_items (
                            id BIGSERIAL PRIMARY KEY,
                            category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
                            name VARCHAR(255) NOT NULL,
                            description TEXT,
                            price NUMERIC(10, 2) NOT NULL,
                            image_url TEXT,
                            available BOOLEAN DEFAULT TRUE,
                            featured BOOLEAN DEFAULT FALSE,
                            display_order INT DEFAULT 0,
                            created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_menu_items_category ON menu_items(category_id);

-- ------------------------------------------
-- 5. RESERVATIONS
-- ------------------------------------------
CREATE TABLE reservations (
                              id BIGSERIAL PRIMARY KEY,
                              business_id BIGINT NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
                              customer_name VARCHAR(255) NOT NULL,
                              customer_phone VARCHAR(50) NOT NULL,
                              reservation_date DATE NOT NULL,
                              reservation_time TIME NOT NULL,
                              guests INT NOT NULL,
                              special_requests TEXT,
                              status VARCHAR(20) DEFAULT 'PENDING', -- 'PENDING', 'CONFIRMED', 'CANCELLED'
                              created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_reservations_business ON reservations(business_id);


-- ==========================================
-- SEED DATA SETUP (PALEO HOTEL & SPA)
-- ==========================================

-- Insert Business
INSERT INTO businesses (name, slug, description, whatsapp_number, location, opening_hours, theme)
VALUES (
           'PALEO HOTEL & SPA',
           'paleo-hotel',
           'Luxury Dining & Wellness Retreat',
           '254791635413',
           'Nairobi, Kenya',
           '07:00 AM - 11:00 PM',
           'LUXURY'
       );

-- Insert Categories (Linked to Business ID 1)
INSERT INTO categories (business_id, name, display_order)
VALUES
    (1, 'GOURMET MAINS', 1),
    (1, 'SIGNATURE DRINKS', 2),
    (1, 'DESSERTS', 3);

-- Insert Menu Items
INSERT INTO menu_items (category_id, name, description, price, featured, display_order)
VALUES
    (1, '21-Day Aged Ribeye Steak', 'Prime beef cut served with truffle mash, grilled asparagus, and red wine reduction.', 2400.00, TRUE, 1),
    (1, 'Creamy Chicken Alfredo', 'Fettuccine pasta tossed in rich parmesan cream sauce with charbroiled chicken breast.', 1350.00, FALSE, 2),
    (2, 'Paleo Sunrise Cocktail', 'Freshly squeezed tropical fruits infused with mint, lime, and botanical gin.', 850.00, TRUE, 1),
    (2, 'Passionfruit Sparkling Mocktail', 'Handcrafted passionfruit puree with sparkling mineral water and fresh rosemary.', 450.00, FALSE, 2),
    (3, 'Warm Lava Chocolate Cake', 'Decadent dark chocolate molten center cake paired with Madagascar vanilla bean gelato.', 750.00, FALSE, 1);