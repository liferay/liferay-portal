create index IX_396C5BCB on OpenIdConnectSession (accessTokenExpirationDate);
create index IX_8B81B4DD on OpenIdConnectSession (issuer[$COLUMN_LENGTH:255$], companyId, sessionId[$COLUMN_LENGTH:255$]);
create unique index IX_60980B41 on OpenIdConnectSession (userId, authServerWellKnownURI[$COLUMN_LENGTH:255$], clientId[$COLUMN_LENGTH:255$]);
create unique index IX_C1F8E7E on OpenIdConnectSession (userId, issuer[$COLUMN_LENGTH:255$]);

create unique index IX_2B0E351D on OpenIdConnectUser (companyId, issuer[$COLUMN_LENGTH:255$], subject[$COLUMN_LENGTH:75$]);
create index IX_85E2536C on OpenIdConnectUser (companyId, userId);