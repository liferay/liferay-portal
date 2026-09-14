create table AudiencesEntry (
	mvccVersion LONG default 0 not null,
	externalReferenceCode VARCHAR(75) null,
	audiencesEntryId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	json TEXT null,
	name VARCHAR(75) null
);

create table AudiencesEntryGroupRel (
	mvccVersion LONG default 0 not null,
	audiencesEntryGroupRelId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	audienceEntryERC VARCHAR(75) null,
	groupERC VARCHAR(75) null
);