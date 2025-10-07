create index IX_D465C9DE on OSBPatcher_PAccounts_PBuilds (companyId);
create index IX_ED298BF2 on OSBPatcher_PAccounts_PBuilds (patcherBuildId);

create index IX_2B6CED5D on OSBPatcher_PBuilds_PFixes (companyId);
create index IX_9AFA27FA on OSBPatcher_PBuilds_PFixes (patcherFixId);

create index IX_CF1FC91 on OSBPatcher_PFixes_PFixPacks (companyId);
create index IX_F9C0797F on OSBPatcher_PFixes_PFixPacks (patcherFixPackId);

create index IX_D2A17063 on OSBPatcher_PProductVersion (fixDeliveryMethod);
create unique index IX_5486A556 on OSBPatcher_PProductVersion (name[$COLUMN_LENGTH:75$]);

create unique index IX_D818D7E2 on OSBPatcher_PProjectVersion (committish[$COLUMN_LENGTH:150$]);
create unique index IX_DD66CDEC on OSBPatcher_PProjectVersion (name[$COLUMN_LENGTH:150$]);
create index IX_7F6073C5 on OSBPatcher_PProjectVersion (patcherProductVersionId, repositoryName[$COLUMN_LENGTH:75$]);
create index IX_B470DAA1 on OSBPatcher_PProjectVersion (patcherProductVersionId, rootPatcherProjectVersionId);
create index IX_64D9A2F0 on OSBPatcher_PProjectVersion (rootPatcherProjectVersionId);

create unique index IX_AFEBA92C on OSBPatcher_PatcherAccount (accountEntryCode[$COLUMN_LENGTH:75$]);
create index IX_FB5EC1C8 on OSBPatcher_PatcherAccount (companyId, accountEntryCode[$COLUMN_LENGTH:75$]);

create unique index IX_D4018EE8 on OSBPatcher_PatcherBuild (key_[$COLUMN_LENGTH:75$], keyVersion);
create index IX_686637A5 on OSBPatcher_PatcherBuild (key_[$COLUMN_LENGTH:75$], latestKeyBuild);
create index IX_93976164 on OSBPatcher_PatcherBuild (notified, status, modifiedDate);
create index IX_F48639DC on OSBPatcher_PatcherBuild (patcherFixId, childBuild);
create index IX_8BFE931C on OSBPatcher_PatcherBuild (patcherFixId, patcherProductVersionId, childBuild, type_);
create index IX_563C9F09 on OSBPatcher_PatcherBuild (patcherProductVersionId, patcherAccountId);
create index IX_ED8751A7 on OSBPatcher_PatcherBuild (patcherProjectVersionId, latestKeyBuild, accountEntryCode[$COLUMN_LENGTH:75$], name[$COLUMN_LENGTH:4000$]);
create index IX_53CC8072 on OSBPatcher_PatcherBuild (supportTicket[$COLUMN_LENGTH:75$], latestSupportTicketBuild);
create index IX_D4A6FF5 on OSBPatcher_PatcherBuild (supportTicket[$COLUMN_LENGTH:75$], supportTicketVersion);

create index IX_604A87A2 on OSBPatcher_PatcherBuildRel (childPatcherBuildId);
create index IX_7C00F1B0 on OSBPatcher_PatcherBuildRel (parentPatcherBuildId);

create index IX_A30F6B71 on OSBPatcher_PatcherFix (patcherProjectVersionId);
create index IX_B967D208 on OSBPatcher_PatcherFix (type_, key_[$COLUMN_LENGTH:75$], keyVersion);
create index IX_F9B1552B on OSBPatcher_PatcherFix (type_, latestFix, key_[$COLUMN_LENGTH:75$]);
create index IX_78667865 on OSBPatcher_PatcherFix (type_, patcherProjectVersionId, latestFix, name[$COLUMN_LENGTH:4000$]);
create index IX_D9A2D60C on OSBPatcher_PatcherFix (type_, patcherProjectVersionId, latestFix, status);
create index IX_DB743104 on OSBPatcher_PatcherFix (type_, status, notified, modifiedDate);

create unique index IX_2066DFDA on OSBPatcher_PatcherFixComponent (name[$COLUMN_LENGTH:75$]);

create unique index IX_286AF3AF on OSBPatcher_PatcherFixPack (patcherBuildId);
create index IX_1C77BC2A on OSBPatcher_PatcherFixPack (patcherFixComponentId, patcherProjectVersionId, version);
create index IX_DC2ECE27 on OSBPatcher_PatcherFixPack (patcherFixComponentId, version);
create unique index IX_4B7FB57 on OSBPatcher_PatcherFixPack (patcherProjectVersionId, name[$COLUMN_LENGTH:75$]);
create index IX_979F647E on OSBPatcher_PatcherFixPack (patcherProjectVersionId, status);
create index IX_4EFD5421 on OSBPatcher_PatcherFixPack (version);

create index IX_4AD488F0 on OSBPatcher_PatcherFixRel (childPatcherFixId);
create index IX_1EC46890 on OSBPatcher_PatcherFixRel (parentPatcherFixId);

create unique index IX_5039C483 on OSBPatcher_PatcherTicketHint (patcherProductVersionId);