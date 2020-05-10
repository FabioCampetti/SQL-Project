#Archivo batch (vuelos.sql) para la creación de la base de datos del Proyecto 2

#Base de datos 2019 - Proyecto 2 (Ejercicio 1)
# Creacion de la Base de Datos
CREATE DATABASE vuelos;

# Selecciono la base de datos sobre la cual voy a hacer modificaciones
USE vuelos;

#-------------------------------------------

# Creación Tablas para las entidades

CREATE TABLE ubicaciones(
	pais VARCHAR(50) NOT NULL,
	estado VARCHAR(50) NOT NULL,
	ciudad VARCHAR(50) NOT NULL,
	huso INT CHECK (huso>-12 AND huso<12) NOT NULL,

	CONSTRAINT pk_ubicaciones
	PRIMARY KEY(pais,estado,ciudad)
)ENGINE InnoDB;

CREATE TABLE aeropuertos(
	codigo VARCHAR(25) NOT NULL,
	nombre VARCHAR(50) NOT NULL,
	telefono VARCHAR(50) NOT NULL,
	direccion VARCHAR(50) NOT NULL,
	pais VARCHAR(50) NOT NULL,
	estado VARCHAR(50) NOT NULL,
	ciudad VARCHAR(50) NOT NULL,

	CONSTRAINT pk_aeropuertos
	PRIMARY KEY(codigo),

	CONSTRAINT fk_aeropuertos_ubicacion
	FOREIGN KEY(pais,estado,ciudad) REFERENCES ubicaciones(pais,estado,ciudad)
)ENGINE InnoDB;

CREATE TABLE modelos_avion(
	modelo VARCHAR(25) NOT NULL,
	fabricante VARCHAR(25) NOT NULL,
	cabinas INT unsigned NOT NULL,
	cant_asientos INT unsigned NOT NULL,

	CONSTRAINT pk_modelos_avion
	PRIMARY KEY(modelo)
)ENGINE InnoDB;

CREATE TABLE vuelos_programados(
	numero VARCHAR(25) NOT NULL,
	aeropuerto_salida VARCHAR(25) NOT NULL,
	aeropuerto_llegada VARCHAR(25) NOT NULL,

	CONSTRAINT pk_vuelos_programados
	PRIMARY KEY(numero),

	CONSTRAINT fk_vuelos_programados_aerop_salida
	FOREIGN KEY(aeropuerto_salida) REFERENCES aeropuertos(codigo),

	CONSTRAINT fk_vuelos_programados_aerop_llegada
	FOREIGN KEY(aeropuerto_llegada) REFERENCES aeropuertos(codigo)
)ENGINE InnoDB;

CREATE TABLE salidas(
	vuelo VARCHAR(25) NOT NULL,
	dia ENUM('Do','Lu','Ma','Mi','Ju','Vi','Sa'), 
	hora_sale TIME NOT NULL,
	hora_llega TIME NOT NULL,
	modelo_avion VARCHAR(25) NOT NULL,
	
	CONSTRAINT pk_salidas
	PRIMARY KEY(vuelo,dia),

	CONSTRAINT fk_salidas_nrovuelo
	FOREIGN KEY(vuelo) REFERENCES vuelos_programados(numero),

	CONSTRAINT fk_salidas_modelo_avion
	FOREIGN KEY(modelo_avion) REFERENCES modelos_avion(modelo)
)ENGINE InnoDB;

CREATE TABLE instancias_vuelo(
	vuelo VARCHAR(25) NOT NULL,
	fecha DATE NOT NULL,
	dia ENUM('Do','Lu','Ma','Mi','Ju','Vi','Sa') NOT NULL,
	estado VARCHAR(12),

	CONSTRAINT pk_instancias_vuelo
	PRIMARY KEY(vuelo,fecha),

	CONSTRAINT fk_instancias_vuelo_salida
	FOREIGN KEY(vuelo,dia) REFERENCES salidas(vuelo,dia)
)ENGINE InnoDB;

CREATE TABLE clases(
	nombre VARCHAR(20) NOT NULL,
	porcentaje DECIMAL(2,2) unsigned NOT NULL,

	CONSTRAINT pk_clases
	PRIMARY KEY(nombre)
)ENGINE InnoDB;

CREATE TABLE comodidades(
	codigo INT unsigned NOT NULL,
	descripcion TEXT NOT NULL,

	CONSTRAINT pk_comodidades
	PRIMARY KEY(codigo)
)ENGINE InnoDB;

CREATE TABLE pasajeros(
	doc_tipo VARCHAR(25) NOT NULL,
	doc_nro INT(8) unsigned NOT NULL,
	apellido VARCHAR(20) NOT NULL,
	nombre VARCHAR(20) NOT NULL,
	direccion VARCHAR(25) NOT NULL,
	telefono VARCHAR(12) NOT NULL,
	nacionalidad VARCHAR(20) NOT NULL,

	CONSTRAINT pk_pasajeros
	PRIMARY KEY(doc_tipo,doc_nro)
)ENGINE InnoDB;

CREATE TABLE empleados(
	legajo INT unsigned NOT NULL,
	password VARCHAR(32) NOT NULL,
	doc_tipo VARCHAR(25) NOT NULL,
	doc_nro INT(8) unsigned NOT NULL,
	apellido VARCHAR(20) NOT NULL,
	nombre VARCHAR(20) NOT NULL,
	direccion VARCHAR(25) NOT NULL,
	telefono VARCHAR(12) NOT NULL,
	
	CONSTRAINT pk_empleados
	PRIMARY KEY(legajo)
)ENGINE InnoDB;

CREATE TABLE reservas(
	numero INT unsigned NOT NULL AUTO_INCREMENT,
	fecha DATE NOT NULL,
	vencimiento DATE NOT NULL,
	estado VARCHAR(10) NOT NULL,
	doc_tipo VARCHAR(25) NOT NULL,
	doc_nro INT(20) unsigned NOT NULL,
	legajo INT unsigned NOT NULL,
	
	CONSTRAINT pk_reservas
	PRIMARY KEY(numero),

	CONSTRAINT fk_reservas_pasajero
	FOREIGN KEY(doc_tipo,doc_nro) REFERENCES pasajeros(doc_tipo,doc_nro),

	CONSTRAINT fk_reservas_empleado
	FOREIGN KEY(legajo) REFERENCES empleados(legajo)
)ENGINE InnoDB;
#--------------------------------------------------------
#DEFINICION DE LAS TABLAS DE RELACIONES

CREATE TABLE brinda(
	vuelo VARCHAR(25) NOT NULL,
	dia ENUM('Do','Lu','Ma','Mi','Ju','Vi','Sa'),
	clase VARCHAR(20) NOT NULL,
	precio DECIMAL(7,2) unsigned NOT NULL,
	cant_asientos INT unsigned NOT NULL,

	CONSTRAINT pk_brinda
	PRIMARY KEY(vuelo,dia,clase),

	CONSTRAINT fk_brinda_salida
	FOREIGN KEY(vuelo,dia) REFERENCES salidas(vuelo,dia),

	CONSTRAINT fk_brinda_clase
	FOREIGN KEY(clase) REFERENCES clases(nombre)
)ENGINE InnoDB;

CREATE TABLE posee(
	clase VARCHAR(20) NOT NULL,
	comodidad INT unsigned NOT NULL,
	
	CONSTRAINT pk_posee
	PRIMARY KEY(clase,comodidad),

	CONSTRAINT fk_posee_clase
	FOREIGN KEY(clase) REFERENCES clases(nombre),
	
	CONSTRAINT fk_posee_comodidad
	FOREIGN KEY(comodidad) REFERENCES comodidades(codigo)
)ENGINE InnoDB;

CREATE TABLE reserva_vuelo_clase(
	numero INT unsigned NOT NULL,
	vuelo VARCHAR(25) NOT NULL,
	fecha_vuelo DATE NOT NULL,
	clase VARCHAR(20) NOT NULL,

	CONSTRAINT pk_reserva_vuelo_clase
	PRIMARY KEY(numero,vuelo,fecha_vuelo),

	CONSTRAINT fk_rerservavueloclase_reserva
	FOREIGN KEY(numero) REFERENCES reservas(numero),
	
	CONSTRAINT fk_rerservavueloclase_vuelo
	FOREIGN KEY(vuelo,fecha_vuelo) REFERENCES instancias_vuelo(vuelo,fecha),

	CONSTRAINT fk_rerservavueloclase_clase
	FOREIGN KEY(clase) REFERENCES clases(nombre)
)ENGINE InnoDB;

#---------------------------------------------------------------------------------------------------------

# Creacion de la 'VISTA' llamada "vuelos disponibles"

CREATE VIEW vuelos_disponibles AS 
	SELECT DISTINCT vp.numero as 'VUELO_NRO', s.modelo_avion as 'MODELO', iv.fecha as 'FECHA', s.dia as 'DIA', s.hora_sale as 'HORA_SALIDA', 
		   s.hora_llega as 'HORA_LLEGADA', TIMEDIFF(s.hora_llega,s.hora_sale) as 'TIEMPO_ESTIMADO_DE_VUELO', 
		   a_sale.codigo as 'COD_AEROP_SALIDA', a_sale.nombre as 'AEROP_SALIDA', a_sale.ciudad as 'CIUDAD_SALIDA', 
		   a_sale.estado as 'ESTADO_SALIDA', a_sale.pais as 'PAIS_SALIDA',
		   a_llega.codigo as 'COD_AEROP_LLEGADA', a_llega.nombre as 'AEROP_LLEGADA', a_llega.ciudad as 'CIUDAD_LLEGADA', 
		   a_llega.estado as 'ESTADO_LLEGADA', a_llega.pais as 'PAIS_LLEGADA',
		   b.precio as 'PRECIO',b.clase as'CLASE', TRUNCATE ((b.cant_asientos + c.porcentaje * b.cant_asientos),0) as 'CANT_ASIENTOS',
		   TRUNCATE ((b.cant_asientos + c.porcentaje * b.cant_asientos) - (SELECT COUNT(*) FROM reserva_vuelo_clase as rvc WHERE (iv.vuelo = rvc.vuelo AND iv.fecha = rvc.fecha_vuelo AND b.clase=rvc.clase)),0) as 'ASIENTOS_DISPONIBLES'
	FROM ((((((vuelos_programados as vp JOIN aeropuertos as a_sale ON vp.aeropuerto_salida = a_sale.codigo)
			JOIN aeropuertos as  a_llega ON vp.aeropuerto_llegada = a_llega.codigo)
			JOIN salidas as s ON vp.numero = s.vuelo) JOIN instancias_vuelo as iv ON vp.numero = iv.vuelo and iv.dia=s.dia) 
			JOIN brinda as b ON vp.numero = b.vuelo and iv.dia=b.dia) JOIN clases as c ON b.clase = c.nombre)
	WHERE TRUNCATE ((b.cant_asientos + (c.porcentaje * b.cant_asientos)) - (SELECT COUNT(*) FROM reserva_vuelo_clase as rvc WHERE (iv.vuelo = rvc.vuelo AND iv.fecha = rvc.fecha_vuelo AND b.clase=rvc.clase)),0) > 0 # (cant asientos que brinda el vuelo en esa clase + porcentaje) - cant total de reservas
	ORDER BY vp.numero, iv.fecha,s.modelo_avion,s.dia;

#--------------------------------------------------------------------------------------------------------

#Creacion tabla asientos reservados
CREATE TABLE asientos_reservados(
	vuelo VARCHAR (25) NOT NULL,
	fecha DATE NOT NULL,
	clase VARCHAR (20) NOT NULL,
	cantidad INT unsigned NOT NULL,

	CONSTRAINT pk_asientos_reservados 
	PRIMARY KEY (vuelo,fecha,clase),

	CONSTRAINT fk_asientosdispoinbles_vuelos
	FOREIGN KEY(vuelo,fecha) REFERENCES instancias_vuelo(vuelo,fecha),

	CONSTRAINT fk_asientosdisponibles_clase
	FOREIGN KEY(clase) REFERENCES clases(nombre)
)ENGINE InnoDB;

#----------------------------------------------STORE PROCEDURES------------------------------------------------

delimiter !

CREATE PROCEDURE reservar_ida_vuelta(IN numero_vuelo_ida VARCHAR (25), IN fecha_vuelo_ida DATE, IN clase_vuelo_ida VARCHAR (20),
							IN numero_vuelo_vuelta VARCHAR (25), IN fecha_vuelo_vuelta DATE, IN clase_vuelo_vuelta VARCHAR (20),
							 IN tipo_doc VARCHAR (25), IN numero_doc INT (20), IN numero_legajo INT)

	BEGIN
		#Declaro una variable local para la cantidad de asientos disponibles y reservados
	DECLARE total_asientos_ida INT unsigned;
	DECLARE asientos_reservados_ida INT unsigned;
	DECLARE total_asientos_vuelta INT unsigned;
	DECLARE asientos_reservados_vuelta INT unsigned;
		 # Declaro variables locales para recuperar los errores 
	 DECLARE codigo_SQL  CHAR(5) DEFAULT '00000';	 
	 DECLARE codigo_MYSQL INT DEFAULT 0;
	 DECLARE mensaje_error TEXT;
	 
     DECLARE EXIT HANDLER FOR SQLEXCEPTION 	 	 
	  BEGIN 
		GET DIAGNOSTICS CONDITION 1  codigo_MYSQL= MYSQL_ERRNO,  
		                             codigo_SQL= RETURNED_SQLSTATE, 
									 mensaje_error= MESSAGE_TEXT;
	    SELECT 'SQLEXCEPTION!, Transacción abortada.' AS resultado, 
		        codigo_MySQL, codigo_SQL,  mensaje_error;		
        ROLLBACK;
	  END;

	START TRANSACTION; #Comienza la transaccion
		IF EXISTS(SELECT * FROM vuelos_disponibles WHERE VUELO_NRO=numero_vuelo_ida AND FECHA=fecha_vuelo_ida AND CLASE=clase_vuelo_ida)
			THEN #Verifico que el vuelo,fecha y clase de ida existe
				IF EXISTS(SELECT * FROM vuelos_disponibles WHERE VUELO_NRO=numero_vuelo_vuelta AND FECHA=fecha_vuelo_vuelta AND CLASE=clase_vuelo_vuelta)
					THEN #Verifico que el vuelo, fecha y clase
						IF EXISTS (SELECT * FROM pasajeros WHERE doc_tipo=tipo_doc AND doc_nro=numero_doc)
							THEN #Verifico que el documento existe
								IF EXISTS (SELECT * FROM empleados WHERE legajo=numero_legajo)
									THEN #Verifico que el empleado existe
										SELECT CANT_ASIENTOS INTO total_asientos_ida
										FROM vuelos_disponibles WHERE VUELO_NRO=numero_vuelo_ida AND FECHA=fecha_vuelo_ida AND CLASE=clase_vuelo_ida; 
										#Recupero el total de asientos del vuelo de ida
										SELECT ASIENTOS_DISPONIBLES INTO asientos_reservados_ida
										FROM vuelos_disponibles WHERE VUELO_NRO=numero_vuelo_ida AND FECHA=fecha_vuelo_ida AND CLASE=clase_vuelo_ida;
										#Recupero el total de asientos reservados del vuelo de ida
										SELECT CANT_ASIENTOS INTO total_asientos_vuelta
										FROM vuelos_disponibles WHERE VUELO_NRO=numero_vuelo_vuelta AND FECHA=fecha_vuelo_vuelta AND CLASE=clase_vuelo_vuelta;
										#Recupero el total de asientos del vuelo de vuelta
										SELECT ASIENTOS_DISPONIBLES INTO asientos_reservados_vuelta
										FROM vuelos_disponibles WHERE VUELO_NRO=numero_vuelo_vuelta AND FECHA=fecha_vuelo_vuelta AND CLASE=clase_vuelo_vuelta;
										#Recupero el total de asientos reservados del vuelo de vuelta
										IF total_asientos_ida>0 AND total_asientos_vuelta>0
											#Si los vuelos tienen mas de un asiento entonces se puede realizar la reserva
											THEN
												IF total_asientos_ida > asientos_reservados_ida AND total_asientos_vuelta > asientos_reservados_vuelta
													#Si el total de asientos del vuelo de ida es mayor a el total de asientos reservados
													#la reserva que 'Confirmada' sino 'En Espera' 
													THEN
														INSERT INTO asientos_reservados VALUES (numero_vuelo_ida,fecha_vuelo_ida,clase_vuelo_ida,1) ON DUPLICATE KEY UPDATE cantidad=cantidad+1;
														INSERT INTO reservas (fecha,vencimiento,estado,doc_tipo,doc_nro,legajo) VALUES (CURRENT_DATE(), ADDDATE(CURRENT_DATE(), INTERVAL 15 DAY), 'Confirmada', tipo_doc, numero_doc, numero_legajo);
														INSERT INTO reserva_vuelo_clase VALUES (LAST_INSERT_ID(), numero_vuelo_ida, fecha_vuelo_ida, clase_vuelo_ida);
														INSERT INTO asientos_reservados VALUES (numero_vuelo_vuelta,fecha_vuelo_vuelta,clase_vuelo_vuelta,1) ON DUPLICATE KEY UPDATE cantidad=cantidad+1;
														INSERT INTO reservas (fecha,vencimiento,estado,doc_tipo,doc_nro,legajo) VALUES (CURRENT_DATE(), ADDDATE(CURRENT_DATE(), INTERVAL 15 DAY), 'Confirmada', tipo_doc, numero_doc, numero_legajo);
														INSERT INTO reserva_vuelo_clase VALUES (LAST_INSERT_ID(), numero_vuelo_vuelta, fecha_vuelo_vuelta, clase_vuelo_vuelta);
														SELECT 'Reserva Exitosa' AS resultado;

													ELSE
														INSERT INTO asientos_reservados VALUES (numero_vuelo_ida,fecha_vuelo_ida,clase_vuelo_ida,1) ON DUPLICATE KEY UPDATE cantidad=cantidad+1;
														INSERT INTO reservas (fecha,vencimiento,estado,doc_tipo,doc_nro,legajo) VALUES (CURRENT_DATE(), ADDDATE(CURRENT_DATE(), INTERVAL 15 DAY), 'En Espera', tipo_doc, numero_doc, numero_legajo);
														INSERT INTO reserva_vuelo_clase VALUES (LAST_INSERT_ID(), numero_vuelo_ida, fecha_vuelo_ida, clase_vuelo_ida);
														INSERT INTO asientos_reservados VALUES (numero_vuelo_vuelta,fecha_vuelo_vuelta,clase_vuelo_vuelta,1) ON DUPLICATE KEY UPDATE cantidad=cantidad+1;
														INSERT INTO reservas (fecha,vencimiento,estado,doc_tipo,doc_nro,legajo) VALUES (CURRENT_DATE(), ADDDATE(CURRENT_DATE(), INTERVAL 15 DAY), 'En Espera', tipo_doc, numero_doc, numero_legajo);
														INSERT INTO reserva_vuelo_clase VALUES (LAST_INSERT_ID(), numero_vuelo_vuelta, fecha_vuelo_vuelta, clase_vuelo_vuelta);
														SELECT 'Reserva En Espera' AS resultado;
												END IF;
											ELSE
												SELECT 'Vuelos sin asientos' AS resultado;
										END IF;
									ELSE
										SELECT 'Empleado inexistente' AS resultado;
								END IF;
							ELSE
								SELECT 'Pasajero inexistente' AS resultado;
						END IF;
					ELSE
						SELECT 'Vuelo vuelta inexistente' AS resultado;
				END IF;
			ELSE
				SELECT 'Vuelo ida inexistente' AS resultado;
		END IF;
	COMMIT; #Comete la transaccion
	END; !


CREATE PROCEDURE reservar_ida(IN numero_vuelo VARCHAR (25), IN fecha_vuelo DATE, IN clase_vuelo VARCHAR (20),
							 IN tipo_doc VARCHAR (25), IN numero_doc INT (20), IN numero_legajo INT)

	BEGIN
		#Declaro una variable local para la cantidad de asientos disponibles
	DECLARE total_asientos INT unsigned;
	DECLARE asientos_reservados INT unsigned;
		 # Declaro variables locales para recuperar los errores 
	 DECLARE codigo_SQL  CHAR(5) DEFAULT '00000';	 
	 DECLARE codigo_MYSQL INT DEFAULT 0;
	 DECLARE mensaje_error TEXT;
	 
     DECLARE EXIT HANDLER FOR SQLEXCEPTION 	 	 
	  BEGIN 
		GET DIAGNOSTICS CONDITION 1  codigo_MYSQL= MYSQL_ERRNO,  
		                             codigo_SQL= RETURNED_SQLSTATE, 
									 mensaje_error= MESSAGE_TEXT;
	    SELECT 'SQLEXCEPTION!, Transacción abortada.' AS resultado, 
		        codigo_MySQL, codigo_SQL,  mensaje_error;		
        ROLLBACK;
	  END;
	START TRANSACTION; #Comienza la transaccion
		IF EXISTS (SELECT * FROM vuelos_disponibles WHERE VUELO_NRO=numero_vuelo AND FECHA=fecha_vuelo AND CLASE=clase_vuelo)
			THEN #Verifico que el nro, fecha y la clase del vuelo exista
				IF EXISTS (SELECT * FROM pasajeros WHERE doc_tipo=tipo_doc AND doc_nro=numero_doc)
					THEN #Verifico que el documento existe
						IF EXISTS (SELECT * FROM empleados WHERE legajo=numero_legajo)
							THEN #Verifico que el empleado existe
								SELECT CANT_ASIENTOS INTO total_asientos
								FROM vuelos_disponibles WHERE VUELO_NRO=numero_vuelo AND FECHA=fecha_vuelo AND CLASE=clase_vuelo;
								#Recupero el total de asientos del vuelo
								SELECT ASIENTOS_DISPONIBLES INTO asientos_reservados
								FROM vuelos_disponibles WHERE VUELO_NRO=numero_vuelo AND FECHA=fecha_vuelo AND CLASE=clase_vuelo;
								#Recupero el total de asientos reservados del vuelo
								IF total_asientos>0
									#Si el total de asientos es mayor a 0 entonces se puede realizar la reserva
									THEN
										IF total_asientos > asientos_reservados
											#Si la el total de asientos es mayor al total de reservados entonces la reserva
											#tendra estado 'Confirmada' sino 'En Espera'
											THEN
												INSERT INTO asientos_reservados VALUES (numero_vuelo,fecha_vuelo,clase_vuelo,0) ON DUPLICATE KEY UPDATE cantidad=cantidad+1;
												INSERT INTO reservas (fecha,vencimiento,estado,doc_tipo,doc_nro,legajo) VALUES (CURRENT_DATE(), ADDDATE(CURRENT_DATE(), INTERVAL 15 DAY), 'Confirmada', tipo_doc, numero_doc, numero_legajo);
												INSERT INTO reserva_vuelo_clase VALUES (LAST_INSERT_ID(), numero_vuelo, fecha_vuelo, clase_vuelo);
												SELECT 'Reserva Exitosa' AS resultado;

											ELSE
												INSERT INTO asientos_reservados VALUES (numero_vuelo,fecha_vuelo,clase_vuelo,0) ON DUPLICATE KEY UPDATE cantidad=cantidad+1;
												INSERT INTO reservas (fecha,vencimiento,estado,doc_tipo,doc_nro,legajo) VALUES (CURRENT_DATE(), ADDDATE(CURRENT_DATE(), INTERVAL 15 DAY), 'En Espera', tipo_doc, numero_doc, numero_legajo);
												INSERT INTO reserva_vuelo_clase VALUES (LAST_INSERT_ID(), numero_vuelo, fecha_vuelo, clase_vuelo);
												SELECT 'Reserva En Espera' AS resultado;
										END IF;
									ELSE
										SELECT 'Reserva Fallida: Vuelo sin asientos' AS resultado;
								END IF;
							ELSE
								SELECT 'Reserva Fallida: Empleado inexistente' AS resultado;
						END IF;
					ELSE
						SELECT 'Reserva Fallida: Pasajero inexistente' AS resultado;
				END IF;
			ELSE
				SELECT 'Reserva Fallida: Error en vuelo o clase seleccionado' AS resultado;
		END IF;
	COMMIT; #Comete la transaccion
	END; !

#-----------------------------------------------------USUARIOS--------------------------------------------

delimiter ;
# Creacion y privilegios del usuario "admin"
CREATE USER admin@localhost IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON vuelos.* TO admin@localhost WITH GRANT OPTION;

# Creacion y privilegios del usuario "empleado"
#DROP USER ''@localhost;
CREATE USER empleado@'%' IDENTIFIED BY 'empleado';
GRANT INSERT,UPDATE,DELETE ON vuelos.reservas TO empleado@'%';
GRANT INSERT,UPDATE,DELETE ON vuelos.pasajeros TO empleado@'%';
GRANT INSERT,UPDATE,DELETE ON vuelos.reserva_vuelo_clase TO empleado@'%';
GRANT SELECT ON vuelos.* TO empleado@'%';
GRANT EXECUTE ON PROCEDURE vuelos.reservar_ida TO empleado@'%';
GRANT EXECUTE ON PROCEDURE vuelos.reservar_ida_vuelta TO empleado@'%';

# Creacion y privilegios del usuario "cliente"
CREATE USER cliente@'%' IDENTIFIED BY 'cliente';
GRANT SELECT ON vuelos.vuelos_disponibles TO cliente@'%';

	