DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS rental;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user`
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(250) NOT NULL,
    email    VARCHAR(250) NOT NULL,
    password VARCHAR(250) NOT NULL
);

INSERT INTO `user` (name, email, password) VALUES
('Thibaud GAUTON', 'gauton.thibaud@gmail.com', 'pass');



CREATE TABLE rental (
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    price FLOAT,
    surface FLOAT,
    owner_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    description TEXT,
    picture VARCHAR(255),
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES `user`(id)
);



CREATE TABLE message (
    id BIGINT NOT NULL PRIMARY KEY,
    message VARCHAR(255),
    rental_id BIGINT,
    author_id BIGINT,
    CONSTRAINT fk_rental FOREIGN KEY (rental_id) REFERENCES rental(id),
    CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES `user`(id)
);