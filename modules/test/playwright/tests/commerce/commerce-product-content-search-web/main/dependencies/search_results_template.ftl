<#if entries?has_content>
	<#list entries as curCPCatalogEntry>
		<p>Search Result ${curCPCatalogEntry.getCPDefinitionId()}</p>
	</#list>
</#if>