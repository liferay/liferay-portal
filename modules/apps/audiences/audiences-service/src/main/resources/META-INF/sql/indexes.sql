create unique index IX_D67F63BE on AudiencesEntry (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_E49A231E on AudiencesEntry (companyId, name[$COLUMN_LENGTH:75$]);

create unique index IX_83EE5CC on AudiencesEntryGroupRel (companyId, audienceEntryERC[$COLUMN_LENGTH:75$], groupERC[$COLUMN_LENGTH:75$]);
create index IX_933DF770 on AudiencesEntryGroupRel (companyId, groupERC[$COLUMN_LENGTH:75$]);