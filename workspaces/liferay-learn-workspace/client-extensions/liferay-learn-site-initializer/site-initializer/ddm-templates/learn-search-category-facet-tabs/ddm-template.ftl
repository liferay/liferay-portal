<#if entries?has_content>
	<#assign
		entryFrequency = 0
		knowledgeBaseFrequency = 0
		knowledgeBaseIds = []
		label = ""
		sortedTaxonomyCategories = []
		totalCount = 0
	/>

	<#list entries as entry>
		<#assign label = entry.bucketText?upper_case />

		<#if entry.isSelected()>
			<#assign
				entryFrequency = entry.getFrequency()
				label = entry.bucketText
			/>
		</#if>

		<#if stringUtil.equals(label, "HOW TO") || stringUtil.equals(label, "REFERENCE") || stringUtil.equals(label, "TROUBLESHOOTING")>
			<#assign
				knowledgeBaseFrequency += entry.getFrequency()
				knowledgeBaseIds += [entry.getFilterValue()]
			/>
		<#elseif stringUtil.equals(label, "OFFICIAL DOCUMENTATION")>
			<#assign sortedTaxonomyCategories = [entry] + sortedTaxonomyCategories />
		</#if>
	</#list>

	<#list assetCategoriesSearchFacetDisplayContext.getBucketDisplayContexts() as bucketDisplayContext>
		<#assign totalCount = totalCount + bucketDisplayContext.getCount() />
	</#list>

	<#assign
		selectedResourceTypeIds = paramUtil.getParameterValues(request, "resource-type")![]

		knowledgeBaseSelected = (selectedResourceTypeIds?filter(id -> knowledgeBaseIds?seq_contains(id))?size > 0)
	/>

	<#if knowledgeBaseSelected>
		<#assign
			entryFrequency = knowledgeBaseFrequency
			label = languageUtil.get(locale, "knowledge-base", "Knowledge Base")
		/>
	</#if>

	<#if selectedResourceTypeIds?size == 0>
		<#assign label = languageUtil.get(locale, "all-results", "All Results") />
	</#if>

	<div class="filter-toggle">
		<div class="filter-toggle-content">
			<div class="filter-toggle-term-text-term-count">
				<span class="term-text">${label} </span>

				<span class="term-count">
					<#if entryFrequency != 0>
						${entryFrequency}
					<#else>
						${totalCount}
					</#if>
				</span>
			</div>

			<div class="filter-toggle-arrow-icon">
			</div>
		</div>
	</div>

	<ul class="learn-category-facet-tabs list-unstyled tab-list" id="tab-list">
		<li class="facet-value">
			<@clay.button
				cssClass="btn-unstyled facet-clear tab-btn text-center ${assetCategoriesSearchFacetDisplayContext.isNothingSelected()?then('selected-tab-btn', '')}"
				displayType="link"
				onClick="${namespace}updateSelection(event)"
				value="clear"
			>
				<div class="facet-value-term-text-term-count">
					<span class="term-text">${languageUtil.get(locale, "all-results", "All Results")}</span>

					<#if totalCount?has_content>
						<span class="term-count">${totalCount}</span>
					</#if>
				</div>
			</@clay.button>
		</li>

		<#list sortedTaxonomyCategories as entry>
			<li class="facet-value">
				<@clay.button
					cssClass="btn-unstyled facet-term tab-btn term-name text-center ${(entry.isSelected())?then('selected-tab-btn', '')}"
					data\-term\-id="${entry.getFilterValue()}"
					disabled="true"
					displayType="link"
					onClick="${namespace}updateSelection(event)"
				>
					<div class="facet-value-term-text-term-count">
						<span class="term-text">
							${htmlUtil.escape(entry.getBucketText())}
						</span>

						<#if entry.isFrequencyVisible()>
							<span class="term-count">
								${entry.getFrequency()}
							</span>
						</#if>
					</div>
				</@clay.button>
			</li>
		</#list>

		<#list selectedResourceTypeIds as selectedId>
			<#if knowledgeBaseIds?seq_contains(selectedId)>
				<#assign knowledgeBaseSelected = true />
			</#if>
		</#list>

		<li class="facet-value">
			<@clay.button
				cssClass="btn-unstyled facet-term tab-btn term-name text-center ${knowledgeBaseSelected?then('selected-tab-btn', '')}"
				data\-term\-ids="${knowledgeBaseIds?join(',')}"
				displayType="link"
				onClick="${namespace}updateSelection(event)"
			>
				<div class="facet-value-term-text-term-count">
					<span class="term-text">${languageUtil.get(locale, "knowledge-base", "Knowledge Base")}</span>

					<#if knowledgeBaseFrequency?has_content>
						<span class="term-count">${knowledgeBaseFrequency}</span>
					</#if>
				</div>
			</@clay.button>
		</li>
	</ul>
</#if>

<@liferay_aui.script>
	document.querySelector(".filter-toggle").addEventListener("click", function() {
		document.querySelector(".learn-category-facet-tabs").classList.toggle("open");
	});

	function handleStyleTabs(event) {
		const buttons = document.querySelectorAll('.tab-btn');

		buttons.forEach(button => button.classList.remove('selected-tab-btn'));

		const targetButton = event.currentTarget;

		if (targetButton.classList.contains('tab-btn')) {
			targetButton.classList.add('selected-tab-btn');
		}
	}

	function ${namespace}updateSelection(event) {
		event.preventDefault();
		handleStyleTabs(event);

		const formElement = event.currentTarget.form;

		if (!formElement) {
			return;
		}

		const urlSearchParams = new URLSearchParams(window.location.search);

		if (event.currentTarget.value === 'clear') {
			urlSearchParams.delete('resource-type');

			const clearedUrl = window.location.pathname + '?' + urlSearchParams.toString();

			window.location.href = clearedUrl;

			return;
		}

		urlSearchParams.delete('resource-type');

		const dataTermId = event.currentTarget.getAttribute('data-term-id');
		const dataTermIds = event.currentTarget.getAttribute('data-term-ids');

		if (dataTermIds) {
			const resourceTypeIds = dataTermIds.split(',');

			resourceTypeIds.forEach(id => {
				urlSearchParams.append('resource-type', id.trim());
			});
		}
		else if (dataTermId) {
			urlSearchParams.append('resource-type', dataTermId);
		}

		window.location.href = window.location.pathname + '?' + urlSearchParams.toString();
	}
</@liferay_aui.script>

<style>
	.facet-value-term-text-term-count {
		width: 100%;
	}

	.filter-toggle {
		display: none;
	}

	.filter-toggle-arrow-icon::after {
		background-image: url("data:image/svg+xml,%3Csvg width='16' height='16' viewBox='0 0 16 16' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cmask id='mask0_820_11850' style='mask-type:alpha' maskUnits='userSpaceOnUse' x='3' y='5' width='10' height='6'%3E%3Cpath d='M3.21478 6.3781L7.48679 10.6499C7.76929 10.9324 8.23071 10.9324 8.51321 10.6499L12.7852 6.3781C13.2435 5.91985 12.9202 5.13831 12.2704 5.13831H3.72956C3.07981 5.13831 2.75651 5.91985 3.21478 6.3781Z' fill='%236B6C7E' /%3E%3C/mask%3E%3Cg mask='url(%23mask0_820_11850)'%3E%3Crect width='16' height='16' fill='%23999AA3' /%3E%3C/g%3E%3C/svg%3E");
		content: '';
		display: block;
		height: 16px;
		width: 16px;
	}

	.learn-category-facet-tabs {
		display: flex;
		flex-direction: row !important;
	}

	.learn-category-facet-tabs.open {
		display: flex;
	}

	.learn-category-facet-tabs .facet-term-unselected .term-text {
		opacity: 0.8;
	}

	.learn-category-facet-tabs .facet-value {
		flex: 1;
	}

	.learn-category-facet-tabs.tab-list {
		align-items: center;
		background: var(--Neutral-01, #f7f7f8);
		border-radius: 99px;
		display: flex;
		height: 52px;
		padding: 4px 6px;
	}

	.learn-category-facet-tabs .selected-tab-btn {
		background: var(--Action-Primary-Active-Lighten, #e6edfb);
		border-radius: 99px;
		opacity: 1;
		padding: 8px;
		text-align: center;
		width: 100%;
	}

	.term-count {
		background: var(--Status-Info-Info, #2e5aac);
		border-radius: 12px;
		color: var(--Neutral-00, #fff);
		font-size: 13px;
		padding: 2px 5px;
	}

	.term-text {
		color: var(--Neutral-10, #282934);
		font-size: 14px;
		font-style: normal;
		font-weight: 600;
	}

	@media screen and (max-width: 992px) {
		.facet-value {
			button {
				width: 100%;
			}
		}

		.filter-toggle {
			align-items: center;
			background: #f7f7f8;
			border-radius: 99px;
			display: flex;
			gap: 4px;
			justify-content: center;
			padding: 5px;
			width: 100%;

			.filter-toggle-content {
				align-items: center;
				border-radius: 99px;
				display: flex;
				padding: 8px;
				width: 100%;

				.filter-toggle-term-text-term-count {
					width: 100%;
				}

				&:hover,
				&:active,
				&:focus {
					background: #e6edfb;
				}
			}
		}

		.learn-category-facet-tabs {
			.facet-value {
				border-radius: 6px;
				padding: 8px;
				text-align-last: start;
				width: 100%;

				&:hover {
					background: #edf3fe;
				}
			}

			.selected-tab-btn {
				align-items: center;
				background: unset;
				display: flex;
				justify-content: space-between;
				padding: unset;

				&:after {
					background-image: url("data:image/svg+xml,%3Csvg width='16' height='16' viewBox='0 0 16 16' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cmask id='mask0_820_175' style='mask-type:alpha' maskUnits='userSpaceOnUse' x='1' y='2' width='14' height='12'%3E%3Cpath d='M6.02807 13.4237C5.76869 13.4237 5.51557 13.3205 5.33119 13.1362L1.39057 9.19242C0.468691 8.2268 1.95932 6.9393 2.78432 7.79867L5.98432 11.0018L13.1718 2.91117C14.0218 1.95805 15.4937 3.27055 14.6437 4.22055L6.76244 13.0924C6.58119 13.2955 6.32494 13.4143 6.05619 13.4237C6.04682 13.4237 6.03744 13.4237 6.02807 13.4237Z' fill='%236B6C7E'/%3E%3C/mask%3E%3Cg mask='url(%23mask0_820_175)'%3E%3Crect width='16' height='16' fill='%2354555F'/%3E%3C/g%3E%3C/svg%3E%0A");
					content: '';
					display: block;
					height: 16px;
					width: 16px;
				}
			}

			&.tab-list {
				align-items: start;
				background: #ffff;
				border-radius: 10px;
				box-shadow: 0 5px 15px 0 rgba(19, 20, 31, 0.12);
				display: none;
				flex-direction: column;
				height: 100%;
				padding: 8px;
			}
		}

		.learn-category-facet-tabs.open {
			display: flex;
			flex-direction: column !important;
		}

		.learn-category-facet-tabs:not(.open) {
			display: none;
		}
	}
</style>