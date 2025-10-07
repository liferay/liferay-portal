create table LayoutClassedModelUsage (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	layoutClassedModelUsageId LONG not null,
	groupId LONG,
	companyId LONG,
	createDate DATE null,
	modifiedDate DATE null,
	classExternalReferenceCode VARCHAR(75) null,
	classNameId LONG,
	classPK LONG,
	containerKey VARCHAR(200) null,
	containerType LONG,
	plid LONG,
	type_ INTEGER,
	lastPublishDate DATE null,
	primary key (layoutClassedModelUsageId, ctCollectionId)
);

create table LayoutLocalization (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	layoutLocalizationId LONG not null,
	groupId LONG,
	companyId LONG,
	createDate DATE null,
	modifiedDate DATE null,
	content TEXT null,
	languageId VARCHAR(75) null,
	plid LONG,
	lastPublishDate DATE null,
	primary key (layoutLocalizationId, ctCollectionId)
);