<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<c:choose>
	<c:when test='<%= !FeatureFlagManagerUtil.isEnabled(themeDisplay.getCompanyId(), "LPD-101272") %>'>
		<liferay-util:include page="/view.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:otherwise>

		<%
		String cmd = ParamUtil.getString(request, Constants.CMD);

		if (Validator.isNull(cmd)) {
			cmd = Constants.ADD;
		}

		long exportImportConfigurationId = 0;

		ExportImportConfiguration exportImportConfiguration = null;
		Map<String, Serializable> exportImportConfigurationSettingsMap = Collections.emptyMap();
		Map<String, String[]> parameterMap = Collections.emptyMap();

		if (SessionMessages.contains(liferayPortletRequest, portletDisplay.getId() + "exportImportConfigurationId")) {
			exportImportConfigurationId = (Long)SessionMessages.get(liferayPortletRequest, portletDisplay.getId() + "exportImportConfigurationId");

			if (exportImportConfigurationId > 0) {
				exportImportConfiguration = ExportImportConfigurationLocalServiceUtil.getExportImportConfiguration(exportImportConfigurationId);
			}

			exportImportConfigurationSettingsMap = (Map<String, Serializable>)SessionMessages.get(liferayPortletRequest, portletDisplay.getId() + "settingsMap");
		}
		else {
			exportImportConfigurationId = ParamUtil.getLong(request, "exportImportConfigurationId");

			if (exportImportConfigurationId > 0) {
				exportImportConfiguration = ExportImportConfigurationLocalServiceUtil.getExportImportConfiguration(exportImportConfigurationId);

				exportImportConfigurationSettingsMap = exportImportConfiguration.getSettingsMap();
			}
		}

		if (MapUtil.isNotEmpty(exportImportConfigurationSettingsMap)) {
			parameterMap = (Map<String, String[]>)exportImportConfigurationSettingsMap.get("parameterMap");
			privateLayout = GetterUtil.getBoolean(exportImportConfigurationSettingsMap.get("privateLayout"), privateLayout);
		}

		if (exportImportConfiguration != null) {
			cmd = Constants.UPDATE;
		}

		long layoutSetBranchId = MapUtil.getLong(parameterMap, "layoutSetBranchId", ParamUtil.getLong(request, "layoutSetBranchId"));
		String layoutSetBranchName = MapUtil.getString(parameterMap, "layoutSetBranchName", ParamUtil.getString(request, "layoutSetBranchName"));

		String treeId = "liveLayoutsTree";

		if (liveGroup.isStaged()) {
			if (!liveGroup.isStagedRemotely()) {
				treeId = "stageLayoutsTree";
			}
			else {
				treeId = "remoteLayoutsTree";
			}
		}

		treeId = treeId + liveGroupId;

		treeId = treeId + privateLayout + layoutSetBranchId;

		PortletURL renderURL = PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCRenderCommandName(
			"/staging_processes/view_publish_configurations"
		).setParameter(
			"groupId", stagingGroupId
		).setParameter(
			"layoutSetBranchId", layoutSetBranchId
		).setParameter(
			"layoutSetBranchName", layoutSetBranchName
		).setParameter(
			"privateLayout", privateLayout
		).buildPortletURL();

		response.setHeader("Ajax-ID", request.getHeader("Ajax-ID"));

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(renderURL.toString());

		renderResponse.setTitle((exportImportConfiguration == null) ? LanguageUtil.get(request, "new-publish-template") : exportImportConfiguration.getName());
		%>

		<c:if test='<%= SessionMessages.contains(renderRequest, "requestProcessed") %>'>

			<%
			String successMessage = (String)SessionMessages.get(renderRequest, "requestProcessed");
			%>

			<c:if test='<%= Validator.isNotNull(successMessage) && !successMessage.equals("request_processed") %>'>
				<div class="alert alert-success">
					<%= HtmlUtil.escape(successMessage) %>
				</div>
			</c:if>
		</c:if>

		<clay:container-fluid>
			<div id="<portlet:namespace />customConfiguration">
				<portlet:actionURL name="/staging_processes/edit_publish_configuration" var="updatePublishConfigurationURL">
					<portlet:param name="mvcRenderCommandName" value="/staging_processes/edit_publish_configuration" />
					<portlet:param name="groupId" value="<%= String.valueOf(stagingGroupId) %>" />
				</portlet:actionURL>

				<aui:form action='<%= updatePublishConfigurationURL + "&etag=0&strip=0" %>' cssClass="lfr-export-dialog" method="post" name="exportPagesFm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "publishPages();" %>'>
					<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (exportImportConfiguration == null) ? Constants.ADD : Constants.UPDATE %>" />
					<aui:input name="redirect" type="hidden" value="<%= renderURL.toString() %>" />
					<aui:input name="exportImportConfigurationId" type="hidden" value="<%= exportImportConfigurationId %>" />
					<aui:input name="groupId" type="hidden" value="<%= stagingGroupId %>" />
					<aui:input name="privateLayout" type="hidden" value="<%= privateLayout %>" />
					<aui:input name="layoutSetBranchName" type="hidden" value="<%= layoutSetBranchName %>" />
					<aui:input name="lastImportUserName" type="hidden" value="<%= user.getFullName() %>" />
					<aui:input name="lastImportUserUuid" type="hidden" value="<%= String.valueOf(user.getUserUuid()) %>" />
					<aui:input name="treeId" type="hidden" value="<%= treeId %>" />
					<aui:input name="<%= PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS_ALL %>" type="hidden" value="<%= true %>" />
					<aui:input name="<%= PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL %>" type="hidden" value="<%= true %>" />
					<aui:input name="<%= PortletDataHandlerKeys.PORTLET_SETUP_ALL %>" type="hidden" value="<%= true %>" />
					<aui:input name="<%= PortletDataHandlerKeys.PORTLET_USER_PREFERENCES_ALL %>" type="hidden" value="<%= true %>" />

					<%@ include file="/error/error_auth_exception.jspf" %>

					<%@ include file="/error/error_remote_export_exception.jspf" %>

					<%@ include file="/error/error_remote_options_exception.jspf" %>

					<div id="<portlet:namespace />publishOptions">
						<div class="export-dialog-tree">
							<div class="sheet">
								<div class="panel-group panel-group-flush">
									<liferay-staging:configuration-header
										exportImportConfiguration="<%= exportImportConfiguration %>"
									/>

									<liferay-staging:deletions
										cmd="<%= Constants.PUBLISH %>"
										exportImportConfigurationId="<%= exportImportConfigurationId %>"
									/>

									<c:if test="<%= !group.isCompany() && GroupCapabilityUtil.isSupportsPages(group) %>">
										<liferay-staging:select-pages
											action="<%= Constants.PUBLISH %>"
											exportImportConfigurationId="<%= exportImportConfigurationId %>"
											groupId="<%= stagingGroupId %>"
											privateLayout="<%= privateLayout %>"
											treeId="<%= treeId %>"
										/>
									</c:if>

									<liferay-staging:content
										cmd="<%= cmd %>"
										exportImportConfigurationId="<%= exportImportConfigurationId %>"
										showAllPortlets="<%= true %>"
										type="<%= stagingGroup.isStagedRemotely() ? Constants.PUBLISH_TO_REMOTE : Constants.PUBLISH_TO_LIVE %>"
									/>

									<liferay-staging:permissions
										action="<%= Constants.PUBLISH %>"
										descriptionCSSClass="permissions-description"
										exportImportConfigurationId="<%= exportImportConfigurationId %>"
										global="<%= group.isCompany() %>"
										labelCSSClass="permissions-label"
									/>

									<c:if test="<%= stagingGroup.isStagedRemotely() %>">
										<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" cssClass="options-group" label="remote-live-connection-settings">
											<liferay-staging:remote-options
												exportImportConfigurationId="<%= exportImportConfigurationId %>"
												privateLayout="<%= privateLayout %>"
											/>
										</aui:fieldset>
									</c:if>
								</div>
							</div>
						</div>

						<aui:button-row>
							<aui:button type="submit" value="save" />

							<aui:button href="<%= renderURL.toString() %>" type="cancel" />
						</aui:button-row>
					</div>
				</aui:form>
			</div>
		</clay:container-fluid>

		<aui:script>
			function <portlet:namespace />publishPages() {
				var exportImport = Liferay.component(
					'<portlet:namespace />ExportImportComponent'
				);

				var dateChecker = exportImport.getDateRangeChecker();

				if (dateChecker.validRange) {
					submitForm(document.<portlet:namespace />exportPagesFm);
				}
				else {
					exportImport.showNotification(dateChecker);
				}
			}

			Liferay.Util.toggleRadio(
				'<portlet:namespace />allApplications',
				'<portlet:namespace />showChangeGlobalConfiguration',
				['<portlet:namespace />selectApplications']
			);
			Liferay.Util.toggleRadio(
				'<portlet:namespace />allContent',
				'<portlet:namespace />showChangeGlobalContent',
				['<portlet:namespace />selectContents']
			);
			Liferay.Util.toggleRadio(
				'<portlet:namespace />publishingEventNow',
				'<portlet:namespace />publishButton',
				['<portlet:namespace />selectSchedule', '<portlet:namespace />addButton']
			);
			Liferay.Util.toggleRadio(
				'<portlet:namespace />publishingEventSchedule',
				['<portlet:namespace />selectSchedule', '<portlet:namespace />addButton'],
				'<portlet:namespace />publishButton'
			);
			Liferay.Util.toggleRadio('<portlet:namespace />rangeAll', '', [
				'<portlet:namespace />startEndDate',
				'<portlet:namespace />rangeLastInputs',
			]);
			Liferay.Util.toggleRadio(
				'<portlet:namespace />rangeDateRange',
				'<portlet:namespace />startEndDate',
				'<portlet:namespace />rangeLastInputs'
			);
			Liferay.Util.toggleRadio('<portlet:namespace />rangeLastPublish', '', [
				'<portlet:namespace />startEndDate',
				'<portlet:namespace />rangeLastInputs',
			]);
			Liferay.Util.toggleRadio(
				'<portlet:namespace />rangeLast',
				'<portlet:namespace />rangeLastInputs',
				['<portlet:namespace />startEndDate']
			);
		</aui:script>

		<aui:script use="liferay-staging-processes-export-import">
			<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/staging_processes/publish_layouts" var="publishProcessesURL">
				<portlet:param name="<%= Constants.CMD %>" value="<%= cmd %>" />
				<portlet:param name="<%= SearchContainer.DEFAULT_CUR_PARAM %>" value="<%= ParamUtil.getString(request, SearchContainer.DEFAULT_CUR_PARAM) %>" />
				<portlet:param name="<%= SearchContainer.DEFAULT_DELTA_PARAM %>" value="<%= ParamUtil.getString(request, SearchContainer.DEFAULT_DELTA_PARAM) %>" />
				<portlet:param name="groupId" value="<%= String.valueOf(stagingGroupId) %>" />
				<portlet:param name="layoutSetBranchId" value="<%= String.valueOf(layoutSetBranchId) %>" />
				<portlet:param name="layoutSetBranchName" value="<%= layoutSetBranchName %>" />
				<portlet:param name="liveGroupId" value="<%= String.valueOf(liveGroupId) %>" />
				<portlet:param name="privateLayout" value="<%= String.valueOf(privateLayout) %>" />
			</liferay-portlet:resourceURL>

			var exportImport = new Liferay.ExportImport({
				commentsNode: '#<%= PortletDataHandlerKeys.COMMENTS %>',
				deletionsNode: '#<%= PortletDataHandlerKeys.DELETIONS %>',
				form: document.<portlet:namespace />exportPagesFm,
				incompleteProcessMessageNode:
					'#<portlet:namespace />incompleteProcessMessage',
				locale: '<%= locale.toLanguageTag() %>',
				namespace: '<portlet:namespace />',
				pageTreeId: '<%= treeId %>',
				processesNode: '#publishProcesses',
				processesResourceURL:
					'<%= HtmlUtil.escapeJS(publishProcessesURL.toString()) %>',
				rangeAllNode: '#rangeAll',
				rangeDateRangeNode: '#rangeDateRange',
				rangeLastNode: '#rangeLast',
				rangeLastPublishNode: '#rangeLastPublish',
				ratingsNode: '#<%= PortletDataHandlerKeys.RATINGS %>',
				setupNode: '#<%= PortletDataHandlerKeys.PORTLET_SETUP_ALL %>',
				timeZoneOffset: <%= timeZoneOffset %>,
				userPreferencesNode:
					'#<%= PortletDataHandlerKeys.PORTLET_USER_PREFERENCES_ALL %>',
			});

			Liferay.component('<portlet:namespace />ExportImportComponent', exportImport);
		</aui:script>
	</c:otherwise>
</c:choose>