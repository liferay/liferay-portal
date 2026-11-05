create table CIAudit (
	mvccVersion LONG default 0 not null,
	CIAuditId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	logType VARCHAR(75) null,
	logTypeSettings TEXT null,
	quantity BIGDECIMAL null,
	sku VARCHAR(75) null,
	unitOfMeasureKey VARCHAR(75) null
);

create table CIBookedQuantity (
	mvccVersion LONG default 0 not null,
	CIBookedQuantityId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	bookedNote VARCHAR(75) null,
	expirationDate DATE null,
	quantity BIGDECIMAL null,
	sku VARCHAR(75) null,
	unitOfMeasureKey VARCHAR(75) null
);

create table CIReplenishmentItem (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	CIReplenishmentItemId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	commerceInventoryWarehouseId LONG,
	availabilityDate DATE null,
	quantity BIGDECIMAL null,
	sku VARCHAR(75) null,
	unitOfMeasureKey VARCHAR(75) null
);

create table CIWarehouse (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	CIWarehouseId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	name STRING null,
	description STRING null,
	active_ BOOLEAN,
	street1 VARCHAR(75) null,
	street2 VARCHAR(75) null,
	street3 VARCHAR(75) null,
	city VARCHAR(75) null,
	zip VARCHAR(75) null,
	commerceRegionCode VARCHAR(75) null,
	countryTwoLettersISOCode VARCHAR(75) null,
	latitude DOUBLE,
	longitude DOUBLE,
	type_ VARCHAR(75) null,
	status INTEGER
);

create table CIWarehouseGroupRel (
	CIWarehouseGroupRelId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	commerceWarehouseId LONG,
	primary_ BOOLEAN
);

create table CIWarehouseItem (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	CIWarehouseItemId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	commerceInventoryWarehouseId LONG,
	quantity BIGDECIMAL null,
	reservedQuantity BIGDECIMAL null,
	sku VARCHAR(75) null,
	unitOfMeasureKey VARCHAR(75) null
);

create table CIWarehouseRel (
	mvccVersion LONG default 0 not null,
	CIWarehouseRelId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	CIWarehouseId LONG
);