create table LayoutContentVersion (
	mvccVersion LONG default 0 not null,
	externalReferenceCode VARCHAR(75) null,
	layoutContentVersionId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	data_ TEXT null,
	dataHash VARCHAR(75) null,
	name STRING null,
	plid LONG,
	specSchemaVersion VARCHAR(75) null,
	version INTEGER,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null
);

create table LayoutContentVersionPreview (
	mvccVersion LONG default 0 not null,
	layoutContentVersionPreviewId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	layoutContentVersionId LONG,
	html TEXT null,
	languageId VARCHAR(75) null,
	segmentsExperienceERC VARCHAR(75) null
);