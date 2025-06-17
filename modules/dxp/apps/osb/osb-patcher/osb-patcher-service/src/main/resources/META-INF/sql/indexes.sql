create index IX_D465C9DE on OSBPatcher_PAccounts_PBuilds (companyId);
create index IX_ED298BF2 on OSBPatcher_PAccounts_PBuilds (patcherBuildId);

create index IX_2B6CED5D on OSBPatcher_PBuilds_PFixes (companyId);
create index IX_9AFA27FA on OSBPatcher_PBuilds_PFixes (patcherFixId);

create index IX_CF1FC91 on OSBPatcher_PFixes_PFixPacks (companyId);
create index IX_F9C0797F on OSBPatcher_PFixes_PFixPacks (patcherFixPackId);

create unique index IX_D4018EE8 on OSBPatcher_PatcherBuild (key_[$COLUMN_LENGTH:75$], keyVersion);

create unique index IX_435911CB on OSBPatcher_PatcherFixPack (patcherFixComponentId, patcherProjectVersionId, name[$COLUMN_LENGTH:75$], version);

create unique index IX_5039C483 on OSBPatcher_PatcherTicketHint (patcherProductVersionId);