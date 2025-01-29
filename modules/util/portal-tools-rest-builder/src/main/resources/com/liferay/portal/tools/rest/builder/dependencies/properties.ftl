#
# This is a generated file.
#

api.version=${openAPIYAML.info.version}
<#assign
	javaDataType = freeMarkerTool.getJavaDataType(configYAML, openAPIYAML, schemaName)!""
	javaMethodSignatures = freeMarkerTool.getResourceJavaMethodSignatures(configYAML, openAPIYAML, schemaName)
	generateBatch = freeMarkerTool.generateBatch(configYAML, javaDataType, javaMethodSignatures, schemaName)
	generateCRUD = false
/>
<#if stringUtil.equals(schemaName, "openapi")>
openapi.resource=true
<#if configYAML.application??>
openapi.resource.path=${configYAML.application.baseURI}
</#if>
<#elseif generateBatch>
batch.engine.entity.class.name=${javaDataType}
batch.engine.task.item.delegate=true
batch.planner.export.enabled=${freeMarkerTool.hasReadVulcanBatchImplementation(javaMethodSignatures)?c}
batch.planner.import.enabled=${freeMarkerTool.getVulcanBatchImplementationCreateStrategies(javaMethodSignatures, freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, schema, allSchemas))?has_content?c}
</#if>
<#if generateCRUD>
crud.item.delegate=true
</#if>
<#if javaDataType?has_content>
entity.class.name=${javaDataType}
</#if>
<#if configYAML.resourceApplicationSelect??>
osgi.jaxrs.application.select=${configYAML.resourceApplicationSelect}
<#elseif configYAML.application??>
osgi.jaxrs.application.select=(osgi.jaxrs.name=${configYAML.application.name})
</#if>
osgi.jaxrs.resource=true