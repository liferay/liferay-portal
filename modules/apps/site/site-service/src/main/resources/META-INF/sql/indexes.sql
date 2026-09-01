create unique index IX_FF899B2F on SiteFriendlyURL (companyId, friendlyURL[$COLUMN_LENGTH:75$]);
create unique index IX_7A3B7A2C on SiteFriendlyURL (companyId, groupId, languageId[$COLUMN_LENGTH:75$]);
create unique index IX_82D4AAD9 on SiteFriendlyURL (uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_40402A2A on SiteSitemapRegenerationEntry (companyId, groupId, assetTypeKey[$COLUMN_LENGTH:75$]);