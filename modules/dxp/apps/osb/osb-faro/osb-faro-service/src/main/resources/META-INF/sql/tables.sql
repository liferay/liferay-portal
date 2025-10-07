create table OSBFaro_FaroChannel (
	mvccVersion LONG default 0 not null,
	faroChannelId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createTime LONG,
	modifiedTime LONG,
	channelId VARCHAR(75) null,
	name VARCHAR(75) null,
	permissionType INTEGER,
	workspaceGroupId LONG
);

create table OSBFaro_FaroNotification (
	mvccVersion LONG default 0 not null,
	faroNotificationId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	createTime LONG,
	modifiedTime LONG,
	ownerId LONG,
	scope VARCHAR(75) null,
	read_ BOOLEAN,
	type_ VARCHAR(75) null,
	subtype VARCHAR(75) null
);

create table OSBFaro_FaroPreferences (
	mvccVersion LONG default 0 not null,
	faroPreferencesId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createTime LONG,
	modifiedTime LONG,
	ownerId LONG,
	preferences STRING null
);

create table OSBFaro_FaroProject (
	mvccVersion LONG default 0 not null,
	faroProjectId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createTime LONG,
	modifiedTime LONG,
	name VARCHAR(75) null,
	accountKey VARCHAR(75) null,
	accountName VARCHAR(75) null,
	corpProjectName VARCHAR(75) null,
	corpProjectUuid VARCHAR(75) null,
	dataSourceConnected BOOLEAN,
	ipAddresses STRING null,
	incidentReportEmailAddresses STRING null,
	lastAccessTime LONG,
	recommendationsEnabled BOOLEAN,
	serverLocation VARCHAR(75) null,
	services STRING null,
	state_ VARCHAR(75) null,
	subscription STRING null,
	subscriptionModifiedTime LONG,
	timeZoneId VARCHAR(75) null,
	weDeployKey VARCHAR(75) null
);

create table OSBFaro_FaroProjectEmailDomain (
	mvccVersion LONG default 0 not null,
	faroProjectEmailDomainId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	faroProjectId LONG,
	emailDomain VARCHAR(75) null
);

create table OSBFaro_FaroProjectUsage (
	mvccVersion LONG default 0 not null,
	faroProjectUsageId LONG not null primary key,
	companyId LONG,
	userId LONG,
	createTime LONG,
	modifiedTime LONG,
	faroProjectId LONG,
	knownIndividualsCount LONG,
	monthDateKey VARCHAR(75) null,
	pageViewsCount LONG,
	usageTime LONG
);

create table OSBFaro_FaroUser (
	mvccVersion LONG default 0 not null,
	faroUserId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createTime LONG,
	modifiedTime LONG,
	liveUserId LONG,
	roleId LONG,
	emailAddress VARCHAR(75) null,
	key_ VARCHAR(75) null,
	status INTEGER
);