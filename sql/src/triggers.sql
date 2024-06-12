DROP TRIGGER IF EXISTS rentalordercount ON rentalOrder;
DROP FUNCTION IF EXISTS addRental();
DROP TABLE IF EXISTS newrentalorderid;


CREATE TABLE newrentalorderid (
    total_count INTEGER
);

-- Starting value for the trigger
INSERT INTO newrentalorderid (total_count)
SELECT MAX(CAST(SUBSTRING(rentalorderid FROM 16) AS INTEGER)) AS max_game_number
FROM rentalorder;


-- Create the trigger function
CREATE OR REPLACE FUNCTION addRental()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE newrentalorderid
    SET total_count = total_count + 1;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the trigger
CREATE TRIGGER rentalordercount
AFTER INSERT ON rentalOrder
FOR EACH ROW
EXECUTE PROCEDURE addRental();
