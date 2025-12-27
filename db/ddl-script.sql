DROP DATABASE IF EXISTS payments;

DROP USER IF EXISTS 'payments'@'%';

-- Creates databases // /**/
CREATE DATABASE payments;

-- Creates user & grants permission
CREATE USER 'payments'@'%' IDENTIFIED BY 'P9v@tX3#nLz!Q8wK';


-- GRANT START Either this 
-- GRANT ALL ON *.* TO 'payments'@'%' ;

-- or this
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, RELOAD, PROCESS, REFERENCES, INDEX, ALTER, SHOW DATABASES, CREATE TEMPORARY TABLES, LOCK TABLES, EXECUTE, REPLICATION SLAVE, REPLICATION CLIENT, CREATE VIEW, SHOW VIEW, CREATE ROUTINE, ALTER ROUTINE, CREATE USER, EVENT, TRIGGER ON *.* TO 'payments'@'%' ;
-- GRANT END Either this.

-- Create Tables payments Schema Start***
CREATE TABLE payments.`Payment_Method` (
 `id` int NOT NULL,
 `name` varchar(50) NOT NULL,
 `status` tinyint DEFAULT 1,
 `creationDate` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE payments.`Payment_Type` (
 `id` int NOT NULL,
 `type` varchar(50) NOT NULL,
 `status` tinyint DEFAULT 1,
 `creationDate` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE payments.`Provider` (
 `id` int NOT NULL AUTO_INCREMENT,
 `providerName` varchar(50) NOT NULL,
 `status` tinyint DEFAULT 1,
 `creationDate` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE payments.`Transaction_Status` (
 `id` int NOT NULL,
 `name` varchar(50) NOT NULL,
 `status` tinyint DEFAULT 1,
 `creationDate` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE payments.`Transaction` (
 `id` int NOT NULL AUTO_INCREMENT,
 `userId` int NOT NULL,
 
 `paymentMethodId` int NOT NULL,
 `providerId` int NOT NULL,
 `paymentTypeId` int NOT NULL,
 `txnStatusId` int NOT NULL,
 
 `amount` decimal(19,2) DEFAULT '0.00',
 `currency` varchar(3) NOT NULL,
 
 `merchantTransactionReference` varchar(50) NOT NULL,
 `txnReference` varchar(50) NOT NULL,
 `providerReference` varchar(100) DEFAULT NULL, 
 
 `errorCode` varchar(500) DEFAULT NULL,
 `errorMessage` varchar(1000) DEFAULT NULL,

 `creationDate` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
 `retryCount` int DEFAULT 0,
 PRIMARY KEY (`id`),
 UNIQUE KEY `transaction_txnReference` (`txnReference`),
 KEY `transaction_paymentMethodId` (`paymentMethodId`),
 KEY `transaction_providerId` (`providerId`),
 KEY `transaction_txnStatusId` (`txnStatusId`),
 kEY `transaction_paymentTypeId` (`paymentTypeId`),
 CONSTRAINT `transaction_paymentMethodId` FOREIGN KEY (`paymentMethodId`) REFERENCES `Payment_Method` (`id`),
 CONSTRAINT `transaction_providerId` FOREIGN KEY (`providerId`) REFERENCES `Provider` (`id`),
 CONSTRAINT `transaction_txnStatusId` FOREIGN KEY (`txnStatusId`) REFERENCES `Transaction_Status` (`id`),
 CONSTRAINT `transaction_paymentTypeId` FOREIGN KEY (`paymentTypeId`) REFERENCES `Payment_Type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE payments.`Transaction_Log` (
 `id` int NOT NULL AUTO_INCREMENT,
 `transactionId` int NOT NULL,
 `txnFromStatus` varchar(50) DEFAULT '-1',
 `txnToStatus` varchar(50) DEFAULT '-1',
 `creationDate` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
 PRIMARY KEY (`id`),
 KEY `transaction_log_transactionId` (`transactionId`),
 CONSTRAINT `transaction_log_transactionId` FOREIGN KEY (`transactionId`) REFERENCES `Transaction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- Create Tables payments Schema End***


