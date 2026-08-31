CREATE DATABASE IF NOT EXISTS prenotazioneviaggi
    DEFAULT CHARACTER SET utf8mb4;

USE prenotazioneviaggi;

CREATE TABLE IF NOT EXISTS utente (
    id       INT PRIMARY KEY,
    nickname VARCHAR(50)  NOT NULL UNIQUE,
    nome     VARCHAR(50)  NOT NULL,
    cognome  VARCHAR(50)  NOT NULL,
    email    VARCHAR(120) NOT NULL,
    password VARCHAR(120) NOT NULL,
    tipo     VARCHAR(20)  NOT NULL DEFAULT 'CONSUMER'
);

CREATE TABLE IF NOT EXISTS catalogo (
    id     INT PRIMARY KEY,
    titolo VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS pacchetto (
    id            INT PRIMARY KEY,
    destinazione  VARCHAR(80) NOT NULL,
    data_partenza BIGINT      NOT NULL,
    data_rientro  BIGINT      NOT NULL,
    prezzo        FLOAT       NOT NULL,
    posti         INT         NOT NULL,
    stelle        INT         NOT NULL,
    tipo_volo     VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS pagamento (
    id                    INT PRIMARY KEY,
    metodo                VARCHAR(120) NOT NULL,
    importo               FLOAT       NOT NULL,
    codice_autorizzazione VARCHAR(60) NOT NULL,
    data_esecuzione       BIGINT      NOT NULL
);

CREATE TABLE IF NOT EXISTS prenotazione (
    id                INT PRIMARY KEY,
    id_utente         INT    NOT NULL,
    id_pacchetto      INT    NOT NULL,
    id_pagamento      INT    NOT NULL,
    data_partenza     BIGINT NOT NULL,
    data_rientro      BIGINT NOT NULL,
    data_prenotazione BIGINT NOT NULL,
    CONSTRAINT fk_prenotazione_utente    FOREIGN KEY (id_utente)    REFERENCES utente (id),
    CONSTRAINT fk_prenotazione_pacchetto FOREIGN KEY (id_pacchetto) REFERENCES pacchetto (id),
    CONSTRAINT fk_prenotazione_pagamento FOREIGN KEY (id_pagamento) REFERENCES pagamento (id)
);

CREATE TABLE IF NOT EXISTS partecipante (
    id              INT PRIMARY KEY,
    id_prenotazione INT         NOT NULL,
    nome            VARCHAR(50) NOT NULL,
    cognome         VARCHAR(50) NOT NULL,
    data_nascita    BIGINT      NOT NULL,
    codice_fiscale  VARCHAR(16) NOT NULL,
    CONSTRAINT fk_partecipante_prenotazione FOREIGN KEY (id_prenotazione) REFERENCES prenotazione (id)
);

INSERT IGNORE INTO catalogo (id, titolo) VALUES
    (1, 'Catalogo viaggi PrenotazioneViaggi');

INSERT IGNORE INTO utente (id, nickname, nome, cognome, email, password, tipo) VALUES
    (1, 'mariorossi',    'Mario', 'Rossi',   'mario.rossi@prenotazioneviaggi.it', 'cliente123', 'CONSUMER'),
    (2, 'annaverdi',     'Anna',  'Verdi',   'anna.verdi@prenotazioneviaggi.it',  'cliente456', 'CONSUMER'),
    (3, 'agenziaviaggi', 'Laura', 'Bianchi', 'agenzia@prenotazioneviaggi.it',     'agenzia123', 'AGENZIA');

INSERT IGNORE INTO pacchetto
    (id, destinazione, data_partenza, data_rientro, prezzo, posti, stelle, tipo_volo) VALUES
    (1, 'Roma',           UNIX_TIMESTAMP(CURDATE() + INTERVAL  10 DAY) * 1000,
                          UNIX_TIMESTAMP(CURDATE() + INTERVAL 130 DAY) * 1000,  350, 10, 4, 'DIRETTO'),
    (2, 'Parigi',         UNIX_TIMESTAMP(CURDATE() + INTERVAL  20 DAY) * 1000,
                          UNIX_TIMESTAMP(CURDATE() + INTERVAL 170 DAY) * 1000,  480,  3, 5, 'DIRETTO'),
    (3, 'Barcellona',     UNIX_TIMESTAMP(CURDATE() + INTERVAL  15 DAY) * 1000,
                          UNIX_TIMESTAMP(CURDATE() + INTERVAL 150 DAY) * 1000,  400,  2, 3, 'CON_SCALO'),
    (4, 'Londra',         UNIX_TIMESTAMP(CURDATE() + INTERVAL  30 DAY) * 1000,
                          UNIX_TIMESTAMP(CURDATE() + INTERVAL 210 DAY) * 1000,  520,  6, 4, 'DIRETTO'),
    (5, 'New York',       UNIX_TIMESTAMP(CURDATE() + INTERVAL  45 DAY) * 1000,
                          UNIX_TIMESTAMP(CURDATE() + INTERVAL 240 DAY) * 1000,  950,  4, 5, 'CON_SCALO'),
    (6, 'Pechino',        UNIX_TIMESTAMP(CURDATE() + INTERVAL  60 DAY) * 1000,
                          UNIX_TIMESTAMP(CURDATE() + INTERVAL 260 DAY) * 1000, 1100,  2, 4, 'CON_SCALO'),
    (7, 'Melbourne',      UNIX_TIMESTAMP(CURDATE() + INTERVAL  75 DAY) * 1000,
                          UNIX_TIMESTAMP(CURDATE() + INTERVAL 300 DAY) * 1000, 1450,  8, 4, 'CON_SCALO'),
    (8, 'Rio de Janeiro', UNIX_TIMESTAMP(CURDATE() + INTERVAL  50 DAY) * 1000,
                          UNIX_TIMESTAMP(CURDATE() + INTERVAL 270 DAY) * 1000, 1200,  5, 5, 'CON_SCALO');
