create table OSB_PatcherAccount (
	patcherAccountId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	accountEntryId LONG,
	accountEntryCode VARCHAR(75) null
);

create table OSB_PatcherAccounts_PatcherBuilds (
	patcherAccountId LONG not null,
	patcherBuildId LONG not null,
	primary key (patcherAccountId, patcherBuildId)
);

create table OSB_PatcherBuild (
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
	name TEXT null,
	originalName TEXT null,
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
	fileName VARCHAR(500) null,
	sourceName VARCHAR(500) null,
	childBuild BOOLEAN,
	comments TEXT null,
	qaComments TEXT null,
	qaStatus INTEGER,
	requestKey VARCHAR(75) null,
	notified BOOLEAN,
	productVersion INTEGER,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null
);

create table OSB_PatcherBuildRel (
	patcherBuildRelId LONG not null primary key,
	childPatcherBuildId LONG,
	parentPatcherBuildId LONG
);

create table OSB_PatcherBuilds_PatcherFixes (
	patcherBuildId LONG not null,
	patcherFixId LONG not null,
	primary key (patcherBuildId, patcherFixId)
);

create table OSB_PatcherFix (
	patcherFixId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	patcherProductVersionId LONG,
	patcherProjectVersionId LONG,
	name TEXT null,
	key_ VARCHAR(75) null,
	keyVersion DOUBLE,
	type_ INTEGER,
	latestFix BOOLEAN,
	obsolete BOOLEAN,
	committish VARCHAR(75) null,
	gitHash VARCHAR(75) null,
	gitRemoteURL VARCHAR(500) null,
	dependencies VARCHAR(500) null,
	requirements VARCHAR(75) null,
	requestKey VARCHAR(75) null,
	jenkinsResults TEXT null,
	comments TEXT null,
	fixPackStatus INTEGER,
	notified BOOLEAN,
	productVersion INTEGER,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null
);

create table OSB_PatcherFixComponent (
	patcherFixComponentId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	name VARCHAR(75) null
);

create table OSB_PatcherFixPack (
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

create table OSB_PatcherFixRel (
	patcherFixRelId LONG not null primary key,
	childPatcherFixId LONG,
	parentPatcherFixId LONG
);

create table OSB_PatcherFixes_PatcherFixPacks (
	patcherFixId LONG not null,
	patcherFixPackId LONG not null,
	primary key (patcherFixId, patcherFixPackId)
);

create table OSB_PatcherProductVersion (
	patcherProductVersionId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	name VARCHAR(75) null,
	fixDeliveryMethod INTEGER,
	moduleFolderName VARCHAR(500) null
);

create table OSB_PatcherProjectVersion (
	patcherProjectVersionId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	patcherProductVersionId LONG,
	rootPatcherProjectVersionId LONG,
	name VARCHAR(150) null,
	combinedBranch BOOLEAN,
	hide BOOLEAN,
	committish VARCHAR(150) null,
	repositoryName VARCHAR(75) null,
	fixedIssues TEXT null,
	productVersion INTEGER
);

create table OSB_PatcherTicketHint (
	patcherTicketHintId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	patcherProductVersionId LONG,
	script VARCHAR(75) null
);