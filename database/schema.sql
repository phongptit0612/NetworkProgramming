-- MySQL 8+. Khung CSDL cho giai doan tich hop JDBC; server hien CHUA ghi CSDL.
CREATE DATABASE IF NOT EXISTS vigenere_chat
    CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
USE vigenere_chat;

CREATE TABLE IF NOT EXISTS analysis_history (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    client_address VARCHAR(255) NOT NULL,
    ciphertext MEDIUMTEXT NOT NULL,
    plaintext MEDIUMTEXT NOT NULL,
    most_frequent_characters JSON NOT NULL,
    occurrence_count INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS file_transfers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    client_address VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_path VARCHAR(1024) NOT NULL,
    size_bytes BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
