create unique index IX_EFD3CBF7 on ClientExtensionEntry (companyId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_32C1FC31 on ClientExtensionEntry (companyId, type_[$COLUMN_LENGTH:75$]);
create index IX_526820B0 on ClientExtensionEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_A3BB58FF on ClientExtensionEntryRel (classNameId, classPK, type_[$COLUMN_LENGTH:75$]);
create index IX_44C5316 on ClientExtensionEntryRel (companyId, cetExternalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_8C6A0432 on ClientExtensionEntryRel (groupId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_BE94634 on ClientExtensionEntryRel (type_[$COLUMN_LENGTH:75$]);
create unique index IX_E6F09C55 on ClientExtensionEntryRel (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);