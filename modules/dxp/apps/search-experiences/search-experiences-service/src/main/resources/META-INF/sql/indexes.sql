create unique index IX_7A27FEA3 on SXPBlueprint (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_D45697E6 on SXPBlueprint (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_D4D87BCC on SXPElement (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_62CF31E7 on SXPElement (companyId, readOnly);
create index IX_6EA5F664 on SXPElement (companyId, type_);
create index IX_47DA885D on SXPElement (uuid_[$COLUMN_LENGTH:75$]);