<#assign
	dlFolderModel = dataFactory.newDLFolderModel("Objects_" + objectEntryPageCount + groupId)
	listTypeDefinitionModel = dataFactory.newListTypeDefinitionModel()
	objectDefinitionModel = dataFactory.newObjectDefinitionModel(objectFolderModel.getObjectFolderId(), "Ticket_" + objectEntryPageCount)

	listTypeEntryModels = dataFactory.newListTypeEntryModels(listTypeDefinitionModel.getListTypeDefinitionId())
	objectFieldModels = dataFactory.newObjectFieldModels(objectDefinitionModel.getObjectDefinitionId(), objectDefinitionModel.getDBTableName(), listTypeDefinitionModel.getListTypeDefinitionId())
/>

${dataFactory.toInsertSQL(dlFolderModel)}

<@insertAssetEntry _entry = dlFolderModel />

${dataFactory.toInsertSQL(listTypeDefinitionModel)}

<#list listTypeEntryModels as listTypeEntryModel>
	${dataFactory.toInsertSQL(listTypeEntryModel)}
</#list>

${dataFactory.toInsertSQL(objectDefinitionModel)}

${dataFactory.getDynamicObjectDefinitionTableCreateSQL(objectDefinitionModel, objectFieldModels)}

${dataFactory.getExtensionDynamicObjectDefinitionTableCreateSQL(objectDefinitionModel)}

<#list dataFactory.newObjectEntryModels(objectDefinitionModel.getObjectDefinitionId()) as objectEntryModel>
	<#assign
		dlFileEntryModel = dataFactory.newDLFileEntryModel(dlFolderModel, "FileEntry" + objectEntryModel.getObjectEntryId(), "txt", "text/plain", dataFactory.getCounterNext())
		dlFileVersionModel = dataFactory.newDLFileVersionModel(dlFileEntryModel)
	 />

	${dataFactory.toInsertSQL(dlFileEntryModel)}

	<@insertAssetEntry _entry = dlFileEntryModel />

	${dataFactory.toInsertSQL(dlFileVersionModel)}

	${dataFactory.toInsertSQL(objectEntryModel)}

	<@insertAssetEntry _entry = objectEntryModel />

	<#list dataFactory.generateDynamicSQLs(objectDefinitionModel.getDBTableName(), dlFileEntryModel.getFileEntryId(), objectEntryModel.getObjectEntryId(), objectFieldModels, objectEntryModel.getUserId()) as dynamicSQL>
		${dynamicSQL}
	</#list>
</#list>

<#list objectFieldModels as objectFieldModel>
	${dataFactory.toInsertSQL(objectFieldModel)}

	<#if !objectFieldModel.getSystem()>
		<#list dataFactory.newObjectFieldSettingModels(objectFieldModel) as objectFieldSettingModel>
			${dataFactory.toInsertSQL(objectFieldSettingModel)}
		</#list>

		<#if objectFieldModel.getState()>
			<#assign
				objectStateFlowModel = dataFactory.newObjectStateFlowModel(objectFieldModel.getObjectFieldId())
				objectStates = dataFactory.newObjectStateModels(listTypeEntryModels, objectStateFlowModel.getObjectStateFlowId())
			 />

			${dataFactory.toInsertSQL(objectStateFlowModel)}

			<#list objectStates as objectStateModel>
				${dataFactory.toInsertSQL(objectStateModel)}
			</#list>

			<#list dataFactory.newObjectStateTransitionModels(objectStates) as objectStateTransitionModel>
				${dataFactory.toInsertSQL(objectStateTransitionModel)}
			</#list>
		</#if>
	</#if>
</#list>

${dataFactory.toInsertSQL(dataFactory.newObjectRelationshipModel(objectDefinitionModel.getObjectDefinitionId()))}

<#list dataFactory.newResourcePermissionModels(objectDefinitionModel) as resourcePermissionModel>
	${dataFactory.toInsertSQL(resourcePermissionModel)}
</#list>