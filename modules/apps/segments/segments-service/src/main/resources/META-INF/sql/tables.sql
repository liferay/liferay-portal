create table SExperienceAudienceEntryRel (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	sExperienceAudienceEntryRelId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	audienceEntryERC VARCHAR(75) null,
	priority INTEGER,
	segmentsExperienceERC VARCHAR(75) null,
	primary key (sExperienceAudienceEntryRelId, ctCollectionId)
);

create table SegmentsEntry (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	segmentsEntryId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	segmentsEntryKey VARCHAR(75) null,
	name STRING null,
	description STRING null,
	active_ BOOLEAN,
	criteria TEXT null,
	source VARCHAR(75) null,
	type_ INTEGER,
	lastPublishDate DATE null,
	primary key (segmentsEntryId, ctCollectionId)
);

create table SegmentsEntryRel (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	segmentsEntryRelId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	segmentsEntryId LONG,
	classNameId LONG,
	classPK LONG,
	primary key (segmentsEntryRelId, ctCollectionId)
);

create table SegmentsEntryRole (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	segmentsEntryRoleId LONG not null,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	segmentsEntryId LONG,
	roleId LONG,
	primary key (segmentsEntryRoleId, ctCollectionId)
);

create table SegmentsExperience (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	externalReferenceCode VARCHAR(75) null,
	segmentsExperienceId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	segmentsEntryERC VARCHAR(75) null,
	segmentsEntryScopeERC VARCHAR(75) null,
	segmentsExperienceKey VARCHAR(75) null,
	plid LONG,
	name STRING null,
	priority INTEGER,
	active_ BOOLEAN,
	typeSettings VARCHAR(75) null,
	lastPublishDate DATE null,
	primary key (segmentsExperienceId, ctCollectionId)
);

create table SegmentsExperiment (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	segmentsExperimentId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	segmentsEntryId LONG,
	segmentsExperienceId LONG,
	segmentsExperimentKey VARCHAR(75) null,
	plid LONG,
	name VARCHAR(75) null,
	description STRING null,
	typeSettings TEXT null,
	status INTEGER,
	primary key (segmentsExperimentId, ctCollectionId)
);

create table SegmentsExperimentRel (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	segmentsExperimentRelId LONG not null,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	segmentsExperimentId LONG,
	segmentsExperienceId LONG,
	split DOUBLE,
	primary key (segmentsExperimentRelId, ctCollectionId)
);