create unique index IX_ABEEE793 on ChangesetCollection (groupId, name[$COLUMN_LENGTH:75$]);

create unique index IX_71B99FC2 on ChangesetEntry (changesetCollectionId, classNameId, classExternalReferenceCode[$COLUMN_LENGTH:500$]);
create unique index IX_EF48912A on ChangesetEntry (changesetCollectionId, classNameId, classPK);