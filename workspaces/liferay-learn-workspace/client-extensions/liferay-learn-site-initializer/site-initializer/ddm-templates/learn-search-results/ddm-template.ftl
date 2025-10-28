<#macro displayResourceTypeTags
	taxonomyCategoryBriefs
>
	<#if taxonomyCategoryBriefs?has_content>
		<#list taxonomyCategoryBriefs as taxonomyCategoryBrief>
			<#assign taxonomyVocabulary = taxonomyCategoryBrief.embeddedTaxonomyCategory.parentTaxonomyVocabulary.externalReferenceCode!"N/A" />

			<#if stringUtil.equals(taxonomyVocabulary, "RESOURCE_TYPE")>
				<span class="font-weight-normal label label-inverse-light label-secondary m-0 px-2 text-paragraph-sm">
					${taxonomyCategoryBrief.taxonomyCategoryName}
				</span>
			</#if>
		</#list>
	</#if>
</#macro>

<#function getValue contentString end start>
	<#assign startIndex = contentString?index_of(start) />

	<#if startIndex == -1>
		<#return "" />
	</#if>

	<#assign
		substring = contentString?substring(startIndex + start?length)

		endIndex = substring?index_of(end)
	/>

	<#if endIndex == -1>
		<#return substring?trim />
	</#if>

	<#return substring?substring(0, endIndex)?trim />
</#function>

<div class="search-results" id="searchResults">
	<#if entries?has_content>
		<#list entries as searchEntry>
			<#assign
				className = searchEntry.getClassName()!""
				classPK = searchEntry.getClassPK()!""
				searchEntryContent = searchEntry.getContent()!languageUtil.get(locale, "no-content-preview", "No content preview")
				searchEntryTitle = searchEntry.getTitle()!""
			/>

			<#if searchEntryTitle?has_content>
				<div class="align-items-stretch pb-4 search-results-entry">
					<a class="font-weight-bold search-results-entry-title text-decoration-none unstyled" href="${searchEntry.getViewURL()}&highlight=${htmlUtil.escape(searchResultsPortletDisplayContext.getKeywords()?url('ISO-8859-1'))}">
						<div class="d-flex justify-content-between search-results-entry-header">
							${searchEntryTitle}
							<div class="search-results-entry-tags">
								<#if className?contains("com.liferay.journal.model.JournalArticle")>
									<#assign structuredContent = restClient.get("/headless-delivery/v1.0/structured-contents/" + classPK + "?fields=taxonomyCategoryBriefs&nestedFields=embeddedTaxonomyCategory") />

									<#if structuredContent??>
										<@displayResourceTypeTags taxonomyCategoryBriefs = structuredContent.taxonomyCategoryBriefs />
									</#if>
								<#elseif className?contains("com.liferay.object.model.ObjectDefinition")>
									<#assign knowledgeArticle = restClient.get("/c/p2s3knowledgearticles/" + classPK + "?nestedFields=embeddedTaxonomyCategory") />

									<#if knowledgeArticle??>
										<#if knowledgeArticle.legacy?? && knowledgeArticle.legacy == true>
											<span class="font-weight-normal label label-secondary label-inverse-light m-0 px-2 text-paragraph-sm">
												<@liferay_ui["message"] key="legacy" />
											</span>
										</#if>

										<@displayResourceTypeTags taxonomyCategoryBriefs = knowledgeArticle.taxonomyCategoryBriefs />
									</#if>
								</#if>
							</div>
						</div>

						<div class="description search-results-entry-content">
							<#if className?contains("com.liferay.journal.model.JournalArticle")>
								${searchEntryContent}
							<#else>
								${getValue(searchEntryContent, " end:", "content:")}
							</#if>
						</div>

						<#if searchEntry.getPublishedDateString()?has_content>
							<div class="pt-2 published-date">
								${languageUtil.get(locale, "published-date")}: ${searchEntry.getPublishedDateString()}
							</div>
						</#if>
					</a>
				</div>
			</#if>
		</#list>
	<#else>
		<p class="search-results-empty">
			${languageUtil.format(locale, "no-results-were-found-that-matched-the-keywords-x", htmlUtil.escape(searchResultsPortletDisplayContext.getKeywords()), false)}
		</p>
	</#if>
</div>

<style>
	.label-inverse-light {
		background-color: var(--color-state-neutral-lighten-2);
		border-color: var(--color-state-neutral-lighten-2);
		color: var(--color-neutral-8);
		height: max-content;
	}

	.search-results-entry-tags {
		align-items: flex-start;
		display: flex;
		gap: 0.5rem;
	}
</style>