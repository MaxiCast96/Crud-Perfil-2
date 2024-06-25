CREATE TABLE tickets (
    id_ticket INT PRIMARY KEY,
    titulo VARCHAR2(100) NOT NULL,
    descripcion CLOB NOT NULL,
    responsable VARCHAR2(100),
    email_autor VARCHAR2(100) NOT NULL,
    telefono_autor VARCHAR2(20),
    ubicacion VARCHAR2(255),
    estado VARCHAR2(20) DEFAULT 'Activo' CHECK (estado IN ('Activo', 'Finalizado'))
);

CREATE SEQUENCE seq_tickets_id
  START WITH 1
  INCREMENT BY 1
  NOMAXVALUE;

CREATE OR REPLACE TRIGGER trg_tickets_id
BEFORE INSERT ON tickets
FOR EACH ROW
BEGIN
    SELECT seq_tickets_id.NEXTVAL
    INTO :new.id_ticket
    FROM dual;
END;
/