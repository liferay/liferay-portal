create index IX_5B76D798 on DepotAppCustomization (depotEntryId, enabled);
create unique index IX_2CE1592A on DepotAppCustomization (depotEntryId, portletId[$COLUMN_LENGTH:75$], ctCollectionId);

create index IX_35C266B1 on DepotEntry (companyId, type_);
create unique index IX_E3EB2C84 on DepotEntry (groupId, ctCollectionId);

create index IX_146497CB on DepotEntryGroupRel (depotEntryId);
create index IX_7CA33F81 on DepotEntryGroupRel (toGroupId, ddmStructuresAvailable);
create unique index IX_1DD0EA9C on DepotEntryGroupRel (toGroupId, depotEntryId, ctCollectionId);
create index IX_BA106967 on DepotEntryGroupRel (toGroupId, searchable);
create index IX_98F19EC4 on DepotEntryGroupRel (toGroupId, type_);
create unique index IX_23B06412 on DepotEntryGroupRel (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_B423314A on DepotEntryPin (depotEntryId);
create unique index IX_D51E5D62 on DepotEntryPin (userId, depotEntryId, ctCollectionId);
create unique index IX_6BE13BD1 on DepotEntryPin (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);