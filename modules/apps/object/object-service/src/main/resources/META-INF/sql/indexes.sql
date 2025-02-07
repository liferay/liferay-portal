create index IX_D89CE7B9 on ObjectAction (active_, objectActionExecutorKey[$COLUMN_LENGTH:255$]);
create index IX_E3B248CA on ObjectAction (active_, objectActionTriggerKey[$COLUMN_LENGTH:75$], companyId);
create index IX_2B979E5C on ObjectAction (objectDefinitionId, active_, objectActionTriggerKey[$COLUMN_LENGTH:75$], name[$COLUMN_LENGTH:75$]);
create unique index IX_7CB6AA71 on ObjectAction (objectDefinitionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_E817201B on ObjectAction (objectDefinitionId, name[$COLUMN_LENGTH:75$]);
create index IX_570E3859 on ObjectAction (uuid_[$COLUMN_LENGTH:75$]);

create index IX_2B2CA94C on ObjectDefinition (accountEntryRestricted);
create index IX_2A008543 on ObjectDefinition (companyId, className[$COLUMN_LENGTH:255$]);
create unique index IX_F861636D on ObjectDefinition (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_3E56F38F on ObjectDefinition (companyId, name[$COLUMN_LENGTH:75$]);
create index IX_66A8EEB3 on ObjectDefinition (companyId, rootObjectDefinitionId);
create index IX_7D686D13 on ObjectDefinition (companyId, status, active_);
create index IX_12BECBE8 on ObjectDefinition (companyId, system_, modifiable);
create index IX_F8B95773 on ObjectDefinition (companyId, system_, status, active_);
create index IX_86E0480A on ObjectDefinition (companyId, userId);
create index IX_8D232754 on ObjectDefinition (objectFolderId);
create index IX_55C39BCE on ObjectDefinition (system_, status);
create index IX_7B61F95C on ObjectDefinition (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_BB97A04 on ObjectDefinitionSetting (objectDefinitionId, name[$COLUMN_LENGTH:75$]);
create index IX_89F99D10 on ObjectDefinitionSetting (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_E60FE3FC on ObjectEntry (groupId, externalReferenceCode[$COLUMN_LENGTH:1000$], companyId);
create unique index IX_28B2B723 on ObjectEntry (groupId, uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_5979B105 on ObjectEntry (objectDefinitionId, externalReferenceCode[$COLUMN_LENGTH:1000$], companyId);
create index IX_622DB416 on ObjectEntry (objectDefinitionId, groupId, status);
create index IX_A388E5A0 on ObjectEntry (objectDefinitionId, status);
create index IX_68B7FB2 on ObjectEntry (objectDefinitionId, userId, createDate);
create index IX_BD205C3B on ObjectEntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_9D7AE9B8 on ObjectEntryFolder (groupId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_F55286DC on ObjectEntryFolder (groupId, companyId, parentObjectEntryFolderId, name[$COLUMN_LENGTH:75$]);
create index IX_772D12BC on ObjectEntryFolder (groupId, companyId, treePath[$COLUMN_LENGTH:4000$]);
create unique index IX_8EC73DF1 on ObjectEntryFolder (groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_56A855AD on ObjectEntryFolder (uuid_[$COLUMN_LENGTH:75$]);

create index IX_EAECE0E1 on ObjectField (companyId, userId);
create index IX_6DCE835D on ObjectField (listTypeDefinitionId, state_);
create index IX_87111650 on ObjectField (objectDefinitionId, businessType[$COLUMN_LENGTH:75$]);
create unique index IX_B0716ED7 on ObjectField (objectDefinitionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_5DDCF209 on ObjectField (objectDefinitionId, dbTableName[$COLUMN_LENGTH:75$]);
create index IX_52AAA62B on ObjectField (objectDefinitionId, indexed, dbType[$COLUMN_LENGTH:75$]);
create index IX_2D0537E9 on ObjectField (objectDefinitionId, localized);
create index IX_A59C5981 on ObjectField (objectDefinitionId, name[$COLUMN_LENGTH:75$]);
create index IX_4A69C63E on ObjectField (objectDefinitionId, system_);
create index IX_FBA3DCB3 on ObjectField (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_BB322D4A on ObjectFieldSetting (objectFieldId, name[$COLUMN_LENGTH:75$]);
create index IX_66E899D9 on ObjectFieldSetting (uuid_[$COLUMN_LENGTH:75$]);

create index IX_B3C95F49 on ObjectFilter (objectFieldId);
create index IX_444AB557 on ObjectFilter (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_677F9088 on ObjectFolder (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_8FBAE114 on ObjectFolder (companyId, name[$COLUMN_LENGTH:75$]);
create index IX_14631921 on ObjectFolder (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_61EBCE03 on ObjectFolderItem (objectDefinitionId, objectFolderId);
create index IX_F9E61F22 on ObjectFolderItem (objectFolderId);
create index IX_880861CE on ObjectFolderItem (uuid_[$COLUMN_LENGTH:75$]);

create index IX_CE888CFD on ObjectLayout (defaultObjectLayout, companyId);
create index IX_FD0CCE8A on ObjectLayout (objectDefinitionId, defaultObjectLayout);
create index IX_7D8E0DE5 on ObjectLayout (uuid_[$COLUMN_LENGTH:75$]);

create index IX_5F97F7CF on ObjectLayoutBox (objectLayoutTabId);
create index IX_356E03CC on ObjectLayoutBox (uuid_[$COLUMN_LENGTH:75$]);

create index IX_E992BFE1 on ObjectLayoutColumn (objectFieldId);
create index IX_46CE5537 on ObjectLayoutColumn (objectLayoutRowId);
create index IX_EC6A2DEF on ObjectLayoutColumn (uuid_[$COLUMN_LENGTH:75$]);

create index IX_FA14DE56 on ObjectLayoutRow (objectLayoutBoxId);
create index IX_BC3EE89D on ObjectLayoutRow (uuid_[$COLUMN_LENGTH:75$]);

create index IX_F01F1EEA on ObjectLayoutTab (objectLayoutId);
create index IX_4CC508B8 on ObjectLayoutTab (objectRelationshipId);
create index IX_9D1A2542 on ObjectLayoutTab (uuid_[$COLUMN_LENGTH:75$]);

create index IX_44505405 on ObjectRelationship (companyId, userId);
create index IX_9FD90360 on ObjectRelationship (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_97E37468 on ObjectRelationship (objectDefinitionId1, edge);
create index IX_A71785B6 on ObjectRelationship (objectDefinitionId1, name[$COLUMN_LENGTH:75$]);
create index IX_C44DA840 on ObjectRelationship (objectDefinitionId1, objectDefinitionId2, reverse, type_[$COLUMN_LENGTH:75$], name[$COLUMN_LENGTH:75$]);
create index IX_FE6B0156 on ObjectRelationship (objectDefinitionId1, objectDefinitionId2, type_[$COLUMN_LENGTH:75$], name[$COLUMN_LENGTH:75$]);
create index IX_6FD91117 on ObjectRelationship (objectDefinitionId1, reverse, deletionType[$COLUMN_LENGTH:75$]);
create index IX_EA05FD3A on ObjectRelationship (objectDefinitionId1, reverse, type_[$COLUMN_LENGTH:75$]);
create index IX_2C27E369 on ObjectRelationship (objectDefinitionId2, edge);
create index IX_B7B05EFB on ObjectRelationship (objectDefinitionId2, reverse, type_[$COLUMN_LENGTH:75$]);
create index IX_F1DC092D on ObjectRelationship (objectFieldId2);
create index IX_820C98BE on ObjectRelationship (parameterObjectFieldId);
create index IX_8B817F36 on ObjectRelationship (reverse, dbTableName[$COLUMN_LENGTH:75$]);
create index IX_E95FE5D7 on ObjectRelationship (uuid_[$COLUMN_LENGTH:75$]);

create index IX_C34F0F9E on ObjectState (listTypeEntryId, objectStateFlowId);
create index IX_F9D4BA53 on ObjectState (objectStateFlowId);
create index IX_3030D2FC on ObjectState (uuid_[$COLUMN_LENGTH:75$]);

create index IX_AE828160 on ObjectStateFlow (objectFieldId);
create index IX_8316DE6E on ObjectStateFlow (uuid_[$COLUMN_LENGTH:75$]);

create index IX_DB56B27E on ObjectStateTransition (objectStateFlowId);
create index IX_9C3FAB55 on ObjectStateTransition (sourceObjectStateId);
create index IX_FB9AC71F on ObjectStateTransition (targetObjectStateId);
create index IX_5E1D73A7 on ObjectStateTransition (uuid_[$COLUMN_LENGTH:75$]);

create index IX_23EC0B65 on ObjectValidationRule (active_, engine[$COLUMN_LENGTH:255$]);
create index IX_C476B36E on ObjectValidationRule (objectDefinitionId, active_);
create index IX_EE533031 on ObjectValidationRule (objectDefinitionId, engine[$COLUMN_LENGTH:255$]);
create unique index IX_88476606 on ObjectValidationRule (objectDefinitionId, externalReferenceCode[$COLUMN_LENGTH:75$], companyId);
create index IX_465D010A on ObjectValidationRule (objectDefinitionId, outputType[$COLUMN_LENGTH:75$]);
create index IX_ADDDA15A on ObjectValidationRule (uuid_[$COLUMN_LENGTH:75$]);

create index IX_76851E60 on ObjectValidationRuleSetting (name[$COLUMN_LENGTH:75$], value[$COLUMN_LENGTH:75$]);
create unique index IX_7FCFA51D on ObjectValidationRuleSetting (objectValidationRuleId, name[$COLUMN_LENGTH:75$], value[$COLUMN_LENGTH:75$]);
create index IX_9CCE9B52 on ObjectValidationRuleSetting (uuid_[$COLUMN_LENGTH:75$]);

create index IX_6AF6C9EA on ObjectView (objectDefinitionId, defaultObjectView);
create index IX_877B3D0A on ObjectView (uuid_[$COLUMN_LENGTH:75$]);

create index IX_B7B14E3 on ObjectViewColumn (objectViewId, objectFieldName[$COLUMN_LENGTH:75$]);
create index IX_FABEAD54 on ObjectViewColumn (uuid_[$COLUMN_LENGTH:75$]);

create index IX_B8CD6D4B on ObjectViewFilterColumn (objectViewId, objectFieldName[$COLUMN_LENGTH:75$]);
create index IX_A8A1BDBC on ObjectViewFilterColumn (uuid_[$COLUMN_LENGTH:75$]);

create index IX_55C88365 on ObjectViewSortColumn (objectViewId, objectFieldName[$COLUMN_LENGTH:75$]);
create index IX_314101D6 on ObjectViewSortColumn (uuid_[$COLUMN_LENGTH:75$]);