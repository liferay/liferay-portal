create table AutoEscapeEntry (
	autoEscapeEntryId LONG not null primary key,
	autoEscapeDisabledColumn VARCHAR(75) null,
	autoEscapeEnabledColumn VARCHAR(75) null
);

create table BigDecimalEntries_LVEntries (
	companyId LONG not null,
	bigDecimalEntryId LONG not null,
	lvEntryId LONG not null,
	primary key (bigDecimalEntryId, lvEntryId)
);

create table BigDecimalEntry (
	bigDecimalEntryId LONG not null primary key,
	companyId LONG,
	bigDecimalValue BIGDECIMAL null
);

create table CacheDisabledEntry (
	cacheDisabledEntryId LONG not null primary key,
	name VARCHAR(75) null
);

create table CacheFieldEntry (
	cacheFieldEntryId LONG not null primary key,
	groupId LONG,
	name VARCHAR(75) null
);

create table CacheMissEntry (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	cacheMissEntryId LONG not null,
	primary key (cacheMissEntryId, ctCollectionId)
);

create table DSLQueryEntry (
	dslQueryEntryId LONG not null primary key,
	name VARCHAR(75) null
);

create table DSLQueryStatusEntry (
	dslQueryStatusEntryId LONG not null primary key,
	dslQueryEntryId LONG,
	status VARCHAR(75) null,
	statusDate DATE null
);

create table DataLimitEntry (
	dataLimitEntryId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null
);

create table DefinedDefaultOrderEntry (
	definedDefaultOrderEntryId LONG not null primary key,
	modifiedDate DATE null,
	name VARCHAR(75) null
);

create table ERCCompanyEntry (
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	ercCompanyEntryId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	column1 INTEGER
);

create table ERCGroupEntry (
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	ercGroupEntryId LONG not null primary key,
	groupId LONG,
	companyId LONG
);

create table ERCVersionedEntry (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	headId LONG,
	head BOOLEAN,
	ercVersionedEntryId LONG not null primary key,
	groupId LONG,
	companyId LONG
);

create table ERCVersionedEntryVersion (
	ercVersionedEntryVersionId LONG not null primary key,
	version INTEGER,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	ercVersionedEntryId LONG,
	groupId LONG,
	companyId LONG
);

create table EagerBlobEntry (
	uuid_ VARCHAR(75) null,
	eagerBlobEntryId LONG not null primary key,
	groupId LONG,
	blob_ BLOB
);

create table FinderWhereClauseEntry (
	finderWhereClauseEntryId LONG not null primary key,
	name VARCHAR(75) null,
	nickname VARCHAR(75) null
);

create table IndexEntry (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	externalReferenceCode VARCHAR(75) null,
	indexEntryId LONG not null,
	companyId LONG,
	ownerId LONG,
	ownerType INTEGER,
	plid LONG,
	portletId VARCHAR(75) null,
	primary key (indexEntryId, ctCollectionId)
);

create table LVEntries_BigDecimalEntries (
	companyId LONG not null,
	bigDecimalEntryId LONG not null,
	lvEntryId LONG not null,
	primary key (bigDecimalEntryId, lvEntryId)
);

create table LVEntry (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	headId LONG,
	head BOOLEAN,
	defaultLanguageId VARCHAR(75) null,
	lvEntryId LONG not null primary key,
	companyId LONG,
	groupId LONG,
	uniqueGroupKey VARCHAR(75) null
);

create table LVEntryLocalization (
	mvccVersion LONG default 0 not null,
	headId LONG,
	head BOOLEAN,
	lvEntryLocalizationId LONG not null primary key,
	companyId LONG,
	lvEntryId LONG,
	languageId VARCHAR(75) null,
	title VARCHAR(75) null,
	content VARCHAR(75) null
);

create table LVEntryLocalizationVersion (
	lvEntryLocalizationVersionId LONG not null primary key,
	version INTEGER,
	lvEntryLocalizationId LONG,
	companyId LONG,
	lvEntryId LONG,
	languageId VARCHAR(75) null,
	title VARCHAR(75) null,
	content VARCHAR(75) null
);

create table LVEntryVersion (
	lvEntryVersionId LONG not null primary key,
	version INTEGER,
	uuid_ VARCHAR(75) null,
	defaultLanguageId VARCHAR(75) null,
	lvEntryId LONG,
	companyId LONG,
	groupId LONG,
	uniqueGroupKey VARCHAR(75) null
);

create table LazyBlobEntry (
	uuid_ VARCHAR(75) null,
	lazyBlobEntryId LONG not null primary key,
	groupId LONG,
	blob1 BLOB,
	blob2 BLOB
);

create table LocalizedEntry (
	defaultLanguageId VARCHAR(75) null,
	localizedEntryId LONG not null primary key
);

create table LocalizedEntryLocalization (
	mvccVersion LONG default 0 not null,
	localizedEntryLocalizationId LONG not null primary key,
	localizedEntryId LONG,
	languageId VARCHAR(75) null,
	title VARCHAR(75) null,
	content VARCHAR(75) null
);

create table ManyColumnsEntry (
	manyColumnsEntryId LONG not null primary key,
	column1 INTEGER,
	column2 INTEGER,
	column3 INTEGER,
	column4 INTEGER,
	column5 INTEGER,
	column6 INTEGER,
	column7 INTEGER,
	column8 INTEGER,
	column9 INTEGER,
	column10 INTEGER,
	column11 INTEGER,
	column12 INTEGER,
	column13 INTEGER,
	column14 INTEGER,
	column15 INTEGER,
	column16 INTEGER,
	column17 INTEGER,
	column18 INTEGER,
	column19 INTEGER,
	column20 INTEGER,
	column21 INTEGER,
	column22 INTEGER,
	column23 INTEGER,
	column24 INTEGER,
	column25 INTEGER,
	column26 INTEGER,
	column27 INTEGER,
	column28 INTEGER,
	column29 INTEGER,
	column30 INTEGER,
	column31 INTEGER,
	column32 INTEGER,
	column33 INTEGER,
	column34 INTEGER,
	column35 INTEGER,
	column36 INTEGER,
	column37 INTEGER,
	column38 INTEGER,
	column39 INTEGER,
	column40 INTEGER,
	column41 INTEGER,
	column42 INTEGER,
	column43 INTEGER,
	column44 INTEGER,
	column45 INTEGER,
	column46 INTEGER,
	column47 INTEGER,
	column48 INTEGER,
	column49 INTEGER,
	column50 INTEGER,
	column51 INTEGER,
	column52 INTEGER,
	column53 INTEGER,
	column54 INTEGER,
	column55 INTEGER,
	column56 INTEGER,
	column57 INTEGER,
	column58 INTEGER,
	column59 INTEGER,
	column60 INTEGER,
	column61 INTEGER,
	column62 INTEGER,
	column63 INTEGER,
	column64 INTEGER
);

create table NestedSetsTreeEntry (
	nestedSetsTreeEntryId LONG not null primary key,
	groupId LONG,
	parentNestedSetsTreeEntryId LONG,
	leftNestedSetsTreeEntryId LONG,
	rightNestedSetsTreeEntryId LONG
);

create table NullConvertibleEntry (
	nullConvertibleEntryId LONG not null primary key,
	name VARCHAR(75) null
);

create table PermissionCheckFinderEntry (
	permissionCheckFinderEntryId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	integer_ INTEGER,
	name VARCHAR(75) null,
	type_ VARCHAR(75) null
);

create table RedundantIndexEntry (
	redundantIndexEntryId LONG not null primary key,
	companyId LONG,
	name VARCHAR(75) null
);

create table RenameFinderColumnEntry (
	renameFinderColumnEntryId LONG not null primary key,
	groupId LONG,
	columnToRename VARCHAR(75) null
);

create table UADPartialEntry (
	uadPartialEntryId LONG not null primary key,
	userId LONG,
	userName VARCHAR(75) null,
	message VARCHAR(75) null
);

create table UndefinedDefaultOrderEntry (
	undefinedDefaultOrderEntryId LONG not null primary key,
	modifiedDate DATE null,
	name VARCHAR(75) null
);

create table VersionedEntry (
	mvccVersion LONG default 0 not null,
	headId LONG,
	head BOOLEAN,
	versionedEntryId LONG not null primary key,
	groupId LONG
);

create table VersionedEntryVersion (
	versionedEntryVersionId LONG not null primary key,
	version INTEGER,
	versionedEntryId LONG,
	groupId LONG
);

create table userId (
	dataLimitEntryId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null
);