<style ${nonceAttribute} type="text/css">
	.tag-cloud .facet-term {
		line-height: 1.2;
	}
</style>

<@liferay_ui["panel-container"]
	extended=true
	id="${namespace + 'facetAssetCategoriesPanelContainer'}"
	markupView="lexicon"
	persistState=true
>
	<@liferay_ui.panel
		collapsible=true
		cssClass="search-facet"
		id="${namespace + 'facetAssetCategoriesPanel'}"
		markupView="lexicon"
		persistState=true
		title="category"
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

		<ul class="list-unstyled tag-cloud">
			<#if entries?has_content>
				<#list entries as entry>
					<span class="facet-value">
						<@clay.button
							cssClass="btn-unstyled facet-term ${(entry.isSelected())?then('facet-term-selected', 'facet-term-unselected')} tag-popularity-${entry.getPopularity()} term-name"
							data\-term\-id="${entry.getFilterValue()}"
							disabled="true"
							displayType="link"
							onClick="Liferay.Search.FacetUtil.changeSelection(event);"
						>
							<#if entry.isSelected()>
								<strong>${htmlUtil.escape(entry.getBucketText())}</strong>
							<#else>
								${htmlUtil.escape(entry.getBucketText())}
							</#if>
						</@clay.button>
					</span>
				</#list>
			</#if>
		</ul>
	</@>
</@>