create table SiteFriendlyURL (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	siteFriendlyURLId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	friendlyURL VARCHAR(75) null,
	languageId VARCHAR(75) null,
	lastPublishDate DATE null
);

create table SiteSitemapRegenerationEntry (
	mvccVersion LONG default 0 not null,
	siteSitemapRegenerationEntryId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	assetTypeKey VARCHAR(75) null
);