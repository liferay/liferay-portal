create unique index IX_68525006 on AssetDisplayPageEntry (groupId, classNameId, classPK, ctCollectionId);
create index IX_BFB8A913 on AssetDisplayPageEntry (layoutPageTemplateEntryId);
create unique index IX_EF42C6CB on AssetDisplayPageEntry (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);