create table MFATimeBasedOTPEntry (
	mvccVersion LONG default 0 not null,
	mfaTimeBasedOTPEntryId LONG not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	algorithm VARCHAR(75) null,
	failedAttempts INTEGER,
	lastFailDate DATE null,
	lastFailIP VARCHAR(75) null,
	lastSuccessDate DATE null,
	lastSuccessIP VARCHAR(75) null,
	lastValidTOTP VARCHAR(75) null,
	sharedSecret VARCHAR(75) null
);