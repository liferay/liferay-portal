create unique index IX_87C75408 on AssetCategoryProperty (categoryId, key_[$COLUMN_LENGTH:255$], ctCollectionId);
create unique index IX_999DB31C on AssetCategoryProperty (companyId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_52340033 on AssetCategoryProperty (companyId, key_[$COLUMN_LENGTH:255$]);