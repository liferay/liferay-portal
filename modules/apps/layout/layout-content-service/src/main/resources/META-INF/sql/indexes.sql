create unique index IX_410FA59C on LayoutContentVersion (externalReferenceCode[$COLUMN_LENGTH:75$], groupId);
create index IX_A2BB1180 on LayoutContentVersion (plid, status);
create unique index IX_DC2DF6AE on LayoutContentVersion (plid, version);

create unique index IX_CC10F2C6 on LayoutContentVersionPreview (layoutContentVersionId, segmentsExperienceERC[$COLUMN_LENGTH:75$], languageId[$COLUMN_LENGTH:75$]);