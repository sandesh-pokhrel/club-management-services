drop table if exists purchase_category;
create table purchase_category
(
    category_id   int auto_increment primary key,
    category_name varchar(300) not null
);

drop table if exists purchase_sub_category;
create table purchase_sub_category
(
    sub_category_id    int auto_increment primary key,
    category_id        int          not null,
    sub_category_name  varchar(300) not null,
    base_rate          double       null,
    amount             double       not null,
    payment_interval   varchar(50)  null,
    commission         double       null,
    appts              double       null,
    pays_on_completion boolean      null,
    constraint fk_purchase_sub_cateogry_categoryid
        foreign key (category_id) references purchase_category (category_id)
);

drop table if exists client_purchase;
create table client_purchase
(
    id                  integer     not null auto_increment,
    client_username     varchar(30) not null,
    sub_category_id     integer     not null,
    appts               integer,
    sales_source        varchar(500),
    external_source     varchar(500),
    scheduled_by        varchar(200),
    ce1                 varchar(200),
    ce2                 varchar(200),
    payment_amount      double      not null,
    initial_downpayment double,
    no_of_postdates     integer,
    first_postdate      date,
    appt_scheduled integer default 0,
    constraint fk_client_purchase_clusername foreign key (client_username)
        references club_management_clients.client (username),
    constraint fk_client_purchase_subcatid foreign key (sub_category_id)
        references purchase_sub_category (sub_category_id),
    constraint pk_client_purchase_id primary key (id)
);

drop table if exists schedule;
create table schedule
(
    id               integer      not null auto_increment,
    trainer_username varchar(30)  not null,
    client_username  varchar(30)  not null,
    subject          varchar(500) not null,
    start_time       datetime     not null,
    end_time         datetime     not null,
    status varchar(50) default 'CREATED',
    is_read_only boolean DEFAULT FALSE,
    sub_category_id integer,
    constraint pk_schedule_id primary key (id),
    constraint fk_schedule_trainer_username foreign key (trainer_username) references club_management_auth.auth_user (username),
    constraint fk_schedule_client_username foreign key (client_username) references club_management_clients.client (username),
    constraint fk_schedule_subcatid foreign key (sub_category_id) references purchase_sub_category (sub_category_id)
);