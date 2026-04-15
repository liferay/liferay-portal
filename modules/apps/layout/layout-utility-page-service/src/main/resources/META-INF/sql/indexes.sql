create unique index IX_E84DFDB8 on LayoutUtilityPageEntry (groupId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_DCFECA00 on LayoutUtilityPageEntry (groupId, type_[$COLUMN_LENGTH:75$], defaultLayoutUtilityPageEntry);
create unique index IX_9C37588F on LayoutUtilityPageEntry (groupId, type_[$COLUMN_LENGTH:75$], name[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_A1F2462F on LayoutUtilityPageEntry (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_6D96DD70 on LayoutUtilityPageEntry (plid, ctCollectionId);
create index IX_997885CD on LayoutUtilityPageEntry (uuid_[$COLUMN_LENGTH:75$]);