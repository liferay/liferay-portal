<style ${nonceAttribute}>
	.facet-specification-panel {
		border-radius: 10px;
	}

	.facet-specification-panel .panel a {
		padding: 1.5rem;
		padding-bottom: 0;
	}

	.facet-specification-panel .collapse-icon .collapse-icon-closed .lexicon-icon,
	.facet-specification-panel .collapse-icon .collapse-icon-open .lexicon-icon {
		margin-top: 0.3rem;
	}

	.facet-specification-panel .panel-body {
		padding: 0 1.5rem;
	}

	.facet-specification-panel .list-unstyled {
		margin-bottom: 0;
	}

	.facet-specification-panel .options-btn {
		color: #2B3A4B;
		font-size: 14px;
		font-weight: 400;
	}

	.facet-specification-panel .separator {
		margin: 1rem auto 0;
		width: 90%;
	}

	.facet-specification-panel .view-all-btn {
		color: #2B3A4B;
		font-size: 14px;
		font-weight: 400;
	}
</style>

<#assign
	filteredCount = 0
	title = cpSpecificationOptionsSearchFacetDisplayContext.getParameterName()?replace('-',' ')
/>

<#list entries?sort_by("displayName") as entry>
	<#if entry.isSelected()>
		<#assign filteredCount++>
	</#if>
</#list>

<#if filteredCount gt 0>
	<#assign title = title + " (${filteredCount})" />
</#if>

<#if cpSpecificationOptionsSearchFacetDisplayContext.getParameterName() != 'developer-name'>

	<@liferay_ui["panel-container"]
		cssClass="bg-white border-radius-xlarge facet-specification-panel"
		extended=true
		id="${namespace + 'facetPriceModelPanelContainer'}"
		markupView="lexicon"
		persistState=true
	>
		<@liferay_ui.panel
			collapsible=true
			cssClass="font-size-paragraph-small font-weight-semi-bold search-facet"
			extended=!browserSniffer.isMobile(request)
			id="${cpSpecificationOptionsSearchFacetDisplayContext.getParameterName()}"
			markupView="lexicon"
			persistState=true
			title="${title}">

			<button class="btn-unstyled options-btn mb-4" id="${namespace + 'facetAssetSelectAll'}" onClick="${namespace}selectAll(event, `${cpSpecificationOptionsSearchFacetDisplayContext.getParameterName()}`)">
				${languageUtil.get(locale, "select-all")}
			</button>

			<button class="btn-unstyled options-btn mb-4 ml-1" onClick="Liferay.Search.FacetUtil.clearSelections(event);">
				${languageUtil.get(locale, "clear")}
			</button>

			<ul class="list-unstyled">
				<#assign
					currentURL = themeDisplay.getURLCurrent()?replace("%20", " ")
					isExpanded = currentURL?contains("${cpSpecificationOptionsSearchFacetDisplayContext.getParameterName()}Expanded")
					optionsCount = 0
				/>

				<#list entries?sort_by("displayName") as entry>
					<#if optionsCount lte 5 || isExpanded>
						<li class="color-neutral-2 <#if optionsCount gte 5 && !isExpanded>d-none</#if> facet-value py-1">
							<div class="custom-checkbox custom-control font-weight-normal">
								<label class="facet-checkbox-label" for="${namespace}_term_${entry.getDisplayName()}">
									<input
										${(entry.isSelected())?then("checked","")}
										class="custom-control-input facet-term"
										data-term-id="${entry.getDisplayName()}"
										id="${namespace}_term_${entry.getDisplayName()}"
										name="${namespace}_term_${entry.getDisplayName()}"
										onChange="Liferay.Search.FacetUtil.changeSelection(event);"
										type="checkbox" />

									<span class="custom-control-label font-size-paragraph-small term-name ${(entry.isSelected())?then('facet-term-selected', 'facet-term-unselected')}">
										<span class="custom-control-label-text">
											<#assign displayName = entry.getDisplayName()?replace("-", " ") />

											<#if displayName == 'dxp'>
												DXP
											<#else>
												${htmlUtil.escape(displayName)?capitalize}
											</#if>
										</span>
									</span>
								</label>
							</div>
						</li>
					</#if>
					<#assign optionsCount++ />
				</#list>

				<#if optionsCount gt 5 && !isExpanded>
					<button
						class="btn-unstyled mt-4 view-all-btn"
						id="${cpSpecificationOptionsSearchFacetDisplayContext.getParameterName() + 'facetAssetCategoriesViewAll'}"
						onClick="${namespace}viewAll(event, `${cpSpecificationOptionsSearchFacetDisplayContext.getParameterName()}`, '${namespace + 'facetAssetCategoriesPanel'}')"
					>
						<span>${languageUtil.get(locale, "view-all")}</span>
					</button>
				</#if>
			</ul>
		</@>
		<hr class="separator" />
	</@>
</#if>

<@liferay_aui.script>
		function ${namespace}selectAll(event, parameterName) {
			event.preventDefault();

			const divId = event.target.closest('.collapse').id;
			const checkboxes = document.querySelectorAll('#' + divId + ' .custom-checkbox input[type="checkbox"]');
			const url = new URL(window.location.href);

			if (url.searchParams.size === 0) {
				url.href += '?'
			}

			checkboxes.forEach((checkbox) => {
				if (!checkbox.checked) {
					if (url.searchParams.size > 0) {
						url.href += '&';
					}
					url.href += parameterName + '=' + checkbox.getAttribute('data-term-id');
				}
			});

			window.location.href = url.href;
		}

	function ${namespace}viewAll(event, parameterName) {
		event.preventDefault();

		const treeElement = document.getElementById(parameterName);

		if (treeElement) {
			const hiddenItems = treeElement.querySelectorAll('.d-none');
			hiddenItems.forEach(item => {
				item.classList.remove('d-none');
			});

			const viewAllButton = treeElement.querySelector('.view-all-btn');
			if (viewAllButton) {
				viewAllButton.style.display = 'none';
			}
		}

	  	let newParamURL = window.location.search === '' ? '?' : window.location.search + '&';

		newParamURL += parameterName + 'Expanded';

		window.history.pushState({}, "", newParamURL);
	}
</@>