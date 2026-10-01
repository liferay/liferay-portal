<#assign totalHitsLabel = searchContainer.getTotal() />

<#if !searchResultsPortletDisplayContext.isTotalHitsExact()>
	<#assign totalHitsLabel = languageUtil.format(locale, "x-plus", [totalHitsLabel], false) />
</#if>

<div class="c-mb-4 c-mt-4 search-total-label">
	<#if searchContainer.getTotal() == 1>
		${languageUtil.format(locale, "x-result-for-x", [totalHitsLabel, "<strong>" + htmlUtil.escape(searchResultsPortletDisplayContext.getKeywords()) + "</strong>"], false)}
	<#else>
		${languageUtil.format(locale, "x-results-for-x", [totalHitsLabel, "<strong>" + htmlUtil.escape(searchResultsPortletDisplayContext.getKeywords()) + "</strong>"], false)}
	</#if>

	<#if !searchResultsPortletDisplayContext.isTotalHitsExact()>
		<span
			aria-label="${languageUtil.get(locale, 'approximate-result-count-help')}"
			class="c-ml-1 inline-item lfr-portal-tooltip text-secondary"
			tabindex="0"
			title="${languageUtil.get(locale, 'approximate-result-count-help')}"
		>
			<@clay["icon"] symbol="info-circle" />
		</span>
	</#if>
</div>

<div class="display-compact">
	<ul class="list-unstyled">
		<#if entries?has_content>
			<#list entries as entry>
				<li class="align-items-center d-flex">
					<div class="c-mb-2 c-mt-2">
						<a class="link-primary single-link" href="${entry.getViewURL()}">
							${entry.getHighlightedTitle()}
						</a>
					</div>

					<#if entry.isAssetRendererURLDownloadVisible()>
						<span
							class="lfr-portal-tooltip c-ml-1"
							title="${(entry.getAssetRendererDownloadSize() > 0)?then(languageUtil.format(locale, 'download-x', ['(' + languageUtil.formatStorageSize(entry.getAssetRendererDownloadSize(), locale) + ')']), languageUtil.get(locale, 'download'))}"
						>
							<@clay.link
								aria\-label="${languageUtil.format(locale, 'download-x', [entry.getTitle()])}"
								cssClass="link-monospaced link-outline link-outline-borderless link-outline-secondary"
								displayType="secondary"
								href="${entry.getAssetRendererURLDownload()}"
							>
								<@clay.icon symbol="download" />
							</@clay.link>
						</span>
					</#if>
				</li>
			</#list>
		</#if>
	</ul>
</div>