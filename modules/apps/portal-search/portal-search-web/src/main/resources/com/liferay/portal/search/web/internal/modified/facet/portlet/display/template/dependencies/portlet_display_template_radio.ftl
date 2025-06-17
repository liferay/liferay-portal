<@liferay_ui["panel-container"]
	extended=true
	id="${namespace + 'facetModifiedPanelContainer'}"
	markupView="lexicon"
	persistState=true
>
	<@liferay_ui.panel
		collapsible=true
		cssClass="search-facet"
		id="${namespace + 'facetModifiedPanel'}"
		markupView="lexicon"
		persistState=true
		title="last-modified"
	>
		<#if !modifiedFacetDisplayContext.isNothingSelected()>
			<@clay.button
				cssClass="btn-unstyled c-mb-4 facet-clear-btn"
				displayType="link"
				id="${namespace + 'facetModifiedClear'}"
				onClick="Liferay.Search.FacetUtil.clearSelections(event);"
			>
				<strong>${languageUtil.get(locale, "clear")}</strong>

				<span class="sr-only">
					${languageUtil.format(locale, 'x-filter', 'modified-facet-portlet-instance-configuration-name')}
				</span>
			</@clay.button>
		</#if>

		<ul class="list-unstyled modified">
			<#if entries?has_content>
				<#list entries as entry>
					<li class="facet-value">
						<div class="custom-control custom-radio">
							<label class="facet-checkbox-label" for="${namespace}${entry.getBucketText()}">
								<@liferay_ui.csp>
									<input
										${(entry.isSelected())?then("checked", "")}
										class="custom-control-input facet-term"
										disabled
										id="${namespace}${entry.getBucketText()}"
										name="${namespace}${entry.getBucketText()}"
										onChange='${"window.location.href = \"${entry.getFilterValue()}\";"}'
										role="radio"
										type="radio"
									/>
								</@liferay_ui.csp>

								<span class="custom-control-label term-name ${(entry.isSelected())?then('facet-term-selected', 'facet-term-unselected')}">
									<span class="custom-control-label-text">
										<#if entry.isSelected()>
											<strong><@liferay_ui["message"] key="${htmlUtil.escape(entry.getBucketText())}" /></strong>
										<#else>
											<@liferay_ui["message"] key="${htmlUtil.escape(entry.getBucketText())}" />
										</#if>
									</span>
								</span>

								<small class="term-count">
									(${entry.getFrequency()})
								</small>
							</label>
						</div>
					</li>
				</#list>
			</#if>

			<li class="facet-value">
				<div class="custom-control custom-radio">
					<label class="facet-checkbox-label" for="${namespace}${customRangeBucketDisplayContext.getBucketText()}">
						<@liferay_ui.csp>
							<input
								${(customRangeBucketDisplayContext.isSelected())?then("checked", "")}
								class="custom-control-input facet-term"
								disabled
								id="${namespace}${customRangeBucketDisplayContext.getBucketText()}"
								name="${namespace}${customRangeBucketDisplayContext.getBucketText()}"
								onChange='${"window.location.href = \"${customRangeBucketDisplayContext.getFilterValue()}\";"}'
								role="radio"
								type="radio"
							/>
						</@liferay_ui.csp>

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

			<div class="${(!modifiedFacetCalendarDisplayContext.isSelected())?then("hide", "")} modified-custom-range" id="${namespace}customRange">
				<div class="col-md-6" id="${namespace}customRangeFrom">
					<@liferay_aui["field-wrapper"]
						label="from"
						name="fromInput"
					>
						<@liferay_ui["input-date"]
							cssClass="modified-facet-custom-range-input-date-from"
							dayParam="fromDay"
							dayValue=modifiedFacetCalendarDisplayContext.getFromDayValue()
							disabled=false
							firstDayOfWeek=modifiedFacetCalendarDisplayContext.getFromFirstDayOfWeek()
							monthParam="fromMonth"
							monthValue=modifiedFacetCalendarDisplayContext.getFromMonthValue()
							name="fromInput"
							yearParam="fromYear"
							yearValue=modifiedFacetCalendarDisplayContext.getFromYearValue()
						/>
					</@>
				</div>

				<div class="col-md-6" id="${namespace}customRangeTo">
					<@liferay_aui["field-wrapper"]
						label="to"
						name="toInput"
					>
						<@liferay_ui["input-date"]
							cssClass="modified-facet-custom-range-input-date-to"
							dayParam="toDay"
							dayValue=modifiedFacetCalendarDisplayContext.getToDayValue()
							disabled=false
							firstDayOfWeek=modifiedFacetCalendarDisplayContext.getToFirstDayOfWeek()
							monthParam="toMonth"
							monthValue=modifiedFacetCalendarDisplayContext.getToMonthValue()
							name="toInput"
							yearParam="toYear"
							yearValue=modifiedFacetCalendarDisplayContext.getToYearValue()
						/>
					</@>
				</div>

				<@clay["button"]
					cssClass="modified-facet-custom-range-filter-button"
					disabled=modifiedFacetCalendarDisplayContext.isRangeBackwards()
					displayType="secondary"
					id="${namespace + 'searchCustomRangeButton'}"
					label="search"
					name="${namespace + 'searchCustomRangeButton'}"
				/>
			</div>
		</ul>
	</@>
</@>