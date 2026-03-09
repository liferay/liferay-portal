create table Address (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	addressId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	countryId LONG,
	listTypeId LONG,
	regionId LONG,
	city VARCHAR(75) null,
	description STRING null,
	latitude DOUBLE,
	longitude DOUBLE,
	mailing BOOLEAN,
	name VARCHAR(255) null,
	primary_ BOOLEAN,
	street1 VARCHAR(255) null,
	street2 VARCHAR(255) null,
	street3 VARCHAR(255) null,
	subtype VARCHAR(75) null,
	validationDate DATE null,
	validationStatus INTEGER,
	zip VARCHAR(75) null,
	status INTEGER,
	primary key (addressId, ctCollectionId)
);

create table AnnouncementsDelivery (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	deliveryId LONG not null,
	companyId LONG,
	userId LONG,
	type_ VARCHAR(75) null,
	email BOOLEAN,
	sms BOOLEAN,
	website BOOLEAN,
	primary key (deliveryId, ctCollectionId)
);

create table AnnouncementsEntry (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	entryId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	title VARCHAR(75) null,
	content TEXT null,
	url STRING null,
	type_ VARCHAR(75) null,
	displayDate DATE null,
	expirationDate DATE null,
	priority INTEGER,
	alert BOOLEAN,
	primary key (entryId, ctCollectionId)
);

create table AnnouncementsFlag (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	flagId LONG not null,
	companyId LONG,
	userId LONG,
	createDate DATE null,
	entryId LONG,
	value INTEGER,
	primary key (flagId, ctCollectionId)
);

create table AssetCategory (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	categoryId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	parentCategoryId LONG,
	treePath STRING null,
	name VARCHAR(255) null,
	title TEXT null,
	description TEXT null,
	vocabularyId LONG,
	lastPublishDate DATE null,
	status INTEGER,
	primary key (categoryId, ctCollectionId)
);

create table AssetEntries_AssetTags (
	companyId LONG not null,
	entryId LONG not null,
	tagId LONG not null,
	ctCollectionId LONG default 0 not null,
	ctChangeType BOOLEAN,
	primary key (entryId, tagId, ctCollectionId)
);

create table AssetEntry (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	entryId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	classUuid VARCHAR(75) null,
	classTypeId LONG,
	listable BOOLEAN,
	visible BOOLEAN,
	startDate DATE null,
	endDate DATE null,
	publishDate DATE null,
	expirationDate DATE null,
	mimeType VARCHAR(75) null,
	title TEXT null,
	description TEXT null,
	summary TEXT null,
	url STRING null,
	layoutUuid VARCHAR(75) null,
	height INTEGER,
	width INTEGER,
	priority DOUBLE,
	primary key (entryId, ctCollectionId)
);

create table AssetTag (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	tagId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	name VARCHAR(75) null,
	assetCount INTEGER,
	lastPublishDate DATE null,
	primary key (tagId, ctCollectionId)
);

create table AssetTagGroupRel (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	assetTagGroupRelId LONG not null,
	groupId LONG,
	companyId LONG,
	tagId LONG,
	primary key (assetTagGroupRelId, ctCollectionId)
);

create table AssetVocabulary (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	vocabularyId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	name VARCHAR(75) null,
	title STRING null,
	description STRING null,
	settings_ STRING null,
	visibilityType INTEGER,
	lastPublishDate DATE null,
	status INTEGER,
	primary key (vocabularyId, ctCollectionId)
);

create table AssetVocabularyGroupRel (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	assetVocabularyGroupRelId LONG not null,
	groupId LONG,
	companyId LONG,
	vocabularyId LONG,
	primary key (assetVocabularyGroupRelId, ctCollectionId)
);

create table BrowserTracker (
	mvccVersion LONG default 0 not null,
	browserTrackerId LONG not null primary key,
	companyId LONG,
	userId LONG,
	browserKey LONG
);

create table ClassName_ (
	mvccVersion LONG default 0 not null,
	classNameId LONG not null primary key,
	value VARCHAR(200) null
);

create table Company (
	mvccVersion LONG default 0 not null,
	companyId LONG not null primary key,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	webId VARCHAR(75) null,
	mx VARCHAR(200) null,
	homeURL STRING null,
	logoId LONG,
	maxUsers INTEGER,
	active_ BOOLEAN,
	name VARCHAR(75) null,
	legalName VARCHAR(75) null,
	legalId VARCHAR(75) null,
	legalType VARCHAR(75) null,
	sicCode VARCHAR(75) null,
	tickerSymbol VARCHAR(75) null,
	industry VARCHAR(75) null,
	type_ VARCHAR(75) null,
	size_ VARCHAR(75) null,
	indexNameCurrent VARCHAR(75) null,
	indexNameNext VARCHAR(75) null
);

create table CompanyInfo (
	mvccVersion LONG default 0 not null,
	companyInfoId LONG not null primary key,
	companyId LONG,
	key_ TEXT null
);

create table Contact_ (
	mvccVersion LONG default 0 not null,
	contactId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	parentContactId LONG,
	emailAddress VARCHAR(254) null,
	firstName VARCHAR(75) null,
	middleName VARCHAR(75) null,
	lastName VARCHAR(75) null,
	prefixListTypeId LONG,
	suffixListTypeId LONG,
	male BOOLEAN,
	birthday DATE null,
	smsSn VARCHAR(75) null,
	facebookSn VARCHAR(75) null,
	jabberSn VARCHAR(75) null,
	skypeSn VARCHAR(75) null,
	twitterSn VARCHAR(75) null,
	employeeStatusId VARCHAR(75) null,
	employeeNumber VARCHAR(75) null,
	jobTitle VARCHAR(100) null,
	jobClass VARCHAR(75) null,
	hoursOfOperation VARCHAR(75) null
);

create table Counter (
	name VARCHAR(150) not null primary key,
	currentId LONG
);

create table Country (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	defaultLanguageId VARCHAR(75) null,
	countryId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	a2 VARCHAR(75) null,
	a3 VARCHAR(75) null,
	active_ BOOLEAN,
	billingAllowed BOOLEAN,
	groupFilterEnabled BOOLEAN,
	idd_ VARCHAR(75) null,
	name VARCHAR(75) null,
	number_ VARCHAR(75) null,
	position DOUBLE,
	shippingAllowed BOOLEAN,
	subjectToVAT BOOLEAN,
	zipRequired BOOLEAN,
	lastPublishDate DATE null,
	primary key (countryId, ctCollectionId)
);

create table CountryLocalization (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	countryLocalizationId LONG not null,
	companyId LONG,
	countryId LONG,
	languageId VARCHAR(75) null,
	title VARCHAR(75) null,
	primary key (countryLocalizationId, ctCollectionId)
);

create table DLFileEntry (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	fileEntryId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	repositoryId LONG,
	folderId LONG,
	treePath STRING null,
	name VARCHAR(255) null,
	fileName VARCHAR(255) null,
	extension VARCHAR(75) null,
	mimeType VARCHAR(75) null,
	title VARCHAR(255) null,
	description STRING null,
	extraSettings TEXT null,
	fileEntryTypeId LONG,
	version VARCHAR(75) null,
	size_ LONG,
	smallImageId LONG,
	largeImageId LONG,
	custom1ImageId LONG,
	custom2ImageId LONG,
	manualCheckInRequired BOOLEAN,
	displayDate DATE null,
	expirationDate DATE null,
	reviewDate DATE null,
	lastPublishDate DATE null,
	primary key (fileEntryId, ctCollectionId)
);

create table DLFileEntryMetadata (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	fileEntryMetadataId LONG not null,
	companyId LONG,
	DDMStorageId LONG,
	DDMStructureId LONG,
	fileEntryId LONG,
	fileVersionId LONG,
	primary key (fileEntryMetadataId, ctCollectionId)
);

create table DLFileEntryType (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	fileEntryTypeId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	dataDefinitionId LONG,
	fileEntryTypeKey VARCHAR(75) null,
	name STRING null,
	description STRING null,
	scope INTEGER,
	lastPublishDate DATE null,
	primary key (fileEntryTypeId, ctCollectionId)
);

create table DLFileEntryTypes_DLFolders (
	companyId LONG not null,
	fileEntryTypeId LONG not null,
	folderId LONG not null,
	ctCollectionId LONG default 0 not null,
	ctChangeType BOOLEAN,
	primary key (fileEntryTypeId, folderId, ctCollectionId)
);

create table DLFileShortcut (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	fileShortcutId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	repositoryId LONG,
	folderId LONG,
	toFileEntryId LONG,
	treePath STRING null,
	active_ BOOLEAN,
	lastPublishDate DATE null,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null,
	primary key (fileShortcutId, ctCollectionId)
);

create table DLFileVersion (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	fileVersionId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	repositoryId LONG,
	folderId LONG,
	fileEntryId LONG,
	treePath STRING null,
	fileName VARCHAR(255) null,
	extension VARCHAR(75) null,
	mimeType VARCHAR(75) null,
	title VARCHAR(255) null,
	description STRING null,
	changeLog VARCHAR(75) null,
	extraSettings TEXT null,
	fileEntryTypeId LONG,
	version VARCHAR(75) null,
	size_ LONG,
	checksum VARCHAR(75) null,
	storeUUID VARCHAR(255) null,
	displayDate DATE null,
	expirationDate DATE null,
	reviewDate DATE null,
	lastPublishDate DATE null,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null,
	primary key (fileVersionId, ctCollectionId)
);

create table DLFolder (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	folderId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	repositoryId LONG,
	mountPoint BOOLEAN,
	parentFolderId LONG,
	treePath STRING null,
	name VARCHAR(255) null,
	description STRING null,
	lastPostDate DATE null,
	defaultFileEntryTypeId LONG,
	hidden_ BOOLEAN,
	restrictionType INTEGER,
	lastPublishDate DATE null,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null,
	primary key (folderId, ctCollectionId)
);

create table EmailAddress (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	emailAddressId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	address VARCHAR(254) null,
	listTypeId LONG,
	primary_ BOOLEAN,
	primary key (emailAddressId, ctCollectionId)
);

create table ExpandoColumn (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	columnId LONG not null,
	companyId LONG,
	modifiedDate DATE null,
	tableId LONG,
	name VARCHAR(75) null,
	type_ INTEGER,
	defaultData TEXT null,
	typeSettings TEXT null,
	primary key (columnId, ctCollectionId)
);

create table ExpandoRow (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	rowId_ LONG not null,
	companyId LONG,
	modifiedDate DATE null,
	tableId LONG,
	classPK LONG,
	primary key (rowId_, ctCollectionId)
);

create table ExpandoTable (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	tableId LONG not null,
	companyId LONG,
	classNameId LONG,
	name VARCHAR(75) null,
	primary key (tableId, ctCollectionId)
);

create table ExpandoValue (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	valueId LONG not null,
	companyId LONG,
	tableId LONG,
	columnId LONG,
	rowId_ LONG,
	classNameId LONG,
	classPK LONG,
	data_ TEXT null,
	primary key (valueId, ctCollectionId)
);

create table ExportImportConfiguration (
	mvccVersion LONG default 0 not null,
	exportImportConfigurationId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	name VARCHAR(200) null,
	description STRING null,
	type_ INTEGER,
	settings_ TEXT null,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null
);

create table Group_ (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	groupId LONG not null,
	companyId LONG,
	creatorUserId LONG,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	parentGroupId LONG,
	liveGroupId LONG,
	treePath STRING null,
	groupKey VARCHAR(150) null,
	name STRING null,
	description STRING null,
	type_ INTEGER,
	typeSettings TEXT null,
	manualMembership BOOLEAN,
	membershipRestriction INTEGER,
	friendlyURL VARCHAR(255) null,
	site BOOLEAN,
	remoteStagingGroupCount INTEGER,
	inheritContent BOOLEAN,
	active_ BOOLEAN,
	primary key (groupId, ctCollectionId)
);

create table Groups_Orgs (
	companyId LONG not null,
	groupId LONG not null,
	organizationId LONG not null,
	ctCollectionId LONG default 0 not null,
	ctChangeType BOOLEAN,
	primary key (groupId, organizationId, ctCollectionId)
);

create table Groups_Roles (
	companyId LONG not null,
	groupId LONG not null,
	roleId LONG not null,
	ctCollectionId LONG default 0 not null,
	ctChangeType BOOLEAN,
	primary key (groupId, roleId, ctCollectionId)
);

create table Groups_UserGroups (
	companyId LONG not null,
	groupId LONG not null,
	userGroupId LONG not null,
	ctCollectionId LONG default 0 not null,
	ctChangeType BOOLEAN,
	primary key (groupId, userGroupId, ctCollectionId)
);

create table Image (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	imageId LONG not null,
	companyId LONG,
	modifiedDate DATE null,
	type_ VARCHAR(75) null,
	height INTEGER,
	width INTEGER,
	size_ INTEGER,
	primary key (imageId, ctCollectionId)
);

create table Layout (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	plid LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	parentPlid LONG,
	privateLayout BOOLEAN,
	layoutId LONG,
	parentLayoutId LONG,
	classNameId LONG,
	classPK LONG,
	name STRING null,
	title TEXT null,
	description TEXT null,
	keywords STRING null,
	robots STRING null,
	type_ VARCHAR(75) null,
	typeSettings TEXT null,
	hidden_ BOOLEAN,
	system_ BOOLEAN,
	friendlyURL VARCHAR(255) null,
	iconImageId LONG,
	themeId VARCHAR(75) null,
	colorSchemeId VARCHAR(75) null,
	styleBookEntryERC VARCHAR(75) null,
	css TEXT null,
	priority INTEGER,
	faviconFileEntryERC VARCHAR(75) null,
	faviconFileEntryScopeERC VARCHAR(75) null,
	masterLPTEERC VARCHAR(75) null,
	portletLPTEERC VARCHAR(75) null,
	portletLPTESERC VARCHAR(75) null,
	portletLPTELE BOOLEAN,
	layoutSetPrototypeLayoutERC VARCHAR(75) null,
	publishDate DATE null,
	lastPublishDate DATE null,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null,
	primary key (plid, ctCollectionId)
);

create table LayoutBranch (
	mvccVersion LONG default 0 not null,
	layoutBranchId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	layoutSetBranchId LONG,
	plid LONG,
	name VARCHAR(75) null,
	description STRING null,
	master BOOLEAN
);

create table LayoutFriendlyURL (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	layoutFriendlyURLId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	plid LONG,
	privateLayout BOOLEAN,
	friendlyURL VARCHAR(255) null,
	languageId VARCHAR(75) null,
	lastPublishDate DATE null,
	primary key (layoutFriendlyURLId, ctCollectionId)
);

create table LayoutPrototype (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	layoutPrototypeId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	name TEXT null,
	description TEXT null,
	settings_ STRING null,
	active_ BOOLEAN,
	primary key (layoutPrototypeId, ctCollectionId)
);

create table LayoutRevision (
	mvccVersion LONG default 0 not null,
	layoutRevisionId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	layoutSetBranchId LONG,
	layoutBranchId LONG,
	parentLayoutRevisionId LONG,
	head BOOLEAN,
	major BOOLEAN,
	plid LONG,
	privateLayout BOOLEAN,
	name STRING null,
	title STRING null,
	description STRING null,
	keywords STRING null,
	robots STRING null,
	typeSettings TEXT null,
	iconImageId LONG,
	themeId VARCHAR(75) null,
	colorSchemeId VARCHAR(75) null,
	css TEXT null,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null
);

create table LayoutSet (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	layoutSetId LONG not null,
	groupId LONG,
	companyId LONG,
	createDate DATE null,
	modifiedDate DATE null,
	privateLayout BOOLEAN,
	logoId LONG,
	themeId VARCHAR(75) null,
	colorSchemeId VARCHAR(75) null,
	faviconFileEntryId LONG,
	css TEXT null,
	settings_ TEXT null,
	layoutSetPrototypeUuid VARCHAR(75) null,
	layoutSetPrototypeLinkEnabled BOOLEAN,
	primary key (layoutSetId, ctCollectionId)
);

create table LayoutSetBranch (
	mvccVersion LONG default 0 not null,
	layoutSetBranchId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	privateLayout BOOLEAN,
	name VARCHAR(75) null,
	description STRING null,
	master BOOLEAN,
	logoId LONG,
	themeId VARCHAR(75) null,
	colorSchemeId VARCHAR(75) null,
	css TEXT null,
	settings_ TEXT null,
	layoutSetPrototypeUuid VARCHAR(75) null,
	layoutSetPrototypeLinkEnabled BOOLEAN
);

create table LayoutSetPrototype (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	layoutSetPrototypeId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	name TEXT null,
	description TEXT null,
	settings_ STRING null,
	active_ BOOLEAN,
	primary key (layoutSetPrototypeId, ctCollectionId)
);

create table ListType (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	listTypeId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	name VARCHAR(75) null,
	type_ VARCHAR(75) null
);

create table MembershipRequest (
	mvccVersion LONG default 0 not null,
	membershipRequestId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	createDate DATE null,
	comments STRING null,
	replyComments STRING null,
	replyDate DATE null,
	replierUserId LONG,
	statusId LONG
);

create table OrgLabor (
	mvccVersion LONG default 0 not null,
	orgLaborId LONG not null primary key,
	companyId LONG,
	organizationId LONG,
	listTypeId LONG,
	sunOpen INTEGER,
	sunClose INTEGER,
	monOpen INTEGER,
	monClose INTEGER,
	tueOpen INTEGER,
	tueClose INTEGER,
	wedOpen INTEGER,
	wedClose INTEGER,
	thuOpen INTEGER,
	thuClose INTEGER,
	friOpen INTEGER,
	friClose INTEGER,
	satOpen INTEGER,
	satClose INTEGER
);

create table Organization_ (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	organizationId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	parentOrganizationId LONG,
	treePath STRING null,
	name VARCHAR(100) null,
	type_ VARCHAR(75) null,
	recursable BOOLEAN,
	regionId LONG,
	countryId LONG,
	statusListTypeId LONG,
	comments STRING null,
	logoId LONG,
	status INTEGER,
	primary key (organizationId, ctCollectionId)
);

create table PasswordPolicy (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	passwordPolicyId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	defaultPolicy BOOLEAN,
	name VARCHAR(75) null,
	description STRING null,
	changeable BOOLEAN,
	changeRequired BOOLEAN,
	minAge LONG,
	checkSyntax BOOLEAN,
	allowDictionaryWords BOOLEAN,
	minAlphanumeric INTEGER,
	minLength INTEGER,
	minLowerCase INTEGER,
	minNumbers INTEGER,
	minSymbols INTEGER,
	minUpperCase INTEGER,
	regex STRING null,
	history BOOLEAN,
	historyCount INTEGER,
	expireable BOOLEAN,
	maxAge LONG,
	warningTime LONG,
	graceLimit INTEGER,
	lockout BOOLEAN,
	maxFailure INTEGER,
	lockoutDuration LONG,
	requireUnlock BOOLEAN,
	resetFailureCount LONG,
	resetTicketMaxAge LONG
);

create table PasswordPolicyRel (
	mvccVersion LONG default 0 not null,
	passwordPolicyRelId LONG not null primary key,
	companyId LONG,
	passwordPolicyId LONG,
	classNameId LONG,
	classPK LONG
);

create table PasswordTracker (
	mvccVersion LONG default 0 not null,
	passwordTrackerId LONG not null primary key,
	companyId LONG,
	userId LONG,
	createDate DATE null,
	password_ VARCHAR(255) null
);

create table Phone (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	phoneId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	number_ VARCHAR(75) null,
	extension VARCHAR(75) null,
	listTypeId LONG,
	primary_ BOOLEAN,
	primary key (phoneId, ctCollectionId)
);

create table PluginSetting (
	mvccVersion LONG default 0 not null,
	pluginSettingId LONG not null primary key,
	companyId LONG,
	pluginId VARCHAR(75) null,
	pluginType VARCHAR(75) null,
	roles STRING null,
	active_ BOOLEAN
);

create table PortalPreferenceValue (
	mvccVersion LONG default 0 not null,
	portalPreferenceValueId LONG not null primary key,
	companyId LONG,
	portalPreferencesId LONG,
	index_ INTEGER,
	key_ VARCHAR(1024) null,
	largeValue TEXT null,
	namespace VARCHAR(255) null,
	smallValue VARCHAR(255) null
);

create table PortalPreferences (
	mvccVersion LONG default 0 not null,
	portalPreferencesId LONG not null primary key,
	companyId LONG,
	ownerId LONG,
	ownerType INTEGER
);

create table Portlet (
	mvccVersion LONG default 0 not null,
	id_ LONG not null primary key,
	companyId LONG,
	portletId VARCHAR(200) null,
	roles STRING null,
	active_ BOOLEAN
);

create table PortletItem (
	mvccVersion LONG default 0 not null,
	portletItemId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	name VARCHAR(75) null,
	portletId VARCHAR(200) null,
	classNameId LONG
);

create table PortletPreferenceValue (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	portletPreferenceValueId LONG not null,
	companyId LONG,
	portletPreferencesId LONG,
	index_ INTEGER,
	largeValue TEXT null,
	name VARCHAR(255) null,
	readOnly BOOLEAN,
	smallValue VARCHAR(255) null,
	primary key (portletPreferenceValueId, ctCollectionId)
);

create table PortletPreferences (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	portletPreferencesId LONG not null,
	companyId LONG,
	ownerId LONG,
	ownerType INTEGER,
	plid LONG,
	portletId VARCHAR(200) null,
	primary key (portletPreferencesId, ctCollectionId)
);

create table RatingsEntry (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	entryId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	score DOUBLE,
	primary key (entryId, ctCollectionId)
);

create table RatingsStats (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	statsId LONG not null,
	companyId LONG,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	totalEntries INTEGER,
	totalScore DOUBLE,
	averageScore DOUBLE,
	primary key (statsId, ctCollectionId)
);

create table RecentLayoutBranch (
	mvccVersion LONG default 0 not null,
	recentLayoutBranchId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	layoutBranchId LONG,
	layoutSetBranchId LONG,
	plid LONG
);

create table RecentLayoutRevision (
	mvccVersion LONG default 0 not null,
	recentLayoutRevisionId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	layoutRevisionId LONG,
	layoutSetBranchId LONG,
	plid LONG
);

create table RecentLayoutSetBranch (
	mvccVersion LONG default 0 not null,
	recentLayoutSetBranchId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	layoutSetBranchId LONG,
	layoutSetId LONG
);

create table Region (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	defaultLanguageId VARCHAR(75) null,
	regionId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	countryId LONG,
	active_ BOOLEAN,
	name VARCHAR(75) null,
	position DOUBLE,
	regionCode VARCHAR(75) null,
	lastPublishDate DATE null,
	primary key (regionId, ctCollectionId)
);

create table RegionLocalization (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	regionLocalizationId LONG not null,
	companyId LONG,
	regionId LONG,
	languageId VARCHAR(75) null,
	title VARCHAR(75) null,
	primary key (regionLocalizationId, ctCollectionId)
);

create table Release_ (
	mvccVersion LONG default 0 not null,
	releaseId LONG not null primary key,
	createDate DATE null,
	modifiedDate DATE null,
	servletContextName VARCHAR(75) null,
	schemaVersion VARCHAR(75) null,
	buildNumber INTEGER,
	buildDate DATE null,
	versionDisplayName VARCHAR(75) null,
	verified BOOLEAN,
	state_ INTEGER,
	testString VARCHAR(1024) null
);

create table RememberMeToken (
	mvccVersion LONG default 0 not null,
	rememberMeTokenId LONG not null primary key,
	companyId LONG,
	userId LONG,
	createDate DATE null,
	expirationDate DATE null,
	value VARCHAR(255) null
);

create table Repository (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	repositoryId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	name VARCHAR(200) null,
	description STRING null,
	portletId VARCHAR(200) null,
	typeSettings TEXT null,
	dlFolderId LONG,
	lastPublishDate DATE null,
	primary key (repositoryId, ctCollectionId)
);

create table RepositoryEntry (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	repositoryEntryId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	repositoryId LONG,
	mappedId VARCHAR(255) null,
	manualCheckInRequired BOOLEAN,
	lastPublishDate DATE null,
	primary key (repositoryEntryId, ctCollectionId)
);

create table ResourceAction (
	mvccVersion LONG default 0 not null,
	resourceActionId LONG not null primary key,
	name VARCHAR(255) null,
	actionId VARCHAR(75) null,
	bitwiseValue LONG
);

create table ResourcePermission (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	resourcePermissionId LONG not null,
	companyId LONG,
	name VARCHAR(255) null,
	scope INTEGER,
	primKey VARCHAR(255) null,
	primKeyId LONG,
	roleId LONG,
	ownerId LONG,
	actionIds LONG,
	viewActionId BOOLEAN,
	primary key (resourcePermissionId, ctCollectionId)
);

create table Role_ (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	roleId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	name VARCHAR(75) null,
	title STRING null,
	description TEXT null,
	type_ INTEGER,
	subtype VARCHAR(75) null,
	status INTEGER,
	primary key (roleId, ctCollectionId)
);

create table ServiceComponent (
	mvccVersion LONG default 0 not null,
	serviceComponentId LONG not null primary key,
	buildNamespace VARCHAR(75) null,
	buildNumber LONG,
	buildDate LONG,
	data_ TEXT null
);

create table SocialActivity (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	activityId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	createDate LONG,
	activitySetId LONG,
	mirrorActivityId LONG,
	classNameId LONG,
	classPK LONG,
	parentClassNameId LONG,
	parentClassPK LONG,
	type_ INTEGER,
	extraData STRING null,
	receiverUserId LONG,
	primary key (activityId, ctCollectionId)
);

create table SocialActivityAchievement (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	activityAchievementId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	createDate LONG,
	name VARCHAR(75) null,
	firstInGroup BOOLEAN,
	primary key (activityAchievementId, ctCollectionId)
);

create table SocialActivityCounter (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	activityCounterId LONG not null,
	groupId LONG,
	companyId LONG,
	classNameId LONG,
	classPK LONG,
	name VARCHAR(75) null,
	ownerType INTEGER,
	currentValue INTEGER,
	totalValue INTEGER,
	graceValue INTEGER,
	startPeriod INTEGER,
	endPeriod INTEGER,
	active_ BOOLEAN,
	primary key (activityCounterId, ctCollectionId)
);

create table SocialActivityLimit (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	activityLimitId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	classNameId LONG,
	classPK LONG,
	activityType INTEGER,
	activityCounterName VARCHAR(75) null,
	value VARCHAR(75) null,
	primary key (activityLimitId, ctCollectionId)
);

create table SocialActivitySet (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	activitySetId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	createDate LONG,
	modifiedDate LONG,
	classNameId LONG,
	classPK LONG,
	type_ INTEGER,
	extraData STRING null,
	activityCount INTEGER,
	primary key (activitySetId, ctCollectionId)
);

create table SocialActivitySetting (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	activitySettingId LONG not null,
	groupId LONG,
	companyId LONG,
	classNameId LONG,
	activityType INTEGER,
	name VARCHAR(75) null,
	value VARCHAR(1024) null,
	primary key (activitySettingId, ctCollectionId)
);

create table SocialRelation (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	relationId LONG not null,
	companyId LONG,
	createDate LONG,
	userId1 LONG,
	userId2 LONG,
	type_ INTEGER,
	primary key (relationId, ctCollectionId)
);

create table SocialRequest (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	requestId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	createDate LONG,
	modifiedDate LONG,
	classNameId LONG,
	classPK LONG,
	type_ INTEGER,
	extraData STRING null,
	receiverUserId LONG,
	status INTEGER,
	primary key (requestId, ctCollectionId)
);

create table SystemEvent (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	systemEventId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	classExternalReferenceCode VARCHAR(1000) null,
	classNameId LONG,
	classPK LONG,
	classUuid VARCHAR(75) null,
	referrerClassNameId LONG,
	parentSystemEventId LONG,
	systemEventSetKey LONG,
	type_ INTEGER,
	extraData TEXT null,
	primary key (systemEventId, ctCollectionId)
);

create table Team (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	teamId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	groupId LONG,
	name VARCHAR(75) null,
	description STRING null,
	lastPublishDate DATE null,
	primary key (teamId, ctCollectionId)
);

create table Ticket (
	mvccVersion LONG default 0 not null,
	ticketId LONG not null primary key,
	companyId LONG,
	createDate DATE null,
	classNameId LONG,
	classPK LONG,
	key_ VARCHAR(255) null,
	type_ INTEGER,
	extraInfo TEXT null,
	expirationDate DATE null
);

create table UserNotificationDelivery (
	mvccVersion LONG default 0 not null,
	userNotificationDeliveryId LONG not null primary key,
	companyId LONG,
	userId LONG,
	portletId VARCHAR(200) null,
	classNameId LONG,
	notificationType INTEGER,
	deliveryType INTEGER,
	deliver BOOLEAN
);

create table User_ (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	userId LONG not null,
	companyId LONG,
	createDate DATE null,
	modifiedDate DATE null,
	contactId LONG,
	password_ VARCHAR(255) null,
	passwordEncrypted BOOLEAN,
	passwordReset BOOLEAN,
	passwordModifiedDate DATE null,
	digest VARCHAR(255) null,
	reminderQueryQuestion VARCHAR(75) null,
	reminderQueryAnswer VARCHAR(75) null,
	graceLoginCount INTEGER,
	screenName VARCHAR(75) null,
	emailAddress VARCHAR(254) null,
	facebookId LONG,
	googleUserId VARCHAR(75) null,
	ldapServerId LONG,
	openId VARCHAR(1024) null,
	portraitId LONG,
	languageId VARCHAR(75) null,
	timeZoneId VARCHAR(75) null,
	greeting VARCHAR(255) null,
	comments STRING null,
	firstName VARCHAR(75) null,
	middleName VARCHAR(75) null,
	lastName VARCHAR(75) null,
	jobTitle VARCHAR(100) null,
	loginDate DATE null,
	loginIP VARCHAR(75) null,
	lastLoginDate DATE null,
	lastLoginIP VARCHAR(75) null,
	lastFailedLoginDate DATE null,
	failedLoginAttempts INTEGER,
	lockout BOOLEAN,
	lockoutDate DATE null,
	agreedToTermsOfUse BOOLEAN,
	emailAddressVerified BOOLEAN,
	type_ INTEGER,
	status INTEGER,
	primary key (userId, ctCollectionId)
);

create table UserGroup (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	userGroupId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	parentUserGroupId LONG,
	name VARCHAR(255) null,
	description STRING null,
	addedByLDAPImport BOOLEAN,
	primary key (userGroupId, ctCollectionId)
);

create table UserGroupGroupRole (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	userGroupGroupRoleId LONG not null,
	companyId LONG,
	userGroupId LONG,
	groupId LONG,
	roleId LONG,
	primary key (userGroupGroupRoleId, ctCollectionId)
);

create table UserGroupRole (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	userGroupRoleId LONG not null,
	companyId LONG,
	userId LONG,
	groupId LONG,
	roleId LONG,
	primary key (userGroupRoleId, ctCollectionId)
);

create table UserGroups_Teams (
	companyId LONG not null,
	teamId LONG not null,
	userGroupId LONG not null,
	ctCollectionId LONG default 0 not null,
	ctChangeType BOOLEAN,
	primary key (teamId, userGroupId, ctCollectionId)
);

create table UserIdMapper (
	mvccVersion LONG default 0 not null,
	userIdMapperId LONG not null primary key,
	companyId LONG,
	userId LONG,
	type_ VARCHAR(75) null,
	description VARCHAR(75) null,
	externalUserId VARCHAR(75) null
);

create table UserNotificationEvent (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	userNotificationEventId LONG not null primary key,
	companyId LONG,
	userId LONG,
	type_ VARCHAR(200) null,
	timestamp LONG,
	deliveryType INTEGER,
	deliverBy LONG,
	delivered BOOLEAN,
	payload TEXT null,
	actionRequired BOOLEAN,
	archived BOOLEAN
);

create table Users_Groups (
	companyId LONG not null,
	groupId LONG not null,
	userId LONG not null,
	ctCollectionId LONG default 0 not null,
	ctChangeType BOOLEAN,
	primary key (groupId, userId, ctCollectionId)
);

create table Users_Orgs (
	companyId LONG not null,
	organizationId LONG not null,
	userId LONG not null,
	ctCollectionId LONG default 0 not null,
	ctChangeType BOOLEAN,
	primary key (organizationId, userId, ctCollectionId)
);

create table Users_Roles (
	companyId LONG not null,
	roleId LONG not null,
	userId LONG not null,
	ctCollectionId LONG default 0 not null,
	ctChangeType BOOLEAN,
	primary key (roleId, userId, ctCollectionId)
);

create table Users_Teams (
	companyId LONG not null,
	teamId LONG not null,
	userId LONG not null,
	ctCollectionId LONG default 0 not null,
	ctChangeType BOOLEAN,
	primary key (teamId, userId, ctCollectionId)
);

create table Users_UserGroups (
	companyId LONG not null,
	userId LONG not null,
	userGroupId LONG not null,
	ctCollectionId LONG default 0 not null,
	ctChangeType BOOLEAN,
	primary key (userId, userGroupId, ctCollectionId)
);

create table UserTracker (
	mvccVersion LONG default 0 not null,
	userTrackerId LONG not null primary key,
	companyId LONG,
	userId LONG,
	modifiedDate DATE null,
	sessionId VARCHAR(200) null,
	remoteAddr VARCHAR(75) null,
	remoteHost VARCHAR(75) null,
	userAgent VARCHAR(200) null
);

create table UserTrackerPath (
	mvccVersion LONG default 0 not null,
	userTrackerPathId LONG not null primary key,
	companyId LONG,
	userTrackerId LONG,
	path_ STRING null,
	pathDate DATE null
);

create table VirtualHost (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	virtualHostId LONG not null,
	companyId LONG,
	layoutSetId LONG,
	hostname VARCHAR(200) null,
	defaultVirtualHost BOOLEAN,
	languageId VARCHAR(75) null,
	primary key (virtualHostId, ctCollectionId)
);

create table WebDAVProps (
	mvccVersion LONG default 0 not null,
	webDavPropsId LONG not null primary key,
	companyId LONG,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	props TEXT null
);

create table Website (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	websiteId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	url STRING null,
	listTypeId LONG,
	primary_ BOOLEAN,
	lastPublishDate DATE null
);

create table WorkflowDefinitionLink (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	workflowDefinitionLinkId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	typePK LONG,
	workflowDefinitionName VARCHAR(75) null,
	workflowDefinitionVersion INTEGER,
	primary key (workflowDefinitionLinkId, ctCollectionId)
);

create table WorkflowInstanceLink (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	workflowInstanceLinkId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	classNameId LONG,
	classPK LONG,
	workflowInstanceId LONG,
	primary key (workflowInstanceLinkId, ctCollectionId)
);