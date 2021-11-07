create database club_management_clients;
use club_management_clients;

create table client
(
    username                varchar(30) not null,
    first_name              varchar(30) not null,
    last_name               varchar(30) not null,
    gender                  char        not null,
    dob                     date,
    email                   varchar(200) unique,
    cell_phone              varchar(20) unique,
    home_phone              varchar(20),
    work_phone              varchar(20),
    emergency_contact       varchar(150),
    emergency_contact_phone varchar(20),
    address                 varchar(100),
    city                    varchar(100),
    province                varchar(100),
    postal_code             integer,
    is_prospect             boolean,
    constraint pk_client_username primary key (username)
);
