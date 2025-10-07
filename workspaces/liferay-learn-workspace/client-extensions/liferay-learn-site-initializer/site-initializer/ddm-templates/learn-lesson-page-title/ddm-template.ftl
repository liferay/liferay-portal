<#assign
	assetEntryId = ObjectEntry_objectEntryId.getData()

	response = restClient.get("/c/lessons/${assetEntryId}?fields=r_lesson_c_module.r_module_c_course.title&nestedFields=course,module&nestedFieldsDepth=2")

	pageTitle = response.r_lesson_c_module.r_module_c_course.title + " - " +.data_model["ObjectRelationship#C_Module#lesson_title"].getData() + " - " + ObjectField_title.getData()
/>

<#if pageTitle?has_content>
	<#assign void = portalUtil.setPageTitle(pageTitle, request) />
</#if>