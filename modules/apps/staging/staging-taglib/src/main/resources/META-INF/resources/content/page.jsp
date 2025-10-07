<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/content/init.jsp" %>

<c:if test="<%= !portlets.isEmpty() %>">
	<aui:fieldset cssClass="options-group" markupView="lexicon">
		<clay:sheet-section>
			<h3 class="sheet-subtitle"><liferay-ui:message key="content" /></h3>

			<ul class="list-unstyled">
				<li class="tree-item">
					<aui:input disabled="<%= disableInputs %>" name="<%= PortletDataHandlerKeys.PORTLET_DATA %>" type="hidden" value="<%= MapUtil.getBoolean(parameterMap, PortletDataHandlerKeys.PORTLET_DATA, true) %>" />
					<aui:input disabled="<%= disableInputs %>" name="<%= PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT %>" type="hidden" value="<%= MapUtil.getBoolean(parameterMap, PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT, true) %>" />

					<ul id="<portlet:namespace />selectContents">
						<li>
							<div id="<portlet:namespace />range">
								<ul class="list-unstyled">
									<li class="tree-item">
										<aui:fieldset cssClass="portlet-data-section" label="date-range">
											<div class="align-items-center c-mb-3 d-flex flex-wrap">

												<%
												String selectedRange = MapUtil.getString(parameterMap, "range", defaultRange);
												%>

												<div class="c-p-4 range-options">
													<liferay-staging:radio
														checked="<%= selectedRange.equals(ExportImportDateUtil.RANGE_ALL) %>"
														disabled="<%= disableInputs %>"
														id="rangeAll"
														label="all"
														name="range"
														value="<%= ExportImportDateUtil.RANGE_ALL %>"
													/>
												</div>

												<c:if test="<%= !type.equals(Constants.EXPORT) %>">
													<div class="c-p-4 range-options">
														<liferay-staging:radio
															checked="<%= selectedRange.equals(ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE) %>"
															disabled="<%= disableInputs %>"
															id="rangeLastPublish"
															label="from-last-publish-date"
															name="range"
															value="<%= ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE %>"
														/>
													</div>
												</c:if>

												<div class="c-p-4 range-options">
													<liferay-staging:radio
														checked="<%= selectedRange.equals(ExportImportDateUtil.RANGE_DATE_RANGE) %>"
														disabled="<%= disableInputs %>"
														id="rangeDateRange"
														label="date-range"
														name="range"
														popover="export-date-range-help"
														value="<%= ExportImportDateUtil.RANGE_DATE_RANGE %>"
													/>
												</div>

												<div class="c-p-4 range-options">
													<liferay-staging:radio
														checked="<%= selectedRange.equals(ExportImportDateUtil.RANGE_LAST) %>"
														disabled="<%= disableInputs %>"
														id="rangeLast"
														label='<%= LanguageUtil.get(request, "last") + StringPool.TRIPLE_PERIOD %>'
														name="range"
														popover="export-last-range-help"
														value="<%= ExportImportDateUtil.RANGE_LAST %>"
													/>
												</div>

												<div class="range-options c-p-4 <%= disableInputs ? "hide" : StringPool.BLANK %>">
													<clay:link
														cssClass="modify-link"
														href="javascript:void(0);"
														icon="reload"
														id='<%= liferayPortletResponse.getNamespace() + "rangeLink" %>'
														label="refresh-counts"
													/>
												</div>
											</div>

											<%
											Calendar endCalendar = CalendarFactoryUtil.getCalendar(timeZone, locale);

											if (endDate != null) {
												endCalendar.setTime(endDate);
											}

											Calendar startCalendar = CalendarFactoryUtil.getCalendar(timeZone, locale);

											if (startDate != null) {
												startCalendar.setTime(startDate);
											}
											else {
												startCalendar.add(Calendar.DATE, -1);
											}
											%>

											<ul class="hide list-unstyled" id="<portlet:namespace />warningSection">
												<clay:alert
													displayType="warning"
													message="publishing-all-content-or-using-wide-date-range-will-take-some-time"
													symbol="page"
												/>
											</ul>

											<ul class="date-range-options hide list-unstyled" id="<portlet:namespace />startEndDate">
												<clay:alert
													displayType="warning"
													message="publishing-all-content-or-using-wide-date-range-will-take-some-time"
													symbol="page"
												/>

												<li class="d-flex flex-wrap">
													<liferay-ui:input-date
														cssClass="form-group form-group-inline"
														dayParam="startDateDay"
														dayValue="<%= startCalendar.get(Calendar.DATE) %>"
														disabled="<%= disableInputs %>"
														firstDayOfWeek="<%= startCalendar.getFirstDayOfWeek() - 1 %>"
														lastEnabledDate="<%= (!cmd.equals(Constants.PUBLISH_TO_LIVE) && !cmd.equals(Constants.PUBLISH_TO_REMOTE)) ? null : new Date() %>"
														monthParam="startDateMonth"
														monthValue="<%= startCalendar.get(Calendar.MONTH) %>"
														name="startDate"
														yearParam="startDateYear"
														yearValue="<%= startCalendar.get(Calendar.YEAR) %>"
													/>

													<liferay-ui:icon
														icon="calendar"
														markupView="lexicon"
													/>

													<liferay-ui:input-time
														amPmParam="startDateAmPm"
														amPmValue="<%= startCalendar.get(Calendar.AM_PM) %>"
														cssClass="form-group form-group-inline range-options"
														dateParam="startDateTime"
														dateValue="<%= startCalendar.getTime() %>"
														disabled="<%= disableInputs %>"
														hourParam="startDateHour"
														hourValue="<%= startCalendar.get(Calendar.HOUR) %>"
														minuteParam="startDateMinute"
														minuteValue="<%= startCalendar.get(Calendar.MINUTE) %>"
														name="startTime"
													/>

													<liferay-ui:input-date
														cssClass="form-group form-group-inline"
														dayParam="endDateDay"
														dayValue="<%= endCalendar.get(Calendar.DATE) %>"
														disabled="<%= disableInputs %>"
														firstDayOfWeek="<%= endCalendar.getFirstDayOfWeek() - 1 %>"
														lastEnabledDate="<%= (!cmd.equals(Constants.PUBLISH_TO_LIVE) && !cmd.equals(Constants.PUBLISH_TO_REMOTE)) ? null : new Date() %>"
														monthParam="endDateMonth"
														monthValue="<%= endCalendar.get(Calendar.MONTH) %>"
														name="endDate"
														yearParam="endDateYear"
														yearValue="<%= endCalendar.get(Calendar.YEAR) %>"
													/>

													<liferay-ui:icon
														icon="calendar"
														markupView="lexicon"
													/>

													<liferay-ui:input-time
														amPmParam="endDateAmPm"
														amPmValue="<%= endCalendar.get(Calendar.AM_PM) %>"
														cssClass="form-group form-group-inline"
														dateParam="endDateTime"
														dateValue="<%= endCalendar.getTime() %>"
														disabled="<%= disableInputs %>"
														hourParam="endDateHour"
														hourValue="<%= endCalendar.get(Calendar.HOUR) %>"
														minuteParam="endDateMinute"
														minuteValue="<%= endCalendar.get(Calendar.MINUTE) %>"
														name="endTime"
													/>
												</li>
											</ul>

											<ul class="hide list-unstyled" id="<portlet:namespace />rangeLastInputs">
												<li>
													<aui:select cssClass="relative-range" disabled="<%= disableInputs %>" label="" name="last">

														<%
														String last = MapUtil.getString(parameterMap, "last");
														%>

														<aui:option label='<%= LanguageUtil.format(request, "x-hours", "12", false) %>' selected='<%= last.equals("12") %>' value="12" />
														<aui:option label='<%= LanguageUtil.format(request, "x-hours", "24", false) %>' selected='<%= last.equals("24") %>' value="24" />
														<aui:option label='<%= LanguageUtil.format(request, "x-hours", "48", false) %>' selected='<%= last.equals("48") %>' value="48" />
														<aui:option label='<%= LanguageUtil.format(request, "x-days", "7", false) %>' selected='<%= last.equals("168") %>' value="168" />
													</aui:select>
												</li>
											</ul>
										</aui:fieldset>
									</li>
								</ul>
							</div>
						</li>
						<li class="options">
							<liferay-staging:portlet-list
								disableInputs="<%= disableInputs %>"
								exportImportConfigurationId="<%= exportImportConfigurationId %>"
								portlets="<%= portlets %>"
								showAllPortlets="<%= showAllPortlets %>"
								type="<%= type %>"
							/>
						</li>
					</ul>
				</li>
			</ul>
		</clay:sheet-section>
	</aui:fieldset>
</c:if>