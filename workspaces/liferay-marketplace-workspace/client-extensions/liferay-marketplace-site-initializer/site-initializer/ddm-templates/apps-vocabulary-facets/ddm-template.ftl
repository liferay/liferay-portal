<style ${nonceAttribute}>
	.facet-vocabulary-panel {
		border-radius: 10px;
	}

	.facet-vocabulary-panel .clear-btn {
		color: #2B3A4B;
		font-size: 14px;
		font-weight: 400;
	}

	.facet-vocabulary-panel .collapse-icon .collapse-icon-closed .lexicon-icon,
	.facet-vocabulary-panel .collapse-icon .collapse-icon-open .lexicon-icon {
		margin-top: 0.3rem;
	}

	.facet-vocabulary-panel .list-unstyled {
		margin-bottom: 0;
	}

	.facet-vocabulary-panel .panel a {
		padding: 1.5rem;
		padding-bottom: 0;
	}

	.facet-vocabulary-panel .panel-body {
		padding: 0 1.5rem;
	}

	.facet-vocabulary-panel .separator {
		margin: 1rem auto 0;
		width: 90%;
	}

	.facet-vocabulary-panel .view-all-btn {
		color: #2B3A4B;
		font-size: 14px;
		font-weight: 400;
	}
</style>

<#assign
	filteredCount = 0
	title = assetCategoriesSearchFacetDisplayContext.getParameterName()?upper_case
/>

<#list entries as entry>
	<#if entry.isSelected()>
		<#assign filteredCount++>
	</#if>
</#list>

<#if filteredCount gt 0>
	<#assign title = title + " (${filteredCount})" />
</#if>

<@liferay_ui["panel-container"]
	cssClass="bg-white border-radius-xlarge facet-vocabulary-panel"
	extended=true
	id="${namespace + 'facetAssetCategoriesPanelContainer'}"
	markupView="lexicon"
	persistState=true
>
	<@liferay_ui.panel
		collapsible=true
		cssClass="font-size-paragraph-small font-weight-semi-bold"
		extended=!browserSniffer.isMobile(request)
		id="${namespace + 'facetAssetCategoriesPanel'}"
		markupView="lexicon"
		persistState=true
		title="${title}"
	>
		<button class="btn-unstyled clear-btn mb-4" id="${namespace + 'facetAssetCategoriesSelectAll'}" onClick="${namespace}selectAll(event)">
			${languageUtil.get(locale, "select-all")}
		</button>

	  	<button class="btn-unstyled clear-btn mb-4 ml-1" onClick="Liferay.Search.FacetUtil.clearSelections(event);">
			${languageUtil.get(locale, "clear")}
	  	</button>

		<ul class="list-unstyled">
			<#assign
				currentURL = themeDisplay.getURLCurrent()?replace("%20", " ")
				isExpanded = currentURL?contains("${assetCategoriesSearchFacetDisplayContext.getParameterName()}Expanded")
				optionsCount = 0
			/>

			<#list entries as entry>
				<li class="color-neutral-2 <#if optionsCount gte 5 && !isExpanded>d-none</#if> facet-value py-1">
					<div class="custom-checkbox custom-control font-weight-normal">
						<label class="facet-checkbox-label" for="${namespace}_term_${entry.getAssetCategoryId()}">
							<input
								${(entry.isSelected())?then("checked","")}
								class="custom-control-input facet-term"
								data-term-id="${entry.getAssetCategoryId()}"
								disabled
								id="${namespace}_term_${entry.getAssetCategoryId()}"
								name="${namespace}_term_${entry.getAssetCategoryId()}"
								onChange="Liferay.Search.FacetUtil.changeSelection(event);"
								type="checkbox"
							/>

							<span class="custom-control-label font-size-paragraph-small term-name ${(entry.isSelected())?then('facet-term-selected', 'facet-term-unselected')}">
								<span class="custom-control-label-text">
									${htmlUtil.escape(entry.getDisplayName())}
								</span>
							</span>
						</label>
					</div>
				</li>
				<#assign optionsCount++ />
			</#list>

			<#if optionsCount gt 5 && !isExpanded>
				<button
					class="btn-unstyled mt-4 view-all-btn"
					id="${assetCategoriesSearchFacetDisplayContext.getParameterName() + 'facetAssetCategoriesViewAll'}"
					onClick="${namespace}viewAll(event, '${namespace + 'facetAssetCategoriesPanel'}')"
				>
					<span>${languageUtil.get(locale, "view-all")}</span>
				</button>
			</#if>
		</ul>
	</@>

	<hr class="separator" />
</@>

<@liferay_aui.script>
	function ${namespace}selectAll(event) {
		event.preventDefault();

		const divId = event.target.closest('.collapse').id;
		const checkboxes = document.querySelectorAll('#' + divId + ' .custom-checkbox input[type="checkbox"]');
		const parameterName = `${assetCategoriesSearchFacetDisplayContext.getParameterName()}`;
		const url = new URL(window.location.href);

		if (url.searchParams.size === 0) {
			url.href += '?';
		}

		checkboxes.forEach((checkbox) => {
			if (!checkbox.checked) {
				if (url.searchParams.size > 0) {
					url.href += '&';
				}

				url.href += parameterName + '=' + checkbox.getAttribute('data-term-id');
			}
		});

		window.location.href = url.href
	}

	function ${namespace}viewAll(event, dataTarget) {
		event.preventDefault();

		const treeElement = document.getElementById(dataTarget);

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

		newParamURL += `${assetCategoriesSearchFacetDisplayContext.getParameterName()}Expanded`

		window.history.pushState({}, "", newParamURL);
	}
</@>