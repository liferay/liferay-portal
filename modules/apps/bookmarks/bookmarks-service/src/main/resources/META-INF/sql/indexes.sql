create index IX_5200100C on BookmarksEntry (groupId, folderId);
create index IX_69D78EAC on BookmarksEntry (groupId, status, folderId, userId);
create index IX_37518B0F on BookmarksEntry (groupId, status, userId);
create unique index IX_788BA343 on BookmarksEntry (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_B670BA39 on BookmarksEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_D16018A6 on BookmarksFolder (groupId, parentFolderId, status);
create unique index IX_F2715D9 on BookmarksFolder (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_451E7AE3 on BookmarksFolder (uuid_[$COLUMN_LENGTH:75$]);