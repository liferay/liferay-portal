create index IX_38280B2E on CChannelAccountEntryRel (accountEntryId);
create index IX_5F765A4F on CChannelAccountEntryRel (classNameId, classPK);
create unique index IX_7451E203 on CChannelAccountEntryRel (commerceChannelId, type_, accountEntryId, classNameId, classPK, ctCollectionId);
create index IX_E6E10CF1 on CChannelAccountEntryRel (commerceChannelId, type_, classNameId, classPK);
create index IX_52FD56CF on CChannelAccountEntryRel (type_, accountEntryId);

create index IX_4E725857 on CPAttachmentFileEntry (classNameId, classPK, cdnURL[$COLUMN_LENGTH:4000$]);
create index IX_DD114140 on CPAttachmentFileEntry (classNameId, classPK, fileEntryId);
create index IX_F34F24D9 on CPAttachmentFileEntry (classNameId, classPK, status, displayDate);
create index IX_5F3A96F1 on CPAttachmentFileEntry (classNameId, classPK, status, type_, galleryEnabled);
create index IX_6A165A0B on CPAttachmentFileEntry (classNameId, fileEntryId, groupId);
create unique index IX_25D041B5 on CPAttachmentFileEntry (ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$], companyId);
create index IX_5B2A1075 on CPAttachmentFileEntry (fileEntryId);
create index IX_E153EF0E on CPAttachmentFileEntry (status, displayDate);
create unique index IX_50416EE0 on CPAttachmentFileEntry (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_FFC978A3 on CPConfigurationEntry (CPConfigurationListId);
create index IX_92EB0304 on CPConfigurationEntry (classNameId, classPK);
create index IX_69AB0AD9 on CPConfigurationEntry (companyId);
create unique index IX_8067B450 on CPConfigurationEntry (ctCollectionId, classNameId, classPK, CPConfigurationListId);
create unique index IX_B5AF3F22 on CPConfigurationEntry (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_DE212C7 on CPConfigurationEntry (uuid_[$COLUMN_LENGTH:75$], ctCollectionId, groupId);

create index IX_C07283B0 on CPConfigurationEntrySetting (CPConfigurationEntryId, type_);
create index IX_576B525B on CPConfigurationEntrySetting (companyId);
create unique index IX_1A3FBF81 on CPConfigurationEntrySetting (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create unique index IX_9AAA5A84 on CPConfigurationList (companyId, ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_AC55D871 on CPConfigurationList (groupId, companyId, status);
create index IX_36C0FFD3 on CPConfigurationList (groupId, master);
create unique index IX_E989EBF5 on CPConfigurationList (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_85ED285B on CPConfigurationList (parentCPConfigurationListId);
create index IX_DD7144ED on CPConfigurationList (status, displayDate);
create index IX_20625D47 on CPConfigurationList (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_5B3D330D on CPConfigurationListRel (CPConfigurationListId, classNameId, classPK, ctCollectionId);

create index IX_95975FB4 on CPDSpecificationOptionValue (CPDefinitionId, CPOptionCategoryId);
create index IX_173E8E91 on CPDSpecificationOptionValue (CPDefinitionId, CPSpecificationOptionId);
create unique index IX_CFB2B6D7 on CPDSpecificationOptionValue (CPDefinitionId, ctCollectionId, key_[$COLUMN_LENGTH:75$]);
create index IX_4F4EDBA5 on CPDSpecificationOptionValue (CPOptionCategoryId);
create index IX_573BE140 on CPDSpecificationOptionValue (CPSpecificationOptionId);
create unique index IX_CE76817F on CPDSpecificationOptionValue (ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$], companyId);
create index IX_8DA57014 on CPDSpecificationOptionValue (groupId);
create unique index IX_1E581E2E on CPDSpecificationOptionValue (uuid_[$COLUMN_LENGTH:75$], ctCollectionId, groupId);

create index IX_3D5A0021 on CPDefinition (CPTaxCategoryId);
create index IX_1F4B9C67 on CPDefinition (CProductId, status);
create index IX_F1AEC8A7 on CPDefinition (CProductId, version);
create index IX_217AF702 on CPDefinition (companyId);
create index IX_419350EA on CPDefinition (groupId, status);
create index IX_99C4ED10 on CPDefinition (groupId, subscriptionEnabled);
create unique index IX_96393D8E on CPDefinition (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_E504F8F4 on CPDefinition (status, displayDate);
create index IX_46B4998E on CPDefinition (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_3A90EAC9 on CPDefinitionLink (CPDefinitionId, CProductId, type_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_5572A666 on CPDefinitionLink (CPDefinitionId, type_[$COLUMN_LENGTH:75$]);
create index IX_F7B5F85A on CPDefinitionLink (CProductId, type_[$COLUMN_LENGTH:75$]);
create index IX_FE4C04C0 on CPDefinitionLink (status, CPDefinitionId, type_[$COLUMN_LENGTH:75$]);
create index IX_786093B4 on CPDefinitionLink (status, CProductId, type_[$COLUMN_LENGTH:75$]);
create index IX_62BEA79A on CPDefinitionLink (status, displayDate);
create index IX_155AEF17 on CPDefinitionLink (status, expirationDate);
create unique index IX_112757D8 on CPDefinitionLink (uuid_[$COLUMN_LENGTH:75$], ctCollectionId, groupId);

create unique index IX_CB617913 on CPDefinitionLocalization (CPDefinitionId, languageId[$COLUMN_LENGTH:75$], ctCollectionId);

create unique index IX_64314388 on CPDefinitionOptionRel (CPDefinitionId, ctCollectionId, CPOptionId);
create unique index IX_78CCF36B on CPDefinitionOptionRel (CPDefinitionId, ctCollectionId, key_[$COLUMN_LENGTH:75$]);
create index IX_BDB8420C on CPDefinitionOptionRel (CPDefinitionId, required);
create index IX_749E99EB on CPDefinitionOptionRel (CPDefinitionId, skuContributor);
create index IX_4E86C11B on CPDefinitionOptionRel (CPOptionId);
create index IX_449BFCFE on CPDefinitionOptionRel (companyId);
create index IX_A65BAB00 on CPDefinitionOptionRel (groupId);
create unique index IX_44B059C2 on CPDefinitionOptionRel (uuid_[$COLUMN_LENGTH:75$], ctCollectionId, groupId);

create unique index IX_55A05F1E on CPDefinitionOptionValueRel (CPDefinitionOptionRelId, key_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_4A77D282 on CPDefinitionOptionValueRel (CPDefinitionOptionRelId, preselected);
create index IX_3EB86274 on CPDefinitionOptionValueRel (CPInstanceUuid[$COLUMN_LENGTH:75$]);
create index IX_44C2E505 on CPDefinitionOptionValueRel (companyId);
create index IX_695AE8C7 on CPDefinitionOptionValueRel (groupId);
create index IX_2434CAD7 on CPDefinitionOptionValueRel (key_[$COLUMN_LENGTH:75$]);
create unique index IX_52855B17 on CPDefinitionOptionValueRel (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_290BF7BA on CPDisplayLayout (classNameId, classPK);
create unique index IX_7F728A18 on CPDisplayLayout (groupId, classNameId, classPK, ctCollectionId);
create index IX_965CA8C5 on CPDisplayLayout (groupId, layoutPageTemplateEntryUuid[$COLUMN_LENGTH:75$]);
create index IX_381B82DE on CPDisplayLayout (groupId, layoutUuid[$COLUMN_LENGTH:75$]);
create unique index IX_7649CF4D on CPDisplayLayout (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_FEC526EF on CPDisplayLayout (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_95564486 on CPInstance (CPDefinitionId, ctCollectionId, CPInstanceUuid[$COLUMN_LENGTH:75$]);
create unique index IX_E06787D8 on CPInstance (CPDefinitionId, ctCollectionId, sku[$COLUMN_LENGTH:75$]);
create index IX_4389A03 on CPInstance (CPDefinitionId, status, displayDate);
create index IX_34763899 on CPInstance (CPInstanceUuid[$COLUMN_LENGTH:75$]);
create index IX_9FB1144D on CPInstance (companyId, sku[$COLUMN_LENGTH:75$]);
create unique index IX_EB17985B on CPInstance (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_A495773C on CPInstance (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);
create index IX_C1F8242 on CPInstance (groupId);
create index IX_BD04B832 on CPInstance (status, displayDate);
create index IX_75478E1C on CPInstance (status, groupId);
create index IX_1140BD8 on CPInstance (status, replacementCPInstanceUuid[$COLUMN_LENGTH:75$], replacementCProductId);
create index IX_4654BD4C on CPInstance (uuid_[$COLUMN_LENGTH:75$]);

create index IX_E551D3AA on CPInstanceOptionValueRel (CPDefinitionOptionRelId);
create unique index IX_C7B0D143 on CPInstanceOptionValueRel (CPInstanceId, CPDefinitionOptionRelId, CPDefinitionOptionValueRelId, ctCollectionId);
create index IX_D3B702C2 on CPInstanceOptionValueRel (CPInstanceId, CPDefinitionOptionValueRelId);
create unique index IX_4399CE9D on CPInstanceOptionValueRel (uuid_[$COLUMN_LENGTH:75$], ctCollectionId, groupId);

create index IX_4351C7A1 on CPInstanceUOM (CPInstanceId, active_);
create unique index IX_C6BA8E9A on CPInstanceUOM (CPInstanceId, key_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_611154B9 on CPInstanceUOM (CPInstanceId, primary_);
create index IX_2EED7F68 on CPInstanceUOM (companyId, key_[$COLUMN_LENGTH:75$], sku[$COLUMN_LENGTH:75$]);
create index IX_9DCFEBFC on CPInstanceUOM (companyId, sku[$COLUMN_LENGTH:75$]);
create index IX_ABE6B4BD on CPInstanceUOM (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_D52621F0 on CPMeasurementUnit (companyId, ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_51CDE4C1 on CPMeasurementUnit (companyId, ctCollectionId, key_[$COLUMN_LENGTH:75$]);
create index IX_F0C14577 on CPMeasurementUnit (companyId, type_, primary_);
create unique index IX_E3424311 on CPMeasurementUnit (uuid_[$COLUMN_LENGTH:75$], ctCollectionId, groupId);

create unique index IX_4E312C7F on CPOption (companyId, ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_143B0E52 on CPOption (companyId, ctCollectionId, key_[$COLUMN_LENGTH:75$]);
create index IX_A64FCE2C on CPOption (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_685B389D on CPOptionCategory (companyId, ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_E4988A74 on CPOptionCategory (companyId, ctCollectionId, key_[$COLUMN_LENGTH:75$]);
create index IX_ABB730CE on CPOptionCategory (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_DA77C838 on CPOptionValue (CPOptionId, ctCollectionId, key_[$COLUMN_LENGTH:75$]);
create unique index IX_DC509C0C on CPOptionValue (companyId, ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_D7C1A0BF on CPOptionValue (uuid_[$COLUMN_LENGTH:75$]);

create index IX_8A69C0A5 on CPSOListTypeDefinitionRel (CPSpecificationOptionId, listTypeDefinitionId);
create index IX_BB2AB5C5 on CPSOListTypeDefinitionRel (listTypeDefinitionId);

create index IX_421ED80 on CPSpecificationOption (CPOptionCategoryId);
create unique index IX_7CEAF068 on CPSpecificationOption (companyId, ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_669F7749 on CPSpecificationOption (companyId, ctCollectionId, key_[$COLUMN_LENGTH:75$]);
create index IX_972DFDE3 on CPSpecificationOption (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_79A007D5 on CPTaxCategory (companyId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_705EAB92 on CPTaxCategory (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_CB3A891B on CProduct (ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$], companyId);
create index IX_77F5B8F8 on CProduct (groupId);
create unique index IX_F70CE3C6 on CProduct (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_7A691498 on CommerceCatalog (accountEntryId);
create unique index IX_A8DE8457 on CommerceCatalog (companyId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_65864AFC on CommerceCatalog (companyId, system_);
create index IX_37D36450 on CommerceCatalog (uuid_[$COLUMN_LENGTH:75$]);

create index IX_C2C38B02 on CommerceChannel (accountEntryId);
create unique index IX_D8DAE041 on CommerceChannel (companyId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_E1ECD95 on CommerceChannel (siteGroupId);
create index IX_9E82EA6 on CommerceChannel (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_4469A625 on CommerceChannelRel (classNameId, classPK, commerceChannelId, ctCollectionId);
create index IX_48F8F6FC on CommerceChannelRel (commerceChannelId);