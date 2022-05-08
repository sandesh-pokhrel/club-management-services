-- ------------- SCHEMA RELATED -- -------------------------
-- clients database
drop database if exists club_management_clients;
create database club_management_clients;
use club_management_clients;

drop table if exists club;
create table club
(
    id       integer      not null auto_increment,
    name     varchar(500) not null,
    location varchar(100) not null,
    constraint pk_club_id primary key (id)
);

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
    club_id                 integer     not null,
	dependent_username varchar(30),
    dependent_relation varchar(100),
    constraint pk_client_username primary key (username),
    constraint fk_client_club_id foreign key (club_id) references club (id),
	constraint fk_client_dependent_username foreign key (dependent_username) references client(username)
);

drop table if exists service;
create table service (
  id integer not null auto_increment,
  name varchar(200),
  constraint pk_services_id primary key (id)
);

drop table if exists questionnaire;
create table questionnaire
(
    id         integer                          not null auto_increment,
    question   varchar(3000) character set utf8 not null,
    input_type varchar(100)                     not null,
    options    varchar(5000),
    enabled    boolean,
	service_id integer,
    constraint pk_questionnaire_id primary key (id),
	constraint fk_questionnaire_service_id foreign key (service_id) references service(id)
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


-- Auth database
SET SESSION sql_require_primary_key = 0;
drop database if exists club_management_auth;
create database club_management_auth;
use club_management_auth;

drop table if exists oauth_access_token;
create table oauth_access_token
(
    token_id          VARCHAR(255),
    token             varbinary(4096),
    authentication_id VARCHAR(255),
    user_name         VARCHAR(255),
    client_id         VARCHAR(255),
    authentication    varbinary(4096),
    refresh_token     VARCHAR(255)
);

drop table if exists oauth_refresh_token;
create table oauth_refresh_token
(
    token_id       VARCHAR(255),
    token          varbinary(4096),
    authentication varbinary(4096)
);

drop table if exists oauth_code;
create table oauth_code
(
    code           VARCHAR(255),
    authentication varbinary(4096)
);

drop table if exists oauth_approvals;
create table oauth_approvals
(
    userId         VARCHAR(255),
    clientId       VARCHAR(255),
    scope          VARCHAR(255),
    status         VARCHAR(10),
    expiresAt      TIMESTAMP,
    lastModifiedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

drop table if exists ClientDetails;
create table ClientDetails
(
    appId                  VARCHAR(255) PRIMARY KEY,
    resourceIds            VARCHAR(255),
    appSecret              VARCHAR(255),
    scope                  VARCHAR(255),
    grantTypes             VARCHAR(255),
    redirectUrl            VARCHAR(255),
    authorities            VARCHAR(255),
    access_token_validity  INTEGER,
    refresh_token_validity INTEGER,
    additionalInformation  VARCHAR(4096),
    autoApproveScopes      VARCHAR(255)
);

create index oauth_access_token_id on oauth_access_token (token_id);
create index oauth_refresh_token_id on oauth_access_token (token_id);


-- START CLIENT CREDENTIAL TABLES--
drop table if exists oauth_client_details;
create table oauth_client_details
(
    client_id               VARCHAR(256) PRIMARY KEY,
    resource_ids            VARCHAR(256),
    client_secret           VARCHAR(256),
    scope                   VARCHAR(256),
    authorized_grant_types  VARCHAR(256),
    web_server_redirect_uri VARCHAR(256),
    authorities             VARCHAR(256),
    access_token_validity   INTEGER,
    refresh_token_validity  INTEGER,
    additional_information  VARCHAR(4096),
    autoapprove             VARCHAR(256)
);

drop table if exists oauth_client_token;
create table oauth_client_token
(
    token_id          VARCHAR(255),
    token             varbinary(4096),
    authentication_id VARCHAR(255),
    user_name         VARCHAR(255),
    client_id         VARCHAR(255)
);

drop table if exists user_level;
create table user_level
(
    id              integer      not null auto_increment,
    level_name      varchar(200) not null,
    level_seniority integer      not null,
    constraint pk_user_level_id primary key (id)
);

drop table if exists auth_user;
create table auth_user
(
    username          varchar(30),
    password          varchar(200),
    email             varchar(100),
    cell_phone        varchar(20),
    first_name        varchar(100),
    last_name         varchar(100),
    level             integer not null,
    custom_rate       double,
    group_custom_rate double,
    club_id           integer not null,
    constraint pk_auth_user_username primary key (username),
    constraint fk_auth_user_club_id foreign key (club_id) references club_management_clients.club (id),
    constraint fk_auth_user_level
        foreign key (level) references user_level (id)
);

drop table if exists auth_role;
create table auth_role
(
    id          integer not null auto_increment,
    name        varchar(100),
    description varchar(1000),
    constraint pk_auth_role_id primary key (id)
);

drop table if exists user_role;
CREATE TABLE user_role
(
    username varchar(30) NOT NULL,
    role_id  integer     NOT NULL,
    constraint fk_user_role_username foreign key (username) references auth_user (username),
    constraint fk_user_role_role_id foreign key (role_id) references auth_role (id)
);

drop table if exists trainer_working_hour;
create table trainer_working_hour
(
    id         integer      not null auto_increment,
    username   varchar(100) not null,
    day        varchar(100) not null,
    start_hour time,
    end_hour   time,
    constraint chk_user_working_hour_start_hour check (start_hour >= '04:00:00'),
    constraint chk_user_working_hour_end_hour check (end_hour <= '22:00:00'),
    constraint chk_user_working_hour_start_end_hour check (end_hour > start_hour),
    constraint chk_user_working_hour_day check ( day in ('SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT') ),
    constraint unq_user_working_hour_username_day unique (username, day),
    constraint fk_user_working_hour_username foreign key (username) references auth_user (username),
    constraint pk_user_working_hour_id primary key (id)
);

-- Back to clients database
use club_management_clients;
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

drop table if exists client_goal;
create table client_goal
(
    id              integer      not null auto_increment,
    client_username varchar(100) not null,
    first_goal      varchar(500),
    second_goal     varchar(500),
    third_goal      varchar(500),
    first_obstacle  varchar(500),
    second_obstacle varchar(500),
    third_obstacle  varchar(500),
    prescription    varchar(3000),
    objection       varchar(500),
    constraint pk_client_goals_id primary key (id),
    constraint fk_client_goals_clusername foreign key (client_username) references client (username)
);

drop view if exists user;
create view user
as
select username
from club_management_auth.auth_user;


-- purchase database
drop database if exists club_management_purchase;
create database club_management_purchase;
use club_management_purchase;

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
    id                    integer     not null auto_increment,
    client_username       varchar(30) not null,
    sub_category_id       integer     not null,
    appts                 integer,
    sales_source          varchar(500),
    external_source       varchar(500),
    scheduled_by          varchar(200),
    ce1                   varchar(200),
    ce2                   varchar(200),
    payment_amount        double      not null,
    initial_downpayment   double,
    no_of_postdates       integer,
    first_postdate        date,
    appt_scheduled        integer default 0,
    purchase_date         date,
    payment_interval      varchar(50),
    payment_method        varchar(50),
    payment_method_ref_no varchar(50),
    constraint fk_client_purchase_clusername foreign key (client_username)
        references club_management_clients.client (username),
    constraint fk_client_purchase_subcatid foreign key (sub_category_id)
        references purchase_sub_category (sub_category_id),
    constraint pk_client_purchase_id primary key (id)
);

drop table if exists client_purchase_installment;
create table client_purchase_installment
(
    id                 integer not null auto_increment,
    client_purchase_id integer,
    amount             double  not null,
    expected_pay_date  date    not null,
    actual_pay_date    date,
    is_pif             boolean default false,
    status             varchar(100),
    payment_method     varchar(50),
    constraint pk_client_purchase_installment_id primary key (id),
    constraint fk_client_purchase_installment_cpid foreign key (client_purchase_id) references client_purchase (id)
);

drop table if exists schedule;
create table schedule
(
    id                   integer      not null auto_increment,
    trainer_username     varchar(30)  not null,
    client_username      varchar(30)  not null,
    purchase_id          integer      not null,
    subject              varchar(500) not null,
    start_time           datetime     not null,
    end_time             datetime     not null,
    status               varchar(50) default 'CREATED',
    is_read_only         boolean     DEFAULT FALSE,
    sub_category_id      integer,
    series_identifier    varchar(50),
    recurrence_rule      varchar(500),
    recurrence_exception varchar(500),
    deleted_count        integer     default 0,
    recurrence_id        integer,
    constraint pk_schedule_id primary key (id),
    constraint fk_schedule_trainer_username foreign key (trainer_username) references club_management_auth.auth_user (username),
    constraint fk_schedule_client_username foreign key (client_username) references club_management_clients.client (username),
    constraint fk_schedule_purchase_id foreign key (purchase_id) references client_purchase (id),
    constraint fk_schedule_subcatid foreign key (sub_category_id) references purchase_sub_category (sub_category_id),
    constraint fk_schedule_recurrence_id foreign key (recurrence_id) references schedule (id)
);


-- ------------- DATA RELATED -- -------------------------
use club_management_clients;
insert into club (name, location) values ('Chicago Gym', 'Chicago');
insert into club (name, location) values ('Texas Gym', 'Texas');

insert into questionnaire (question, input_type, options, enabled)
values ('What is you major intention of joining gym?', 'textarea', null, true);
commit;

use club_management_auth;
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

-- user admin and password admin@123
insert into auth_user
values ('admin', '$2a$12$95R7Hnlh6DtddUvlBkKucOSaIwTm7aTvVftHDCMVfD.Tdo2aE4gR.',
        'admin@allclubsystem.com', '9842100964', 'Admin', 'Admin', 1, 100, 500, 1);
insert into auth_role (name, description)
values ('ROLE_USER', 'Simple User - Has basic privileges');
insert into auth_role (name, description)
values ('ROLE_ADMIN', 'Admin User - Has permission to perform admin tasks');
insert into user_role (username, role_id)
values ('admin', 2);
commit;

use club_management_purchase;
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

insert into purchase_sub_category (category_id, sub_category_name, base_rate, amount, payment_interval, commission,
                                   appts, pays_on_completion) values (2, 'Initial Consultation', 15, 155, 'Weekly', 5,
                                                                      10, false);
insert into purchase_sub_category (category_id, sub_category_name, base_rate, amount, payment_interval, commission,
                                   appts, pays_on_completion) values (2, 'Annual Consulation Package', 670, 1638, 'Weekly', 6,
                                                                      15, false);
insert into purchase_sub_category (category_id, sub_category_name, base_rate, amount, payment_interval, commission,
                                   appts, pays_on_completion) values (2, 'Annual Consultation', 25, 890, 'Weekly', 8,
                                                                      20, true);
commit;

