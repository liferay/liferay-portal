<#if entries?has_content>
	<#list entries as navItem>
		<#assign customFields = navItem.getExpandoAttributes() />

		${customFields["Subtitle"]}
	</#list>
</#if>