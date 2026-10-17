<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

long groupId = ParamUtil.getLong(request, "groupId", scopeGroupId);

Group group = GroupLocalServiceUtil.fetchGroup(groupId);

FileEntry fileEntry = ExportImportHelperUtil.getTempFileEntry(groupId, themeDisplay.getUserId(), ExportImportHelper.TEMP_FOLDER_NAME + selPortlet.getPortletId());

ManifestSummary manifestSummary = ExportImportHelperUtil.getManifestSummary(themeDisplay.getUserId(), groupId, new HashMap<String, String[]>(), fileEntry);
%>

<portlet:actionURL name="/export_import/export_import" var="importPortletActionURL">
	<portlet:param name="mvcRenderCommandName" value="/export_import/export_import" />
</portlet:actionURL>

<portlet:renderURL var="importPortletRenderURL">
	<portlet:param name="mvcRenderCommandName" value="/export_import/export_import" />
	<portlet:param name="tabs2" value="import" />
	<portlet:param name="tabs3" value="current-and-previous" />
	<portlet:param name="redirect" value="<%= redirect %>" />
	<portlet:param name="portletResource" value="<%= portletResource %>" />
</portlet:renderURL>

<aui:form action="<%= importPortletActionURL %>" cssClass="lfr-export-dialog" method="post" name="fm1" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "publishPages();" %>'>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.IMPORT %>" />
	<aui:input name="tabs1" type="hidden" value="export_import" />
	<aui:input name="tabs2" type="hidden" value="import" />
	<aui:input name="redirect" type="hidden" value="<%= importPortletRenderURL %>" />
	<aui:input name="plid" type="hidden" value="<%= plid %>" />
	<aui:input name="groupId" type="hidden" value="<%= groupId %>" />
	<aui:input name="portletResource" type="hidden" value="<%= portletResource %>" />

	<div class="export-dialog-tree">
		<clay:container-fluid>
			<div class="alert alert-warning">
				<liferay-ui:message key="import-lar-file-deletion-warning-message" />
			</div>

			<div class="alert alert-warning">
				<liferay-ui:message key="import-process-deletion-warning-message" />
			</div>

			<div class="sheet">
				<div class="panel-group panel-group-flush">
					<aui:fieldset cssClass="options-group" label="file">
						<dl class="import-file-details options">
							<dt>
								<liferay-ui:message key="name" />
							</dt>
							<dd>
								<%= HtmlUtil.escape(fileEntry.getTitle()) %>
							</dd>
							<dt>
								<liferay-ui:message key="export" />
							</dt>
							<dd>

								<%
								Date exportDate = manifestSummary.getExportDate();
								%>

								<span class="lfr-portal-tooltip" title="<%= HtmlUtil.escape(dateTimeFormat.format(exportDate)) %>">
									<liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - exportDate.getTime(), true) %>" key="x-ago" translateArguments="<%= false %>" />
								</span>
							</dd>
							<dt>
								<liferay-ui:message key="author" />
							</dt>
							<dd>
								<%= HtmlUtil.escape(fileEntry.getUserName()) %>
							</dd>
							<dt>
								<liferay-ui:message key="size" />
							</dt>
							<dd>
								<%= LanguageUtil.formatStorageSize(fileEntry.getSize(), locale) %>
							</dd>
						</dl>
					</aui:fieldset>

					<%
					PortletDataHandler portletDataHandler = selPortlet.getPortletDataHandlerInstance();

					PortletDataHandlerControl[] importConfigurationPortletDataHandlerControls = portletDataHandler.getImportConfigurationPortletDataHandlerControls(selPortlet, manifestSummary);
					%>

					<c:if test="<%= ArrayUtil.isNotEmpty(importConfigurationPortletDataHandlerControls) %>">
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

											<ul id="<portlet:namespace />showChangeConfiguration_<%= selPortlet.getRootPortletId() %>">
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

											<div id="<portlet:namespace />configuration_<%= selPortlet.getRootPortletId() %>">
												<ul class="lfr-tree list-unstyled">
													<li class="tree-item">
														<aui:fieldset cssClass="portlet-type-data-section" id="configuration">
															<ul class="lfr-tree list-unstyled">

																<%
																request.setAttribute("render_controls.jsp-action", Constants.IMPORT);
																request.setAttribute("render_controls.jsp-childControl", false);
																request.setAttribute("render_controls.jsp-controls", importConfigurationPortletDataHandlerControls);
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

					<%
					long importModelCount = portletDataHandler.getExportModelCount(manifestSummary);

					long modelDeletionCount = manifestSummary.getModelDeletionCount(portletDataHandler.getDeletionSystemEventStagedModelTypes());
					%>

					<c:if test="<%= !portletDataHandler.isDisplayPortlet() && ((importModelCount != 0) || (modelDeletionCount != 0)) %>">
						<aui:fieldset collapsible="<%= true %>" cssClass="options-group" label="content">
							<ul class="lfr-tree list-unstyled select-options">
								<li class="options">
									<ul class="portlet-list">
										<li class="tree-item">
											<aui:input name="<%= PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT %>" type="hidden" value="<%= false %>" />

											<aui:input name="<%= PortletDataHandlerKeys.PORTLET_DATA %>" type="hidden" value="<%= true %>" />

											<liferay-util:buffer
												var="badgeHTML"
											>
												<span class="badge badge-info"><%= (importModelCount > 0) ? importModelCount : StringPool.BLANK %></span>
												<span class="badge badge-warning deletions"><%= (modelDeletionCount > 0) ? (modelDeletionCount + StringPool.SPACE + LanguageUtil.get(request, "deletions")) : StringPool.BLANK %></span>
											</liferay-util:buffer>

											<%
											String rootControlId = PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE + selPortlet.getRootPortletId();
											%>

											<aui:input label='<%= LanguageUtil.get(request, "content") + badgeHTML %>' name="<%= rootControlId %>" type="checkbox" value="<%= true %>" />

											<%
											PortletDataHandlerControl[] importMetadataPortletDataHandlerControls = portletDataHandler.getImportMetadataPortletDataHandlerControls();
											PortletDataHandlerControl[] importPortletDataHandlerControls = portletDataHandler.getImportPortletDataHandlerControls();
											%>

											<c:if test="<%= ArrayUtil.isNotEmpty(importMetadataPortletDataHandlerControls) || ArrayUtil.isNotEmpty(importPortletDataHandlerControls) %>">
												<ul id="<portlet:namespace />showChangeContent_<%= selPortlet.getRootPortletId() %>">
													<li class="tree-item">
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

												<div id="<portlet:namespace />content_<%= selPortlet.getRootPortletId() %>">
													<ul class="lfr-tree list-unstyled">
														<li class="tree-item">
															<aui:fieldset cssClass="portlet-type-data-section" label="content">
																<aui:field-wrapper label='<%= ArrayUtil.isNotEmpty(importMetadataPortletDataHandlerControls) ? "content" : StringPool.BLANK %>'>
																	<c:if test="<%= importPortletDataHandlerControls != null %>">

																		<%
																		request.setAttribute("render_controls.jsp-action", Constants.IMPORT);
																		request.setAttribute("render_controls.jsp-childControl", false);
																		request.setAttribute("render_controls.jsp-controls", importPortletDataHandlerControls);
																		request.setAttribute("render_controls.jsp-manifestSummary", manifestSummary);
																		request.setAttribute("render_controls.jsp-portletDisabled", !portletDataHandler.isPublishToLiveByDefault());
																		request.setAttribute("render_controls.jsp-rootControlId", rootControlId);
																		%>

																		<ul class="lfr-tree list-unstyled">
																			<liferay-util:include page="/render_controls.jsp" servletContext="<%= application %>" />
																		</ul>
																	</c:if>
																</aui:field-wrapper>

																<c:if test="<%= importMetadataPortletDataHandlerControls != null %>">

																	<%
																	for (PortletDataHandlerControl portletDataHandlerControl : importMetadataPortletDataHandlerControls) {
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

									<ul>
										<aui:fieldset cssClass="comments-and-ratings" label="for-each-of-the-selected-content-types,-import-their">
											<span class="selected-labels" id="<portlet:namespace />selectedContentOptions"></span>

											<clay:button
												cssClass="pr-1"
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
							</ul>
						</aui:fieldset>

						<liferay-staging:deletions
							cmd="<%= Constants.IMPORT %>"
						/>
					</c:if>

					<liferay-staging:permissions
						action="<%= Constants.IMPORT %>"
						descriptionCSSClass="permissions-description"
						global="<%= group.isCompany() %>"
						labelCSSClass="permissions-label"
					/>

					<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" cssClass="options-group" label="update-data">

						<%
						String taglibMirrorLabel = LanguageUtil.get(request, "mirror") + ": <span style='font-weight: normal'>" + LanguageUtil.get(request, "import-data-strategy-mirror-help") + "</span>";
						%>

						<aui:input checked="<%= true %>" data-name='<%= LanguageUtil.get(request, "mirror") %>' id="mirror" label="<%= taglibMirrorLabel %>" name="<%= PortletDataHandlerKeys.DATA_STRATEGY %>" type="radio" value="<%= PortletDataHandlerKeys.DATA_STRATEGY_MIRROR %>" />

						<%
						String taglibMirrorWithOverwritingLabel = LanguageUtil.get(request, "mirror-with-overwriting") + ": <span style='font-weight: normal'>" + LanguageUtil.get(request, portletDataHandler.isSupportsDataStrategyMirrorWithOverwriting() ? "import-data-strategy-mirror-with-overwriting-help" : "import-data-strategy-mirror-with-overwriting-is-not-available-help") + "</span>";
						%>

						<aui:input data-name='<%= LanguageUtil.get(request, "mirror-with-overwriting") %>' disabled="<%= !portletDataHandler.isSupportsDataStrategyMirrorWithOverwriting() %>" id="mirrorWithOverwriting" label="<%= taglibMirrorWithOverwritingLabel %>" name="<%= PortletDataHandlerKeys.DATA_STRATEGY %>" type="radio" value="<%= PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE %>" />

						<%
						String taglibCopyAsNewLabel = LanguageUtil.get(request, "copy-as-new") + ": <span style='font-weight: normal'>" + LanguageUtil.get(request, portletDataHandler.isSupportsDataStrategyCopyAsNew() ? "import-data-strategy-copy-as-new-help" : "not-supported") + "</span>";
						%>

						<aui:input data-name='<%= LanguageUtil.get(request, "copy-as-new") %>' disabled="<%= !portletDataHandler.isSupportsDataStrategyCopyAsNew() %>" id="copyAsNew" label="<%= taglibCopyAsNewLabel %>" name="<%= PortletDataHandlerKeys.DATA_STRATEGY %>" type="radio" value="<%= PortletDataHandlerKeys.DATA_STRATEGY_COPY_AS_NEW %>" />
					</aui:fieldset>

					<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" cssClass="options-group" label="authorship-of-the-content">

						<%
						String taglibUseTheOriginalAuthorLabel = LanguageUtil.get(request, "use-the-original-author") + ": <span style='font-weight: normal'>" + LanguageUtil.get(request, "use-the-original-author-help") + "</span>";
						%>

						<aui:input checked="<%= true %>" data-name='<%= LanguageUtil.get(request, "use-the-original-author") %>' id="currentUserId" label="<%= taglibUseTheOriginalAuthorLabel %>" name="<%= PortletDataHandlerKeys.USER_ID_STRATEGY %>" type="radio" value="<%= UserIdStrategy.CURRENT_USER_ID %>" />

						<%
						String taglibUseTheCurrentUserAsAuthorLabel = LanguageUtil.get(request, "use-the-current-user-as-author") + ": <span style='font-weight: normal'>" + LanguageUtil.get(request, "use-the-current-user-as-author-help") + "</span>";
						%>

						<aui:input data-name='<%= LanguageUtil.get(request, "always-use-my-user-id") %>' id="alwaysCurrentUserId" label="<%= taglibUseTheCurrentUserAsAuthorLabel %>" name="<%= PortletDataHandlerKeys.USER_ID_STRATEGY %>" type="radio" value="<%= UserIdStrategy.ALWAYS_CURRENT_USER_ID %>" />
					</aui:fieldset>
				</div>
			</div>
		</clay:container-fluid>
	</div>

	<aui:button-row>
		<portlet:renderURL var="backURL">
			<portlet:param name="mvcRenderCommandName" value="/export_import/export_import" />
			<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.VALIDATE %>" />
			<portlet:param name="tabs2" value="import" />
			<portlet:param name="portletResource" value="<%= String.valueOf(portletResource) %>" />
		</portlet:renderURL>

		<aui:button href="<%= backURL %>" name="back" value="back" />

		<aui:button type="submit" value="import" />
	</aui:button-row>
</aui:form>

<aui:script>
	function <portlet:namespace />publishPages() {
		var deletePortletDataBeforeImportingCheckbox = document.getElementById(
			'<portlet:namespace /><%= PortletDataHandlerKeys.DELETE_PORTLET_DATA %>'
		);

		var form = document.<portlet:namespace />fm1;

		if (
			deletePortletDataBeforeImportingCheckbox &&
			deletePortletDataBeforeImportingCheckbox.checked
		) {
			Liferay.Util.openConfirmModal({
				message:
					'<%= UnicodeLanguageUtil.get(request, "delete-application-data-before-importing-confirmation") %>',
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						submitForm(form);
					}
				},
			});
		}
		else {
			submitForm(form);
		}
	}
</aui:script>

<aui:script use="liferay-export-import-export-import">
	new Liferay.ExportImport({
		commentsNode: '#<%= PortletDataHandlerKeys.COMMENTS %>',
		deletionsNode: '#<%= PortletDataHandlerKeys.DELETIONS %>',
		form: document.<portlet:namespace />fm1,
		locale: '<%= locale.toLanguageTag() %>',
		namespace: '<portlet:namespace />',
		ratingsNode: '#<%= PortletDataHandlerKeys.RATINGS %>',
		timeZoneOffset: <%= timeZoneOffset %>,
	});
</aui:script>