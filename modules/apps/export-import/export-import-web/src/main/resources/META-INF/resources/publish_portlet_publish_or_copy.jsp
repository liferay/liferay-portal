<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-staging:defineObjects />

<%
String tabs3 = ParamUtil.getString(request, "tabs3", "new-publish-process");

boolean newPublication = tabs3.equals("new-publish-process");

String defaultRange = ExportImportDateUtil.RANGE_ALL;
String javaScriptOnSubmitFunction = "event.halt(); " + liferayPortletResponse.getNamespace();
long workingGroupId = liveGroupId;

if (newPublication) {
	defaultRange = ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE;
	javaScriptOnSubmitFunction += "publishToLive();";
	workingGroupId = stagingGroupId;
}
else {
	javaScriptOnSubmitFunction += "copyFromLive();";
}
%>

<portlet:actionURL name="/export_import/publish_portlet" var="publishPortletURL">
	<portlet:param name="mvcRenderCommandName" value="/export_import/publish_portlet" />
	<portlet:param name="tabs3" value="<%= tabs3 %>" />
</portlet:actionURL>

<liferay-portlet:renderURL var="redirectURL">
	<portlet:param name="mvcRenderCommandName" value="/export_import/publish_portlet" />
	<portlet:param name="tabs3" value="current-and-previous" />
	<portlet:param name="portletResource" value="<%= portletResource %>" />
</liferay-portlet:renderURL>

<aui:form action="<%= publishPortletURL %>" cssClass="lfr-export-dialog" method="post" name="fm1" onSubmit="<%= javaScriptOnSubmitFunction %>">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.PUBLISH_TO_LIVE %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirectURL %>" />
	<aui:input name="plid" type="hidden" value="<%= plid %>" />
	<aui:input name="groupId" type="hidden" value="<%= themeDisplay.getScopeGroupId() %>" />
	<aui:input name="portletResource" type="hidden" value="<%= portletResource %>" />

	<div class="export-dialog-tree portlet-export-import-publish-processes">
		<clay:container-fluid>

			<%
			int incompleteBackgroundTasksCount = BackgroundTaskManagerUtil.getBackgroundTasksCount(StagingUtil.getStagingAndLiveGroupIds(themeDisplay.getScopeGroupId()), selPortlet.getPortletId(), BackgroundTaskExecutorNames.PORTLET_STAGING_BACKGROUND_TASK_EXECUTOR, false);
			%>

			<div class="<%= (incompleteBackgroundTasksCount == 0) ? "hide" : "in-progress" %>" id="<portlet:namespace />incompleteProcessMessage">
				<liferay-util:include page="/incomplete_processes_message.jsp" servletContext="<%= application %>">
					<liferay-util:param name="incompleteBackgroundTasksCount" value="<%= String.valueOf(incompleteBackgroundTasksCount) %>" />
				</liferay-util:include>
			</div>

			<div class="sheet">
				<div class="panel-group panel-group-flush">
					<clay:alert
						displayType="warning"
						message="publish-small-incremental-changes-to-avoid-large-publishing-processes-that-can-take-a-long-time-to-execute"
						symbol="page"
						title="recommendation"
					/>

					<%
					PortletDataHandler portletDataHandler = selPortlet.getPortletDataHandlerInstance();

					PortletDataHandlerControl[] configurationPortletDataHandlerControls = portletDataHandler.getExportConfigurationPortletDataHandlerControls(company.getCompanyId(), themeDisplay.getScopeGroupId(), selPortlet, plid, false);
					%>

					<c:if test="<%= ArrayUtil.isNotEmpty(configurationPortletDataHandlerControls) %>">
						<aui:fieldset collapsible="<%= true %>" cssClass="options-group" label="application">
							<ul class="lfr-tree list-unstyled select-options">
								<li class="options">
									<ul class="portlet-list">
										<li class="tree-item">
											<aui:input name="<%= PortletDataHandlerKeys.PORTLET_CONFIGURATION %>" type="hidden" value="<%= true %>" />

											<%
											String rootControlId = PortletDataHandlerKeys.PORTLET_CONFIGURATION + StringPool.UNDERLINE + selPortlet.getRootPortletId();
											%>

											<aui:input label="configuration" name="<%= rootControlId %>" type="checkbox" value="<%= true %>" />

											<ul class="hide" id="<portlet:namespace />showChangeConfiguration_<%= selPortlet.getRootPortletId() %>">
												<li>
													<span class="selected-labels" id="<portlet:namespace />selectedConfiguration_<%= selPortlet.getRootPortletId() %>"></span>

													<clay:button
														cssClass="configuration-link modify-link pr-1"
														data-portletid="<%= selPortlet.getRootPortletId() %>"
														displayType="link"
														label="change"
													/>

													<span id="<portlet:namespace />rightConfigurationArrow_<%= selPortlet.getRootPortletId() %>">
														<clay:icon
															symbol="angle-right-small"
														/>
													</span>
													<span class="hide" id="<portlet:namespace />downConfigurationArrow_<%= selPortlet.getRootPortletId() %>">
														<clay:icon
															symbol="angle-down-small"
														/>
													</span>
												</li>
											</ul>

											<div class="hide" id="<portlet:namespace />configuration_<%= selPortlet.getRootPortletId() %>">
												<ul class="lfr-tree list-unstyled">
													<li class="tree-item">
														<aui:fieldset cssClass="portlet-type-data-section" id="configuration">
															<ul class="lfr-tree list-unstyled">

																<%
																request.setAttribute("render_controls.jsp-action", Constants.PUBLISH);
																request.setAttribute("render_controls.jsp-childControl", false);
																request.setAttribute("render_controls.jsp-controls", configurationPortletDataHandlerControls);
																request.setAttribute("render_controls.jsp-portletId", selPortlet.getRootPortletId());
																request.setAttribute("render_controls.jsp-rootControlId", rootControlId);
																%>

																<liferay-util:include page="/render_controls.jsp" servletContext="<%= application %>" />
															</ul>
														</aui:fieldset>
													</li>
												</ul>
											</div>

											<aui:script>
												Liferay.Util.toggleBoxes(
													'<portlet:namespace /><%= PortletDataHandlerKeys.PORTLET_CONFIGURATION + StringPool.UNDERLINE + selPortlet.getRootPortletId() %>',
													'<portlet:namespace />showChangeConfiguration<%= StringPool.UNDERLINE + selPortlet.getRootPortletId() %>'
												);
											</aui:script>
										</li>
									</ul>
								</li>
							</ul>
						</aui:fieldset>
					</c:if>

					<c:if test="<%= !portletDataHandler.isDisplayPortlet() %>">

						<%
						DateRange dateRange = ExportImportDateUtil.getDateRange(renderRequest, themeDisplay.getScopeGroupId(), false, plid, selPortlet.getPortletId(), defaultRange);

						Date startDate = dateRange.getStartDate();
						Date endDate = dateRange.getEndDate();

						String range = ParamUtil.getString(renderRequest, ExportImportDateUtil.RANGE, null);

						PortletDataContext portletDataContext = PortletDataContextFactoryUtil.createPreparePortletDataContext(themeDisplay.getCompanyId(), workingGroupId, (range != null) ? range : defaultRange, startDate, endDate);

						portletDataHandler.prepareManifestSummary(portletDataContext, portletPreferences);

						ManifestSummary manifestSummary = portletDataContext.getManifestSummary();

						long exportModelCount = portletDataHandler.getExportModelCount(manifestSummary);

						long modelDeletionCount = manifestSummary.getModelDeletionCount(portletDataHandler.getDeletionSystemEventStagedModelTypes());
						%>

						<aui:fieldset collapsible="<%= true %>" cssClass="options-group" id="content">
							<ul class="lfr-tree list-unstyled select-options">
								<li class="tree-item">
									<div id="<portlet:namespace />range">
										<div class="align-items-center d-flex flex-wrap">
											<div class="range-options">
												<aui:input checked="<%= !newPublication %>" data-name='<%= LanguageUtil.get(request, "all") %>' id="rangeAll" label="all" name="range" type="radio" value="all" />
											</div>

											<c:if test="<%= newPublication %>">
												<div class="range-options">
													<aui:input checked="<%= true %>" data-name='<%= LanguageUtil.get(request, "from-last-publish-date") %>' id="rangeLastPublish" label="from-last-publish-date" name="range" type="radio" value="fromLastPublishDate" />
												</div>
											</c:if>

											<div class="range-options">
												<aui:input data-name='<%= LanguageUtil.get(request, "date-range") %>' helpMessage="export-date-range-help" id="rangeDateRange" label="date-range" name="range" type="radio" value="dateRange" />
											</div>

											<div class="range-options">
												<aui:input helpMessage="export-last-range-help" id="rangeLast" label='<%= LanguageUtil.get(request, "last") + StringPool.TRIPLE_PERIOD %>' name="range" type="radio" value="last" />
											</div>

											<div class="range-options">
												<liferay-ui:icon
													icon="reload"
													markupView="lexicon"
												/>

												<aui:a cssClass="modify-link" href="javascript:void(0);" id="rangeLink" method="get">
													<liferay-ui:message key="refresh-counts" />
												</aui:a>
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

										<ul class="date-range-options hide list-unstyled" id="<portlet:namespace />startEndDate">
											<li class="d-flex flex-wrap">
												<aui:fieldset label="start-date">
													<liferay-ui:input-date
														cssClass="form-group form-group-inline"
														dayParam="startDateDay"
														dayValue="<%= startCalendar.get(Calendar.DATE) %>"
														disabled="<%= false %>"
														firstDayOfWeek="<%= startCalendar.getFirstDayOfWeek() - 1 %>"
														lastEnabledDate="<%= new Date() %>"
														monthParam="startDateMonth"
														monthValue="<%= startCalendar.get(Calendar.MONTH) %>"
														name="startDate"
														yearParam="startDateYear"
														yearValue="<%= startCalendar.get(Calendar.YEAR) %>"
													/>

													<liferay-ui:input-time
														amPmParam="startDateAmPm"
														amPmValue="<%= startCalendar.get(Calendar.AM_PM) %>"
														cssClass="form-group form-group-inline"
														dateParam="startDateTime"
														dateValue="<%= startCalendar.getTime() %>"
														disabled="<%= false %>"
														hourParam="startDateHour"
														hourValue="<%= startCalendar.get(Calendar.HOUR) %>"
														minuteParam="startDateMinute"
														minuteValue="<%= startCalendar.get(Calendar.MINUTE) %>"
														name="startTime"
													/>
												</aui:fieldset>

												<aui:fieldset label="end-date">
													<liferay-ui:input-date
														cssClass="form-group form-group-inline"
														dayParam="endDateDay"
														dayValue="<%= endCalendar.get(Calendar.DATE) %>"
														disabled="<%= false %>"
														firstDayOfWeek="<%= endCalendar.getFirstDayOfWeek() - 1 %>"
														lastEnabledDate="<%= new Date() %>"
														monthParam="endDateMonth"
														monthValue="<%= endCalendar.get(Calendar.MONTH) %>"
														name="endDate"
														yearParam="endDateYear"
														yearValue="<%= endCalendar.get(Calendar.YEAR) %>"
													/>

													<liferay-ui:input-time
														amPmParam="endDateAmPm"
														amPmValue="<%= endCalendar.get(Calendar.AM_PM) %>"
														cssClass="form-group form-group-inline"
														dateParam="startDateTime"
														dateValue="<%= endCalendar.getTime() %>"
														disabled="<%= false %>"
														hourParam="endDateHour"
														hourValue="<%= endCalendar.get(Calendar.HOUR) %>"
														minuteParam="endDateMinute"
														minuteValue="<%= endCalendar.get(Calendar.MINUTE) %>"
														name="endTime"
													/>
												</aui:fieldset>
											</li>
										</ul>

										<ul class="hide list-unstyled" id="<portlet:namespace />rangeLastInputs">
											<li>
												<aui:select cssClass="relative-range" label="" name="last">
													<aui:option label='<%= LanguageUtil.format(request, "x-hours", "12", false) %>' value="12" />
													<aui:option label='<%= LanguageUtil.format(request, "x-hours", "24", false) %>' value="24" />
													<aui:option label='<%= LanguageUtil.format(request, "x-hours", "48", false) %>' value="48" />
													<aui:option label='<%= LanguageUtil.format(request, "x-days", "7", false) %>' value="168" />
												</aui:select>
											</li>
										</ul>
									</div>
								</li>

								<c:if test="<%= (exportModelCount != 0) || (modelDeletionCount != 0) %>">
									<li class="options">
										<ul class="portlet-list">
											<li class="tree-item">
												<aui:input name="<%= PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT %>" type="hidden" value="<%= false %>" />

												<aui:input name="<%= PortletDataHandlerKeys.PORTLET_DATA %>" type="hidden" value="<%= true %>" />

												<liferay-util:buffer
													var="badgeHTML"
												>
													<span class="badge badge-info"><%= (exportModelCount > 0) ? exportModelCount : StringPool.BLANK %></span>
													<span class="badge badge-warning deletions"><%= (modelDeletionCount > 0) ? (modelDeletionCount + StringPool.SPACE + LanguageUtil.get(request, "deletions")) : StringPool.BLANK %></span>
												</liferay-util:buffer>

												<%
												String rootControlId = PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE + selPortlet.getRootPortletId();
												%>

												<aui:input label='<%= LanguageUtil.get(request, "content") + badgeHTML %>' name="<%= rootControlId %>" type="checkbox" value="<%= true %>" />

												<%
												PortletDataHandlerControl[] exportMetadataPortletDataHandlerControls = portletDataHandler.getExportMetadataPortletDataHandlerControls();
												PortletDataHandlerControl[] exportPortletDataHandlerControls = portletDataHandler.getExportPortletDataHandlerControls();
												%>

												<c:if test="<%= ArrayUtil.isNotEmpty(exportMetadataPortletDataHandlerControls) || ArrayUtil.isNotEmpty(exportPortletDataHandlerControls) %>">
													<ul id="<portlet:namespace />showChangeContent_<%= selPortlet.getRootPortletId() %>">
														<li>
															<span class="selected-labels" id="<portlet:namespace />selectedContent_<%= selPortlet.getRootPortletId() %>"></span>

															<clay:button
																cssClass="content-link modify-link pr-1"
																data-portletid="<%= selPortlet.getRootPortletId() %>"
																displayType="link"
																id='<%= liferayPortletResponse.getNamespace() + "contentLink_" + selPortlet.getRootPortletId() %>'
																label="change"
															/>

															<span id="<portlet:namespace />rightContentArrow_<%= selPortlet.getRootPortletId() %>">
																<clay:icon
																	symbol="angle-right-small"
																/>
															</span>
															<span class="hide" id="<portlet:namespace />downContentArrow_<%= selPortlet.getRootPortletId() %>">
																<clay:icon
																	symbol="angle-down-small"
																/>
															</span>
														</li>
													</ul>

													<div class="hide" id="<portlet:namespace />content_<%= selPortlet.getRootPortletId() %>">
														<ul class="lfr-tree list-unstyled">
															<li class="tree-item">
																<aui:fieldset cssClass="portlet-type-data-section" id="content">
																	<aui:field-wrapper label='<%= ArrayUtil.isNotEmpty(exportMetadataPortletDataHandlerControls) ? "content" : StringPool.BLANK %>'>
																		<c:if test="<%= exportPortletDataHandlerControls != null %>">

																			<%
																			request.setAttribute("render_controls.jsp-action", Constants.PUBLISH);
																			request.setAttribute("render_controls.jsp-childControl", false);
																			request.setAttribute("render_controls.jsp-controls", exportPortletDataHandlerControls);
																			request.setAttribute("render_controls.jsp-manifestSummary", manifestSummary);
																			request.setAttribute("render_controls.jsp-portletDisabled", !portletDataHandler.isPublishToLiveByDefault());
																			request.setAttribute("render_controls.jsp-rootControlId", rootControlId);
																			%>

																			<ul class="lfr-tree list-unstyled">
																				<liferay-util:include page="/render_controls.jsp" servletContext="<%= application %>" />
																			</ul>
																		</c:if>
																	</aui:field-wrapper>

																	<c:if test="<%= exportMetadataPortletDataHandlerControls != null %>">

																		<%
																		for (PortletDataHandlerControl portletDataHandlerControl : exportMetadataPortletDataHandlerControls) {
																			PortletDataHandlerBoolean portletDataHandlerBoolean = (PortletDataHandlerBoolean)portletDataHandlerControl;
																		%>

																			<c:if test="<%= ArrayUtil.isNotEmpty(portletDataHandlerBoolean.getChildrenPortletDataHandlerControls()) %>">

																				<%
																				request.setAttribute("render_controls.jsp-controls", portletDataHandlerBoolean.getChildrenPortletDataHandlerControls());
																				%>

																				<aui:field-wrapper label="content-metadata">
																					<ul class="lfr-tree list-unstyled">
																						<liferay-util:include page="/render_controls.jsp" servletContext="<%= application %>" />
																					</ul>
																				</aui:field-wrapper>
																			</c:if>

																		<%
																		}
																		%>

																	</c:if>
																</aui:fieldset>
															</li>
														</ul>
													</div>

													<aui:script>
														Liferay.Util.toggleBoxes(
															'<portlet:namespace /><%= PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE + selPortlet.getRootPortletId() %>',
															'<portlet:namespace />showChangeContent<%= StringPool.UNDERLINE + selPortlet.getRootPortletId() %>'
														);
													</aui:script>
												</c:if>
											</li>
										</ul>
									</li>
								</c:if>
							</ul>
						</aui:fieldset>

						<liferay-staging:deletions
							cmd="<%= Constants.PUBLISH %>"
						/>

						<liferay-staging:permissions
							action="<%= Constants.PUBLISH %>"
							descriptionCSSClass="permissions-description"
							global="<%= group.isCompany() %>"
							labelCSSClass="permissions-label"
						/>
					</c:if>
				</div>
			</div>
		</clay:container-fluid>
	</div>

	<aui:button-row>
		<c:choose>
			<c:when test="<%= newPublication %>">
				<aui:button type="submit" value="publish-to-live" />
			</c:when>
			<c:otherwise>
				<aui:button type="submit" value="copy-from-live" />
			</c:otherwise>
		</c:choose>
	</aui:button-row>
</aui:form>