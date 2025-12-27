

INSERT INTO payments.Payment_Method
(id, name, status, creationDate)
VALUES(1, 'APM', 1, '2025-08-08 21:29:49.25');



INSERT INTO payments.Payment_Type
(id, `type`, status, creationDate)
VALUES(1, 'SALE', 1, '2025-08-08 21:30:42.05');


INSERT INTO payments.Provider
(id, providerName, status, creationDate)
VALUES(1, 'STRIPE', 1, '2025-08-08 21:31:28.08');


INSERT INTO payments.Transaction_Status
(id, name, status, creationDate)
VALUES(1, 'CREATED', 1, '2025-08-08 21:33:39.84');
INSERT INTO payments.Transaction_Status
(id, name, status, creationDate)
VALUES(2, 'INITIATED', 1, '2025-08-08 21:33:39.84');
INSERT INTO payments.Transaction_Status
(id, name, status, creationDate)
VALUES(3, 'PENDING', 1, '2025-08-08 21:33:39.84');


INSERT INTO payments.Transaction_Status
(id, name, status, creationDate)
VALUES(4, 'SUCCESS', 1, '2025-08-08 21:33:39.84');
INSERT INTO payments.Transaction_Status
(id, name, status, creationDate)
VALUES(5, 'FAILED', 1, '2025-08-08 21:33:39.84');


