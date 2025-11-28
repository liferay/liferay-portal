create table OAuthClientASLocalMetadata (
	mvccVersion LONG default 0 not null,
	oAuthClientASLocalMetadataId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	issuer VARCHAR(75) null,
	localWellKnownEnabled BOOLEAN,
	localWellKnownURIOAS VARCHAR(256) null,
	localWellKnownURIOIC VARCHAR(256) null,
	metadataJSONOAS TEXT null,
	metadataJSONOIC TEXT null
);

create table OAuthClientEntry (
	mvccVersion LONG default 0 not null,
	oAuthClientEntryId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	authRequestParametersJSON VARCHAR(3999) null,
	authServerWellKnownURI VARCHAR(256) null,
	clientId VARCHAR(256) null,
	customClaimsJSON TEXT null,
	infoJSON TEXT null,
	metadataCacheTime LONG,
	oidcUserInfoMapperJSON VARCHAR(3999) null,
	tokenRequestParametersJSON VARCHAR(3999) null
);