<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", "published");

String redirect = ParamUtil.getString(request, "redirect");
String backURL = HttpComponentsUtil.setParameter(currentURL, liferayPortletResponse.getNamespace() + "historyKey", "workflow");

KaleoProcess kaleoProcess = (KaleoProcess)request.getAttribute(KaleoFormsWebKeys.KALEO_PROCESS);

String workflowDefinition = KaleoFormsUtil.getWorkflowDefinition(kaleoProcess, portletSession);

String workflowDefinitionName = StringPool.BLANK;
int workflowDefinitionVersion = 0;

if (Validator.isNotNull(workflowDefinition)) {
	String[] workflowDefinitionParts = StringUtil.split(workflowDefinition, CharPool.AT);

	workflowDefinitionName = workflowDefinitionParts[0];
	workflowDefinitionVersion = GetterUtil.getInteger(workflowDefinitionParts[1]);

	if (!KaleoFormsUtil.isWorkflowDefinitionActive(themeDisplay.getCompanyId(), workflowDefinitionName, workflowDefinitionVersion)) {
		workflowDefinition = StringPool.BLANK;
	}
}
%>

<h3 class="kaleo-process-header"><liferay-ui:message key="workflow" /></h3>

<p class="kaleo-process-message"><liferay-ui:message key="please-select-or-create-a-new-workflow-definition-to-guide-the-completion-of-forms.-you-can-associate-forms-to-workflow-tasks-in-the-next-step" /></p>

<aui:field-wrapper>
	<liferay-ui:message key="selected-workflow" />:

	<%
	String workflowDefinitionDisplay = StringPool.BLANK;

	if (Validator.isNotNull(workflowDefinitionName)) {
		WorkflowDefinition kaleoWorkflowDefinition = KaleoFormsUtil.getWorkflowDefinition(themeDisplay.getCompanyId(), workflowDefinitionName, workflowDefinitionVersion);

		if (kaleoWorkflowDefinition != null) {
			workflowDefinitionDisplay = kaleoWorkflowDefinition.getTitle(themeDisplay.getLanguageId());
		}
	}
	%>

	<span class="badge badge-info" id="<portlet:namespace />workflowDefinitionDisplay"><%= HtmlUtil.escape(workflowDefinitionDisplay) %></span>

	<aui:input name="workflowDefinition" required="<%= true %>" type="hidden" value="<%= workflowDefinition %>" />

	<aui:input name="workflowDefinitionName" type="hidden" value="<%= workflowDefinitionName %>" />
	<aui:input name="workflowDefinitionVersion" type="hidden" value="<%= workflowDefinitionVersion %>" />
</aui:field-wrapper>

<liferay-ui:search-container
	searchContainer="<%= kaleoFormsAdminDisplayContext.getSearchContainer() %>"
>
	<liferay-portlet:renderURL portletName="<%= KaleoDesignerPortletKeys.KALEO_DESIGNER %>" var="addURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
		<portlet:param name="mvcPath" value="/designer/edit_kaleo_definition_version.jsp" />
		<portlet:param name="closeRedirect" value="<%= backURL %>" />
	</liferay-portlet:renderURL>

	<c:if test="<%= permissionChecker.isCompanyAdmin() %>">
		<aui:button onClick='<%= "javascript:" + liferayPortletResponse.getNamespace() + "editWorkflow('" + addURL + "');" %>' primary="<%= true %>" value="add-workflow" />
	</c:if>

	<hr class="separator" />

	<liferay-portlet:renderURL varImpl="portletURL">
		<portlet:param name="mvcPath" value="/admin/edit_kaleo_process.jsp" />
		<portlet:param name="tabs1" value="<%= tabs1 %>" />
		<portlet:param name="redirect" value="<%= redirect %>" />
		<portlet:param name="historyKey" value="workflow" />
		<portlet:param name="kaleoProcessId" value="<%= String.valueOf(kaleoFormsAdminDisplayContext.getKaleoProcessId()) %>" />
	</liferay-portlet:renderURL>

	<clay:navigation-bar
		navigationItems='<%=
			new JSPNavigationItemList(pageContext) {
				{
					add(
						navigationItem -> {
							navigationItem.setActive(kaleoFormsAdminDisplayContext.isTabs1Published());
							navigationItem.setHref(renderResponse.createRenderURL(), "mvcPath", "/admin/edit_kaleo_process.jsp", "tabs1", "published", "redirect", redirect, "historyKey", "workflow", "kaleoProcessId", kaleoFormsAdminDisplayContext.getKaleoProcessId());
							navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "published"));
						});

					add(
						navigationItem -> {
							navigationItem.setActive(kaleoFormsAdminDisplayContext.isTabs1Unpublished());
							navigationItem.setHref(renderResponse.createRenderURL(), "mvcPath", "/admin/edit_kaleo_process.jsp", "tabs1", "unpublished", "redirect", redirect, "historyKey", "workflow", "kaleoProcessId", kaleoFormsAdminDisplayContext.getKaleoProcessId());
							navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "unpublished"));
						});
				}
			}
		%>'
	/>

	<c:choose>
		<c:when test="<%= kaleoFormsAdminDisplayContext.isTabs1Published() %>">
			<liferay-ui:search-container-row
				className="com.liferay.portal.kernel.workflow.WorkflowDefinition"
				modelVar="workflowDefinitionVar"
			>
				<liferay-ui:search-container-row-parameter
					name="backURL"
					value="<%= backURL %>"
				/>

				<c:if test="<%= kaleoProcess != null %>">
					<liferay-ui:search-container-row-parameter
						name="kaleoProcessId"
						value="<%= String.valueOf(kaleoProcess.getKaleoProcessId()) %>"
					/>
				</c:if>

				<liferay-ui:search-container-column-text
					name="title"
					value="<%= HtmlUtil.escape(workflowDefinitionVar.getTitle(themeDisplay.getLanguageId())) %>"
				/>

				<liferay-ui:search-container-column-text
					name="version"
					value="<%= String.valueOf(workflowDefinitionVar.getVersion()) %>"
				/>

				<liferay-ui:search-container-column-jsp
					align="right"
					cssClass="entry-action"
					path="/admin/process/workflow_action.jsp"
				/>
			</liferay-ui:search-container-row>
		</c:when>
		<c:otherwise>
			<liferay-ui:search-container-row
				className="com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion"
				keyPropertyName="kaleoDefinitionVersionId"
				modelVar="kaleoDefinitionVersion"
			>
				<liferay-ui:search-container-row-parameter
					name="backURL"
					value="<%= backURL %>"
				/>

				<c:if test="<%= kaleoProcess != null %>">
					<liferay-ui:search-container-row-parameter
						name="kaleoProcessId"
						value="<%= String.valueOf(kaleoProcess.getKaleoProcessId()) %>"
					/>
				</c:if>

				<liferay-ui:search-container-row-parameter
					name="name"
					value="<%= kaleoDefinitionVersion.getName() %>"
				/>

				<liferay-ui:search-container-column-text
					name="title"
					value="<%= HtmlUtil.escape(kaleoDefinitionVersion.getTitle(themeDisplay.getLanguageId())) %>"
				/>

				<liferay-ui:search-container-row-parameter
					name="version"
					value="<%= kaleoDefinitionVersion.getVersion() %>"
				/>

				<liferay-ui:search-container-column-jsp
					align="right"
					cssClass="entry-action"
					path="/admin/process/kaleo_draft_definition_action.jsp"
				/>
			</liferay-ui:search-container-row>
		</c:otherwise>
	</c:choose>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>

<aui:script>
	Liferay.on(
		'<portlet:namespace />chooseWorkflow',
		(event) => {
			const workflowDefinition = event.name + '@' + event.version;

			document.getElementById(
				'<portlet:namespace />workflowDefinition'
			).value = workflowDefinition;

			document.getElementById(
				'<portlet:namespace />workflowDefinitionDisplay'
			).innerHTML = Liferay.Util.escapeHTML(event.title);

			const kaleoFormsAdmin = Liferay.component(
				'<portlet:namespace />KaleoFormsAdmin'
			);

			kaleoFormsAdmin.saveInPortletSession({
				workflowDefinition: workflowDefinition,
			});

			kaleoFormsAdmin.updateNavigationControls();
		},
		['aui-base']
	);

	window['<portlet:namespace />editWorkflow'] = (uri) => {
		Liferay.Util.openModal({
			containerProps: {},
			iframeBodyCssClass: '',
			title: '<liferay-ui:message key="workflow" />',
			url: uri,
		});
	};
</aui:script>