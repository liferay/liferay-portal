create index IX_745E04FA on JournalArticle (DDMStructureId);
create index IX_75CCA4D1 on JournalArticle (DDMTemplateKey[$COLUMN_LENGTH:75$]);
create index IX_3D070845 on JournalArticle (companyId, version);
create index IX_A2D2CDB8 on JournalArticle (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_3048AF7A on JournalArticle (groupId, DDMStructureId);
create index IX_31B74F51 on JournalArticle (groupId, DDMTemplateKey[$COLUMN_LENGTH:75$]);
create unique index IX_D3ACAD4A on JournalArticle (groupId, articleId[$COLUMN_LENGTH:75$], version, ctCollectionId);
create index IX_6D117C1E on JournalArticle (groupId, classNameId, DDMStructureId);
create index IX_6E801BF5 on JournalArticle (groupId, classNameId, DDMTemplateKey[$COLUMN_LENGTH:75$]);
create index IX_9CE6E0FA on JournalArticle (groupId, classNameId, classPK);
create index IX_A2534AC2 on JournalArticle (groupId, classNameId, layoutUuid[$COLUMN_LENGTH:75$]);
create index IX_373DCC43 on JournalArticle (groupId, classNameId, userId);
create index IX_5CD17502 on JournalArticle (groupId, folderId);
create index IX_3C028C1E on JournalArticle (groupId, layoutUuid[$COLUMN_LENGTH:75$]);
create index IX_5CA5E0F6 on JournalArticle (groupId, status, articleId[$COLUMN_LENGTH:75$]);
create index IX_BCAFC000 on JournalArticle (groupId, status, classNameId, folderId);
create index IX_9D8D768 on JournalArticle (groupId, status, folderId);
create index IX_CF8F8F68 on JournalArticle (groupId, status, urlTitle[$COLUMN_LENGTH:255$]);
create index IX_22882D02 on JournalArticle (groupId, urlTitle[$COLUMN_LENGTH:255$]);
create index IX_D19C1B9F on JournalArticle (groupId, userId);
create unique index IX_F73C7E0D on JournalArticle (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_FE3DB4F8 on JournalArticle (groupId, version, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_3F1EA19E on JournalArticle (layoutUuid[$COLUMN_LENGTH:75$]);
create index IX_89FF8B06 on JournalArticle (resourcePrimKey, indexable);
create index IX_EF9B7028 on JournalArticle (smallImageId);
create index IX_15ADA32B on JournalArticle (status, companyId, version);
create index IX_2AA511D5 on JournalArticle (status, displayDate);
create index IX_BCE7DFEC on JournalArticle (status, resourcePrimKey, indexable);
create index IX_F029602F on JournalArticle (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_9B44D13C on JournalArticleLocalization (companyId, articlePK, languageId[$COLUMN_LENGTH:75$], ctCollectionId);

create unique index IX_57129BA8 on JournalArticleResource (groupId, articleId[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_37A8A767 on JournalArticleResource (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_9207CB31 on JournalContentSearch (articleId[$COLUMN_LENGTH:75$]);
create index IX_42F51F38 on JournalContentSearch (companyId);
create index IX_6838E427 on JournalContentSearch (groupId, articleId[$COLUMN_LENGTH:75$]);
create unique index IX_F91BC3CC on JournalContentSearch (groupId, privateLayout, articleId[$COLUMN_LENGTH:75$], layoutId, portletId[$COLUMN_LENGTH:200$], ctCollectionId);
create index IX_7ACC74C9 on JournalContentSearch (groupId, privateLayout, layoutId, portletId[$COLUMN_LENGTH:200$]);
create index IX_8DAF8A35 on JournalContentSearch (portletId[$COLUMN_LENGTH:200$]);

create unique index IX_53294B1A on JournalFeed (groupId, feedId[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_800F33AF on JournalFeed (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_E6E2725D on JournalFolder (companyId);
create unique index IX_D67689C on JournalFolder (groupId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_E988689E on JournalFolder (groupId, name[$COLUMN_LENGTH:100$]);
create unique index IX_A2109363 on JournalFolder (groupId, parentFolderId, name[$COLUMN_LENGTH:100$], ctCollectionId);
create index IX_EFD9CAC on JournalFolder (groupId, parentFolderId, status);
create unique index IX_1965F913 on JournalFolder (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_8D6902B7 on JournalFolder (status, companyId);
create index IX_63BDFA69 on JournalFolder (uuid_[$COLUMN_LENGTH:75$]);