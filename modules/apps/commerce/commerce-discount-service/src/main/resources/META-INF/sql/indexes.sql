create index IX_F7FFBCCA on CDiscountCAccountGroupRel (commerceAccountGroupId);
create unique index IX_9D768AF5 on CDiscountCAccountGroupRel (commerceDiscountId, commerceAccountGroupId);

create index IX_A7A710FC on CommerceDiscount (companyId, couponCode[$COLUMN_LENGTH:75$], active_);
create unique index IX_D294CDB7 on CommerceDiscount (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_1CCF5211 on CommerceDiscount (companyId, status, active_, levelType[$COLUMN_LENGTH:75$]);
create index IX_52CB3DB8 on CommerceDiscount (status, displayDate);
create index IX_DE0C3C39 on CommerceDiscount (status, expirationDate);
create index IX_F1A4C552 on CommerceDiscount (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_E082887A on CommerceDiscountAccountRel (commerceAccountId, commerceDiscountId);
create index IX_6EA2AA99 on CommerceDiscountAccountRel (commerceDiscountId);
create index IX_CEE71686 on CommerceDiscountAccountRel (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_614617A on CommerceDiscountOrderTypeRel (commerceDiscountId, commerceOrderTypeId);
create index IX_707E0345 on CommerceDiscountOrderTypeRel (commerceOrderTypeId);
create index IX_CEE22E81 on CommerceDiscountOrderTypeRel (uuid_[$COLUMN_LENGTH:75$]);

create index IX_6B4EEC38 on CommerceDiscountRel (classNameId, classPK);
create index IX_2BF40CA3 on CommerceDiscountRel (commerceDiscountId, classNameId, classPK);

create index IX_CB9E6769 on CommerceDiscountRule (commerceDiscountId);

create index IX_28CE20FF on CommerceDiscountUsageEntry (commerceDiscountId, commerceAccountId, commerceOrderId);
create index IX_E40C6220 on CommerceDiscountUsageEntry (commerceDiscountId, commerceOrderId);