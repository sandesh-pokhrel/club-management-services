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
