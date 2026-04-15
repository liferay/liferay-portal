create unique index IX_68525006 on AssetDisplayPageEntry (groupId, classNameId, classPK, ctCollectionId);
create unique index IX_9920AB1F on AssetDisplayPageEntry (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_BFB8A913 on AssetDisplayPageEntry (layoutPageTemplateEntryId);
create index IX_DEA3F2DD on AssetDisplayPageEntry (uuid_[$COLUMN_LENGTH:75$]);