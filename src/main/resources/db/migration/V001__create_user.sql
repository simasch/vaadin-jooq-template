create table "user"
(
    username        varchar(100) primary key,
    first_name      varchar(100) not null,
    last_name       varchar(100) not null,
    hashed_password varchar      not null,
    picture         bytea
);

create table user_role
(
    username varchar(100) not null,
    role     varchar(100) not null,

    primary key (username, role)
);