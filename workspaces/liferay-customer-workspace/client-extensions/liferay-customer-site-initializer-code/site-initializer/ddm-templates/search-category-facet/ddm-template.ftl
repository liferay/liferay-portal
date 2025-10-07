<#function hasTaxonomyProperty category key value>
	<#if category.taxonomyCategoryProperties?has_content>
		<#list category.taxonomyCategoryProperties as prop>
			<#if (prop.key == key) && (prop.value == value)>
				<#return true />
			</#if>
		</#list>
	</#if>

	<#return false />
</#function>

<#macro panel_item
	categories
	contextMap
	parentId
	title
>
	<#assign displayableCategories = [] />

	<#list categories.items as item>
		<#if hasTaxonomyProperty(item, "visibleInSearch", "false")>
			<#continue>
		</#if>

		<#assign
			context = (contextMap[item.id?string])!{}
			isFrequencyVisible = (context.isFrequencyVisible)!false
			isSelected = (context.selected)!false
		/>

		<#if !isFrequencyVisible && !isSelected>
			<#continue>
		</#if>

		<#assign displayableCategories = displayableCategories + [item] />
	</#list>

	<#assign
		displayableCategories = displayableCategories?sort_by("name")
		panelId = stringUtil.replace(title, ' ', '')
	/>

	<@liferay_ui["panel-container"]
		extended=true
		id="${namespace}_${panelId}_facetAssetCategoriesPanelContainer"
		markupView="lexicon"
		persistState=true
	>
		<@liferay_ui.panel
			collapsible=true
			cssClass="bg-brand-primary-lighten-5 mb-4 p-3 rounded search-facet search-facet-display-vocabulary"
			id="${namespace}_${panelId}_facetAssetCategoriesPanel"
			markupView="lexicon"
			persistState=true
			title="${title}"
		>
			<div class="action-buttons">
				<@clay.button
					cssClass="btn-unstyled facet-clear-btn mb-3 mr-1 text-body text-decoration-none"
					displayType="link"
					id="${namespace}_${panelId}_facetAssetCategoriesSelectAll"
					onClick="${namespace}selectAll(event)"
				>
					<span>${languageUtil.get(locale, "select-all")}</span>
				</@clay.button>

				<@clay.button
					cssClass="btn-unstyled facet-clear-btn mb-3 text-body text-decoration-none"
					displayType="link"
					id="${namespace}_${panelId}_facetAssetCategoriesClear"
					onClick="${namespace}clearSelections(event)"
				>
					<span>${languageUtil.get(locale, "clear")}</span>
				</@clay.button>
			</div>

			<div class="collapse show" id="${namespace}_${panelId}_categoryItem">
				<#if displayableCategories?size gt 8>
					<input
						class="form-control mb-3 pb-2 pl-3 pr-3 pt-2"
						id="${namespace}_${panelId}_search"
						onInput="${namespace}searchCategories(event)"
						placeholder='${languageUtil.get(locale, "search")}'
						type="text"
					/>
				</#if>

				<ul class="m-0 p-0">
					<#list displayableCategories as item>
						<#assign
							context = (contextMap[item.id?string])!{}
							isSelected = (context.selected)!false
						/>

						<li class="m-0 category-item <#if item_index gte 8>d-none</#if>">
							<span class="autofit-row">
								<span class="autofit-col autofit-col-expand">
									<label class="align-items-center d-flex font-weight-normal" style="cursor: pointer;">
										<input
											autocomplete="off"
											${isSelected?then("checked", "")}
											class="facet-term mr-1"
											data-parent-id="${parentId}"
											data-term-id="${item.id}"
											data-term-name="${item.name}"
											data-term-param="category"
											data-term-value="${item.id}"
											onChange="${namespace}handleSelection(event);"
											type="checkbox"
										/>

										<span>
											${item.name}
										</span>
									</label>
								</span>
							</span>
						</li>
					</#list>
				</ul>

				<#if displayableCategories?size gt 8>
					<@clay.button
						cssClass="btn-unstyled facet-clear-btn view-all-btn mt-3 text-body text-decoration-none"
						displayType="link"
						id="${namespace}_${panelId}_facetAssetCategoriesViewAll"
						onClick="${namespace}viewAll('${namespace}_${panelId}_categoryItem', event)"
					>
						<span>${languageUtil.get(locale, "view-all")}</span>
					</@clay.button>
				</#if>
			</div>
		</@>
	</@>

	<#list categories.items as item>
		<#if hasTaxonomyProperty(item, "visible", "false")>
			<#continue>
		</#if>

		<#if item.numberOfTaxonomyCategories gt 0>
			<#assign isSelected = ((contextMap[item.id?string])!{}).selected!false />

			<#if isSelected>
				<#if hasTaxonomyProperty(item, "skipNextLevel", "true")>
					<#assign childCategories = restClient.get("/headless-admin-taxonomy/v1.0/taxonomy-categories/${item.id}/taxonomy-categories?sort=name:asc") />

					<#if childCategories.items?has_content>
						<#list childCategories.items as childCategory>
							<#if hasTaxonomyProperty(childCategory, "visible", "false")>
								<#continue>
							</#if>

							<input
								checked
								class="facet-term d-none"
								data-parent-id="${item.id}"
								data-term-id="${childCategory.id}"
								onChange="${namespace}handleSelection(event);"
								type="checkbox"
							/>

							<#assign grandChildCategories = restClient.get("/headless-admin-taxonomy/v1.0/taxonomy-categories/${childCategory.id}/taxonomy-categories?sort=name:asc") />

							<@panel_item
								categories = grandChildCategories
								contextMap = contextMap
								parentId = childCategory.id
								title = childCategory.name
							/>
						</#list>
					</#if>
				<#else>
					<#assign childCategories = restClient.get("/headless-admin-taxonomy/v1.0/taxonomy-categories/${item.id}/taxonomy-categories?sort=name:asc") />

					<@panel_item
						categories = childCategories
						contextMap = contextMap
						parentId = item.id
						title = item.name
					/>
				</#if>
			</#if>
		</#if>
	</#list>
</#macro>

<#assign contextMap = {} />

<#list assetCategoriesSearchFacetDisplayContext.getBucketDisplayContexts() as bucketDisplayContext>
	<#assign contextMap = contextMap + {
		(bucketDisplayContext.getFilterValue()): {
			"isFrequencyVisible": bucketDisplayContext.isFrequencyVisible(),
			"selected": false
		}
	} />
</#list>

<#list assetCategoriesSearchFacetDisplayContext.getParameterValues() as categoryId>
	<#attempt>
		<#assign category = restClient.get("/headless-admin-taxonomy/v1.0/taxonomy-categories/${categoryId}?sort=name:asc") />

		<#if category?has_content>
			<#assign currentCategory = category />

			<#list 0..9 as _>
				<#if currentCategory?has_content>
					<#assign
						currentCategoryId = currentCategory.id?string
						existingData = (contextMap[currentCategoryId])!{}

						contextMap = contextMap + {
							(currentCategoryId): {
								"isFrequencyVisible": (existingData.isFrequencyVisible)!false,
								"selected": true
							}
						}
					/>

					<#if currentCategory.parentTaxonomyCategory?has_content && (currentCategory.parentTaxonomyCategory.id?string) != "0">
						<#assign
							parentId = currentCategory.parentTaxonomyCategory.id
							currentCategory = restClient.get("/headless-admin-taxonomy/v1.0/taxonomy-categories/${parentId}?sort=name:asc")
						/>
					<#else>
						<#break />
					</#if>
				<#else>
					<#break />
				</#if>
			</#list>
		</#if>
	<#recover>
	</#attempt>
</#list>

<#assign vocabularyNames = assetCategoriesSearchFacetDisplayContext.getVocabularyNames()![] />

<#if vocabularyNames?has_content>
	<#list vocabularyNames as vocabularyName>
		<#assign
			assetCategoryId = assetCategoriesSearchFacetDisplayContext.getBucketDisplayContexts(vocabularyName)[0].getAssetCategoryId()

			category = restClient.get("/headless-admin-taxonomy/v1.0/taxonomy-categories/${assetCategoryId}")
			categories = restClient.get("/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/${category.parentTaxonomyVocabulary.id}/taxonomy-categories?sort=name:asc")
		/>

		<#if categories?has_content>
			<@panel_item
				categories = categories
				contextMap = contextMap
				parentId = 0
				title = languageUtil.get(locale, "type")
			/>
		</#if>
	</#list>
</#if>

<@liferay_aui.script>
	function ${namespace}clearSelections(event) {
		var panel = event.target.closest('.search-facet-display-vocabulary');

		if (panel) {
			var checkboxes = panel.querySelectorAll(
				'input[type="checkbox"]:checked'
			);

			checkboxes.forEach((checkbox) => {
				checkbox.checked = false;
				var changeEvent = new Event('change', {
					bubbles: true,
				});
				checkbox.dispatchEvent(changeEvent);
			});
		}
	}

	function ${namespace}deselectChildren(termId) {
		const children = document.querySelectorAll(
			'input.facet-term[data-parent-id="' + termId + '"]:checked'
		);

		children.forEach((child) => {
			child.checked = false;

			const childTermId = child.getAttribute('data-term-id');

			${namespace}deselectChildren(childTermId);
		});
	}

	function ${namespace}deselectParents(parentId) {
		if (!parentId || parentId === '0') {
			return;
		}

		const parentCheckbox = document.querySelector(
			'input.facet-term[data-term-id="' + parentId + '"]:checked'
		);

		if (parentCheckbox) {
			parentCheckbox.checked = false;

			const grandParentId = parentCheckbox.getAttribute('data-parent-id');

			${namespace}deselectParents(grandParentId);
		}
	}

	function ${namespace}handleSelection(event) {
		event.preventDefault();

		const checkbox = event.target;

		if (checkbox.checked) {
			const parentId = checkbox.getAttribute('data-parent-id');

			${namespace}deselectParents(parentId);
		}
		else {
			const termId = checkbox.getAttribute('data-term-id');

			${namespace}deselectChildren(termId);
		}

		Liferay.Search.FacetUtil.changeSelection(event);
	}

	function ${namespace}searchCategories(event) {
		var searchInput = event.target;
		var searchTerm = searchInput.value.toLowerCase();
		var panel = searchInput.closest('.search-facet-display-vocabulary');

		if (panel) {
			var categoryItems = panel.querySelectorAll('.category-item');
			var viewAllButton = panel.querySelector('.view-all-btn');

			if (viewAllButton) {
				viewAllButton.style.display = 'none';
			}

			categoryItems.forEach(function(item) {
				var label = item.querySelector('label');
				var categoryName = label.textContent || label.innerText;

				if (categoryName.toLowerCase().indexOf(searchTerm) > -1) {
					item.classList.remove('d-none');
					item.style.display = '';
				} else {
					item.style.display = 'none';
				}
			});
		}
	}

	function ${namespace}selectAll(event) {
		var panel = event.target.closest('.search-facet-display-vocabulary');

		if (panel) {
			var checkboxes = panel.querySelectorAll(
				'input[type="checkbox"]'
			);

			checkboxes.forEach((checkbox) => {
				if (!checkbox.checked) {
					checkbox.checked = true;
					var changeEvent = new Event('change', {
						bubbles: true,
					});
					checkbox.dispatchEvent(changeEvent);
				}
			});
		}
	}

	function ${namespace}viewAll(dataTarget, event) {
		const categoryElement = document.getElementById(dataTarget);

		if (categoryElement) {
			const hiddenItems = categoryElement.querySelectorAll('.d-none');

			hiddenItems.forEach((item) => {
				item.classList.remove('d-none');
			});

			if (event && event.target) {
				const viewAllButton = event.target.closest('.view-all-btn');

				if (viewAllButton) {
					viewAllButton.style.display = 'none';
				}
			}
		}
	}
</@>

<style>
	input[type=checkbox] {
		accent-color: var(--color-brand-primary)
	}

	.panel ul {
		list-style: none;
	}

	.panel-body, .panel-header {
		padding: 0;
	}

	.panel-header .collapse-icon-closed, .panel-header .collapse-icon-open {
		top: var(--spacer-1);
	}

	.panel-title {
		font-size: var(--h5-font-size, 0.875rem);
		font-weight: var(--h5-font-weight);
		text-transform: none;
	}
</style>