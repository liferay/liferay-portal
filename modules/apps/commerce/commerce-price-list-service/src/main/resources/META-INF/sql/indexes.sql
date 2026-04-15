create unique index IX_8EF03EDA on CPLCommerceGroupAccountRel (commercePriceListId, commerceAccountGroupId, ctCollectionId);
create index IX_29EF081D on CPLCommerceGroupAccountRel (uuid_[$COLUMN_LENGTH:75$]);

create index IX_20ED9B62 on CommercePriceEntry (CPInstanceUuid[$COLUMN_LENGTH:75$], commercePriceListId, status);
create index IX_CCBB916A on CommercePriceEntry (CPInstanceUuid[$COLUMN_LENGTH:75$], quantity, unitOfMeasureKey[$COLUMN_LENGTH:75$]);
create index IX_CA7A2D0D on CommercePriceEntry (commercePriceListId);
create unique index IX_95608EBD on CommercePriceEntry (companyId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_B9AEC410 on CommercePriceEntry (status, displayDate);
create index IX_255AF6E1 on CommercePriceEntry (status, expirationDate);
create index IX_C15BC5AA on CommercePriceEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_6C8A40A3 on CommercePriceList (companyId, commerceCurrencyCode[$COLUMN_LENGTH:75$]);
create unique index IX_517D0585 on CommercePriceList (companyId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_3AE5B429 on CommercePriceList (groupId, catalogBasePriceList);
create index IX_3BE0F85F on CommercePriceList (groupId, companyId, status, type_[$COLUMN_LENGTH:75$]);
create index IX_31F12A8E on CommercePriceList (groupId, type_[$COLUMN_LENGTH:75$], catalogBasePriceList);
create unique index IX_22D6C1BA on CommercePriceList (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_863045BB on CommercePriceList (parentCommercePriceListId);
create index IX_72305848 on CommercePriceList (status, displayDate);
create index IX_1B0C9BE2 on CommercePriceList (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_5FBCA042 on CommercePriceListAccountRel (commercePriceListId, commerceAccountId, ctCollectionId);
create index IX_919FF916 on CommercePriceListAccountRel (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_6B436902 on CommercePriceListChannelRel (commercePriceListId, commerceChannelId, ctCollectionId);
create index IX_A7045AEC on CommercePriceListChannelRel (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_7D707AEE on CommercePriceListDiscountRel (commercePriceListId, commerceDiscountId, ctCollectionId);
create index IX_4F76A982 on CommercePriceListDiscountRel (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_4EA60BE2 on CommercePriceListOrderTypeRel (commercePriceListId, commerceOrderTypeId, ctCollectionId);
create index IX_C6ECAD11 on CommercePriceListOrderTypeRel (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_4072830C on CommerceTierPriceEntry (commercePriceEntryId, minQuantity, ctCollectionId);
create index IX_89DE1E88 on CommerceTierPriceEntry (commercePriceEntryId, status, minQuantity);
create unique index IX_FC1787BF on CommerceTierPriceEntry (companyId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_CB288BCE on CommerceTierPriceEntry (status, displayDate);
create index IX_D00E2E63 on CommerceTierPriceEntry (status, expirationDate);
create index IX_71F6D1E8 on CommerceTierPriceEntry (uuid_[$COLUMN_LENGTH:75$]);