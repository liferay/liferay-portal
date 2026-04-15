create index IX_D8D58598 on AssetListEntry (groupId, assetEntryType[$COLUMN_LENGTH:255$], assetEntrySubtype[$COLUMN_LENGTH:255$]);
create unique index IX_366FAE09 on AssetListEntry (groupId, assetListEntryKey[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_B69949FB on AssetListEntry (groupId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_40A918D0 on AssetListEntry (groupId, title[$COLUMN_LENGTH:75$], assetEntryType[$COLUMN_LENGTH:255$], assetEntrySubtype[$COLUMN_LENGTH:255$]);
create unique index IX_5B95A9C6 on AssetListEntry (groupId, title[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_4FE08A35 on AssetListEntry (groupId, type_);
create unique index IX_5A6C7872 on AssetListEntry (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_5B11862A on AssetListEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_622F04CA on AssetListEntryAssetEntryRel (assetEntryId);
create unique index IX_FAAE938C on AssetListEntryAssetEntryRel (assetListEntryId, segmentsEntryId, position, ctCollectionId);
create unique index IX_1FDDE58D on AssetListEntryAssetEntryRel (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create unique index IX_56302677 on AssetListEntrySegmentsEntryRel (assetListEntryId, segmentsEntryId, ctCollectionId);
create index IX_1C9A6A4C on AssetListEntrySegmentsEntryRel (segmentsEntryId);
create unique index IX_29F4FD05 on AssetListEntrySegmentsEntryRel (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_E1D6CA09 on AssetListEntryUsage (classNameId, key_[$COLUMN_LENGTH:255$], companyId);
create unique index IX_512AA275 on AssetListEntryUsage (groupId, classNameId, key_[$COLUMN_LENGTH:255$], plid, containerType, containerKey[$COLUMN_LENGTH:255$], ctCollectionId);
create index IX_10BA153A on AssetListEntryUsage (groupId, classNameId, key_[$COLUMN_LENGTH:255$], type_);
create unique index IX_57EC072B on AssetListEntryUsage (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_BBE5024F on AssetListEntryUsage (plid, containerType, containerKey[$COLUMN_LENGTH:255$]);
create index IX_561E0151 on AssetListEntryUsage (uuid_[$COLUMN_LENGTH:75$]);