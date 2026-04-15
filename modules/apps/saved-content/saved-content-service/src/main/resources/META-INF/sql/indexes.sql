create index IX_85EFE02D on SavedContentEntry (classNameId, classPK, companyId);
create unique index IX_3059B7C5 on SavedContentEntry (classNameId, userId, classPK, companyId, ctCollectionId);
create index IX_5F4B6779 on SavedContentEntry (groupId, classNameId, classPK);
create unique index IX_3C86B2DD on SavedContentEntry (groupId, classNameId, userId, classPK, ctCollectionId);
create index IX_26BC5C5E on SavedContentEntry (groupId, userId);
create unique index IX_8168C76E on SavedContentEntry (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_39920D80 on SavedContentEntry (userId);
create index IX_AAA0B3AE on SavedContentEntry (uuid_[$COLUMN_LENGTH:75$]);