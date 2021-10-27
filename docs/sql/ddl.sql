create table user_profile, (user_id UUID not null, created timestamp not null, display_name varchar(100) not null, external_key UUID not null, oauth_key varchar(30) not null, primary key (user_id))
create index IDX8uhq4grv5e2ld3ve64wnf6hgc on user_profile, (created)
alter table user_profile, add constraint UK_jvqk0p33c6y8wwtioghxoxp2x unique (external_key)
alter table user_profile, add constraint UK_8arsuqlw8jxp7kmmatsm6cq8v unique (oauth_key)
