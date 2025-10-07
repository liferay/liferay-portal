<#list dataFactory.getSequence(dataFactory.maxObjectEntryPageCount) as objectEntryPageCount>
	<#include "custom_object_definitions.ftl">

	<#assign
		name = objectDefinitionModel.getName()

		contentLayoutModels = dataFactory.newContentPageLayoutModels(groupId, name)

		segmentsExperienceModels = dataFactory.newSegmentsExperienceModels(contentLayoutModels)

		fragmentEntryLinkModels = dataFactory.newObjectFieldsFragmentEntryLinkModels(contentLayoutModels, objectFieldModels, segmentsExperienceModels)
	/>

	<#list fragmentEntryLinkModels as fragmentEntryLinkModel>
		${dataFactory.toInsertSQL(fragmentEntryLinkModel)}
	</#list>

	<#list segmentsExperienceModels as segmentsExperienceModel>
		${dataFactory.toInsertSQL(segmentsExperienceModel)}
	</#list>

	<#list contentLayoutModels as contentLayoutModel>
		<#assign layoutPageTemplateStructureModel = dataFactory.newLayoutPageTemplateStructureModel(contentLayoutModel) />

		${dataFactory.toInsertSQL(contentLayoutModel)}

		${dataFactory.toInsertSQL(dataFactory.newLayoutFriendlyURLModel(contentLayoutModel))}

		${dataFactory.toInsertSQL(layoutPageTemplateStructureModel)}

		${dataFactory.toInsertSQL(dataFactory.newObjectDefinitionLayoutPageTemplateStructureRelModel(fragmentEntryLinkModels, contentLayoutModel, layoutPageTemplateStructureModel, objectDefinitionModel))}

		 <#if contentLayoutModel.friendlyURL?contains(name?c_lower_case)>
			${csvFileWriter.write("objectDefinition", virtualHostModel.hostname + "," + groupModel.friendlyURL + "," + contentLayoutModel.getFriendlyURL() + "\n")}
		</#if>
	</#list>
</#list>