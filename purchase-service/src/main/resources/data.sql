insert into purchase_category (category_name) values ('New Personal Training');
insert into purchase_category (category_name) values ('Consultations');

insert into purchase_sub_category (category_id, sub_category_name, base_rate, amount, payment_interval, commission,
                                   appts, pays_on_completion) values (1, 'New Personal Training(1)', 12, 48, 'Monthly', 5,
                                                                      30, true);
insert into purchase_sub_category (category_id, sub_category_name, base_rate, amount, payment_interval, commission,
                                   appts, pays_on_completion) values (1, 'New Personal Training(12)', 12, 576, 'Monthly', 5,
                                                                      30, true);
insert into purchase_sub_category (category_id, sub_category_name, base_rate, amount, payment_interval, commission,
                                   appts, pays_on_completion) values (1, 'New Personal Training(24)', 12, 1122, 'Monthly', 5,
                                                                      30, true);
insert into purchase_sub_category (category_id, sub_category_name, base_rate, amount, payment_interval, commission,
                                   appts, pays_on_completion) values (1, 'New Personal Training(36)', 12, 1638, 'Monthly', 5,
                                                                      30, true);

