create index IX_31653559 on CPricingClassCPDefinitionRel (CPDefinitionId);
create unique index IX_DA09B6F3 on CPricingClassCPDefinitionRel (commercePricingClassId, CPDefinitionId, ctCollectionId);

create index IX_176CA5EC on CommercePriceModifier (commercePriceListId);
create unique index IX_8C8A963E on CommercePriceModifier (companyId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_AEE7A167 on CommercePriceModifier (companyId, status, groupId);
create index IX_FCACD082 on CommercePriceModifier (companyId, target[$COLUMN_LENGTH:75$]);
create index IX_6A13CEF on CommercePriceModifier (status, displayDate);
create index IX_921ADDA2 on CommercePriceModifier (status, expirationDate);
create unique index IX_FAB45A5F on CommercePriceModifier (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_391477EF on CommercePriceModifierRel (classNameId, classPK);
create unique index IX_510AD1A9 on CommercePriceModifierRel (commercePriceModifierId, classNameId, classPK, ctCollectionId);

create unique index IX_925EA26 on CommercePricingClass (companyId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_33040DE1 on CommercePricingClass (uuid_[$COLUMN_LENGTH:75$]);