create index IX_E00D30A2 on OSB_PatcherAccounts_PatcherBuilds (patcherAccountId);
create index IX_C4A11663 on OSB_PatcherAccounts_PatcherBuilds (patcherBuildId);

create unique index IX_4C479721 on OSB_PatcherBuild (key_[$COLUMN_LENGTH:75$], keyVersion);

create index IX_A0F7A382 on OSB_PatcherBuilds_PatcherFixes (patcherBuildId);
create index IX_AFCD2B69 on OSB_PatcherBuilds_PatcherFixes (patcherFixId);

create unique index IX_41801472 on OSB_PatcherFixPack (patcherFixComponentId, patcherProjectVersionId, name[$COLUMN_LENGTH:75$], version);

create index IX_576FFC1F on OSB_PatcherFixes_PatcherFixPacks (patcherFixId);
create index IX_86FC84D8 on OSB_PatcherFixes_PatcherFixPacks (patcherFixPackId);

create unique index IX_C2E03F3C on OSB_PatcherTicketHint (patcherProductVersionId);