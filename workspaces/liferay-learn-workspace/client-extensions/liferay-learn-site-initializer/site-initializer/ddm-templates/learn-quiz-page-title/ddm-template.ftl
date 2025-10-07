<#assign
	assetEntryId = ObjectEntry_objectEntryId.getData()

	response = restClient.get("/c/quizes/${assetEntryId}?fields=r_quiz_c_module.r_module_c_course.title&nestedFields=course,module&nestedFieldsDepth=2")

	pageTitle = response.r_quiz_c_module.r_module_c_course.title + " - " +.data_model["ObjectRelationship#C_Module#quiz_title"].getData() + " - " + ObjectField_title.getData()
/>

<#if pageTitle?has_content>
	<#assign void = portalUtil.setPageTitle(pageTitle, request) />
</#if>