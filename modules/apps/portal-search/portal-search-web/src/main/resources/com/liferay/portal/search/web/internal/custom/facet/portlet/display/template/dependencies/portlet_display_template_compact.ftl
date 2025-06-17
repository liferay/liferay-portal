<@liferay_ui["panel-container"]
	extended=true
	id="${namespace + 'facetCustomPanelContainer'}"
	markupView="lexicon"
	persistState=true
>
	<@liferay_ui.panel
		collapsible=true
		cssClass="search-facet"
		id="${namespace + 'facetCustomPanel'}"
		markupView="lexicon"
		persistState=true
		title="${customFacetDisplayContext.getDisplayCaption()}"
	>
		<#if !customFacetDisplayContext.isNothingSelected()>
			<@clay.button
				cssClass="btn-unstyled c-mb-4 facet-clear-btn"
				displayType="link"
				id="${namespace + 'facetCustomClear'}"
				onClick="Liferay.Search.FacetUtil.clearSelections(event);"
			>
				<strong>${languageUtil.get(locale, "clear")}</strong>

				<span class="sr-only">
					${languageUtil.format(locale, 'x-filter', 'custom-facet-portlet-instance-configuration-name')}
				</span>
			</@clay.button>
		</#if>

		<ul class="list-unstyled">
			<#if entries?has_content>
				<#list entries as entry>
					<li>
						<@clay.button
							cssClass="facet-term btn-unstyled ${(entry.isSelected())?then('facet-term-selected', 'facet-term-unselected')} term-name"
							data\-term\-id="${entry.getBucketText()}"
							disabled="true"
							displayType="link"
							onClick="Liferay.Search.FacetUtil.changeSelection(event);"
						>
							<#if entry.isSelected()>
								<strong><@liferay_ui["message"] key="${htmlUtil.escape(entry.getBucketText())}" /></strong>
							<#else>
								<@liferay_ui["message"] key="${htmlUtil.escape(entry.getBucketText())}" />
							</#if>

							<#if entry.isFrequencyVisible()>
								<small class="term-count">
									(${entry.getFrequency()})
								</small>
							</#if>
						</@clay.button>
					</li>
				</#list>
			</#if>

			<#if customFacetDisplayContext.isShowInputRange()>
				<#if (customFacetDisplayContext.getAggregationType() == "range") || (customFacetDisplayContext.getAggregationType() == "dateRange")>
					<li class="facet-value">
						<@clay.button
							cssClass="facet-term btn-unstyled ${(customRangeBucketDisplayContext.isSelected())?then('facet-term-selected', 'facet-term-unselected')} term-name"
							data\-term\-id="${htmlUtil.escape(customRangeBucketDisplayContext.getBucketText())}"
							disabled="true"
							displayType="link"
							id="${namespace}${customRangeBucketDisplayContext.getBucketText()}"
							name="${namespace}${customRangeBucketDisplayContext.getBucketText()}"
						>
							<#if customRangeBucketDisplayContext.isSelected()>
								<strong><@liferay_ui["message"] key="${htmlUtil.escape(customRangeBucketDisplayContext.getBucketText())}" /></strong>
							<#else>
								<@liferay_ui["message"] key="${htmlUtil.escape(customRangeBucketDisplayContext.getBucketText())}" />
							</#if>

							<#if customRangeBucketDisplayContext.isSelected()>
								<small class="term-count">
									(${customRangeBucketDisplayContext.getFrequency()})
								</small>
							</#if>
						</@clay.button>

						<@liferay_aui.script>
							document.getElementById('${namespace}${customRangeBucketDisplayContext.getBucketText()}').onclick = function(event) {
								if ("${customFacetDisplayContext.getAggregationType()}" == "dateRange") {
									Liferay.Search.FacetUtil.changeSelection(event);
								}

								if ("${customFacetDisplayContext.getAggregationType()}" == "range") {
									event.preventDefault();

									const customRangeElement = document.getElementById('${namespace}customRange');

									if (customRangeElement && customRangeElement.classList.contains('hide')) {
										customRangeElement.classList.remove('hide');
									}
									else if (Liferay.Search.FacetUtil.isCustomRangeValid(event)) {
										Liferay.Search.FacetUtil.changeSelection(event);
									}
								}
							}
						</@liferay_aui.script>
					</li>
				</#if>

				<#if customFacetDisplayContext.getAggregationType() == "range">
					<div class="${(!customRangeBucketDisplayContext.isSelected())?then("hide", "")} date-custom-range" id="${namespace}customRange">
						<div class="col-md-6" id="${namespace}customRangeFrom">
							<@liferay_aui["field-wrapper"]>
								<@liferay_aui.input
									label="from"
									id="fromInput"
									name="fromInput"
									type="number"
									value=customFacetDisplayContext.getFromParameterValue()
								/>
							</@>
						</div>

						<div class="col-md-6" id="${namespace}customRangeTo">
							<@liferay_aui["field-wrapper"]>
								<@liferay_aui.input
									label="to"
									id="toInput"
									name="toInput"
									type="number"
									value=customFacetDisplayContext.getToParameterValue()
								/>
							</@>
						</div>

						<@clay["button"]
							cssClass="custom-range-filter-button"
							disabled=!customFacetDisplayContext.getToParameterValue()?? || !customFacetDisplayContext.getFromParameterValue()?? || customFacetDisplayContext.getToParameterValue()?number < customFacetDisplayContext.getFromParameterValue()?number
							displayType="secondary"
							id="${namespace + 'searchCustomRangeButton'}"
							label="search"
							name="${namespace + 'searchCustomRangeButton'}"
						/>
					</div>
				</#if>

				<#if customFacetDisplayContext.getAggregationType() == "dateRange">
					<div class="${(!customFacetCalendarDisplayContext.isSelected())?then("hide", "")} date-custom-range" id="${namespace}customRange">
						<div class="col-md-6" id="${namespace}customRangeFrom">
							<@liferay_aui["field-wrapper"]
								label="from"
								name="fromInput"
							>
								<@liferay_ui["input-date"]
									cssClass="date-facet-custom-range-input-date-from"
									dayParam="fromDay"
									dayValue=customFacetCalendarDisplayContext.getFromDayValue()
									disabled=false
									firstDayOfWeek=customFacetCalendarDisplayContext.getFromFirstDayOfWeek()
									monthParam="fromMonth"
									monthValue=customFacetCalendarDisplayContext.getFromMonthValue()
									name="fromInput"
									yearParam="fromYear"
									yearValue=customFacetCalendarDisplayContext.getFromYearValue()
								/>
							</@>
						</div>

						<div class="col-md-6" id="${namespace}customRangeTo">
							<@liferay_aui["field-wrapper"]
								label="to"
								name="toInput"
							>
								<@liferay_ui["input-date"]
									cssClass="date-facet-custom-range-input-date-to"
									dayParam="toDay"
									dayValue=customFacetCalendarDisplayContext.getToDayValue()
									disabled=false
									firstDayOfWeek=customFacetCalendarDisplayContext.getToFirstDayOfWeek()
									monthParam="toMonth"
									monthValue=customFacetCalendarDisplayContext.getToMonthValue()
									name="toInput"
									yearParam="toYear"
									yearValue=customFacetCalendarDisplayContext.getToYearValue()
								/>
							</@>
						</div>

						<@clay["button"]
							cssClass="date-facet-custom-range-filter-button"
							disabled=customFacetCalendarDisplayContext.isRangeBackwards()
							displayType="secondary"
							id="${namespace + 'searchCustomRangeButton'}"
							label="search"
							name="${namespace + 'searchCustomRangeButton'}"
						/>
					</div>
				</#if>
			</#if>
		</ul>
	</@>
</@>