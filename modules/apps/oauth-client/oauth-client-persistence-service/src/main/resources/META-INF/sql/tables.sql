create table OAuthClientASLocalMetadata (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	oAuthClientASLocalMetadataId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	issuer VARCHAR(75) null,
	localWellKnownEnabled BOOLEAN,
	localWellKnownURI VARCHAR(256) null,
	metadataJSON TEXT null,
	oAuthASLocalWellKnownURI VARCHAR(256) null,
	oAuthASMetadataJSON TEXT null
);

create table OAuthClientEntry (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
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
	matcherField VARCHAR(75) null,
	metadataCacheTime LONG,
	oidcUserInfoMapperJSON VARCHAR(3999) null,
	tokenRequestParametersJSON VARCHAR(3999) null
);

create table OAuthClientPRLocalMetadata (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	oAuthClientPRLocalMetadataId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	localWellKnownEnabled BOOLEAN,
	localWellKnownURI VARCHAR(256) null,
	metadataJSON TEXT null,
	resource VARCHAR(256) null
);