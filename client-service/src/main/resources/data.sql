-- Client table inserts
insert into client (username, first_name, last_name, gender, dob, email, cell_phone, home_phone, work_phone,
                    emergency_contact, emergency_contact_phone, address, city, province, postal_code, is_prospect)
values ('sandesh', 'Sandesh', 'Pokhrel', 'M', '1996-07-03', 'sandesh.pokhrel56@gmail.com', '9842100964',
        null, null, 'John Davis', '98343425234', null, null, null, null, false);

insert into client (username, first_name, last_name, gender, dob, email, cell_phone, home_phone, work_phone,
                    emergency_contact,
                    emergency_contact_phone, address, city, province, postal_code, is_prospect)
values ('sandesh1', 'Sandesh', 'Pokhrel', 'M', '1996-07-03', 'sandesh.pokhrel561@gmail.com',
        '98421009641', '', '', 'Rimesh Sapkota', '9832445353', 'Biratnagar',
        'Biratnagar', 'Province 1', 56613, false);

insert into client (username, first_name, last_name, gender, dob, email, cell_phone, home_phone, work_phone,
                    emergency_contact,
                    emergency_contact_phone, address, city, province, postal_code, is_prospect)
values ('sandesh2', 'Sandesh', 'Pokhrel', 'M', '1996-07-03', 'sandesh.pokhrel562@gmail.com',
        '98421009642', '', '', 'Rimesh Sapkota', '9832445353', 'Biratnagar',
        'Biratnagar', 'Province 1', 56613, false);

insert into client (username, first_name, last_name, gender, dob, email, cell_phone, home_phone, work_phone,
                    emergency_contact,
                    emergency_contact_phone, address, city, province, postal_code, is_prospect)
values ('sandesh3', 'Sandesh', 'Pokhrel', 'M', '1996-07-03', 'sandesh.pokhrel563@gmail.com',
        '98421009643', '', '', 'Rimesh Sapkota', '9832445353', 'Biratnagar',
        'Biratnagar', 'Province 1', 56613, false);

insert into client (username, first_name, last_name, gender, dob, email, cell_phone, home_phone, work_phone,
                    emergency_contact,
                    emergency_contact_phone, address, city, province, postal_code, is_prospect)
values ('sandesh4', 'Sandesh', 'Pokhrel', 'M', '1996-07-03', 'sandesh.pokhrel564@gmail.com',
        '98421009644', '', '', 'Rimesh Sapkota', '9832445353', 'Biratnagar',
        'Biratnagar', 'Province 1', 56613, false);

COMMIT;

-- Questionnaire table inserts
insert into questionnaire (question, input_type, options)
values ('What is you major intention of joining gym?', 'textarea', null);

insert into questionnaire (question, input_type, options)
values ('Please rate how much muscle you intend to make?', 'rating', 5);
