create unique index IX_6BDE593A on PLOEntry (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_FD42DFE on PLOEntry (companyId, key_[$COLUMN_LENGTH:1000$], languageId[$COLUMN_LENGTH:75$]);
create index IX_5EFCB06A on PLOEntry (companyId, languageId[$COLUMN_LENGTH:75$]);