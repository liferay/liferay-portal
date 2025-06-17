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
					<li class="facet-value">
						<div class="custom-control custom-radio">
							<label class="facet-checkbox-label" for="${namespace}${entry.getBucketText()}">
								<input
									${(entry.isSelected())?then("checked", "")}
									class="custom-control-input facet-term"
									disabled
									id="${namespace}${entry.getBucketText()}"
									name="${namespace}${entry.getBucketText()}"
									role="radio"
									type="radio"
								/>

								<@liferay_aui.script>
									document.getElementById('${namespace}${entry.getBucketText()}').onchange = function() {
										window.location.href = "${entry.getFilterValue()}";
									}
								</@liferay_aui.script>

								<span class="custom-control-label term-name ${(entry.isSelected())?then('facet-term-selected', 'facet-term-unselected')}">
									<span class="custom-control-label-text">
										<#if entry.isSelected()>
											<strong><@liferay_ui["message"] key="${htmlUtil.escape(entry.getBucketText())}" /></strong>
										<#else>
											<@liferay_ui["message"] key="${htmlUtil.escape(entry.getBucketText())}" />
										</#if>
									</span>
								</span>

								<#if entry.isFrequencyVisible()>
									<small class="term-count">
										(${entry.getFrequency()})
									</small>
								</#if>
							</label>
						</div>
					</li>
				</#list>
			</#if>

			<#if customFacetDisplayContext.isShowInputRange()>
				<#if (customFacetDisplayContext.getAggregationType() == "range") || (customFacetDisplayContext.getAggregationType() == "dateRange")>
					<li class="facet-value">
						<div class="custom-control custom-radio">
							<label class="facet-checkbox-label" for="${namespace}${customRangeBucketDisplayContext.getBucketText()}">
								<input
									${(customRangeBucketDisplayContext.isSelected())?then("checked", "")}
									data-term-id="${htmlUtil.escape(customRangeBucketDisplayContext.getBucketText())}"
									class="custom-control-input facet-term"
									disabled
									id="${namespace}${customRangeBucketDisplayContext.getBucketText()}"
									name="${namespace}${customRangeBucketDisplayContext.getBucketText()}"
									role="radio"
									type="radio"
								/>

								<@liferay_aui.script>
									document.getElementById('${namespace}${customRangeBucketDisplayContext.getBucketText()}').onclick = function(event) {
										if ("${customFacetDisplayContext.getAggregationType()}" == "dateRange") {
											window.location.href = "${customRangeBucketDisplayContext.getFilterValue()}";
										}

										if ("${customFacetDisplayContext.getAggregationType()}" == "range") {
											event.preventDefault();

											const customRangeElement = document.getElementById('${namespace}customRange');

											if (customRangeElement && customRangeElement.classList.contains('hide')) {
												customRangeElement.classList.remove('hide');
											}
											else if (Liferay.Search.FacetUtil.isCustomRangeValid(event)) {
												Liferay.Search.FacetUtil.changeSingleSelection(event);
											}
										}
									}
								</@liferay_aui.script>

								<span class="custom-control-label term-name ${(customRangeBucketDisplayContext.isSelected())?then('facet-term-selected', 'facet-term-unselected')}">
									<span class="custom-control-label-text">
										<#if customRangeBucketDisplayContext.isSelected()>
											<strong><@liferay_ui["message"] key="${htmlUtil.escape(customRangeBucketDisplayContext.getBucketText())}" /></strong>
										<#else>
											<@liferay_ui["message"] key="${htmlUtil.escape(customRangeBucketDisplayContext.getBucketText())}" />
										</#if>
									</span>
								</span>

								<#if customRangeBucketDisplayContext.isSelected()>
									<small class="term-count">
										(${customRangeBucketDisplayContext.getFrequency()})
									</small>
								</#if>
							</label>
						</div>
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