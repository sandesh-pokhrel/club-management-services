use club_management_auth;

-- BASIC AUTH with token time declarationuser_role
-- ACCESS TOKEN VALIDITY = 300 SECOND
-- REFRESH TOKEN VALIDITY = 1800 SECOND
-- insert client details [clientId = club_management_front & clientSecret = club_management_front@123321]
-- resource_ids represet individual resource owners 'club_management_front' is added and autoapprove set to 'true'
INSERT INTO oauth_client_details
(client_id, resource_ids, client_secret, scope, authorized_grant_types,
 authorities, access_token_validity, refresh_token_validity, autoapprove)
VALUES ('club_management_front', 'club_management_front',
        '$2a$12$QvOvgximnr/YN3SoE/ckrOWKyNLu3x7M3u4wQ9AzOCUgh3JgKmP4i',
        'read,write', 'password,refresh_token,client_credentials,authorization_code',
        'TRUSTED_CLUB_MANAGEMENT_CLIENT', 300, 1800, 'true');

insert into user_level (level_name, level_seniority)
values ('Entry Level Consultant', 7),
       ('Entry Level Trainer', 6),
       ('Master Personal Trainer', 5),
       ('Mid Level Consultant', 4),
       ('Mid Level Trainer', 3),
       ('Senior Level Consultant', 2),
       ('Senior Level Trainer', 1);

-- dummy username sandesh and password sandesh
insert into auth_user
values ('sandesh', '$2a$12$K89EqUvfERXAB0/diRrAb.OZdOLI2ZLpR1mTZysCB4qUspYj1e22y',
        'sandesh.pokhrel56@gmail.com', '9842100964', 'Sandesh', 'Pokhrel', 1, 100, 500);
insert into auth_role (name, description)
values ('ROLE_USER', 'Simple User - Has basic privileges');
insert into auth_role (name, description)
values ('ROLE_ADMIN', 'Admin User - Has permission to perform admin tasks');
insert into user_role (username, role_id)
values ('sandesh', 1);
COMMIT;
