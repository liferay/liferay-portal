create table DDMContent (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	contentId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	name STRING null,
	description STRING null,
	data_ TEXT null,
	primary key (contentId, ctCollectionId)
);

create table DDMDataProviderInstance (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	dataProviderInstanceId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	name STRING null,
	description TEXT null,
	definition TEXT null,
	type_ VARCHAR(75) null,
	lastPublishDate DATE null,
	primary key (dataProviderInstanceId, ctCollectionId)
);

create table DDMDataProviderInstanceLink (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	dataProviderInstanceLinkId LONG not null,
	companyId LONG,
	dataProviderInstanceId LONG,
	structureId LONG,
	primary key (dataProviderInstanceLinkId, ctCollectionId)
);

create table DDMField (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	fieldId LONG not null,
	companyId LONG,
	parentFieldId LONG,
	storageId LONG,
	structureVersionId LONG,
	fieldName VARCHAR(255) null,
	fieldType VARCHAR(255) null,
	instanceId VARCHAR(75) null,
	localizable BOOLEAN,
	priority INTEGER,
	primary key (fieldId, ctCollectionId)
);

create table DDMFieldAttribute (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	fieldAttributeId LONG not null,
	companyId LONG,
	fieldId LONG,
	storageId LONG,
	attributeName VARCHAR(255) null,
	languageId VARCHAR(75) null,
	largeAttributeValue TEXT null,
	smallAttributeValue VARCHAR(255) null,
	primary key (fieldAttributeId, ctCollectionId)
);

create table DDMFormInstance (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	formInstanceId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	versionUserId LONG,
	versionUserName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	structureId LONG,
	version VARCHAR(75) null,
	name STRING null,
	description TEXT null,
	settings_ TEXT null,
	lastPublishDate DATE null,
	primary key (formInstanceId, ctCollectionId)
);

create table DDMFormInstanceRecord (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	formInstanceRecordId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	versionUserId LONG,
	versionUserName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	formInstanceId LONG,
	formInstanceVersion VARCHAR(75) null,
	storageId LONG,
	version VARCHAR(75) null,
	ipAddress VARCHAR(75) null,
	lastPublishDate DATE null,
	primary key (formInstanceRecordId, ctCollectionId)
);

create table DDMFormInstanceRecordVersion (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	formInstanceRecordVersionId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	formInstanceId LONG,
	formInstanceVersion VARCHAR(75) null,
	formInstanceRecordId LONG,
	version VARCHAR(75) null,
	storageId LONG,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null,
	primary key (formInstanceRecordVersionId, ctCollectionId)
);

create table DDMFormInstanceReport (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	formInstanceReportId LONG not null,
	groupId LONG,
	companyId LONG,
	createDate DATE null,
	modifiedDate DATE null,
	formInstanceId LONG,
	data_ TEXT null,
	primary key (formInstanceReportId, ctCollectionId)
);

create table DDMFormInstanceVersion (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	formInstanceVersionId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	formInstanceId LONG,
	structureVersionId LONG,
	name STRING null,
	description TEXT null,
	settings_ TEXT null,
	version VARCHAR(75) null,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null,
	primary key (formInstanceVersionId, ctCollectionId)
);

create table DDMStorageLink (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	storageLinkId LONG not null,
	companyId LONG,
	classNameId LONG,
	classPK LONG,
	structureId LONG,
	structureVersionId LONG,
	primary key (storageLinkId, ctCollectionId)
);

create table DDMStructure (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	structureId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	versionUserId LONG,
	versionUserName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	parentStructureId LONG,
	classNameId LONG,
	structureKey VARCHAR(75) null,
	version VARCHAR(75) null,
	name STRING null,
	description TEXT null,
	definition TEXT null,
	storageType VARCHAR(75) null,
	type_ INTEGER,
	lastPublishDate DATE null,
	primary key (structureId, ctCollectionId)
);

create table DDMStructureLayout (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	structureLayoutId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	structureLayoutKey VARCHAR(75) null,
	structureVersionId LONG,
	name TEXT null,
	description TEXT null,
	definition TEXT null,
	primary key (structureLayoutId, ctCollectionId)
);

create table DDMStructureLink (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	structureLinkId LONG not null,
	companyId LONG,
	classNameId LONG,
	classPK LONG,
	structureId LONG,
	primary key (structureLinkId, ctCollectionId)
);

create table DDMStructureVersion (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	structureVersionId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	structureId LONG,
	version VARCHAR(75) null,
	parentStructureId LONG,
	name STRING null,
	description TEXT null,
	definition TEXT null,
	storageType VARCHAR(75) null,
	type_ INTEGER,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null,
	primary key (structureVersionId, ctCollectionId)
);

create table DDMTemplate (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	templateId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	versionUserId LONG,
	versionUserName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	resourceClassNameId LONG,
	templateKey VARCHAR(75) null,
	version VARCHAR(75) null,
	name TEXT null,
	description TEXT null,
	type_ VARCHAR(75) null,
	mode_ VARCHAR(75) null,
	language VARCHAR(75) null,
	script TEXT null,
	cacheable BOOLEAN,
	smallImage BOOLEAN,
	smallImageId LONG,
	smallImageURL STRING null,
	lastPublishDate DATE null,
	primary key (templateId, ctCollectionId)
);

create table DDMTemplateLink (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	templateLinkId LONG not null,
	companyId LONG,
	classNameId LONG,
	classPK LONG,
	templateId LONG,
	primary key (templateLinkId, ctCollectionId)
);

create table DDMTemplateVersion (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	templateVersionId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	classNameId LONG,
	classPK LONG,
	templateId LONG,
	version VARCHAR(75) null,
	name TEXT null,
	description TEXT null,
	language VARCHAR(75) null,
	script TEXT null,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null,
	primary key (templateVersionId, ctCollectionId)
);