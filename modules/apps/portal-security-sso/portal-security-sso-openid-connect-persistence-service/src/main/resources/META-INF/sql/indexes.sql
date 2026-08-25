create index IX_396C5BCB on OpenIdConnectSession (accessTokenExpirationDate);
create index IX_5D03D521 on OpenIdConnectSession (companyId, authServerWellKnownURI[$COLUMN_LENGTH:256$], clientId[$COLUMN_LENGTH:256$]);
create index IX_B1915EA3 on OpenIdConnectSession (companyId, issuer[$COLUMN_LENGTH:75$], sessionId[$COLUMN_LENGTH:75$]);
create unique index IX_60980B41 on OpenIdConnectSession (userId, authServerWellKnownURI[$COLUMN_LENGTH:256$], clientId[$COLUMN_LENGTH:256$]);
create unique index IX_C1F8E7E on OpenIdConnectSession (userId, issuer[$COLUMN_LENGTH:75$]);

create unique index IX_2B0E351D on OpenIdConnectUser (companyId, issuer[$COLUMN_LENGTH:75$], subject[$COLUMN_LENGTH:75$]);
create index IX_85E2536C on OpenIdConnectUser (companyId, userId);