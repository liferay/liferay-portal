create index IX_9B9729B4 on TemplateEntry (ddmTemplateId);
create unique index IX_8480136F on TemplateEntry (groupId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_D011CDAB on TemplateEntry (groupId, infoItemClassName[$COLUMN_LENGTH:75$], infoItemFormVariationKey[$COLUMN_LENGTH:75$]);
create unique index IX_F7DCA9E6 on TemplateEntry (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_3AF3BA36 on TemplateEntry (uuid_[$COLUMN_LENGTH:75$]);