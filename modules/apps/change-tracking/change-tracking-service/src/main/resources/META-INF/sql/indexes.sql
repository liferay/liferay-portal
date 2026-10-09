create index IX_F925260 on CTAutoResolutionInfo (ctCollectionId, modelClassNameId, sourceModelClassPK);

create unique index IX_30F5BCCA on CTCollection (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_8CA3F96F on CTCollection (companyId, schemaVersionId);
create index IX_F75A3633 on CTCollection (companyId, status, userId);
create index IX_DA0BBE1F on CTCollection (uuid_[$COLUMN_LENGTH:75$]);

create index IX_489283B9 on CTCollectionTemplate (companyId);

create index IX_FE644B52 on CTComment (ctCollectionId);

create unique index IX_295C418C on CTEntry (ctCollectionId, modelClassNameId, modelClassPK);
create unique index IX_7FBB3312 on CTEntry (externalReferenceCode[$COLUMN_LENGTH:75$], companyId);
create index IX_EA4D7741 on CTEntry (userId);
create index IX_E1E08DCD on CTEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_9FB742FA on CTMessage (ctCollectionId);

create unique index IX_516E5375 on CTPreferences (companyId, userId);
create index IX_3FECC82B on CTPreferences (ctCollectionId);
create index IX_D9EA7A42 on CTPreferences (previousCtCollectionId);

create index IX_7523B0A4 on CTProcess (companyId);
create index IX_46BA2033 on CTProcess (ctCollectionId, type_);

create index IX_9B9391EB on CTRemote (companyId);

create index IX_687AE35C on CTSchemaVersion (companyId);