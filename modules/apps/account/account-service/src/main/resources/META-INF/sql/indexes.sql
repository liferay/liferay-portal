create unique index IX_FBFAF640 on AccountEntry (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_48CB043 on AccountEntry (companyId, status);
create index IX_908C3410 on AccountEntry (userId, type_[$COLUMN_LENGTH:75$]);
create index IX_6901A669 on AccountEntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_EC6CC41D on AccountEntryOrganizationRel (accountEntryId, organizationId);
create index IX_2FA4FA69 on AccountEntryOrganizationRel (organizationId);

create unique index IX_ED720A80 on AccountEntryUserRel (accountEntryId, accountUserId);
create index IX_4EA60AB4 on AccountEntryUserRel (accountUserId);

create index IX_38BDB33 on AccountGroup (companyId, defaultAccountGroup);
create unique index IX_F7BFA1CD on AccountGroup (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_E86A36FC on AccountGroup (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_448835E3 on AccountGroupRel (accountGroupId, classNameId, classPK);
create index IX_E31F0762 on AccountGroupRel (classNameId, classPK);

create index IX_3A47CDD on AccountRole (accountEntryId);
create index IX_6BCBD313 on AccountRole (companyId, accountEntryId);
create unique index IX_88B680FE on AccountRole (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_714A358E on AccountRole (roleId);