-- Create an Index based on login values
CREATE UNIQUE INDEX idx_users_login ON users (login);
-- Create an Index based on both login and password values
CREATE INDEX idx_users_login_password ON users (login, password);

CREATE INDEX rentalorderid_index ON rentalorder (rentalorderid);
CREATE INDEX trackingorderid_index ON trackinginfo (rentalorderid);