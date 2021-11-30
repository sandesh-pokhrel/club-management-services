drop table if exists purchase_category;
create table purchase_category
(
    category_id   int auto_increment primary key,
    category_name varchar(300) not null
);

drop table if exists purchase_sub_category;
create table purchase_sub_category
(
    sub_category_id int auto_increment primary key,
    category_id int not null,
    sub_category_name varchar(300) not null,
    base_rate double null,
    amount double not null,
    payment_interval varchar(50) null,
    commission double null,
    appts double null,
    pays_on_completion boolean null,
    constraint fk_purchase_sub_cateogry_categoryid
        foreign key (category_id) references purchase_category (category_id)
);

