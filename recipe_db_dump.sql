
-- Drop existing tables if they exist (to reset the database)
DROP TABLE IF EXISTS comments, favorites, ratings, recipes, categories, users CASCADE;

-- Create users table
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Create categories table
CREATE TABLE categories (
    category_id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- Create recipes table
CREATE TABLE recipes (
    recipe_id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    ingredients TEXT NOT NULL,
    instructions TEXT NOT NULL,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    category_id INTEGER REFERENCES categories(category_id) ON DELETE CASCADE
);

-- Create ratings table
CREATE TABLE ratings (
    rating_id SERIAL PRIMARY KEY,
    stars INTEGER NOT NULL CHECK (stars BETWEEN 1 AND 5),
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    recipe_id INTEGER REFERENCES recipes(recipe_id) ON DELETE CASCADE
);

-- Create favorites table
CREATE TABLE favorites (
    favorite_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    recipe_id INTEGER REFERENCES recipes(recipe_id) ON DELETE CASCADE
);

-- Create comments table
CREATE TABLE comments (
    comment_id SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    recipe_id INTEGER REFERENCES recipes(recipe_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT NOW()
);

INSERT INTO categories (category_id, name) VALUES
    (1, 'Greek'),
    (2, 'Italian'),
    (3, 'Mexican'),
    (4, 'Indian'),
    (5, 'Chinese'),
    (6, 'American'),
    (7, 'French'),
    (8, 'Japanese'),
    (9, 'Thai'),
    (10, 'Spanish'),
    (11, 'Mediterranean'),
    (12, 'Korean'),
    (13, 'Vietnamese'),
    (14, 'Turkish'),
    (15, 'Lebanese'),
    (16, 'Brazilian'),
    (17, 'Caribbean'),
    (18, 'African'),
    (19, 'German'),
    (20, 'Russian'),
    (21, 'British');

-- Insert users
INSERT INTO users (firstname, lastname, email, password) VALUES
    ('Einstein', 'Albert', 'einstein@science.com', '$2b$12$0ghQYE.JnutMHwx0OQCtsuc0Sx350nf9u71OnnkTCsHXjUVUIjCzC'),
    ('Newton', 'Isaac', 'newton@science.com', '$2b$12$WxZo7sgbu4feRZ3Ga0.yge5o4xVtQxz/2SrtYBbSpdi7Fmj0CVC8O'),
    ('Curie', 'Maria', 'curie@science.com', '$2b$12$VevF6mCuKahaEMtb7ncxyOtWjrJBIfymMh1eU/VzXMh6NRN/d52B.'),
    ('Darwin', 'Charles', 'darwin@science.com', '$2b$12$x0Q1Tlga.2C/4w9Ij81L6eBHcjS5qjhudsDqhCgR.MWBfSlfIJ5ZO'),
    ('Tesla', 'Nicola', 'tesla@science.com', '$2b$12$yd6O.7ERfWFWYPLmf1z4nOIhVoUi/RI8P7ZIFVfTrhzJwBIqChhsW'),
    ('Galileo', 'Galilei', 'galileo@science.com', '$2b$12$s2dwT88Q3RLHJPMZQpQao.DinyJz03MX15xvFOpRAYViGwYlKerBu'),
    ('Hawking', 'Stephen', 'hawking@science.com', '$2b$12$5FKpOP8HfpOnYDLEx2vAqedtE.cKAcUgNUHIm/2OSZBnqioIrvxGi'),
    ('Pasteur', 'Louis', 'pasteur@science.com', '$2b$12$vXjP4bz4rXkZrNpFVT4x6.mX31nX8eR/mUwUQXB6jH9HiFm5V4QMm'),
    ('Bohr', 'Niels', 'bohr@science.com', '$2b$12$aTNmb6ztb3pCL9yPBh8ntuX0D0AymIlRg23IsNVJjImOpp.7G2rH2'),
    ('Lovelace', 'Ada', 'lovelace@science.com', '$2b$12$hiALLBRYLdM5NAhMG0pNwO9z70itiM2BzLGIwdO3YOwZA6Mt7ptYK'),
    ('Fermi', 'Enrico', 'fermi@science.com', '$2b$12$iwHMVndagesphBHg6F9hZuS9GRSj7EczSaZ7lhCrg7Qz8spIjcjEe'),
    ('Planck', 'Max', 'planck@science.com', '$2b$12$oEiXL/S2BIc4flB7C0X3ouTs82QPsjU1hJ0Mh79BqhejblcYj2T1e'),
    ('Edison', 'Thomas', 'edison@science.com', '$2b$12$aBfFMNMsNoHTTsibkkFFHuVj2xU1Sgp35ALiDnbMJOZDD8ZEn6WW2'),
    ('Mendel', 'Gregor', 'mendel@science.com', '$2b$12$N4GUnVmWIXyBy5ib0qQkSuWW6SG3f5jJcO6xfCwb3vLj/qE16ZRJ.'),
    ('Hubble', 'Edwin', 'hubble@science.com', '$2b$12$BpVfAZc5i6Z5wDlwm4GDZ.zuScpttbfFO5bSIX4pMgecz1wPlOwI.'),
    ('Turing', 'Alan', 'turing@science.com', '$2b$12$ME21zSLC/8RjhMyvZf0sfen.vsIIUustK9FdAQiUR1r7tCcx407re'),
    ('Mark', 'Baz', 'mark@mail.com', '$2b$12$qY1C5E7Ebm7dPNiOaxSpQuwn4nYgbqTjSgf8IsSQ7wfYJfph4n5W.');


INSERT INTO recipes (title, description, ingredients, instructions, user_id, category_id) VALUES
    -- Greek Recipes
    ('Moussaka', 'A Greek layered dish with eggplant and minced meat.', 
     'Eggplant
Ground Beef
Tomato Sauce
Onions
Garlic
Bechamel Sauce', 
     'Slice and bake eggplant.
Cook ground beef with onions and tomato sauce.
Layer and top with bechamel sauce.
Bake until golden brown.', 
     1, 1),
     
    ('Souvlaki', 'Greek grilled meat skewers.', 
     'Pork
Olive Oil
Garlic
Lemon Juice
Oregano
Salt', 
     'Marinate pork with olive oil, garlic, lemon juice, oregano, and salt.
Skewer and grill until golden brown.
Serve with pita bread and tzatziki.', 
     2, 1),

    -- Italian Recipes
    ('Pizza Margherita', 'Classic Italian pizza with tomato, basil, and mozzarella.', 
     'Dough
Tomato Sauce
Mozzarella
Basil
Olive Oil', 
     'Spread tomato sauce on rolled dough.
Add mozzarella and basil.
Bake in a hot oven until crust is crispy.', 
     3, 2),
    
    ('Lasagna', 'Layered Italian pasta with meat sauce.', 
     'Pasta Sheets
Ricotta Cheese
Tomato Sauce
Ground Beef
Parmesan', 
     'Cook ground beef and mix with tomato sauce.
Layer pasta sheets with ricotta cheese and meat sauce.
Repeat layers and bake for 45 minutes.', 
     4, 2),

    ('Carbonara', 'Classic Italian pasta with eggs, cheese, and pancetta.', 
     'Spaghetti
Egg Yolks
Parmesan Cheese
Pancetta
Black Pepper', 
     'Cook spaghetti.
Fry pancetta until crispy.
Mix egg yolks with cheese and black pepper.
Combine everything together and serve.', 
     3, 2),

    -- Mexican Recipes
    ('Tacos', 'Mexican dish with meat-filled tortillas.', 
     'Corn Tortillas
Beef
Onions
Cilantro
Lime
Salsa', 
     'Cook beef with onions and spices.
Warm tortillas and fill with beef mixture.
Top with cilantro, salsa, and lime juice.', 
     5, 3),

    ('Guacamole', 'Mexican avocado dip.', 
     'Avocados
Lime Juice
Tomato
Onions
Cilantro
Salt', 
     'Mash avocados with lime juice.
Mix with chopped tomato, onions, and cilantro.
Add salt to taste and serve with chips.', 
     6, 3),

    -- Indian Recipes
    ('Biryani', 'Indian spiced rice dish.', 
     'Rice
Chicken
Spices
Yogurt
Saffron
Onions', 
     'Marinate chicken with yogurt and spices.
Cook rice with saffron.
Layer chicken and rice and steam until flavors blend.', 
     7, 4),

    ('Butter Chicken', 'Rich and creamy Indian chicken dish.', 
     'Chicken
Tomato Sauce
Butter
Cream
Spices
Garlic', 
     'Cook chicken with butter and garlic.
Add tomato sauce and cream.
Simmer with spices and serve with naan.', 
     8, 4),

    -- Chinese Recipes
    ('Kung Pao Chicken', 'Spicy Sichuan chicken stir-fry.', 
     'Chicken
Peanuts
Chili Peppers
Soy Sauce
Garlic
Ginger', 
     'Stir-fry chicken with garlic and ginger.
Add chili peppers and peanuts.
Toss with soy sauce and serve hot.', 
     9, 5),

    ('Fried Rice', 'Chinese-style rice stir-fried with vegetables.', 
     'Rice
Eggs
Carrots
Peas
Soy Sauce
Green Onions', 
     'Cook and cool rice.
Scramble eggs and mix with rice.
Add vegetables and soy sauce.
Stir-fry until well combined.', 
     10, 5),

    -- American Recipes
    ('Burger', 'Classic American beef burger.', 
     'Beef Patty
Lettuce
Tomato
Cheese
Burger Bun
Pickles', 
     'Grill beef patty.
Assemble burger with lettuce, tomato, cheese, and pickles.
Serve in a toasted bun.', 
     11, 6),

    ('Mac and Cheese', 'Classic American comfort food.', 
     'Macaroni
Cheddar Cheese
Milk
Butter
Flour', 
     'Cook macaroni.
Melt butter and stir in flour.
Add milk and cheese, then mix with macaroni.', 
     12, 6),

    -- Additional Categories (Japanese, Thai, Spanish, etc.)
    ('Sushi', 'Traditional Japanese rice and fish rolls.', 
     'Sushi Rice
Nori
Raw Fish
Soy Sauce
Wasabi', 
     'Cook and season sushi rice.
Lay out nori and spread rice evenly.
Add fish and roll tightly.
Slice and serve with soy sauce.', 
     13, 7),

    ('Pad Thai', 'Thai stir-fried noodle dish.', 
     'Rice Noodles
Shrimp
Eggs
Peanuts
Lime
Soy Sauce', 
     'Cook rice noodles.
Stir-fry with shrimp, eggs, and peanuts.
Add soy sauce and lime juice.', 
     14, 8),

    ('Paella', 'Spanish saffron rice dish with seafood.', 
     'Rice
Shrimp
Mussels
Chicken
Saffron
Tomatoes', 
     'Sauté chicken and seafood.
Add rice and saffron broth.
Simmer until rice is cooked and flavors combine.', 
     15, 9),

    ('Kimchi Fried Rice', 'Korean spicy fried rice dish.', 
     'Rice
Kimchi
Eggs
Green Onions
Soy Sauce', 
     'Cook rice and mix with chopped kimchi.
Stir-fry with eggs and green onions.
Add soy sauce and serve hot.', 
     16, 10);



-- Insert ratings
INSERT INTO ratings (stars, user_id, recipe_id) VALUES
    (5, 1, 1), (4, 2, 1), (5, 3, 2), (3, 4, 2),
    (5, 5, 3), (4, 6, 4), (5, 7, 5), (3, 8, 6),
    (5, 9, 7), (4, 10, 8), (5, 11, 2), (3, 12, 4);

-- Insert favorites
INSERT INTO favorites (user_id, recipe_id) VALUES
    (1, 1), (2, 3), (3, 5), (4, 7),
    (5, 2), (6, 4), (7, 6), (8, 8);

-- Insert comments
INSERT INTO comments (content, user_id, recipe_id) VALUES
    ('Amazing recipe, loved it!', 2, 1),
    ('Could use more seasoning.', 3, 2),
    ('My family enjoyed this dish!', 4, 3),
    ('Easy and delicious.', 5, 4),
    ('Authentic taste!', 6, 5),
    ('Best dish I have made!', 7, 6),
    ('So flavorful!', 8, 7),
    ('Took some time, but worth it!', 9, 8);
