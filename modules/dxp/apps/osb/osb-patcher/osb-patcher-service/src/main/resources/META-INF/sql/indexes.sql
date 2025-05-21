create index IX_3A6CB4CC on PatcherAccounts_PatcherBuilds (companyId);
create index IX_3F0645C4 on PatcherAccounts_PatcherBuilds (patcherBuildId);

create unique index IX_9F3FB1E0 on PatcherBuild (key_[$COLUMN_LENGTH:75$], keyVersion);

create index IX_458258AF on PatcherBuilds_PatcherFixes (companyId);
create index IX_FD8E2368 on PatcherBuilds_PatcherFixes (patcherFixId);

create unique index IX_32277FD3 on PatcherFixPack (patcherFixComponentId, patcherProjectVersionId, name[$COLUMN_LENGTH:75$], version);

create index IX_D9CB9F79 on PatcherFixes_PatcherFixPacks (companyId);
create index IX_593D4197 on PatcherFixes_PatcherFixPacks (patcherFixPackId);

create unique index IX_A443337B on PatcherTicketHint (patcherProductVersionId);