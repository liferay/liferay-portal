create index IX_99628DD1 on DEDataDefinitionFieldLink (classNameId, classPK);
create unique index IX_B0B67DC9 on DEDataDefinitionFieldLink (ddmStructureId, classNameId, fieldName[$COLUMN_LENGTH:255$], classPK, ctCollectionId);
create index IX_E931B304 on DEDataDefinitionFieldLink (ddmStructureId, fieldName[$COLUMN_LENGTH:255$]);
create unique index IX_AA8B1050 on DEDataDefinitionFieldLink (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_1C932689 on DEDataListView (ddmStructureId);
create index IX_6C111CBD on DEDataListView (groupId, ddmStructureId, companyId);
create unique index IX_275E4568 on DEDataListView (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);