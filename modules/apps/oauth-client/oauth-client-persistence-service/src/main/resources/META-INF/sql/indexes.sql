create index IX_51E47B3C on OAuthClientASLocalMetadata (companyId);
create unique index IX_DA54D59 on OAuthClientASLocalMetadata (issuer[$COLUMN_LENGTH:75$]);
create unique index IX_5AF68FF on OAuthClientASLocalMetadata (localWellKnownURIOAS[$COLUMN_LENGTH:75$]);
create unique index IX_5B2CFE7 on OAuthClientASLocalMetadata (localWellKnownURIOIC[$COLUMN_LENGTH:75$]);
create index IX_D41859A6 on OAuthClientASLocalMetadata (userId);

create unique index IX_FEC415C2 on OAuthClientEntry (companyId, authServerWellKnownURI[$COLUMN_LENGTH:256$], clientId[$COLUMN_LENGTH:256$]);
create index IX_29A83E50 on OAuthClientEntry (userId);