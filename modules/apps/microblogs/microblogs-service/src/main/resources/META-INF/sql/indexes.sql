create index IX_837C013D on MicroblogsEntry (companyId);
create index IX_6BD29B9C on MicroblogsEntry (type_, parentMicroblogsEntryId);
create index IX_92BA6F0 on MicroblogsEntry (userId, type_);