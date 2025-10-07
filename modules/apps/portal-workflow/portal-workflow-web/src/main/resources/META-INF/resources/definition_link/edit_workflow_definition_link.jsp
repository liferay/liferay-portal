<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/definition_link/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

String randomNamespace = (String)row.getParameter("randomNamespace");

WorkflowDefinitionLinkSearchEntry workflowDefinitionLinkSearchEntry = (WorkflowDefinitionLinkSearchEntry)row.getObject();

String className = workflowDefinitionLinkSearchEntry.getClassName();
%>

<portlet:actionURL name="/portal_workflow/update_workflow_definition_link" var="updateWorkflowDefinitionLinkURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<div class="d-none" id="<%= randomNamespace %>formContainer">
	<aui:form action="<%= updateWorkflowDefinitionLinkURL %>" cssClass="workflow-definition-form" method="post">
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="namespace" type="hidden" value="<%= randomNamespace %>" />
		<aui:input name="groupId" type="hidden" value="<%= workflowDefinitionLinkDisplayContext.getGroupId() %>" />
		<aui:input name="resource" type="hidden" value="<%= workflowDefinitionLinkSearchEntry.getResource() %>" />
		<aui:input name="editMode" type="hidden" value="false" />

		<%
		String workflowAssignedValue = "";
		%>

		<aui:select cssClass="form-control-sm workflow-definition-form" data-qa-id='<%= "select" + workflowDefinitionLinkSearchEntry.getResource() %>' label="<%= StringPool.BLANK %>" name='<%= "workflowDefinitionName@" + className %>' title="workflow-definition">

			<%
			WorkflowDefinition defaultWorkflowDefinition = workflowDefinitionLinkDisplayContext.fetchDefaultWorkflowDefinition(className);
			String defaultWorkflowDefinitionLabel = workflowDefinitionLinkDisplayContext.getDefaultWorkflowDefinitionLabel(className);
			%>

			<aui:option><%= HtmlUtil.escape(defaultWorkflowDefinitionLabel) %></aui:option>

			<%
			for (WorkflowDefinition workflowDefinition : workflowDefinitionLinkDisplayContext.getWorkflowDefinitions()) {
				if (!workflowDefinitionLinkDisplayContext.isControlPanelPortlet() && workflowDefinitionLinkDisplayContext.isWorkflowDefinitionEquals(workflowDefinition, defaultWorkflowDefinition)) {
					continue;
				}

				boolean selected = workflowDefinitionLinkDisplayContext.isWorkflowDefinitionSelected(workflowDefinition, className);

				String value = workflowDefinitionLinkDisplayContext.getWorkflowDefinitionValue(workflowDefinition);

				if (selected) {
					workflowAssignedValue = value;
				}
			%>

				<aui:option label="<%= HtmlUtil.escape(workflowDefinitionLinkDisplayContext.getWorkflowDefinitionLabel(workflowDefinition)) %>" selected="<%= selected %>" value="<%= value %>" />

			<%
			}
			%>

		</aui:select>

		<aui:input name="workflowAssignedValue" type="hidden" value="<%= workflowAssignedValue %>" />
	</aui:form>
</div>

<div id="<%= randomNamespace %>definitionLabel">
	<%= HtmlUtil.escape(workflowDefinitionLinkSearchEntry.getWorkflowDefinitionLabel()) %>

	<c:if test="<%= !workflowDefinitionLinkDisplayContext.isWorkflowDefinitionActive(workflowDefinitionLinkSearchEntry) %>">
		<clay:label
			label="not-published"
		/>
	</c:if>
</div>