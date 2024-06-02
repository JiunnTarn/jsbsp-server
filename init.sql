create table if not exists member
(
    id           integer     not null
    constraint member_pk
    primary key,
    phone_number varchar(11) not null
    constraint member_uk_phone_number
    unique,
    name         varchar(10) not null,
    status       smallint    not null,
    password     text        not null,
    cx369_token  text        not null
    );

alter table member
    owner to jsbsp_admin;

create table if not exists block_info
(
    member_id integer   not null
    constraint block_info_pk
    primary key
    constraint block_info_member_id_fk
    references member
    on delete cascade,
    time      timestamp not null,
    reason    text      not null
);

alter table block_info
    owner to jsbsp_admin;

create table if not exists activate_info
(
    member_id integer   not null
    constraint activate_info_pk
    primary key
    constraint activate_info_member_id_fk
    references member
    on delete cascade,
    time      timestamp not null
);

alter table activate_info
    owner to jsbsp_admin;

