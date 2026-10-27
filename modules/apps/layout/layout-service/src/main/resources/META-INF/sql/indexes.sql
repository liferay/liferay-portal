create unique index IX_DDA034D on LayoutClassedModelUsage (classNameId, classPK, containerType, plid, groupId, containerKey[$COLUMN_LENGTH:200$], classExternalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_B041F1F5 on LayoutClassedModelUsage (classNameId, classPK, type_);
create index IX_C6C80E5 on LayoutClassedModelUsage (classNameId, companyId);
create index IX_65B533A0 on LayoutClassedModelUsage (classNameId, containerType, companyId);
create index IX_F747B9BD on LayoutClassedModelUsage (containerType, plid, containerKey[$COLUMN_LENGTH:200$]);
create index IX_19448DD6 on LayoutClassedModelUsage (plid);
create unique index IX_8A32D79F on LayoutClassedModelUsage (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create unique index IX_E26CFF79 on LayoutLocalization (languageId[$COLUMN_LENGTH:75$], plid, ctCollectionId);
create unique index IX_9336DDD on LayoutLocalization (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);