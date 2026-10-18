create index IX_837C013D on MicroblogsEntry (companyId);
create index IX_9A7A988B on MicroblogsEntry (type_, creatorClassNameId, creatorClassPK);
create index IX_6BD29B9C on MicroblogsEntry (type_, parentMicroblogsEntryId);
create index IX_AA96AEF9 on MicroblogsEntry (type_, userId, socialRelationType, createDate);
create index IX_6C297B45 on MicroblogsEntry (userId);