create table SAPEntry (
	uuid_ VARCHAR(75) null,
	sapEntryId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	allowedServiceSignatures STRING null,
	defaultSAPEntry BOOLEAN,
	enabled BOOLEAN,
	name VARCHAR(75) null,
	title TEXT null
);