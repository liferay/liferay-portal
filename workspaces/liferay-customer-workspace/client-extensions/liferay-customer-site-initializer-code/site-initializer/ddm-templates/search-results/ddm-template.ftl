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
				<a class="d-block search-results-entry text-decoration-none text-body" href="${searchEntry.getViewURL()}&highlight=${htmlUtil.escape(searchResultsPortletDisplayContext.getKeywords()?url('ISO-8859-1'))}">
					<div class="card card-flat card-transparent mb-3 ml-1 mr-1 mt-1 pb-3 pl-3 pr-3 pt-3">
						<h5 class="search-results-entry-title text-primary">
							${searchEntryTitle}
						</h5>

						<div class="search-results-entry-content">
							${searchEntryContent}
						</div>

						<#if searchEntry.getPublishedDateString()?has_content>
							<div class="pt-2 published-date text-paragraph-sm">
								${languageUtil.get(locale, "published-date")}: ${searchEntry.getPublishedDateString()}
							</div>
						</#if>
					</div>
				</a>
			</#if>
		</#list>
	<#else>
		<p class="search-results-empty">
			${languageUtil.format(locale, "no-results-were-found-that-matched-the-keywords-x", htmlUtil.escape(searchResultsPortletDisplayContext.getKeywords()), false)}
		</p>
	</#if>
</div>