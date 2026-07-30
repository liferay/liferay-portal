create table PLOEntry (
	mvccVersion LONG default 0 not null,
	externalReferenceCode VARCHAR(75) null,
	ploEntryId LONG not null primary key,
	companyId LONG,
	userId LONG,
	createDate DATE null,
	modifiedDate DATE null,
	key_ VARCHAR(1000) null,
	languageId VARCHAR(75) null,
	value TEXT null
);