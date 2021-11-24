drop database if exists club_management_clients;
create database club_management_clients;
use club_management_clients;

drop table if exists client;
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

drop table if exists questionnaire;
create table questionnaire
(
    id         integer                          not null auto_increment,
    question   varchar(3000) character set utf8 not null,
    input_type varchar(100)                     not null,
    options    varchar(5000),
    enabled    boolean,
    constraint pk_questionnaire_id primary key (id)
);

drop table if exists client_questionnaire;
create table client_questionnaire
(
    id              integer     not null auto_increment,
    client_username varchar(30) not null,
    question_id     integer     not null,
    answer          varchar(4000),
    constraint fk_client_questionnaire_question_id foreign key (question_id) references questionnaire (id),
    constraint fk_client_questionnaire_clusername foreign key (client_username) references client (username),
    constraint pk_client_questionnaire_id primary key (id)
);

drop table if exists client_extra_info;
create table client_extra_info
(
    client_username         varchar(30),
    questionnaire_initiated boolean,
    questionnaire_serial    varchar(100),
    constraint pk_client_extra_info_clusername primary key (client_username),
    constraint fk_client_extra_info_clusername foreign key (client_username) references client (username)
);

drop table if exists client_trainer_note;
create table client_trainer_note
(
    id               integer not null auto_increment,
    client_username  varchar(30),
    trainer_username varchar(30),
    note             varchar(5000),
    created_date     date,
    constraint pk_client_trainer_note_id primary key (id),
    constraint fk_client_trainer_note_clusername foreign key (client_username) references client (username),
    constraint fk_client_trainer_note_trusername foreign key (trainer_username)
        references club_management_auth.auth_user (username)
);
