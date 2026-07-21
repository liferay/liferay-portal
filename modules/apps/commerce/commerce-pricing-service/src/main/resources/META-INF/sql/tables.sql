create table CPricingClassCPDefinitionRel (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	CPricingClassCPDefinitionRelId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	commercePricingClassId LONG,
	CPDefinitionId LONG,
	primary key (CPricingClassCPDefinitionRelId, ctCollectionId)
);

create table CommercePriceModifier (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	commercePriceModifierId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	commercePriceListId LONG,
	title VARCHAR(75) null,
	target VARCHAR(75) null,
	modifierAmount BIGDECIMAL null,
	modifierType VARCHAR(75) null,
	priority DOUBLE,
	active_ BOOLEAN,
	displayDate DATE null,
	expirationDate DATE null,
	lastPublishDate DATE null,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null,
	primary key (commercePriceModifierId, ctCollectionId)
);

create table CommercePriceModifierRel (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	commercePriceModifierRelId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	commercePriceModifierId LONG,
	classNameId LONG,
	classPK LONG,
	primary key (commercePriceModifierRelId, ctCollectionId)
);

create table CommercePricingClass (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	commercePricingClassId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	title STRING null,
	description STRING null,
	lastPublishDate DATE null,
	status INTEGER,
	primary key (commercePricingClassId, ctCollectionId)
);