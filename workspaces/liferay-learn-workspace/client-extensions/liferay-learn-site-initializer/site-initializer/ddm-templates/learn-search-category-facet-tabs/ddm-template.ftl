<#if entries?has_content>
	<#assign
		sortedTaxonomyCategories = []
		taxonomyVocabularyId = restClient.get("/headless-admin-taxonomy/v1.0/sites/${themeDisplay.getCompanyGroupId()}/taxonomy-vocabularies/by-external-reference-code/RESOURCE_TYPE").id
		totalCount = 0
		validTaxonomyCategoryIds = []

		taxonomyCategories = restClient.get("/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/${taxonomyVocabularyId}/taxonomy-categories").items
	/>

	<#list taxonomyCategories as taxonomyCategory>
		<#if stringUtil.equals(taxonomyCategory.externalReferenceCode, "HOW_TO") || stringUtil.equals(taxonomyCategory.externalReferenceCode, "OFFICIAL_DOCUMENTATION")>
			<#assign validTaxonomyCategoryIds += [taxonomyCategory.id] />
		</#if>
	</#list>

	<#list assetCategoriesSearchFacetDisplayContext.getBucketDisplayContexts() as bucketDisplayContext>
		<#assign totalCount = totalCount + bucketDisplayContext.getCount() />
	</#list>

	<ul class="learn-category-facet-tabs list-unstyled tab-list" id="tab-list">
		<li class="facet-value">
			<@clay.button
				cssClass="btn-unstyled facet-clear tab-btn text-center ${assetCategoriesSearchFacetDisplayContext.isNothingSelected()?then('selected-tab-btn', '')}"
				displayType="link"
				onClick="${namespace}updateSelection(event)"
				value="clear"
			>
				<span class="term-text">${languageUtil.get(locale, "all-results", "All Results")}</span>

				<#if entry.isFrequencyVisible()>
					<span class="term-count">${totalCount}</span>
				</#if>
			</@clay.button>
		</li>

		<#list entries as entry>
			<#assign taxonomyCategoryId = entry.getFilterValue() />

			<#list taxonomyCategories as taxonomyCategory>
				<#if taxonomyCategory.id == taxonomyCategoryId>
					<#if stringUtil.equals(taxonomyCategory.externalReferenceCode, "OFFICIAL_DOCUMENTATION")>
						<#assign sortedTaxonomyCategories = [entry] + sortedTaxonomyCategories />
					<#elseif stringUtil.equals(taxonomyCategory.externalReferenceCode, "HOW_TO")>
						<#assign sortedTaxonomyCategories += [entry] />
					</#if>
				</#if>
			</#list>
		</#list>

		<#list sortedTaxonomyCategories as entry>
			<li class="facet-value">
				<@clay.button
					cssClass="btn-unstyled facet-term tab-btn term-name text-center ${(entry.isSelected())?then('selected-tab-btn', '')}"
					data\-term\-id="${entry.getFilterValue()}"
					disabled="true"
					displayType="link"
					onClick="${namespace}updateSelection(event)"
				>
					<span class="term-text">
						${htmlUtil.escape(entry.getBucketText())}
					</span>

					<#if entry.isFrequencyVisible()>
						<span class="term-count">
							${entry.getFrequency()}
						</span>
					</#if>
				</@clay.button>
			</li>
		</#list>
	</ul>

	<div class="dropdown learn-category-facet-tabs tab-list" id="tab-list-mobile">
		<button
			aria-expanded="false"
			aria-haspopup="true"
			class="btn btn-unstyled d-inline-block selected-tab-btn"
			data-toggle="liferay-dropdown"
			displayType="button"
			id="dropdownAlignment1"
		>
			<div class="d-flex facet-value-mobile justify-content-center opacity-75">
				<#assign facetCount = 0 />
				<#list entries as entry>
					<#if entry.isSelected()>
						<#assign facetCount++ />

						<span class="term-text">${entry.getBucketText()}</span>
						<#if entry.isFrequencyVisible()>
							<span class="term-count">${entry.getFrequency()}</span>
						</#if>
					</#if>
				</#list>
				<#if facetCount == 0>
					<span class="term-text">${languageUtil.get(locale, "all-results", "All Results")}</span>
					<span class="term-count">${totalCount}</span>
				</#if>
			</div>
		</button>

		<ul
			aria-labelledby="dropdownAlignment1"
			class="dropdown-menu"
			x-placement="bottom-start"
		>
			<li class="align-items-center d-flex position-relative ${assetCategoriesSearchFacetDisplayContext.isNothingSelected()?then('selected-item-mobile-tab', '')}">
				<@clay.button
					cssClass="dropdown-item facet-clear nav-link rounded"
					displayType="link"
					onClick="${namespace}updateSelection(event)"
					value="clear"
				>
					<span class="term-text">${languageUtil.get(locale, "all-results", "All Results")}</span>
					<#if entry.isFrequencyVisible()>
						<span class="term-count">${totalCount}</span>
					</#if>
				</@clay.button>
			</li>

			<#list entries as entry>
				<li class="align-items-center d-flex ${(entry.isSelected())?then('selected-item-mobile-tab', '')}">
					<@clay.button
						cssClass="dropdown-item facet-clear nav-link rounded"
						data\-term\-id="${entry.getFilterValue()}"
						displayType="link"
						onClick="${namespace}updateSelection(event)"
					>
						<span class="term-text">${htmlUtil.escape(entry.getBucketText())}</span>
						<#if entry.isFrequencyVisible()>
							<span class="term-count">${entry.getFrequency()}</span>
						</#if>
					</@clay.button>
				</li>
			</#list>
		</ul>
	</div>
</#if>

<@liferay_aui.script>
	function handleStyleTabs(event) {
		const targetButton = event.currentTarget;
		const buttons = document.querySelectorAll('.tab-btn');

		buttons.forEach(button => {
			button.classList.remove('selected-tab-btn');
		});

		if (targetButton.classList.contains('tab-btn')) {
			targetButton.classList.add('selected-tab-btn');
		}
	}

	function ${namespace}updateSelection(event) {
		handleStyleTabs(event);

		const form = event.currentTarget.form;

		if (form) {
			Liferay.Search.FacetUtil.selectTerms(form, []);

			if (event.target.value === "clear") {
				Liferay.Search.FacetUtil.clearSelections(event);
			}
			else {
				Liferay.Search.FacetUtil.changeSelection(event);
			}
		}
	}
</@>

<style>
	.learn-category-facet-tabs .facet-term-unselected .term-text {
		opacity: 0.8;
	}

	.learn-category-facet-tabs .facet-value {
		flex:1;
	}

	.learn-category-facet-tabs.tab-list {
		align-items:center;
		display: flex;
		background: var(--Neutral-01, #F7F7F8);
		border-radius: 99px;
		height: 52px;
		padding: 4px 6px;
	}

	.learn-category-facet-tabs .selected-tab-btn {
		background: var(--Action-Primary-Active-Lighten, #E6EDFB);
		border-radius: 99px;
		opacity: 1;
		padding: 8px;
		text-align: center;
		width: 100%;
	}

	.learn-category-facet-tabs .term-count {
		background: var(--Status-Info-Info, #2E5AAC);
		border-radius: 12px;
		color: var(--Neutral-00, #FFF);
		font-size: 13px;
		padding: 2px 5px;
	}

	.learn-category-facet-tabs .term-text {
		color: var(--Neutral-10, #282934);
		font-size: 14px;
		font-style: normal;
		font-weight: 600;
	}

	.selected-item-mobile-tab::after {
		background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='15' height='15' fill='currentColor' class='lexicon-icon lexicon-icon-check' role='presentation' viewBox='0 0 512 512'%3E%3Cpath d='M192.9,429.5c-8.3,0-16.4-3.3-22.3-9.2L44.5,294.1C15,263.2,62.7,222,89.1,249.5L191.5,352l230-258.9 c27.2-30.5,74.3,11.5,47.1,41.9L216.4,418.9c-5.8,6.5-14,10.3-22.6,10.6C193.5,429.5,193.2,429.5,192.9,429.5z'%3E%3C/path%3E%3C/svg%3E");
		background-repeat: no-repeat;
		background-size: contain;
		content: "";
		height: 15px;
		position: absolute;
		right: 1rem;
		top: 50%;
		transform: translateY(-50%);
		width: 15px;
	}

	@media screen and (max-width: 992px) {
		.learn-category-facet-tabs .facet-value-mobile {
			gap: var(--spacer-2, 0.5rem);
		}

		.learn-category-facet-tabs .facet-value-mobile .term-text {
			opacity: 0.80;
		}

		.learn-category-facet-tabs .dropdown-menu,
		.learn-category-facet-tabs#tab-list-mobile {
			max-width: none;
			padding: var(--spacer-2, 0.5rem);
			width: 100%;
		}

		.learn-category-facet-tabs#tab-list {
			display: none !important;
		}

		.learn-category-facet-tabs#tab-list-mobile {
			align-items: center;
			display: flex !important;
			width: 100%;
		}
	}

	#tab-list-mobile {
		display: none;
	}

	#tab-list-mobile::after {
		background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 512 512'%3E%3Cpath fill='%23999AA3' d='M103.5 204.3l136.1 136.1c9 9 23.7 9 32.7 0l136.1-136.1c14.6-14.6 4.3-39.5-16.4-39.5H119.9C99.2 164.8 88.9 189.7 103.5 204.3z'/%3E%3C/svg%3E");
		background-repeat: no-repeat;
		background-size: contain;
		content: "";
		height: 15px;
		position: absolute;
		right: 1rem;
		top: 50%;
		transform: translateY(-50%);
		width: 15px;
	}
</style>