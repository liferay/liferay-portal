<#list dataFactory.newCompanyModels() as companyModel>
	${dataFactory.setCompanyId(companyModel.companyId)}

	${dataFactory.setWebId(companyModel.webId)}

	<#assign virtualHostModel = dataFactory.newVirtualHostModel() />

	${dataFactory.toInsertSQL(companyModel)}

	${dataFactory.toInsertSQL(virtualHostModel)}

	${dataFactory.toInsertSQL(dataFactory.newPortalPreferencesModel(companyModel.companyId))}

	${csvFileWriter.write("company", virtualHostModel.hostname + "," + companyModel.companyId + "\n")}

	<#include "list_type_definitions.ftl">

	<#include "roles.ftl">

	<#include "default_groups.ftl">

	<#include "notification_templates.ftl">

	<#include "system_object_definitions.ftl">

	<#include "groups.ftl">

	${csvFileWriter.write("portalPreferenceValue", companyModel.companyId + "," + dataFactory.recentGroupIds + "\n")}
</#list>