create index IX_838D8DFC on BigDecimalEntries_LVEntries (companyId);
create index IX_67100507 on BigDecimalEntries_LVEntries (lvEntryId);

create index IX_867C5A9 on BigDecimalEntry (bigDecimalValue);

create unique index IX_1CF99E19 on CacheDisabledEntry (name[$COLUMN_LENGTH:75$]);

create index IX_4F11FECA on CacheFieldEntry (groupId);

create index IX_D7D86B01 on DefinedDefaultOrderEntry (name[$COLUMN_LENGTH:75$]);

create unique index IX_1B0249DC on ERCCompanyEntry (externalReferenceCode[$COLUMN_LENGTH:75$], companyId);
create index IX_84557D43 on ERCCompanyEntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_DA61F9E2 on ERCGroupEntry (groupId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_17A11405 on ERCGroupEntry (uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_6EA6F478 on ERCVersionedEntry (externalReferenceCode[$COLUMN_LENGTH:75$], groupId, head);
create unique index IX_4DC13E66 on ERCVersionedEntry (headId);
create unique index IX_A1B6E921 on ERCVersionedEntry (uuid_[$COLUMN_LENGTH:75$], groupId, head);

create unique index IX_D06D55F9 on ERCVersionedEntryVersion (ercVersionedEntryId, version);
create unique index IX_1F1BC169 on ERCVersionedEntryVersion (uuid_[$COLUMN_LENGTH:75$], version, groupId);

create unique index IX_6E042099 on EagerBlobEntry (uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_420C1E47 on FinderWhereClauseEntry (name[$COLUMN_LENGTH:75$]);

create unique index IX_EA51DD99 on IndexEntry (companyId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_366486F8 on IndexEntry (ownerId);
create index IX_24229732 on IndexEntry (ownerType, ownerId, plid);
create index IX_18CD6471 on IndexEntry (plid);
create index IX_A157BA68 on IndexEntry (portletId[$COLUMN_LENGTH:75$], ownerType, ownerId, companyId);
create unique index IX_A3ADF069 on IndexEntry (portletId[$COLUMN_LENGTH:75$], ownerType, ownerId, plid, ctCollectionId);
create index IX_49C16EA9 on IndexEntry (portletId[$COLUMN_LENGTH:75$], ownerType, plid);
create index IX_1D983CA on IndexEntry (portletId[$COLUMN_LENGTH:75$], plid);

create unique index IX_2FF02DF5 on LVEntry (groupId, head, uniqueGroupKey[$COLUMN_LENGTH:75$]);
create index IX_8F9FD921 on LVEntry (groupId, uniqueGroupKey[$COLUMN_LENGTH:75$]);
create unique index IX_50CAD09D on LVEntry (headId);
create unique index IX_91BCCF18 on LVEntry (uuid_[$COLUMN_LENGTH:75$], groupId, head);

create unique index IX_FC1C4C16 on LVEntryLocalization (headId);
create unique index IX_5233ABD3 on LVEntryLocalization (lvEntryId, languageId[$COLUMN_LENGTH:75$]);

create index IX_8EAC6E7D on LVEntryLocalizationVersion (lvEntryId, languageId[$COLUMN_LENGTH:75$]);
create unique index IX_754BD8D9 on LVEntryLocalizationVersion (lvEntryId, version, languageId[$COLUMN_LENGTH:75$]);
create index IX_142D1FEF on LVEntryLocalizationVersion (lvEntryLocalizationId);
create unique index IX_2EDFD541 on LVEntryLocalizationVersion (version, lvEntryLocalizationId);

create index IX_1A357E79 on LVEntryVersion (groupId, uniqueGroupKey[$COLUMN_LENGTH:75$]);
create unique index IX_D2FB5119 on LVEntryVersion (groupId, version, uniqueGroupKey[$COLUMN_LENGTH:75$]);
create index IX_1287D6FD on LVEntryVersion (lvEntryId);
create unique index IX_E9BD379C on LVEntryVersion (uuid_[$COLUMN_LENGTH:75$], groupId, version);
create unique index IX_47B1B7A1 on LVEntryVersion (version, lvEntryId);

create unique index IX_F723689D on LazyBlobEntry (uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_2E833843 on LocalizedEntryLocalization (localizedEntryId, languageId[$COLUMN_LENGTH:75$]);

create unique index IX_46C721B9 on NullConvertibleEntry (name[$COLUMN_LENGTH:75$]);

create index IX_61FDBFF1 on PermissionCheckFinderEntry (groupId);

create unique index IX_32712A54 on RedundantIndexEntry (companyId, name[$COLUMN_LENGTH:75$]);

create index IX_DA817981 on RenameFinderColumnEntry (columnToRename[$COLUMN_LENGTH:75$]);

create index IX_3C1F4D9A on UndefinedDefaultOrderEntry (name[$COLUMN_LENGTH:75$]);

create index IX_6770C47D on VersionedEntry (groupId, head);
create unique index IX_AAA6F330 on VersionedEntry (headId);

create index IX_D2594361 on VersionedEntryVersion (groupId, version);
create unique index IX_B51BCCBB on VersionedEntryVersion (versionedEntryId, version);