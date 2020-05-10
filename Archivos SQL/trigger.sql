#Creacion del TRIGGER

USE vuelos;

delimiter !

CREATE TRIGGER salida_insert
AFTER INSERT ON salidas
FOR EACH ROW
BEGIN
	DECLARE cantDias INT DEFAULT 1;
	DECLARE totalDias INT;
	SELECT DATEDIFF(ADDDATE(CURRENT_DATE(), INTERVAL 1 YEAR),CURRENT_DATE()) INTO totalDias;
	WHILE cantDias <= totalDias DO
		IF (NEW.dia=DAYOFWEEK(ADDDATE(CURRENT_DATE(), INTERVAL cantDias DAY)))
			THEN
				INSERT INTO instancias_vuelo (vuelo,fecha,dia,estado) VALUES (NEW.vuelo, ADDDATE(CURRENT_DATE(), INTERVAL cantDias DAY), NEW.dia, 'a tiempo');
		END IF;
		SET cantDias=cantDias+1;
	END WHILE;
END; !

delimiter ;