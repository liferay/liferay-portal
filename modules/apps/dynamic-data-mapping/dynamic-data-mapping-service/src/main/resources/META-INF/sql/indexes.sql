create index IX_E3BAF436 on DDMContent (companyId);
create index IX_50BF1038 on DDMContent (groupId);
create unique index IX_D4156486 on DDMContent (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_DB54A6E5 on DDMDataProviderInstance (companyId);
create index IX_1333A2A7 on DDMDataProviderInstance (groupId);
create unique index IX_B7498537 on DDMDataProviderInstance (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create unique index IX_EC5795A0 on DDMDataProviderInstanceLink (dataProviderInstanceId, structureId, ctCollectionId);
create index IX_CB823541 on DDMDataProviderInstanceLink (structureId);

create index IX_DEA6624F on DDMField (companyId, fieldType[$COLUMN_LENGTH:255$]);
create index IX_10FC3BA2 on DDMField (storageId, fieldName[$COLUMN_LENGTH:500$]);
create unique index IX_1BB20E75 on DDMField (storageId, instanceId[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_DE90A287 on DDMField (structureVersionId);

create unique index IX_83964B3A on DDMFieldAttribute (attributeName[$COLUMN_LENGTH:255$], languageId[$COLUMN_LENGTH:75$], fieldId, ctCollectionId);
create index IX_167E6FEA on DDMFieldAttribute (attributeName[$COLUMN_LENGTH:255$], smallAttributeValue[$COLUMN_LENGTH:255$]);
create index IX_D3B57A06 on DDMFieldAttribute (storageId, attributeName[$COLUMN_LENGTH:255$]);
create index IX_FECE9ED8 on DDMFieldAttribute (storageId, languageId[$COLUMN_LENGTH:75$]);

create index IX_9E1C31FE on DDMFormInstance (groupId);
create unique index IX_C0288850 on DDMFormInstance (structureId, ctCollectionId);
create unique index IX_EAB7A400 on DDMFormInstance (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_5BC982B on DDMFormInstanceRecord (companyId);
create index IX_242301EA on DDMFormInstanceRecord (formInstanceId, formInstanceVersion[$COLUMN_LENGTH:75$]);
create index IX_3C8DBDFF on DDMFormInstanceRecord (formInstanceId, userId);
create unique index IX_90833BB1 on DDMFormInstanceRecord (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_EAAF6D80 on DDMFormInstanceRecordVersion (formInstanceId, formInstanceVersion[$COLUMN_LENGTH:75$]);
create index IX_F0C9356C on DDMFormInstanceRecordVersion (formInstanceId, userId, formInstanceVersion[$COLUMN_LENGTH:75$], status);
create index IX_B5A3FAC6 on DDMFormInstanceRecordVersion (formInstanceRecordId, status);
create unique index IX_272BBC86 on DDMFormInstanceRecordVersion (formInstanceRecordId, version[$COLUMN_LENGTH:75$], ctCollectionId);

create index IX_953190E8 on DDMFormInstanceReport (formInstanceId);

create index IX_EB92EF26 on DDMFormInstanceVersion (formInstanceId, status);
create unique index IX_8D381426 on DDMFormInstanceVersion (formInstanceId, version[$COLUMN_LENGTH:75$], ctCollectionId);

create unique index IX_6979A733 on DDMStorageLink (classPK, ctCollectionId);
create index IX_81776090 on DDMStorageLink (structureId);
create index IX_14DADA22 on DDMStorageLink (structureVersionId);
create index IX_32A18526 on DDMStorageLink (uuid_[$COLUMN_LENGTH:75$]);

create index IX_FC299886 on DDMStructure (classNameId, companyId);
create unique index IX_43C6D14B on DDMStructure (groupId, classNameId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_4CFAC78E on DDMStructure (groupId, classNameId, structureKey[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_43395316 on DDMStructure (groupId, parentStructureId);
create unique index IX_7BD0A294 on DDMStructure (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_657899A8 on DDMStructure (parentStructureId);
create index IX_20FDE04C on DDMStructure (structureKey[$COLUMN_LENGTH:75$]);
create index IX_E61809C8 on DDMStructure (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_BBA9AF0E on DDMStructureLayout (groupId, classNameId, structureLayoutKey[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_C72DCE6E on DDMStructureLayout (groupId, classNameId, structureVersionId);
create unique index IX_1D9B22DE on DDMStructureLayout (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_4CDF64C on DDMStructureLayout (structureLayoutKey[$COLUMN_LENGTH:75$]);
create index IX_B7158C0A on DDMStructureLayout (structureVersionId);
create index IX_CC63DA3E on DDMStructureLayout (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_C8DE7401 on DDMStructureLink (classNameId, classPK, structureId, ctCollectionId);
create index IX_17692B58 on DDMStructureLink (structureId);

create index IX_17B3C96C on DDMStructureVersion (structureId, status);
create unique index IX_1F8A4EA0 on DDMStructureVersion (structureId, version[$COLUMN_LENGTH:75$], ctCollectionId);

create index IX_B6356F93 on DDMTemplate (classNameId, classPK, type_[$COLUMN_LENGTH:75$]);
create index IX_32F83D16 on DDMTemplate (classPK);
create index IX_F0C3449 on DDMTemplate (groupId, classNameId, classPK, type_[$COLUMN_LENGTH:75$], mode_[$COLUMN_LENGTH:75$]);
create unique index IX_ED2AF9E2 on DDMTemplate (groupId, classNameId, templateKey[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_B1C33EA6 on DDMTemplate (groupId, classPK);
create unique index IX_35AC1D1E on DDMTemplate (groupId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_BE57F195 on DDMTemplate (groupId, uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_33BEF579 on DDMTemplate (language[$COLUMN_LENGTH:75$]);
create index IX_127A35B0 on DDMTemplate (smallImageId);
create index IX_CAE41A28 on DDMTemplate (templateKey[$COLUMN_LENGTH:75$]);
create index IX_C4F283C8 on DDMTemplate (type_[$COLUMN_LENGTH:75$]);
create index IX_F2A243A7 on DDMTemplate (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_79ED5CFA on DDMTemplateLink (classNameId, classPK, ctCollectionId);
create index IX_85278170 on DDMTemplateLink (templateId);

create index IX_66382FC6 on DDMTemplateVersion (templateId, status);
create unique index IX_64E82786 on DDMTemplateVersion (templateId, version[$COLUMN_LENGTH:75$], ctCollectionId);