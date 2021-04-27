<#if entries?has_content>
	<#list entries as entry>
		<#if entry.isSelected()>
			<#assign cssClass = "current-language" />
		</#if>

		<#if !entry.isDisabled()>
			<@liferay_aui["icon"]
				cssClass=cssClass
				image=entry.getW3cLanguageId()?lower_case
				markupView="lexicon"
				url=entry.getURL()
			/>
		</#if>
	</#list>
</#if>
