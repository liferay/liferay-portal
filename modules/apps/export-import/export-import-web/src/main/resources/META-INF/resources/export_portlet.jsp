<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String tabs3 = ParamUtil.getString(request, "tabs3", "new-export-process");

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/export_import/export_import"
).setPortletResource(
	portletResource
).buildPortletURL();
%>

<clay:navigation-bar
	navigationItems='<%=
		new JSPNavigationItemList(pageContext) {
			{
				portletURL.setParameter("tabs3", "new-export-process");

				add(
					navigationItem -> {
						navigationItem.setActive(tabs3.equals("new-export-process"));
						navigationItem.setHref(portletURL.toString());
						navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "new-export-process"));
					});

				portletURL.setParameter("tabs3", "current-and-previous");

				add(
					navigationItem -> {
						navigationItem.setActive(tabs3.equals("current-and-previous"));
						navigationItem.setHref(portletURL.toString());
						navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "current-and-previous"));
					});
			}
		}
	%>'
/>

<c:choose>
	<c:when test='<%= tabs3.equals("new-export-process") %>'>

		<%
		int incompleteBackgroundTasksCount = BackgroundTaskManagerUtil.getBackgroundTasksCount(themeDisplay.getScopeGroupId(), selPortlet.getPortletId(), BackgroundTaskExecutorNames.PORTLET_EXPORT_BACKGROUND_TASK_EXECUTOR, false);
		%>

		<div class="<%= (incompleteBackgroundTasksCount == 0) ? "hide" : "in-progress" %>" id="<portlet:namespace />incompleteProcessMessage">
			<liferay-util:include page="/incomplete_processes_message.jsp" servletContext="<%= application %>">
				<liferay-util:param name="incompleteBackgroundTasksCount" value="<%= String.valueOf(incompleteBackgroundTasksCount) %>" />
			</liferay-util:include>
		</div>

		<portlet:actionURL name="/export_import/export_import" var="exportURL">
			<portlet:param name="mvcRenderCommandName" value="/export_import/export_import" />
		</portlet:actionURL>

		<liferay-portlet:renderURL var="redirectURL">
			<portlet:param name="mvcRenderCommandName" value="/export_import/export_import" />
			<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.EXPORT %>" />
			<portlet:param name="tabs2" value="export" />
			<portlet:param name="tabs3" value="current-and-previous" />
			<portlet:param name="portletResource" value="<%= portletResource %>" />
		</liferay-portlet:renderURL>

		<aui:form action='<%= exportURL + "&etag=0&strip=0" %>' cssClass="lfr-export-dialog" method="post" name="fm1">
			<aui:input name="tabs1" type="hidden" value="export_import" />
			<aui:input name="tabs2" type="hidden" value="export" />
			<aui:input name="redirect" type="hidden" value="<%= redirectURL %>" />
			<aui:input name="plid" type="hidden" value="<%= plid %>" />
			<aui:input name="groupId" type="hidden" value="<%= themeDisplay.getScopeGroupId() %>" />
			<aui:input name="portletResource" type="hidden" value="<%= portletResource %>" />

			<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.EXPORT %>" />

			<div class="export-dialog-tree">
				<clay:container-fluid>
					<div class="alert alert-warning">
						<liferay-ui:message key="export-process-deletion-warning-message" />
					</div>

					<div class="sheet">
						<div class="panel-group panel-group-flush">
							<aui:fieldset>
								<aui:input label="export-the-selected-data-to-the-given-lar-file-name" name="exportFileName" required="<%= true %>" showRequiredLabel="<%= false %>" size="50" value="<%= ExportImportHelperUtil.getPortletExportFileName(selPortlet) %>" />
							</aui:fieldset>

							<%
							PortletDataHandler portletDataHandler = selPortlet.getPortletDataHandlerInstance();

							PortletDataHandlerControl[] configurationControls = portletDataHandler.getExportConfigurationControls(company.getCompanyId(), themeDisplay.getScopeGroupId(), selPortlet, plid, false);
							%>

							<c:if test="<%= ArrayUtil.isNotEmpty(configurationControls) %>">
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
																		request.setAttribute("render_controls.jsp-action", Constants.EXPORT);
																		request.setAttribute("render_controls.jsp-childControl", false);
																		request.setAttribute("render_controls.jsp-controls", configurationControls);
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
								DateRange dateRange = ExportImportDateUtil.getDateRange(renderRequest, themeDisplay.getScopeGroupId(), false, plid, selPortlet.getPortletId(), ExportImportDateUtil.RANGE_ALL);

								Date startDate = dateRange.getStartDate();
								Date endDate = dateRange.getEndDate();

								PortletDataContext portletDataContext = PortletDataContextFactoryUtil.createPreparePortletDataContext(themeDisplay, startDate, endDate);

								portletDataHandler.prepareManifestSummary(portletDataContext, portletPreferences);

								ManifestSummary manifestSummary = portletDataContext.getManifestSummary();

								long exportModelCount = portletDataHandler.getExportModelCount(manifestSummary);

								long modelDeletionCount = manifestSummary.getModelDeletionCount(portletDataHandler.getDeletionSystemEventStagedModelTypes());
								%>

								<c:if test="<%= (exportModelCount != 0) || (modelDeletionCount != 0) || (startDate != null) || (endDate != null) %>">
									<aui:fieldset collapsible="<%= true %>" cssClass="options-group" id="content">
										<ul class="lfr-tree list-unstyled select-options">
											<li class="tree-item">
												<div id="<portlet:namespace />range">
													<div class="align-items-center d-flex flex-wrap">
														<div class="range-options">
															<aui:input checked="<%= true %>" data-name='<%= LanguageUtil.get(request, "all") %>' id="rangeAll" label="all" name="range" type="radio" value="all" />
														</div>

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

															<aui:a cssClass="modify-link refresh-link" href="javascript:void(0);" id="rangeLink" method="get">
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
															PortletDataHandlerControl[] exportControls = portletDataHandler.getExportControls();
															PortletDataHandlerControl[] metadataControls = portletDataHandler.getExportMetadataControls();
															%>

															<c:if test="<%= ArrayUtil.isNotEmpty(exportControls) || ArrayUtil.isNotEmpty(metadataControls) %>">
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
																				<c:if test="<%= exportControls != null %>">

																					<%
																					request.setAttribute("render_controls.jsp-action", Constants.EXPORT);
																					request.setAttribute("render_controls.jsp-childControl", false);
																					request.setAttribute("render_controls.jsp-controls", exportControls);
																					request.setAttribute("render_controls.jsp-manifestSummary", manifestSummary);
																					request.setAttribute("render_controls.jsp-portletDisabled", !portletDataHandler.isPublishToLiveByDefault());
																					request.setAttribute("render_controls.jsp-rootControlId", rootControlId);
																					%>

																					<aui:field-wrapper label='<%= ArrayUtil.isNotEmpty(metadataControls) ? "content" : StringPool.BLANK %>'>
																						<ul class="lfr-tree list-unstyled">
																							<liferay-util:include page="/render_controls.jsp" servletContext="<%= application %>" />
																						</ul>
																					</aui:field-wrapper>
																				</c:if>

																				<c:if test="<%= metadataControls != null %>">

																					<%
																					for (PortletDataHandlerControl metadataControl : metadataControls) {
																						PortletDataHandlerBoolean control = (PortletDataHandlerBoolean)metadataControl;

																						PortletDataHandlerControl[] childrenControls = control.getChildren();
																					%>

																						<c:if test="<%= ArrayUtil.isNotEmpty(childrenControls) %>">

																							<%
																							request.setAttribute("render_controls.jsp-controls", childrenControls);
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

													<ul>
														<liferay-util:buffer
															var="selectedContentOptionsLabel"
														>
															<liferay-ui:message key="for-each-of-the-selected-content-types,-export-their" />

															<span aria-label="<%= LanguageUtil.get(request, "comments-associated-to-object-entries-are-currently-excluded-from-the-export") %>" class="lfr-portal-tooltip ml-1" title="<%= LanguageUtil.get(request, "comments-associated-to-object-entries-are-currently-excluded-from-the-export") %>">
																<clay:icon
																	symbol="question-circle-full"
																/>
															</span>
														</liferay-util:buffer>

														<aui:fieldset cssClass="content-options" label="<%= selectedContentOptionsLabel %>">
															<span class="selected-labels" id="<portlet:namespace />selectedContentOptions"></span>

															<clay:button
																cssClass="modify-link options-link pr-1"
																displayType="link"
																id='<%= liferayPortletResponse.getNamespace() + "contentOptionsLink" %>'
																label="change"
															/>

															<span id="<portlet:namespace />rightContentOptionsArrow">
																<clay:icon
																	symbol="angle-right-small"
																/>
															</span>
															<span class="hide" id="<portlet:namespace />downContentOptionsArrow">
																<clay:icon
																	symbol="angle-down-small"
																/>
															</span>

															<div class="hide" id="<portlet:namespace />contentOptions">
																<ul class="lfr-tree list-unstyled">
																	<li class="tree-item">
																		<aui:input label="comments" name="<%= PortletDataHandlerKeys.COMMENTS %>" type="checkbox" value="<%= true %>" />

																		<aui:input label="ratings" name="<%= PortletDataHandlerKeys.RATINGS %>" type="checkbox" value="<%= true %>" />
																	</li>
																</ul>
															</div>
														</aui:fieldset>
													</ul>
												</li>
											</c:if>
										</ul>
									</aui:fieldset>
								</c:if>

								<liferay-staging:deletions
									cmd="<%= Constants.EXPORT %>"
								/>

								<%
								Group group = themeDisplay.getScopeGroup();
								%>

								<liferay-staging:permissions
									action="<%= Constants.EXPORT %>"
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
				<aui:button type="submit" value="export" />

				<aui:button type="cancel" />
			</aui:button-row>
		</aui:form>

		<aui:script use="aui-base">
			var liferayForm = Liferay.Form.get('<portlet:namespace />fm1');

			var form = liferayForm.formNode;

			form.on('submit', (event) => {
				event.halt();

				var exportImport = Liferay.component(
					'<portlet:namespace />ExportImportComponent'
				);

				var dateChecker = exportImport.getDateRangeChecker();

				if (dateChecker.validRange) {
					submitForm(form, form.attr('action'), false);
				}
				else {
					exportImport.showNotification(dateChecker);
				}
			});

			var oldFieldRules = liferayForm.get('fieldRules');

			var fieldRules = [
				{
					body: function (val, fieldNode, ruleValue) {

						<%
						JSONArray blacklistCharJSONArray = JSONFactoryUtil.createJSONArray();

						for (String s : PropsValues.DL_CHAR_BLACKLIST) {
							blacklistCharJSONArray.put(s);
						}
						%>

						var blacklistCharJSONArray =
							<%= blacklistCharJSONArray.toJSONString() %>;

						for (var i = 0; i < blacklistCharJSONArray.length; i++) {
							if (val.indexOf(blacklistCharJSONArray[i]) !== -1) {
								return false;
							}
						}

						return true;
					},
					custom: true,
					errorMessage:
						'<%= LanguageUtil.get(request, "the-following-are-invalid-characters") + HtmlUtil.escapeJS(Arrays.toString(PropsValues.DL_CHAR_BLACKLIST)) %>',
					fieldName: '<portlet:namespace />exportFileName',
					validatorName: 'custom_exportFileNameValidator',
				},
			];

			if (oldFieldRules) {
				fieldRules = fieldRules.concat(oldFieldRules);
			}

			liferayForm.set('fieldRules', fieldRules);
		</aui:script>
	</c:when>
	<c:when test='<%= tabs3.equals("current-and-previous") %>'>
		<div class="portlet-export-import-export-processes process-list" id="<portlet:namespace />exportProcesses">
			<liferay-util:include page="/export_portlet_processes.jsp" servletContext="<%= application %>" />
		</div>
	</c:when>
</c:choose>

<aui:script use="liferay-export-import-export-import">
	<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/export_import/export_import" var="exportProcessesURL">
		<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.EXPORT %>" />
		<portlet:param name="tabs2" value="export" />
		<portlet:param name="<%= SearchContainer.DEFAULT_CUR_PARAM %>" value="<%= ParamUtil.getString(request, SearchContainer.DEFAULT_CUR_PARAM) %>" />
		<portlet:param name="<%= SearchContainer.DEFAULT_DELTA_PARAM %>" value="<%= ParamUtil.getString(request, SearchContainer.DEFAULT_DELTA_PARAM) %>" />
		<portlet:param name="groupId" value="<%= String.valueOf(themeDisplay.getScopeGroupId()) %>" />
		<portlet:param name="portletResource" value="<%= portletResource %>" />
	</liferay-portlet:resourceURL>

	var exportImport = new Liferay.ExportImport({
		commentsNode: '#<%= PortletDataHandlerKeys.COMMENTS %>',
		deletionsNode: '#<%= PortletDataHandlerKeys.DELETIONS %>',
		form: document.<portlet:namespace />fm1,
		incompleteProcessMessageNode:
			'#<portlet:namespace />incompleteProcessMessage',
		locale: '<%= locale.toLanguageTag() %>',
		namespace: '<portlet:namespace />',
		processesNode: '#exportProcesses',
		processesResourceURL:
			'<%= HtmlUtil.escapeJS(exportProcessesURL.toString()) %>',
		rangeAllNode: '#rangeAll',
		rangeDateRangeNode: '#rangeDateRange',
		rangeLastNode: '#rangeLast',
		ratingsNode: '#<%= PortletDataHandlerKeys.RATINGS %>',
		timeZoneOffset: <%= timeZoneOffset %>,
	});

	Liferay.component('<portlet:namespace />ExportImportComponent', exportImport);

	Liferay.once('destroyPortlet', () => {
		exportImport.destroy();
	});
</aui:script>

<aui:script>
	Liferay.Util.toggleRadio('<portlet:namespace />rangeAll', '', [
		'<portlet:namespace />startEndDate',
		'<portlet:namespace />rangeLastInputs',
	]);
	Liferay.Util.toggleRadio(
		'<portlet:namespace />rangeDateRange',
		'<portlet:namespace />startEndDate',
		'<portlet:namespace />rangeLastInputs'
	);
	Liferay.Util.toggleRadio(
		'<portlet:namespace />rangeLast',
		'<portlet:namespace />rangeLastInputs',
		['<portlet:namespace />startEndDate']
	);
</aui:script>