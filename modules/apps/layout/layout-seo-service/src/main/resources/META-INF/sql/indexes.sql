create unique index IX_93D96C8F on LayoutSEOEntry (groupId, privateLayout, layoutId, ctCollectionId);
create unique index IX_63195F59 on LayoutSEOEntry (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_C16BEEAD on LayoutSEOEntryCustomMetaTag (groupId, layoutSEOEntryId);

create unique index IX_E4DFAF28 on LayoutSEOSite (groupId, ctCollectionId);