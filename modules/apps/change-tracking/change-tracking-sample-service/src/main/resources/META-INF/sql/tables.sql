create table CTSChild (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	ctsChildId LONG not null,
	companyId LONG,
	ctsGrandParentId LONG,
	parentCTSChildId LONG,
	ctsParentName VARCHAR(75) null,
	name VARCHAR(75) null,
	primary key (ctsChildId, ctCollectionId)
);

create table CTSGrandParent (
	mvccVersion LONG default 0 not null,
	ctsGrandParentId LONG not null primary key,
	companyId LONG,
	parentCTSGrandParentId LONG,
	name VARCHAR(75) null
);

create table CTSParent (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	ctsParentId LONG not null,
	companyId LONG,
	ctsGrandParentId LONG,
	name VARCHAR(75) null,
	primary key (ctsParentId, ctCollectionId)
);