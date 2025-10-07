create table SamlIbSloMessage (
	samlIbSloMessageId LONG not null primary key,
	companyId LONG,
	createDate DATE null,
	samlIdpEntityId VARCHAR(1024) null,
	logoutRequestXml TEXT null,
	samlIdpSessionIndex VARCHAR(200) null
);

create table SamlIdpSpConnection (
	samlIdpSpConnectionId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	samlSpEntityId VARCHAR(1024) null,
	assertionLifetime INTEGER,
	attributeNames STRING null,
	attributesEnabled BOOLEAN,
	attributesNamespaceEnabled BOOLEAN,
	enabled BOOLEAN,
	encryptionForced BOOLEAN,
	metadataUpdatedDate DATE null,
	metadataUrl VARCHAR(1024) null,
	metadataXml TEXT null,
	name VARCHAR(75) null,
	nameIdAttribute VARCHAR(1024) null,
	nameIdFormat VARCHAR(1024) null
);

create table SamlIdpSpSession (
	samlIdpSpSessionId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	samlIdpSsoSessionId LONG,
	samlPeerBindingId LONG
);

create table SamlIdpSsoSession (
	samlIdpSsoSessionId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	samlIdpSsoSessionKey VARCHAR(75) null
);

create table SamlPeerBinding (
	samlPeerBindingId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	samlPeerEntityId VARCHAR(1024) null,
	deleted BOOLEAN,
	samlNameIdFormat VARCHAR(128) null,
	samlNameIdNameQualifier VARCHAR(1024) null,
	samlNameIdSpNameQualifier VARCHAR(75) null,
	samlNameIdSpProvidedId VARCHAR(75) null,
	samlNameIdValue VARCHAR(1024) null
);

create table SamlSpAuthRequest (
	samlSpAuthnRequestId LONG not null primary key,
	companyId LONG,
	createDate DATE null,
	samlIdpEntityId VARCHAR(1024) null,
	samlRelayState VARCHAR(2048) null,
	samlSpAuthRequestKey VARCHAR(75) null
);

create table SamlSpIdpConnection (
	samlSpIdpConnectionId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	samlIdpEntityId VARCHAR(1024) null,
	assertionSignatureRequired BOOLEAN,
	clockSkew LONG,
	enabled BOOLEAN,
	forceAuthn BOOLEAN,
	ldapImportEnabled BOOLEAN,
	metadataUpdatedDate DATE null,
	metadataUrl VARCHAR(1024) null,
	metadataXml TEXT null,
	name VARCHAR(75) null,
	nameIdFormat VARCHAR(1024) null,
	signAuthnRequest BOOLEAN,
	unknownUsersAreStrangers BOOLEAN,
	userAttributeMappings STRING null,
	userIdentifierExpression VARCHAR(200) null
);

create table SamlSpMessage (
	samlSpMessageId LONG not null primary key,
	companyId LONG,
	createDate DATE null,
	samlIdpEntityId VARCHAR(1024) null,
	expirationDate DATE null,
	samlIdpResponseKey VARCHAR(75) null
);

create table SamlSpSession (
	samlSpSessionId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	samlPeerBindingId LONG,
	assertionXml TEXT null,
	jSessionId VARCHAR(200) null,
	samlSpSessionKey VARCHAR(75) null,
	sessionIndex VARCHAR(200) null,
	terminated_ BOOLEAN
);