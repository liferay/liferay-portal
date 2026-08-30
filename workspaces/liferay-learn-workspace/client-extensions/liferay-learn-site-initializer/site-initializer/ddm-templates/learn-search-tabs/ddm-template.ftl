<style>
.panel-body {
	padding: 0.75rem 0rem;
}

.panel-title, .facet-clear-btn, .collapse-icon, .custom-control-input {
	display: none;
}

.custom-checkbox input[type="checkbox"]:checked + label {
background-color: var(--Action-Primary-Active-Lighten, #E6EDFB);
color: var(--Neutral-10, #282934);
border-top: solid 1px var(--Action-Primary-Active-Lighten, #E6EDFB);
}

.custom-checkbox input[type="checkbox"] + label::before {
content: '';
display: none;
}

.custom-checkbox .custom-control-input:checked ~ .custom-control-label::after {
content: none;
}

.treeview-group {
display: flex;
flex-wrap: wrap;
flex-direction: row;
border-radius: 99px;
background-color: var(--Neutral-01, #F7F7F8);
height: 52px;
padding: 3px;
width: 100%;
}

.treeview-link, .treeview .custom-control, .search-facet-display-vocabulary .treeview-vocabulary-display .tree-item-category>.c-inner, .treeview-item, .treeview-group {
	margin: 0px;
}

.search-facet-display-vocabulary .treeview-vocabulary-display .tree-item-category {
	padding: 0px;
	height: 44px;
}

.treeview-item {
	height: 44px;
	flex:1;
	border
	border-radius: 99px;
	cursor: pointer;
	transition: background-color 0.3s ease;
	color: var(--Neutral-10, #282934);
	font-size: 14px;
	font-weight: 600;
	}

.custom-checkbox {
	display: flex;
	align-items: center;
}

.custom-control-label {
	width: 100%;
	text-align: center;
	height: 44px;
	padding: 12px;
	border-radius: 99px;
}

.results-frequency {
	background-color: var(--Status-Info-Info, #2E5AAC);
	color: var(--Neutral-00, #FFF);
	border-radius: 12px;
	padding: 2px 5px;
}
</style>

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
			<div
				aria-controls="${namespace}treeItem${id}"
				aria-expanded="true"
				class="treeview-link ${cssClassTreeItem}"
				data-target="#${namespace}treeItem${id}"
				data-toggle="collapse"
				onClick="${namespace}toggleTreeItem('${namespace}treeItem${id}', '${id}');"
				role="treeitem"
				tabindex="0"
			>
				<span class="c-inner" tabindex="-2">
					<span class="autofit-row">
						<#if termDisplayContexts?has_content>
						</#if>

						<#if selectable>
							<span class="autofit-col autofit-col-expand">
					<div class="custom-checkbox custom-control">
									<input
										autocomplete="off"
										${selected?then("checked", "")}
										class="custom-control-input facet-term"
										data-term-id=${id}
										disabled
										onChange="Liferay.Search.FacetUtil.changeSelection(event);"
				 						type="checkbox"
										id="checkbox_${id}"
									>
										<label class="custom-control-label" for="checkbox_${id}">
											${name}
											<#if frequencyVisible>
	  											<span class="results-frequency">${frequency}</span>
											</#if>
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
												<span class="results-frequency">${frequency}</span>
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
			<div class="collapse show" id="${namespace}treeItem${id}">
				<ul class="treeview-group" role="group">
					<#list termDisplayContexts as termDisplayContext>
						<@treeview_item
							cssClassTreeItem="tree-item-category"
							frequency=termDisplayContext.getFrequency()
							frequencyVisible=termDisplayContext.isFrequencyVisible()
							id=termDisplayContext.getFilterValue()
							name=htmlUtil.escape(termDisplayContext.getBucketText())
							selectable=true
							selected=termDisplayContext.isSelected()
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
		<#if vocabularyNames?has_content>
			<ul class="treeview treeview-light treeview-nested treeview-vocabulary-display" role="tree">
				<#list vocabularyNames as vocabularyName>
					<@treeview_item
						cssClassTreeItem="tree-item-vocabulary"
						frequencyVisible=false
						id=vocabularyName + vocabularyName?index
						name="${(vocabularyNames?size == 1)?then('', htmlUtil.escape(vocabularyName))}"
						termDisplayContexts=assetCategoriesSearchFacetDisplayContext.getBucketDisplayContexts(vocabularyName)
					/>
				</#list>
			</ul>
		</#if>
	</@>
</@>

<@liferay_aui.script>

	window.onload = function() {
		if (!window.location.search.includes('category=')) {
			var defaultCheckbox = document.querySelector('.custom-checkbox input[type="checkbox"]');
			if (defaultCheckbox) {
				defaultCheckbox.checked = true;
			}
		}
	};

	function ${namespace}toggleTreeItem(dataTarget, id) {
		const dataTargetElements = document.querySelectorAll("[data-target=\"#" + dataTarget + "\"]");

		dataTargetElements.forEach(
			element => {
				if (element.classList.contains('collapsed')) {
					element.classList.remove('collapsed');
					element.setAttribute('aria-expanded', true);
				} else {
					element.classList.add('collapsed');
					element.setAttribute('aria-expanded', false);
				}
			}
		);

		const subtreeCategoryTreeElement = document.getElementById(dataTarget);

		if (subtreeCategoryTreeElement) {
			if (subtreeCategoryTreeElement.classList.contains('show')) {
				subtreeCategoryTreeElement.classList.remove('show');
			} else {
				subtreeCategoryTreeElement.classList.add('show');
			}
		}

		const checkboxes = document.querySelectorAll('.custom-checkbox input[type="checkbox"]');
		checkboxes.forEach(checkbox => {
			if (checkbox.id !== 'checkbox_' + id) {
				checkbox.checked = false;
			}
		});
	}
</@>