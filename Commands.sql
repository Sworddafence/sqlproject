-- Insert a user
INSERT INTO users (login, password, role, favgames, phonenum, numoverduegames)
VALUES ('johndoe', 'securepassword123', 'customer', '', '123-456-7890', 2);

-- Check login

select * from users where users.login = 'qwe' AND users.password = 'qwe'

-- Update login

UPDATE users
SET favgames = CONCAT(favgames, ', new_game_name')
WHERE login = 'johndoe';


-- Update games list

UPDATE users
SET favgames = 'specific_string'
WHERE login = 'specific_user';


-- Get new rental order id

SELECT rentalOrderID
FROM rentalOrder
WHERE rentalOrderID = (SELECT MAX(rentalOrderID) FROM rentalOrder);

-- insert rental order

INSERT INTO RentalOrder (rentalorderid, login, noofgames, totalprice, ordertimestamp, duedate) 
VALUES (yourRentalOrderIdValue, 'yourLoginValue', yourNoOfGamesValue, yourTotalPriceValue, 'yourOrderTimestampValue', 'yourDueDateValue');

-- insert games to order

INSERT INTO gamesinorder (rentalorderid, gameid, unitsordered) 
VALUES (12345, 'game123', 2);

SELECT rentalorderid
FROM RentalOrder
WHERE login = 'your_specific_login'
ORDER BY ordertimestamp DESC
LIMIT 5;


SELECT 
    t.trackingid, r.ordertimestamp, r.duedate, r.totalprice, c.gamename
FROM 
    rentalorder r, trackinginfo t, gamesinorder g, catalog c
WHERE 
    r.rentalorderid = t.rentalorderid
    AND r.rentalorderid = g.rentalorderid
    AND g.gameid = c.gameid
    AND r.login = 'qwe' 
    AND r.rentalorderid = 'gamerentalorder4152';




SELECT t.courierName, r.rentalOrderID, t.currentLocation, t.status, t.lastupdatedate, t.additionalComments
FROM rentalorder r, trackinginfo t
WHERE r.rentalorderid = t.rentalorderid
  AND r.login = 'qwe' 
  AND t.trackingid = 'trackingid4152'


UPDATE trackinginfo 
SET status = 'finished'
WHERE trackingid = 'your_tracking_id_here';


SELECT MAX(CAST(SUBSTRING(rentalorderid FROM 16) AS INTEGER)) AS max_game_number
FROM rentalorder;


SELECT t.courierName, r.rentalOrderID, t.currentLocation, t.status, t.lastupdatedate, t.additionalComments  FROM rentalorder r, trackinginfo t WHERE r.rentalorderid = t.rentalorderid AND (r.login = '" + esql.getAuth() +"' OR EXISTS (SELECT 1 FROM users WHERE login = '" + esql.getAuth() +"' AND role = 'manager'))  AND t.trackingid = '" + b + "'


SELECT t.courierName, r.rentalOrderID, t.currentLocation, t.status, t.lastupdatedate, t.additionalComments 
FROM rentalorder r, trackinginfo t 
WHERE r.rentalorderid = t.rentalorderid AND (r.login = 'qwe' OR EXISTS (SELECT 1 FROM users WHERE login = 'qwe' AND role = 'manager'))  AND t.trackingid = 'trackingid4148"
