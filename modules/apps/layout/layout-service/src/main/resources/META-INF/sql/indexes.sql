create unique index IX_4C18FD9B on LayoutClassedModelUsage (classNameId, classExternalReferenceCode[$COLUMN_LENGTH:75$], classPK, containerType, plid, groupId, containerKey[$COLUMN_LENGTH:200$], ctCollectionId);
create index IX_B041F1F5 on LayoutClassedModelUsage (classNameId, classPK, type_);
create index IX_82A5B647 on LayoutClassedModelUsage (classNameId, companyId, classExternalReferenceCode[$COLUMN_LENGTH:75$], type_);
create index IX_6AAEDC6 on LayoutClassedModelUsage (classNameId, companyId, containerType);
create index IX_F747B9BD on LayoutClassedModelUsage (containerType, plid, containerKey[$COLUMN_LENGTH:200$]);
create index IX_19448DD6 on LayoutClassedModelUsage (plid);
create unique index IX_8A32D79F on LayoutClassedModelUsage (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create unique index IX_F4B6F839 on LayoutLocalization (plid, languageId[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_9336DDD on LayoutLocalization (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);