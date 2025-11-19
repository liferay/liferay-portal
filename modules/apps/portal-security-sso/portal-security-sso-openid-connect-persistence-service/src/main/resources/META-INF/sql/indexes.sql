create index IX_396C5BCB on OpenIdConnectSession (accessTokenExpirationDate);
create index IX_AE077141 on OpenIdConnectSession (authServerWellKnownURI[$COLUMN_LENGTH:256$], clientId[$COLUMN_LENGTH:256$], companyId);
create unique index IX_1EEEA291 on OpenIdConnectSession (authServerWellKnownURI[$COLUMN_LENGTH:256$], sid[$COLUMN_LENGTH:75$]);
create unique index IX_43108341 on OpenIdConnectSession (authServerWellKnownURI[$COLUMN_LENGTH:256$], userId, clientId[$COLUMN_LENGTH:256$]);
create index IX_2C256391 on OpenIdConnectSession (userId);

create unique index IX_2B0E351D on OpenIdConnectUser (companyId, issuer[$COLUMN_LENGTH:75$], subject[$COLUMN_LENGTH:75$]);
create index IX_85E2536C on OpenIdConnectUser (companyId, userId);