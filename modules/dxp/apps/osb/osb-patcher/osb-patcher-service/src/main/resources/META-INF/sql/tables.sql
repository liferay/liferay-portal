create table PatcherAccount (
	patcherAccountId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	accountEntryId LONG,
	accountEntryCode VARCHAR(75) null
);

create table PatcherAccounts_PatcherBuilds (
	companyId LONG not null,
	patcherAccountId LONG not null,
	patcherBuildId LONG not null,
	primary key (patcherAccountId, patcherBuildId)
);

create table PatcherBuild (
	patcherBuildId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	patcherAccountId LONG,
	patcherFixId LONG,
	patcherProductVersionId LONG,
	patcherProjectVersionId LONG,
	ticketEntryId LONG,
	hotfixId LONG,
	name VARCHAR(75) null,
	originalName VARCHAR(75) null,
	key_ VARCHAR(75) null,
	keyVersion DOUBLE,
	type_ INTEGER,
	latestBuild BOOLEAN,
	latestKeyBuild BOOLEAN,
	latestLESATicketBuild BOOLEAN,
	latestSupportTicketBuild BOOLEAN,
	accountEntryCode VARCHAR(75) null,
	lesaTicket VARCHAR(75) null,
	lesaTicketVersion DOUBLE,
	supportTicket VARCHAR(75) null,
	supportTicketVersion DOUBLE,
	fileName VARCHAR(75) null,
	sourceName VARCHAR(75) null,
	childBuild BOOLEAN,
	comments VARCHAR(75) null,
	qaComments VARCHAR(75) null,
	qaStatus INTEGER,
	requestKey VARCHAR(75) null,
	notified BOOLEAN,
	productVersion INTEGER,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null
);

create table PatcherBuildRel (
	patcherBuildRelId LONG not null primary key,
	childPatcherBuildId LONG,
	parentPatcherBuildId LONG
);

create table PatcherBuilds_PatcherFixes (
	companyId LONG not null,
	patcherBuildId LONG not null,
	patcherFixId LONG not null,
	primary key (patcherBuildId, patcherFixId)
);

create table PatcherFix (
	patcherFixId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	patcherProductVersionId LONG,
	patcherProjectVersionId LONG,
	name VARCHAR(75) null,
	key_ VARCHAR(75) null,
	keyVersion DOUBLE,
	type_ INTEGER,
	latestFix BOOLEAN,
	obsolete BOOLEAN,
	committish VARCHAR(75) null,
	gitHash VARCHAR(75) null,
	gitRemoteURL VARCHAR(75) null,
	dependencies VARCHAR(75) null,
	requirements VARCHAR(75) null,
	requestKey VARCHAR(75) null,
	jenkinsResults VARCHAR(75) null,
	comments VARCHAR(75) null,
	fixPackStatus INTEGER,
	notified BOOLEAN,
	productVersion INTEGER,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null
);

create table PatcherFixComponent (
	patcherFixComponentId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	name VARCHAR(75) null
);

create table PatcherFixPack (
	patcherFixPackId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	patcherBuildId LONG,
	patcherFixComponentId LONG,
	patcherProjectVersionId LONG,
	name VARCHAR(75) null,
	version INTEGER,
	releasedDate DATE null,
	requirements VARCHAR(75) null,
	status INTEGER
);

create table PatcherFixRel (
	patcherFixRelId LONG not null primary key,
	childPatcherFixId LONG,
	parentPatcherFixId LONG
);

create table PatcherFixes_PatcherFixPacks (
	companyId LONG not null,
	patcherFixId LONG not null,
	patcherFixPackId LONG not null,
	primary key (patcherFixId, patcherFixPackId)
);

create table PatcherProductVersion (
	patcherProductVersionId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	name VARCHAR(75) null,
	fixDeliveryMethod INTEGER,
	moduleFolderName VARCHAR(75) null
);

create table PatcherProjectVersion (
	patcherProjectVersionId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	patcherProductVersionId LONG,
	rootPatcherProjectVersionId LONG,
	name VARCHAR(75) null,
	combinedBranch BOOLEAN,
	hide BOOLEAN,
	committish VARCHAR(75) null,
	repositoryName VARCHAR(75) null,
	fixedIssues VARCHAR(75) null,
	productVersion INTEGER
);

create table PatcherTicketHint (
	patcherTicketHintId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	patcherProductVersionId LONG,
	script VARCHAR(75) null
);