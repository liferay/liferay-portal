<#assign defaultCompanyModel = dataFactory.newDefaultCompanyModel() />

${dataFactory.setCompanyId(defaultCompanyModel.companyId)}

${dataFactory.setWebId(defaultCompanyModel.webId)}

${dataFactory.toInsertSQL(defaultCompanyModel)}

<#assign defaultVirtualHostModel = dataFactory.newVirtualHostModel() />

${dataFactory.toInsertSQL(defaultVirtualHostModel)}

${dataFactory.toInsertSQL(dataFactory.newPortalPreferencesModel(defaultCompanyModel.companyId))}
${dataFactory.toInsertSQL(dataFactory.newPortalPreferencesModel(0))}

${csvFileWriter.write("company", defaultVirtualHostModel.hostname + "," + defaultCompanyModel.companyId + "\n")}

<#include "roles.ftl">

<#include "default_groups.ftl">

<#include "notification_templates.ftl">

<#include "system_object_definitions.ftl">