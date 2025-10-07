<#macro treeview_item
	cssClassTreeItem = ""
	frequency = 0
	frequencyVisible = true
	id = ""
	name = ""
	selectable = false
	selected = false
	termDisplayContexts = ""
	termDisplayContextClass = ""
	vocabularyName = ""
>
	<li class="treeview-item ${termDisplayContextClass}" role="none">
		<#if name?has_content>
			<div
				aria-controls="${namespace}treeItem${id}"
				aria-expanded="true"
				class="treeview-link ${cssClassTreeItem}"
				data-target="#${namespace}treeItem${id}"
				data-toggle="collapse"
				onClick="${namespace}toggleTreeItem('${namespace}treeItem${id}');"
				role="treeitem"
				tabindex="0"
			>
				<span class="autofit-row">
					<#if termDisplayContexts?has_content>
						<span class="autofit-col">
							<@clay.button
								aria-controls="${namespace}treeItem${id}"
								aria-expanded="true"
								cssClass="btn btn-monospaced component-expander"
								data-target="#${namespace}treeItem${id}"
								data-toggle="collapse"
								displayType="link"
								tabindex="-1"
							>
								<span class="c-inner" tabindex="-2">
									<@clay["icon"] symbol="angle-down" />

									<@clay["icon"]
										cssClass="component-expanded-d-none"
										symbol="angle-right"
									/>
								</span>
							</@clay.button>
						</span>
					</#if>

					<#if selectable>
						<span class="autofit-col autofit-col-expand">
							<div class="custom-checkbox custom-control">
								<label>
									<input
										autocomplete="off"
										${selected?then("checked", "")}
										class="custom-control-input facet-term"
										data-term-id=${id}
										disabled
										onChange="Liferay.Search.FacetUtil.changeSelection(event);"
										type="checkbox"
									/>

									<span class="custom-control-label">
										<span class="custom-control-label-text">
											${name}

											<#if frequencyVisible>
												(${frequency})
											</#if>
										</span>
									</span>
								</label>
							</div>
						</span>
					<#else>
						<span class="autofit-col autofit-col-expand">
							<span class="component-text">
								<span
									class="text-truncate-inline"
									title="${name}"
								>
									<span class="text-truncate">
										${name}

										<#if frequencyVisible>
											(${frequency})
										</#if>
									</span>
								</span>
							</span>
						</span>
					</#if>
				</span>
			</div>
		</#if>

		<#if termDisplayContexts?has_content>
			<div class="actionBtns">
				<@clay.button
					cssClass="btn-unstyled c-mb-3 facet-clear-btn"
					displayType="link"
					id="${namespace + 'facetAssetCategoriesSelectAll'}"
					onClick="${namespace}selectAll(event)"
				>
					<span>${languageUtil.get(locale, "select-all")}</span>
				</@clay.button>

				<@clay.button
					cssClass="btn-unstyled c-mb-3 facet-clear-btn"
					displayType="link"
					id="${namespace + 'facetAssetCategoriesClear'}"
					onClick="Liferay.Search.FacetUtil.clearSelections(event);"
				>
					<span>${languageUtil.get(locale, "clear")}</span>
				</@clay.button>
			</div>

			<div class="collapse show" id="${namespace}treeItem${id}">
				<ul class="treeview-group" role="group">
					<#assign
						hasTermDisplayContextHidden = false
						termDisplayContextCount = 1
					/>

					<#list termDisplayContexts as termDisplayContext>
						<#assign cssClassTreeItem = "tree-item-category" />

						<#if termDisplayContextCount lte 8>
							<@treeview_item cssClassTreeItem = "tree-item-category d-none" />
						</#if>

						<@treeview_item
							cssClassTreeItem = "${cssClassTreeItem}"
							frequency = termDisplayContext.getFrequency()
							frequencyVisible = termDisplayContext.isFrequencyVisible()
							id = termDisplayContext.getFilterValue()
							name = htmlUtil.escape(termDisplayContext.getBucketText())
							selectable = true
							selected = termDisplayContext.isSelected()
							termDisplayContextClass = ""
							vocabularyName = vocabularyName
						/>

						<#assign termDisplayContextCount++ />
					</#list>

					<#if termDisplayContextCount gt 8>
						<@clay.button
							cssClass="btn-unstyled facet-clear-btn view-all-btn c-mt-3"
							displayType="link"
							id="${vocabularyName + 'facetAssetCategoriesViewAll'}"
							onClick="${namespace}viewAll('${namespace}treeItem${id}')"
						>
							<span>${languageUtil.get(locale, "view-all")}</span>
						</@clay.button>
					</#if>
				</ul>
			</div>
		</#if>
	</li>
</#macro>

<@liferay_ui["panel-container"]
	extended=true
	id="${namespace + 'facetAssetCategoriesPanelContainer'}"
	markupView="lexicon"
	persistState=true
>
	<#assign vocabularyNames = assetCategoriesSearchFacetDisplayContext.getVocabularyNames()![] />

	<@liferay_ui.panel
		collapsible=true
		cssClass="p-2 search-facet search-facet-display-vocabulary"
		id="${namespace + 'facetAssetCategoriesPanel'}"
		markupView="lexicon"
		persistState=true
		title=languageUtil.get(locale, "filter-by-type")
	>
		<#if vocabularyNames?has_content>
			<ul class="learn-treeview treeview treeview-light treeview-nested treeview-vocabulary-display" role="tree">
				<#list vocabularyNames as vocabularyName>
					<@treeview_item
						cssClassTreeItem = "tree-item-vocabulary"
						frequencyVisible = false
						id = vocabularyName + vocabularyName?index
						name = "${(vocabularyNames?size == 1)?then('', htmlUtil.escape(vocabularyName))}"
						termDisplayContexts = assetCategoriesSearchFacetDisplayContext.getBucketDisplayContexts(vocabularyName)
					/>
				</#list>
			</ul>
		</#if>
	</@>
</@>

<@liferay_aui.script>
	function ${namespace}selectAll(event) {
		var checkboxes = document.querySelectorAll('#' + event.target.closest('.collapse').id + ' .custom-checkbox input[type="checkbox"]');

		checkboxes.forEach((checkbox) => {
			checkbox.checked = true;
			checkbox.dispatchEvent(new Event('change'));
		});
	}

	function ${namespace}toggleTreeItem(dataTarget) {
		const dataTargetElements = document.querySelectorAll("[data-target=\"#" + dataTarget + "\"]");

		dataTargetElements.forEach(
			element => {
				if (element.classList.contains('collapsed')) {
					element.classList.remove('collapsed');
					element.setAttribute('aria-expanded', true);
				}
				else {
					element.classList.add('collapsed');
					element.setAttribute('aria-expanded', false);
				}
			}
		);

		const subtreeCategoryTreeElement = document.getElementById(dataTarget);

		if (subtreeCategoryTreeElement) {
			if (subtreeCategoryTreeElement.classList.contains('show')) {
				subtreeCategoryTreeElement.classList.remove('show');
			}
			else {
				subtreeCategoryTreeElement.classList.add('show');
			}
		}
	}

	function ${namespace}viewAll(dataTarget) {
		const subtreeCategoryTreeElement = document.getElementById(dataTarget);

		if (subtreeCategoryTreeElement) {
			const hiddenItems = subtreeCategoryTreeElement.querySelectorAll('.d-none');

			hiddenItems.forEach(item => {
				item.classList.remove('d-none');
			});

			const viewAllButton = subtreeCategoryTreeElement.querySelector('.view-all-btn');

			if (viewAllButton) {
				viewAllButton.style.display = 'none';
			}
		}
	}

	document.addEventListener('DOMContentLoaded', () => {
		const facetAssetCategoriesPanel = document.getElementById('${namespace}facetAssetCategoriesPanel');

		const panelBody = facetAssetCategoriesPanel.querySelector('.panel-collapse');
		const panelHeaderButton = facetAssetCategoriesPanel.querySelector('.panel-header .btn');

		if (window.innerWidth <= 768) {
			if (panelBody) {
				panelBody.classList.remove('show');
			}

			if (panelHeaderButton) {
				panelHeaderButton.classList.add('collapsed');
				panelHeaderButton.setAttribute('aria-expanded', 'false');
			}

			document.querySelectorAll('.treeview-link').forEach(item => {
				item.classList.add('collapsed');
				item.setAttribute('aria-expanded', 'false');
			});

			document.querySelectorAll('.treeview-group').forEach(group => {
				group.classList.remove('show');
			});
		}
	});
</@>

<style>
	.announcement-filter .portlet {
		margin-bottom: 0;
	}

	.collapse-icon {
		align-items: center !important;
		display: flex !important;
		justify-content: space-between !important;
		padding-right: 1rem !important;
	}

	.collapse-icon-closed {
		position: unset !important;
	}

	.learn-treeview .custom-control-label::before {
		border-color: var(--gray-600, #6b6c7e);
	}

	.learn-treeview .custom-control-label-text {
		font-size: 13px;
		font-weight: 400;
	}

	.learn-treeview.treeview .btn {
		margin-right: 0.5rem;
		padding: 0px;
	}

	.learn-treeview.treeview .custom-control {
		margin: 0rem;
	}

	.panel-collapse .panel-body {
		padding: 0rem 1.25rem;
	}

	.search-facet-display-vocabulary .learn-treeview.treeview-vocabulary-display .tree-item-category {
		padding-left: 0rem;
	}
</style>