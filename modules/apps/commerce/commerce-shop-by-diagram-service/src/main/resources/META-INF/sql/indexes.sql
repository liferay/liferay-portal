create unique index IX_E9871D7 on CSDiagramEntry (CPDefinitionId, ctCollectionId, sequence[$COLUMN_LENGTH:75$]);
create index IX_129C0EC6 on CSDiagramEntry (CPInstanceId);
create index IX_E1E7EA90 on CSDiagramEntry (CProductId);
create unique index IX_EBC4025E on CSDiagramEntry (ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$], companyId);

create index IX_B0DD2127 on CSDiagramPin (CPDefinitionId);

create unique index IX_4F753100 on CSDiagramSetting (CPDefinitionId, ctCollectionId);
create index IX_BCB38741 on CSDiagramSetting (uuid_[$COLUMN_LENGTH:75$]);