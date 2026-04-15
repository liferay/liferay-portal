create unique index IX_4748C557 on CSDiagramEntry (CPDefinitionId, sequence[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_129C0EC6 on CSDiagramEntry (CPInstanceId);
create index IX_E1E7EA90 on CSDiagramEntry (CProductId);
create unique index IX_D09A23DE on CSDiagramEntry (externalReferenceCode[$COLUMN_LENGTH:75$], companyId, ctCollectionId);

create index IX_B0DD2127 on CSDiagramPin (CPDefinitionId);

create unique index IX_4F753100 on CSDiagramSetting (CPDefinitionId, ctCollectionId);
create index IX_BCB38741 on CSDiagramSetting (uuid_[$COLUMN_LENGTH:75$]);