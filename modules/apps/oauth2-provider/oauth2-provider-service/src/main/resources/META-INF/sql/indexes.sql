create index IX_87DAF9C3 on OA2Auths_OA2ScopeGrants (companyId);
create index IX_2F541817 on OA2Auths_OA2ScopeGrants (oAuth2ScopeGrantId);

create index IX_523E5C67 on OAuth2Application (companyId, clientId[$COLUMN_LENGTH:75$]);
create index IX_949C9C01 on OAuth2Application (companyId, clientProfile);
create unique index IX_67BC29B0 on OAuth2Application (companyId, externalReferenceCode[$COLUMN_LENGTH:500$]);
create index IX_361558F9 on OAuth2Application (uuid_[$COLUMN_LENGTH:75$]);

create index IX_2F9EBCBB on OAuth2ApplicationScopeAliases (oAuth2ApplicationId);

create index IX_FB9F7B8A on OAuth2Authorization (companyId, accessTokenContentHash);
create index IX_673EE35 on OAuth2Authorization (companyId, refreshTokenContentHash);
create index IX_70DD169C on OAuth2Authorization (oAuth2ApplicationId);
create index IX_EFE93C04 on OAuth2Authorization (userId, oAuth2ApplicationId, rememberDeviceContent[$COLUMN_LENGTH:75$]);

create index IX_8E6F6B4B on OAuth2ScopeGrant (oA2AScopeAliasesId, companyId, applicationName[$COLUMN_LENGTH:255$], bundleSymbolicName[$COLUMN_LENGTH:255$], scope[$COLUMN_LENGTH:240$]);