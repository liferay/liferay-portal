create unique index IX_17D56F1B on CPDAvailabilityEstimate (CProductId);
create index IX_E560850D on CPDAvailabilityEstimate (commerceAvailabilityEstimateId);
create index IX_109320B4 on CPDAvailabilityEstimate (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_2A31024F on CPDefinitionInventory (CPDefinitionId, ctCollectionId);
create unique index IX_E7C1FC36 on CPDefinitionInventory (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create unique index IX_4F4C712A on CSOptionAccountEntryRel (accountEntryId, commerceChannelId);
create index IX_B48AB5E on CSOptionAccountEntryRel (commerceChannelId);
create index IX_64B9CFFC on CSOptionAccountEntryRel (commerceShippingOptionKey[$COLUMN_LENGTH:75$]);

create unique index IX_9DD3ABD3 on CommerceAddressRestriction (classNameId, classPK, countryId);
create index IX_AE21488 on CommerceAddressRestriction (countryId);

create index IX_72527224 on CommerceAvailabilityEstimate (companyId);
create index IX_B83AF5B0 on CommerceAvailabilityEstimate (uuid_[$COLUMN_LENGTH:75$]);

create index IX_12131FC1 on CommerceOrder (billingAddressId);
create index IX_4650BB0A on CommerceOrder (commerceAccountId, orderStatus, createDate);
create unique index IX_30FF90A4 on CommerceOrder (externalReferenceCode[$COLUMN_LENGTH:75$], companyId);
create index IX_4F4CAEE4 on CommerceOrder (groupId, commerceAccountId, orderStatus);
create index IX_9C04F6F8 on CommerceOrder (groupId, commercePaymentMethodKey[$COLUMN_LENGTH:75$]);
create index IX_2F8AA139 on CommerceOrder (groupId, orderStatus, userId);
create unique index IX_25C927E3 on CommerceOrder (groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_7759000F on CommerceOrder (orderStatus, userId, createDate);
create index IX_4B11FAD8 on CommerceOrder (shippingAddressId);
create index IX_EFAA753 on CommerceOrder (userId);
create index IX_35A4137B on CommerceOrder (uuid_[$COLUMN_LENGTH:75$]);

create index IX_654BB574 on CommerceOrderItem (CIBookedQuantityId);
create index IX_2E1BB39D on CommerceOrderItem (CPInstanceId);
create index IX_F9E8D927 on CommerceOrderItem (CProductId);
create index IX_415AF3E3 on CommerceOrderItem (commerceOrderId, CPInstanceId);
create index IX_2E425475 on CommerceOrderItem (commerceOrderId, parentCommerceOrderItemId);
create index IX_15B37023 on CommerceOrderItem (commerceOrderId, subscription);
create index IX_F0E98FC7 on CommerceOrderItem (customerCommerceOrderItemId);
create unique index IX_FA3620D7 on CommerceOrderItem (externalReferenceCode[$COLUMN_LENGTH:75$], companyId);
create index IX_8E1472FB on CommerceOrderItem (parentCommerceOrderItemId);
create unique index IX_F0116282 on CommerceOrderItem (uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_CEB86C22 on CommerceOrderNote (commerceOrderId, restricted);
create unique index IX_D75F9236 on CommerceOrderNote (externalReferenceCode[$COLUMN_LENGTH:75$], companyId);
create unique index IX_76D9BDA1 on CommerceOrderNote (uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_CF274005 on CommerceOrderPayment (commerceOrderId);

create index IX_72C90BD4 on CommerceOrderType (companyId, active_);
create unique index IX_4EC1CAC8 on CommerceOrderType (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_B535907 on CommerceOrderType (status, displayDate);
create index IX_4EE2A8A on CommerceOrderType (status, expirationDate);
create index IX_FAD246E1 on CommerceOrderType (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_1110AF1B on CommerceOrderTypeRel (commerceOrderTypeId, classNameId, classPK);
create unique index IX_AD1B97D on CommerceOrderTypeRel (externalReferenceCode[$COLUMN_LENGTH:75$], companyId);
create index IX_D24B6642 on CommerceOrderTypeRel (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_DD20F446 on CommerceShipment (externalReferenceCode[$COLUMN_LENGTH:75$], companyId);
create index IX_616BDD15 on CommerceShipment (groupId, commerceAddressId);
create index IX_68FBA2B5 on CommerceShipment (groupId, status);
create unique index IX_88139005 on CommerceShipment (groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_67979D19 on CommerceShipment (uuid_[$COLUMN_LENGTH:75$]);

create index IX_3615B923 on CommerceShipmentItem (commerceOrderItemId);
create index IX_8B292722 on CommerceShipmentItem (commerceShipmentId, commerceInventoryWarehouseId, quantity);
create unique index IX_4FAC36D0 on CommerceShipmentItem (commerceShipmentId, commerceOrderItemId, commerceInventoryWarehouseId);
create unique index IX_29D8E379 on CommerceShipmentItem (externalReferenceCode[$COLUMN_LENGTH:75$], companyId);
create index IX_DB0BB83C on CommerceShipmentItem (groupId);
create unique index IX_D8C6E9A4 on CommerceShipmentItem (uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_42E5F6EF on CommerceShippingMethod (groupId, active_);
create unique index IX_C4557F93 on CommerceShippingMethod (groupId, engineKey[$COLUMN_LENGTH:75$]);

create unique index IX_D7D137B1 on CommerceSubscriptionEntry (commerceOrderItemId);
create index IX_43E6F382 on CommerceSubscriptionEntry (companyId, userId);
create index IX_B99DE058 on CommerceSubscriptionEntry (groupId, companyId, userId);
create index IX_B496E103 on CommerceSubscriptionEntry (subscriptionStatus);
create unique index IX_943E0A56 on CommerceSubscriptionEntry (uuid_[$COLUMN_LENGTH:75$], groupId);