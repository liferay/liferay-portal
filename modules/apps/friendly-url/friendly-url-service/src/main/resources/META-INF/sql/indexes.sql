create index IX_56567C8E on FriendlyURLEntry (classNameId, companyId);
create index IX_F3DC928B on FriendlyURLEntry (groupId, classNameId, classPK);
create unique index IX_D51F1A48 on FriendlyURLEntry (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_2B00D1D3 on FriendlyURLEntryLocalization (classNameId, groupId, languageId[$COLUMN_LENGTH:75$], classPK);
create unique index IX_7FE9DA6A on FriendlyURLEntryLocalization (classNameId, groupId, urlTitle[$COLUMN_LENGTH:255$], languageId[$COLUMN_LENGTH:75$], parentClassPK, ctCollectionId);
create index IX_B5E04A33 on FriendlyURLEntryLocalization (classNameId, groupId, urlTitle[$COLUMN_LENGTH:255$], parentClassPK);
create index IX_8EAFA9A8 on FriendlyURLEntryLocalization (classNameId, urlTitle[$COLUMN_LENGTH:255$], companyId, ctCollectionId);
create index IX_BFA6E36A on FriendlyURLEntryLocalization (friendlyURLEntryId);
create unique index IX_E46130F on FriendlyURLEntryLocalization (languageId[$COLUMN_LENGTH:75$], friendlyURLEntryId, ctCollectionId);

create unique index IX_5BE324B9 on FriendlyURLEntryMapping (classNameId, classPK, ctCollectionId);