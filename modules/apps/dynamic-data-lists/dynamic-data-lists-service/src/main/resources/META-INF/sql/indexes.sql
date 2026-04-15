create index IX_D443D273 on DDLRecord (className[$COLUMN_LENGTH:300$], classPK);
create index IX_6A6C1C85 on DDLRecord (companyId);
create index IX_F12C61D4 on DDLRecord (recordSetId, recordSetVersion[$COLUMN_LENGTH:75$]);
create index IX_AAC564D3 on DDLRecord (recordSetId, userId);
create unique index IX_7E71D397 on DDLRecord (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_6705D180 on DDLRecordSet (DDMStructureId);
create unique index IX_D0A9257F on DDLRecordSet (groupId, recordSetKey[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_A7D89A3F on DDLRecordSet (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_1C4E1CC9 on DDLRecordSetVersion (recordSetId, status);
create unique index IX_577F80E3 on DDLRecordSetVersion (recordSetId, version[$COLUMN_LENGTH:75$], ctCollectionId);

create index IX_762ADC7 on DDLRecordVersion (recordId, status);
create unique index IX_8EDB4BA5 on DDLRecordVersion (recordId, version[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_A1B81B16 on DDLRecordVersion (recordSetId, recordSetVersion[$COLUMN_LENGTH:75$], status, userId);