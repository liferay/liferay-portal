create index IX_45C5C370 on CIAudit (companyId, sku[$COLUMN_LENGTH:75$], unitOfMeasureKey[$COLUMN_LENGTH:75$]);
create index IX_E7D143D9 on CIAudit (createDate);

create index IX_33BF9CB0 on CIBookedQuantity (expirationDate);
create index IX_EB8535EA on CIBookedQuantity (sku[$COLUMN_LENGTH:75$], companyId, unitOfMeasureKey[$COLUMN_LENGTH:75$]);

create index IX_967CACA8 on CIReplenishmentItem (commerceInventoryWarehouseId);
create unique index IX_3462AACC on CIReplenishmentItem (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_EBEBFEE2 on CIReplenishmentItem (sku[$COLUMN_LENGTH:75$], companyId, unitOfMeasureKey[$COLUMN_LENGTH:75$]);
create index IX_DA9C2C43 on CIReplenishmentItem (sku[$COLUMN_LENGTH:75$], unitOfMeasureKey[$COLUMN_LENGTH:75$], availabilityDate);
create index IX_B359B95D on CIReplenishmentItem (uuid_[$COLUMN_LENGTH:75$]);

create index IX_331A3FD3 on CIWarehouse (companyId, active_, countryTwoLettersISOCode[$COLUMN_LENGTH:75$]);
create index IX_DADA8974 on CIWarehouse (companyId, countryTwoLettersISOCode[$COLUMN_LENGTH:75$]);
create unique index IX_68E6B8D8 on CIWarehouse (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_3CCB62D1 on CIWarehouse (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_B4413476 on CIWarehouseItem (commerceInventoryWarehouseId, sku[$COLUMN_LENGTH:75$], unitOfMeasureKey[$COLUMN_LENGTH:75$]);
create unique index IX_8A09C40B on CIWarehouseItem (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_B86B6C8B on CIWarehouseItem (companyId, sku[$COLUMN_LENGTH:75$], unitOfMeasureKey[$COLUMN_LENGTH:75$]);
create index IX_4AD4537E on CIWarehouseItem (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_A743341B on CIWarehouseRel (CIWarehouseId, classNameId, classPK);