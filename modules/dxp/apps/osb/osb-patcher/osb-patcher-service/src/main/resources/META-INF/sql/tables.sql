create table OSBPatcher_PAccounts_PBuilds (
	companyId LONG not null,
	patcherAccountId LONG not null,
	patcherBuildId LONG not null,
	primary key (patcherAccountId, patcherBuildId)
);

create table OSBPatcher_PBuilds_PFixes (
	companyId LONG not null,
	patcherBuildId LONG not null,
	patcherFixId LONG not null,
	primary key (patcherBuildId, patcherFixId)
);

create table OSBPatcher_PFixes_PFixPacks (
	companyId LONG not null,
	patcherFixId LONG not null,
	patcherFixPackId LONG not null,
	primary key (patcherFixId, patcherFixPackId)
);

create table OSBPatcher_PatcherAccount (
	mvccVersion LONG default 0 not null,
	patcherAccountId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	accountEntryId LONG,
	accountEntryCode VARCHAR(75) null
);

create table OSBPatcher_PatcherBuild (
	mvccVersion LONG default 0 not null,
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
	accountEntryCode VARCHAR(75) null,
	childBuild BOOLEAN,
	comments TEXT null,
	fileName VARCHAR(500) null,
	hotfixId LONG,
	initialName VARCHAR(75) null,
	key_ VARCHAR(75) null,
	keyVersion DOUBLE,
	latestBuild BOOLEAN,
	latestKeyBuild BOOLEAN,
	latestLESATicketBuild BOOLEAN,
	latestSupportTicketBuild BOOLEAN,
	lesaTicket VARCHAR(75) null,
	lesaTicketVersion DOUBLE,
	name STRING null,
	notified BOOLEAN,
	productVersion INTEGER,
	qaComments TEXT null,
	qaStatus INTEGER,
	requestKey VARCHAR(75) null,
	sourceName VARCHAR(500) null,
	supportTicket VARCHAR(75) null,
	supportTicketVersion DOUBLE,
	type_ INTEGER,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null
);

create table OSBPatcher_PatcherBuildRel (
	mvccVersion LONG default 0 not null,
	patcherBuildRelId LONG not null primary key,
	companyId LONG,
	childPatcherBuildId LONG,
	parentPatcherBuildId LONG
);

create table OSBPatcher_PatcherFix (
	mvccVersion LONG default 0 not null,
	patcherFixId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	patcherProductVersionId LONG,
	patcherProjectVersionId LONG,
	comments TEXT null,
	committish VARCHAR(75) null,
	dependencies VARCHAR(500) null,
	fixPackStatus INTEGER,
	gitHash VARCHAR(75) null,
	gitRemoteURL VARCHAR(500) null,
	jenkinsResults TEXT null,
	key_ VARCHAR(75) null,
	keyVersion DOUBLE,
	latestFix BOOLEAN,
	name STRING null,
	notified BOOLEAN,
	obsolete BOOLEAN,
	productVersion INTEGER,
	requestKey VARCHAR(75) null,
	requirements VARCHAR(75) null,
	type_ INTEGER,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null
);

create table OSBPatcher_PatcherFixComponent (
	mvccVersion LONG default 0 not null,
	patcherFixComponentId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	name VARCHAR(75) null
);

create table OSBPatcher_PatcherFixPack (
	mvccVersion LONG default 0 not null,
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
	releasedDate DATE null,
	requirements VARCHAR(75) null,
	version INTEGER,
	status INTEGER
);

create table OSBPatcher_PatcherFixRel (
	mvccVersion LONG default 0 not null,
	patcherFixRelId LONG not null primary key,
	companyId LONG,
	childPatcherFixId LONG,
	parentPatcherFixId LONG
);

create table OSBPatcher_PatcherTicketHint (
	mvccVersion LONG default 0 not null,
	patcherTicketHintId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	patcherProductVersionId LONG,
	script VARCHAR(75) null
);

create table PProductVersion (
	mvccVersion LONG default 0 not null,
	patcherProductVersionId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	fixDeliveryMethod INTEGER,
	moduleFolderName VARCHAR(500) null,
	name VARCHAR(75) null
);

create table PProjectVersion (
	mvccVersion LONG default 0 not null,
	patcherProjectVersionId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	patcherProductVersionId LONG,
	rootPatcherProjectVersionId LONG,
	combinedBranch BOOLEAN,
	committish VARCHAR(150) null,
	fixedIssues TEXT null,
	hide BOOLEAN,
	name VARCHAR(150) null,
	productVersion INTEGER,
	repositoryName VARCHAR(75) null
);