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
									aria\-controls="${namespace}treeItem${id}"
									aria\-expanded="true"
									cssClass="btn btn-monospaced component-expander"
									data\-target="#${namespace}treeItem${id}"
									data\-toggle="collapse"
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
						<#assign hideClass = "" />

						<#if termDisplayContextCount lte 8>
							<@treeview_item
								cssClassTreeItem = "tree-item-category"
								frequency = termDisplayContext.getFrequency()
								frequencyVisible = termDisplayContext.isFrequencyVisible()
								id = termDisplayContext.getFilterValue()
								name = htmlUtil.escape(termDisplayContext.getBucketText())
								selectable = true
								selected = termDisplayContext.isSelected()
								termDisplayContextClass = hideClass
								vocabularyName = vocabularyName
							/>
						<#else>
							<@treeview_item
								cssClassTreeItem = "tree-item-category d-none"
								frequency = termDisplayContext.getFrequency()
								frequencyVisible = termDisplayContext.isFrequencyVisible()
								id = termDisplayContext.getFilterValue()
								name = htmlUtil.escape(termDisplayContext.getBucketText())
								selectable = true
								selected = termDisplayContext.isSelected()
								termDisplayContextClass = hideClass
								vocabularyName = vocabularyName
							/>
						</#if>

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
		cssClass="p-4 search-facet search-facet-display-vocabulary"
		id="${namespace + 'facetAssetCategoriesPanel'}"
		markupView="lexicon"
		persistState=true
		title="${(vocabularyNames?size == 1)?then(vocabularyNames[0]!'', 'category')}"
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

		function ${namespace}selectAll(event) {

		var divId = event.target.closest('.collapse').id;
		var checkboxes = document.querySelectorAll('#' + divId + ' .custom-checkbox input[type="checkbox"]');

		checkboxes.forEach((checkbox) => {
			checkbox.checked = true;
			var changeEvent = new Event('change');
			checkbox.dispatchEvent(changeEvent);
		});
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
		const panels = document.querySelectorAll('.panel-group .panel');

		panels.forEach(panel => {
			if (window.innerWidth <= 768) {
				const panelBody = panel.querySelector('.panel-collapse');

				if (panelBody) {
					panelBody.classList.remove('show');
					panelBody.classList.add('collapse');
				}

				const panelHeaderButton = panel.querySelector('.panel-header-link');

				if (panelHeaderButton) {
					panelHeaderButton.classList.add('collapsed');
					panelHeaderButton.setAttribute('aria-expanded', 'false');
				}
			}
		});
	});
</@>

<style>
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

	.search-facet-display-vocabulary .learn-treeview.treeview-vocabulary-display .tree-item-category {
		padding-left: 0rem;
	}
</style>