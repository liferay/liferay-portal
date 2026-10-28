create table AssetListEntry (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	assetListEntryId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	assetListEntryKey VARCHAR(75) null,
	title VARCHAR(75) null,
	type_ INTEGER,
	assetEntrySubtype VARCHAR(255) null,
	assetEntryType VARCHAR(255) null,
	lastPublishDate DATE null,
	primary key (assetListEntryId, ctCollectionId)
);

create table AssetListEntryAssetEntryRel (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	assetListEntryAssetEntryRelId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	assetEntryId LONG,
	assetListEntryId LONG,
	audienceEntryERC VARCHAR(75) null,
	segmentsEntryId LONG,
	position INTEGER,
	lastPublishDate DATE null,
	primary key (assetListEntryAssetEntryRelId, ctCollectionId)
);

create table AssetListEntryAudienceEntryRel (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	alEntryAudienceEntryRelId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	assetListEntryId LONG,
	audienceEntryERC VARCHAR(75) null,
	priority INTEGER,
	typeSettings TEXT null,
	lastPublishDate DATE null,
	primary key (alEntryAudienceEntryRelId, ctCollectionId)
);

create table AssetListEntrySegmentsEntryRel (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	alEntrySegmentsEntryRelId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	assetListEntryId LONG,
	priority INTEGER,
	segmentsEntryId LONG,
	typeSettings TEXT null,
	lastPublishDate DATE null,
	primary key (alEntrySegmentsEntryRelId, ctCollectionId)
);

create table AssetListEntryUsage (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	assetListEntryUsageId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	containerKey VARCHAR(255) null,
	containerType LONG,
	key_ VARCHAR(255) null,
	plid LONG,
	type_ INTEGER,
	lastPublishDate DATE null,
	primary key (assetListEntryUsageId, ctCollectionId)
);