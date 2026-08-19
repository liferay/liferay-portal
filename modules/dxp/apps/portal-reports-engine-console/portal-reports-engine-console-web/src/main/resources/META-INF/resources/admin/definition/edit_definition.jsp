<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<portlet:renderURL var="viewDefinitionsURL">
	<portlet:param name="tabs1" value="definitions" />
</portlet:renderURL>

<%
String backURL = ParamUtil.getString(request, "backURL", viewDefinitionsURL);

Definition definition = (Definition)request.getAttribute(ReportsEngineWebKeys.DEFINITION);

long definitionId = BeanParamUtil.getLong(definition, request, "definitionId");

long sourceId = BeanParamUtil.getLong(definition, request, "sourceId");

String reportName = StringPool.BLANK;

if (definition != null) {
	reportName = definition.getReportName();
}

if (reportsEngineDisplayContext.isAdminPortlet()) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(backURL);

	renderResponse.setTitle((definition != null) ? LanguageUtil.format(request, "edit-x", definition.getName(locale), false) : LanguageUtil.get(request, "new-report-definition"));
}
else {
	portletDisplay.setShowBackIcon(false);

	renderResponse.setTitle(definition.getName(locale));
}
%>

<portlet:renderURL var="definitionsURL">
	<portlet:param name="tabs1" value="definitions" />
</portlet:renderURL>

<div class="report-message"></div>

<portlet:actionURL name="/reports_admin/edit_definition" var="actionURL">
	<portlet:param name="mvcPath" value="/admin/definition/edit_definition.jsp" />
</portlet:actionURL>

<aui:form action="<%= actionURL %>" cssClass="container-fluid container-fluid-max-xl" enctype="multipart/form-data" method="post" name="fm">
	<liferay-ui:error exception="<%= DefinitionFileException.InvalidDefinitionFile.class %>" message="please-enter-a-valid-file" />
	<liferay-ui:error exception="<%= DefinitionNameException.class %>" message="please-enter-a-valid-name" />

	<aui:model-context bean="<%= definition %>" model="<%= Definition.class %>" />

	<aui:input name="redirect" type="hidden" value="<%= definitionsURL %>" />
	<aui:input name="viewDefinitionsURL" type="hidden" value="<%= viewDefinitionsURL %>" />
	<aui:input name="definitionId" type="hidden" />

	<c:if test="<%= definition != null %>">
		<div class="management-bar management-bar-light navbar navbar-expand-md">
			<clay:container-fluid>
				<ul class="m-auto navbar-nav"></ul>

				<ul class="middle navbar-nav">
					<li class="nav-item">
						<span class="text-muted">
							<span class="definition-id-label"><liferay-ui:message key="id" />:</span>

							<span class="definition-id-value"><%= definition.getDefinitionId() %></span>
						</span>
					</li>
				</ul>

				<ul class="end m-auto navbar-nav"></ul>
			</clay:container-fluid>
		</div>
	</c:if>

	<div class="sheet">
		<div class="panel-group panel-group-flush">
			<aui:fieldset>
				<aui:input label="definition-name" name="name" />

				<aui:input name="description" />

				<aui:select label="data-source-name" name="sourceId">
					<aui:option label="<%= ReportDataSourceType.PORTAL.getValue() %>" selected="<%= sourceId == 0 %>" value="<%= 0 %>" />

					<%
					for (Source source : SourceServiceUtil.getSources(themeDisplay.getSiteGroupId(), null, null, false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {
					%>

						<aui:option label="<%= HtmlUtil.escape(source.getName(locale)) %>" selected="<%= sourceId == source.getSourceId() %>" value="<%= source.getSourceId() %>" />

					<%
					}
					%>

				</aui:select>

				<aui:field-wrapper>
					<aui:input cssClass='<%= "lfr-reports__template-report-input " + (Validator.isNull(reportName) ? "display-block" : "display-none") %>' name="templateReport" type="file" />

					<span class="lfr-reports__template-report-name <%= Validator.isNull(reportName) ? "display-none" : "display-block" %>">
						<%= HtmlUtil.escape(reportName) %>

						<img class="remove-existing-report" src="<%= themeDisplay.getPathThemeImages() %>/arrows/02_x.png" />

						<aui:input name="reportName" type="hidden" value="<%= reportName %>" />
					</span>

					<aui:button cssClass="cancel-update-template-report" value="cancel" />
				</aui:field-wrapper>
			</aui:fieldset>

			<aui:fieldset collapsible="<%= true %>" cssClass="options-group" label="report-parameters">
				<aui:input cssClass="report-parameters" name="reportParameters" type="hidden" />

				<clay:row>
					<clay:col
						md="4"
					>
						<aui:input cssClass="parameters-key" name="key" size="20" type="text" />
					</clay:col>

					<clay:col
						md="4"
					>

						<%
						Calendar calendar = CalendarFactoryUtil.getCalendar(timeZone, locale);
						%>

						<aui:field-wrapper>
							<aui:input cssClass="parameters-value parameters-value-field-set" name="value" size="20" type="text" />

							<liferay-ui:input-date
								cssClass="parameters-input-date"
								dayParam="parameterDateDay"
								dayValue="<%= calendar.get(Calendar.DATE) %>"
								disabled="<%= false %>"
								firstDayOfWeek="<%= calendar.getFirstDayOfWeek() - 1 %>"
								monthParam="parameterDateMonth"
								monthValue="<%= calendar.get(Calendar.MONTH) %>"
								yearParam="parameterDateYear"
								yearValue="<%= calendar.get(Calendar.YEAR) %>"
							/>
						</aui:field-wrapper>
					</clay:col>

					<clay:col
						md="2"
					>
						<aui:select cssClass="parameters-input-type" label="type" name="type">
							<aui:option label="text" value="text" />
							<aui:option label="date" value="date" />
						</aui:select>
					</clay:col>

					<clay:col
						cssClass="align-items-center d-flex"
						md="2"
					>
						<aui:button-row cssClass="c-mt-1">
							<aui:button cssClass="add-parameter" value="add-parameter" />
						</aui:button-row>
					</clay:col>
				</clay:row>

				<aui:field-wrapper>
					<div class="report-tags"></div>
				</aui:field-wrapper>
			</aui:fieldset>

			<c:if test="<%= definition == null %>">
				<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="permissions">
					<liferay-ui:input-permissions
						modelName="<%= Definition.class.getName() %>"
					/>
				</aui:fieldset>
			</c:if>
		</div>
	</div>

	<aui:button-row>
		<portlet:renderURL var="viewURL">
			<portlet:param name="mvcPath" value="/admin/view.jsp" />
			<portlet:param name="tabs1" value="definitions" />
		</portlet:renderURL>

		<aui:button cssClass="btn-lg" type="submit" value='<%= (definition != null) ? "update" : "save" %>' />

		<c:if test="<%= definition != null %>">
			<c:if test="<%= DefinitionPermissionChecker.contains(permissionChecker, definition, ReportsActionKeys.ADD_REPORT) %>">
				<aui:button cssClass="btn-lg" onClick='<%= liferayPortletResponse.getNamespace() + "addReport();" %>' value="add-report" />

				<aui:button cssClass="btn-lg" onClick='<%= liferayPortletResponse.getNamespace() + "addScheduler();" %>' value="add-schedule" />
			</c:if>

			<aui:button cssClass="btn-lg" onClick='<%= liferayPortletResponse.getNamespace() + "deleteDefinition();" %>' value="delete" />
		</c:if>

		<aui:button cssClass="btn-lg" href="<%= viewURL %>" type="cancel" />
	</aui:button-row>
</aui:form>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"namespace", liferayPortletResponse.getNamespace()
		).put(
			"parameters", reportsEngineDisplayContext.getReportParameters()
		).build()
	%>'
	module="{reportParameters} from portal-reports-engine-console-web"
/>

<aui:script>
	function <portlet:namespace />addReport() {
		submitForm(
			document.<portlet:namespace />fm,
			'<portlet:renderURL><portlet:param name="mvcPath" value="/admin/report/generate_report.jsp" /><portlet:param name="definitionId" value="<%= String.valueOf(definitionId) %>" /></portlet:renderURL>'
		);
	}

	function <portlet:namespace />addScheduler() {
		submitForm(
			document.<portlet:namespace />fm,
			'<portlet:renderURL><portlet:param name="mvcPath" value="/admin/report/edit_schedule.jsp" /><portlet:param name="definitionId" value="<%= String.valueOf(definitionId) %>" /></portlet:renderURL>'
		);
	}

	function <portlet:namespace />deleteDefinition() {
		Liferay.Util.openConfirmModal({
			message:
				'<%= UnicodeLanguageUtil.get(request, "are-you-sure-you-want-to-delete-this") %>',
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					submitForm(
						document.<portlet:namespace />fm,
						'<portlet:actionURL name="/reports_admin/delete_definition"><portlet:param name="redirect" value="<%= definitionsURL %>" /></portlet:actionURL>'
					);
				}
			},
		});
	}
</aui:script>