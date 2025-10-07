create table FriendlyURLEntry (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	defaultLanguageId VARCHAR(75) null,
	friendlyURLEntryId LONG not null,
	groupId LONG,
	companyId LONG,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	primary key (friendlyURLEntryId, ctCollectionId)
);

create table FriendlyURLEntryLocalization (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	friendlyURLEntryLocalizationId LONG not null,
	companyId LONG,
	friendlyURLEntryId LONG,
	languageId VARCHAR(75) null,
	groupId LONG,
	classNameId LONG,
	classPK LONG,
	urlTitle VARCHAR(255) null,
	primary key (friendlyURLEntryLocalizationId, ctCollectionId)
);

create table FriendlyURLEntryMapping (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	friendlyURLEntryMappingId LONG not null,
	companyId LONG,
	classNameId LONG,
	classPK LONG,
	friendlyURLEntryId LONG,
	primary key (friendlyURLEntryMappingId, ctCollectionId)
);