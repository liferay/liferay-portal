create unique index IX_A42CEE24 on PatcherAccount (accountEntryCode[$COLUMN_LENGTH:75$]);

create index IX_3A6CB4CC on PatcherAccounts_PatcherBuilds (companyId);
create index IX_3F0645C4 on PatcherAccounts_PatcherBuilds (patcherBuildId);

create unique index IX_9F3FB1E0 on PatcherBuild (key_[$COLUMN_LENGTH:75$], keyVersion);
create index IX_516A1E9D on PatcherBuild (key_[$COLUMN_LENGTH:75$], latestKeyBuild);
create index IX_B00F25C on PatcherBuild (notified, status, modifiedDate);
create index IX_5ACDE4D4 on PatcherBuild (patcherFixId, childBuild);
create index IX_421C5E14 on PatcherBuild (patcherFixId, patcherProductVersionId, childBuild, type_);
create index IX_B4514311 on PatcherBuild (patcherProductVersionId, patcherAccountId);
create index IX_BA0F3A6F on PatcherBuild (patcherProjectVersionId, latestKeyBuild, name[$COLUMN_LENGTH:2000000$], accountEntryCode[$COLUMN_LENGTH:75$]);
create index IX_194A867A on PatcherBuild (supportTicket[$COLUMN_LENGTH:75$], latestSupportTicketBuild);
create index IX_308CB9FD on PatcherBuild (supportTicket[$COLUMN_LENGTH:75$], supportTicketVersion);

create index IX_17F0909A on PatcherBuildRel (childPatcherBuildId);
create index IX_B91C07B8 on PatcherBuildRel (parentPatcherBuildId);

create index IX_458258AF on PatcherBuilds_PatcherFixes (companyId);
create index IX_FD8E2368 on PatcherBuilds_PatcherFixes (patcherFixId);

create index IX_DA886379 on PatcherFix (patcherProjectVersionId);
create index IX_F0E0CA10 on PatcherFix (type_, key_[$COLUMN_LENGTH:75$], keyVersion);
create index IX_E2B53C23 on PatcherFix (type_, latestFix, key_[$COLUMN_LENGTH:75$]);
create index IX_B3F9D86D on PatcherFix (type_, patcherProjectVersionId, latestFix, name[$COLUMN_LENGTH:2000000$]);
create index IX_7DDE5414 on PatcherFix (type_, patcherProjectVersionId, latestFix, status);
create index IX_FEB67B0C on PatcherFix (type_, status, notified, modifiedDate);

create unique index IX_3D2CC1E2 on PatcherFixComponent (name[$COLUMN_LENGTH:75$]);

create unique index IX_F3A916A7 on PatcherFixPack (patcherBuildId);
create index IX_2A135032 on PatcherFixPack (patcherFixComponentId, patcherProjectVersionId, version);
create index IX_1F78011F on PatcherFixPack (patcherFixComponentId, version);
create unique index IX_7A80895F on PatcherFixPack (patcherProjectVersionId, name[$COLUMN_LENGTH:75$]);
create index IX_BD7C9086 on PatcherFixPack (patcherProjectVersionId, status);
create index IX_AA641829 on PatcherFixPack (version);

create index IX_3F15CDE8 on PatcherFixRel (childPatcherFixId);
create index IX_B2ABC298 on PatcherFixRel (parentPatcherFixId);

create index IX_D9CB9F79 on PatcherFixes_PatcherFixPacks (companyId);
create index IX_593D4197 on PatcherFixes_PatcherFixPacks (patcherFixPackId);

create index IX_44128EB6 on PatcherProductVersion (fixDeliveryMethod);
create unique index IX_5823BC23 on PatcherProductVersion (name[$COLUMN_LENGTH:75$]);

create unique index IX_176AE0EF on PatcherProjectVersion (committish[$COLUMN_LENGTH:150$]);
create unique index IX_E103E4B9 on PatcherProjectVersion (name[$COLUMN_LENGTH:150$]);
create index IX_B41BC1D8 on PatcherProjectVersion (patcherProductVersionId, repositoryName[$COLUMN_LENGTH:75$]);
create index IX_9E7A436E on PatcherProjectVersion (patcherProductVersionId, rootPatcherProjectVersionId);
create index IX_90233583 on PatcherProjectVersion (rootPatcherProjectVersionId);

create unique index IX_A443337B on PatcherTicketHint (patcherProductVersionId);