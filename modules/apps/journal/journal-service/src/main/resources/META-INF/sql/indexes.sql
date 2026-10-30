create index IX_745E04FA on JournalArticle (DDMStructureId);
create index IX_DFF98523 on JournalArticle (companyId);
create index IX_A2D2CDB8 on JournalArticle (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_3048AF7A on JournalArticle (groupId, DDMStructureId);
create unique index IX_D3ACAD4A on JournalArticle (groupId, articleId[$COLUMN_LENGTH:75$], version, ctCollectionId);
create index IX_9CE6E0FA on JournalArticle (groupId, classNameId, classPK);
create index IX_A2534AC2 on JournalArticle (groupId, classNameId, layoutUuid[$COLUMN_LENGTH:75$]);
create index IX_373DCC43 on JournalArticle (groupId, classNameId, userId);
create unique index IX_D9F0782E on JournalArticle (groupId, externalReferenceCode[$COLUMN_LENGTH:75$], version, ctCollectionId);
create index IX_5CD17502 on JournalArticle (groupId, folderId);
create index IX_3C028C1E on JournalArticle (groupId, layoutUuid[$COLUMN_LENGTH:75$]);
create index IX_5CA5E0F6 on JournalArticle (groupId, status, articleId[$COLUMN_LENGTH:75$]);
create index IX_9D8D768 on JournalArticle (groupId, status, folderId);
create index IX_CF8F8F68 on JournalArticle (groupId, status, urlTitle[$COLUMN_LENGTH:255$]);
create index IX_22882D02 on JournalArticle (groupId, urlTitle[$COLUMN_LENGTH:255$]);
create unique index IX_F73C7E0D on JournalArticle (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_33F49D16 on JournalArticle (resourcePrimKey);
create index IX_EF9B7028 on JournalArticle (smallImageId);
create index IX_2AA511D5 on JournalArticle (status, displayDate);
create index IX_84305BF0 on JournalArticle (status, resourcePrimKey);
create index IX_F029602F on JournalArticle (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_9B44D13C on JournalArticleLocalization (companyId, articlePK, languageId[$COLUMN_LENGTH:75$], ctCollectionId);

create unique index IX_57129BA8 on JournalArticleResource (groupId, articleId[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_37A8A767 on JournalArticleResource (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create unique index IX_49A6FA16 on JournalContentSearch (groupId, privateLayout, layoutId, portletId[$COLUMN_LENGTH:200$], articleId[$COLUMN_LENGTH:75$], ctCollectionId);

create unique index IX_53294B1A on JournalFeed (groupId, feedId[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_800F33AF on JournalFeed (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create unique index IX_D67689C on JournalFolder (groupId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_E988689E on JournalFolder (groupId, name[$COLUMN_LENGTH:100$]);
create unique index IX_A2109363 on JournalFolder (groupId, parentFolderId, name[$COLUMN_LENGTH:100$], ctCollectionId);
create index IX_EFD9CAC on JournalFolder (groupId, parentFolderId, status);
create unique index IX_1965F913 on JournalFolder (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_63BDFA69 on JournalFolder (uuid_[$COLUMN_LENGTH:75$]);