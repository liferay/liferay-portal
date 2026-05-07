create unique index IX_A6BA8E81 on OAuthClientASLocalMetadata (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_328DCB29 on OAuthClientASLocalMetadata (companyId, issuer[$COLUMN_LENGTH:75$]);
create index IX_CEF2762B on OAuthClientASLocalMetadata (companyId, localWellKnownEnabled);
create unique index IX_E5878996 on OAuthClientASLocalMetadata (companyId, localWellKnownURI[$COLUMN_LENGTH:256$]);
create unique index IX_B2201FE9 on OAuthClientASLocalMetadata (companyId, oAuthASLocalWellKnownURI[$COLUMN_LENGTH:256$]);
create index IX_D41859A6 on OAuthClientASLocalMetadata (userId);
create index IX_F1AD4AC8 on OAuthClientASLocalMetadata (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_FEC415C2 on OAuthClientEntry (companyId, authServerWellKnownURI[$COLUMN_LENGTH:256$], clientId[$COLUMN_LENGTH:256$]);
create unique index IX_5A4A92AB on OAuthClientEntry (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_29A83E50 on OAuthClientEntry (userId);
create index IX_999928DE on OAuthClientEntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_AA37A6B1 on OAuthClientPRLocalMetadata (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_D26F8E5B on OAuthClientPRLocalMetadata (companyId, localWellKnownEnabled);
create unique index IX_E3839C6 on OAuthClientPRLocalMetadata (companyId, localWellKnownURI[$COLUMN_LENGTH:256$]);
create unique index IX_BF510D8E on OAuthClientPRLocalMetadata (companyId, resource[$COLUMN_LENGTH:256$]);
create index IX_96EB4DD6 on OAuthClientPRLocalMetadata (userId);
create index IX_6B931098 on OAuthClientPRLocalMetadata (uuid_[$COLUMN_LENGTH:75$]);