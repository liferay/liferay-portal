create index IX_4B2F0707 on LayoutContentVersion (groupId, dataHash[$COLUMN_LENGTH:75$]);
create unique index IX_D4C862E2 on LayoutContentVersion (groupId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_813BD3E1 on LayoutContentVersion (groupId, status);
create index IX_A2BB1180 on LayoutContentVersion (plid, status);
create unique index IX_DC2DF6AE on LayoutContentVersion (plid, version);