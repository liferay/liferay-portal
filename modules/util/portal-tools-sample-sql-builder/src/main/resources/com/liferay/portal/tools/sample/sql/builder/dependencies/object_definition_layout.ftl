<#list dataFactory.getSequence(dataFactory.maxObjectEntryPageCount) as objectEntryPageCount>

	<#assign
		layoutModel = dataFactory.newContentLayoutModel(groupId, objectDefinitionModel.getName() + objectEntryPageCount, null)

		fragmentEntryLinkModels = dataFactory.newObjectFieldsFragmentEntryLinkModels(layoutModel, objectFieldModels)
		layoutPageTemplateStructureModel = dataFactory.newLayoutPageTemplateStructureModel(layoutModel)
	/>

	${dataFactory.toInsertSQL(layoutModel)}

	${dataFactory.toInsertSQL(dataFactory.newLayoutFriendlyURLModel(layoutModel))}

	<#list fragmentEntryLinkModels as fragmentEntryLinkModel>
		${dataFactory.toInsertSQL(fragmentEntryLinkModel)}
	</#list>

	${dataFactory.toInsertSQL(layoutPageTemplateStructureModel)}

	${dataFactory.toInsertSQL(dataFactory.newObjectDefinitionLayoutPageTemplateStructureRelModel(fragmentEntryLinkModels, layoutPageTemplateStructureModel, objectDefinitionModel))}

	${csvFileWriter.write("objectDefinition", virtualHostModel.hostname + "," + groupModel.friendlyURL + "," + groupId + "," + layoutModel.getFriendlyURL() + "," + objectDefinitionModel.getObjectDefinitionId() + "\n")}
</#list>