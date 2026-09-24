<ul>
	<#if entries?has_content>
		<#list entries as curLanguage>
			<li class="${(curLanguage.isSelected())?then('selected', '')} language-nav-item">
				<#if curLanguage.isSelected()>
					<div class="d-inline-block selected-icon">
						<@clay["icon"] symbol="check" />
					</div>
				</#if>
				<#assign curLanguageLabel = curLanguage.longDisplayName?capitalize />
				<#if curLanguage.shortDisplayName="en" | curLanguage.shortDisplayName="pt">
					<#assign
						curLanguageLocale=curLanguage.getLocale()
						curLanguageLabel=curLanguageLabel + " (" + curLanguageLocale.getDisplayCountry(curLanguageLocale) + ")" />
				</#if>
				<@clay["link"]
					cssClass="language-entry-long-text"
					href=curLanguage.getURL()
					label=curLanguageLabel
					lang=curLanguage.getW3cLanguageId()
					localizeLabel=false
				/>
			</li>
		</#list>
	</#if>
</ul>