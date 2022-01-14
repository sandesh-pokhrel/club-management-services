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

drop table if exists client_assessment;
create table client_assessment
(
    id                           integer not null auto_increment,
    client_username              varchar(50),
    trainer_username             varchar(50),
    assessment_date              date,
    age                          integer,
    weight_pounds                double,
    height_feet                  double,
    height_inches                double,
    systolic_blood_pressure      double,
    diastolic_blood_pressure     double,
    pre_exercise_heart_rate      double,
    percent_body_fat             double,
    pushups                      integer,
    plank                        integer,
    grip_strength_left           double,
    grip_strength_right          double,
    straight_leg_raise_left      double,
    straight_leg_raise_right     double,
    shoulder_mobility_left       double,
    shoulder_mobility_right      double,
    deep_squat                   integer,
    hurdle_step_left             double,
    hurdle_step_right            double,
    trunk_stability_pushups      integer,
    inline_lunge_left            double,
    inline_lunge_right           double,
    rotatory_stability_left      double,
    rotatory_stability_right     double,
    unipedal_stance_open_left    double,
    unipedal_stance_open_right   double,
    unipedal_stance_closed_left  double,
    unipedal_stance_closed_right double,
    constraint pk_client_trainer_note_id primary key (id),
    constraint fk_client_assessment_clusername foreign key (client_username) references client (username),
    constraint fk_client_assessment_trusername foreign key (trainer_username)
        references club_management_auth.auth_user (username)
);

drop view if exists user;
create view user
as
select username
from club_management_auth.auth_user;
