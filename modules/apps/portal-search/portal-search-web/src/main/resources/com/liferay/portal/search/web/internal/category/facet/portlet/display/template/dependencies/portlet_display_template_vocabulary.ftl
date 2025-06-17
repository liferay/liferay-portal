<#macro treeview_item
	cssClassTreeItem = ""
	frequency = 0
	id = ""
	frequencyVisible = true
	name = ""
	selectable = false
	selected = false
	termDisplayContexts = ""
>
	<li class="treeview-item" role="none">
		<#if name?has_content>
			<@liferay_ui.csp>
				<div
					aria-controls="${(termDisplayContexts?has_content)?then(namespace + 'treeItem' + id, '')}"
					aria-expanded="true"
					class="treeview-link ${cssClassTreeItem}"
					data-target="#${namespace}treeItem${id}"
					data-toggle="collapse"
					onClick="${namespace}toggleTreeItem('${namespace}treeItem${id}');"
					onKeyPress="${namespace}toggleTreeItemKeypress(event);"
					role="treeitem"
					tabindex="${(termDisplayContexts?has_content)?then(0, -1)}"
				>
					<span class="c-inner" tabindex="-2">
						<span class="autofit-row">
							<#if termDisplayContexts?has_content>
								<span class="autofit-col">
									<@clay.button
										aria\-controls="${namespace}treeItem${id}"
										aria\-expanded="true"
										aria\-label="${languageUtil.get(locale, 'toggle')}"
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
											<@liferay_ui.csp>
												<input
													${selected?then("checked", "")}
													class="custom-control-input facet-term"
													data-term-id=${id}
													disabled
													onChange="Liferay.Search.FacetUtil.changeSelection(event);"
													type="checkbox"
												/>
											</@liferay_ui.csp>

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
			</@liferay_ui.csp>
		</#if>

		<#if termDisplayContexts?has_content>
			<div class="collapse show" id="${namespace}treeItem${id}">
				<ul class="treeview-group" role="group">
					<#list termDisplayContexts as termDisplayContext>
						<@treeview_item
							cssClassTreeItem = "tree-item-category"
							frequency = termDisplayContext.getFrequency()
							frequencyVisible = termDisplayContext.isFrequencyVisible()
							id = termDisplayContext.getFilterValue()
							name = htmlUtil.escape(termDisplayContext.getBucketText())
							selectable = true
							selected = termDisplayContext.isSelected()
						/>
					</#list>
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
		cssClass="search-facet search-facet-display-vocabulary"
		id="${namespace + 'facetAssetCategoriesPanel'}"
		markupView="lexicon"
		persistState=true
		title="${(vocabularyNames?size == 1)?then(vocabularyNames[0]!'', 'category')}"
	>
		<#if !assetCategoriesSearchFacetDisplayContext.isNothingSelected()>
			<@clay.button
				cssClass="btn-unstyled c-mb-4 facet-clear-btn"
				displayType="link"
				id="${namespace + 'facetAssetCategoriesClear'}"
				onClick="Liferay.Search.FacetUtil.clearSelections(event);"
			>
				<strong>${languageUtil.get(locale, "clear")}</strong>

				<span class="sr-only">
					${languageUtil.format(locale, 'x-filter', 'category-facet-portlet-instance-configuration-name')}
				</span>
			</@clay.button>
		</#if>

		<#if vocabularyNames?has_content>
			<ul class="treeview treeview-light treeview-nested treeview-vocabulary-display" role="tree">
				<#list vocabularyNames as vocabularyName>
					<@treeview_item
						cssClassTreeItem = "tree-item-vocabulary"
						frequencyVisible = false
						id = vocabularyName?replace("[^\\w]|_", '', 'r') + vocabularyName?index
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

	function ${namespace}toggleTreeItemKeypress(event) {
		event.preventDefault();

		if (event.code === 'Enter' || event.code === 'Space') {
			${namespace}toggleTreeItem(event.target.getAttribute('aria-controls'));
		}
	}
</@>