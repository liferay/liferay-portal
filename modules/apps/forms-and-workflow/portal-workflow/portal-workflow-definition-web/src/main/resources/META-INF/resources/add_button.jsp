<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/init.jsp" %>

<portlet:renderURL var="viewDefinitionsURL">
	<portlet:param name="mvcPath" value="/view.jsp" />
	<portlet:param name="tabs1" value="workflow-definitions" />
</portlet:renderURL>

<portlet:renderURL var="addWorkflowDefinitionURL">
	<portlet:param name="mvcPath" value="/edit_workflow_definition.jsp" />
	<portlet:param name="tabs1" value="workflow-definitions" />
	<portlet:param name="redirect" value="<%= viewDefinitionsURL %>" />
	<portlet:param name="backURL" value="<%= viewDefinitionsURL %>" />
</portlet:renderURL>

<%
List<AddMenuItem> addMenuItems = new ArrayList<>();

addMenuItems.add(new AddMenuItem(HtmlUtil.escape(LanguageUtil.get(request, "upload-definition")), addWorkflowDefinitionURL.toString()));
%>

<c:if test='<%= DeployManagerUtil.isDeployed("kaleo-designer-portlet") %>'>

	<%
	String taglibHREF = "javascript:Liferay.Util.getOpener()." + renderResponse.getNamespace() + "openKaleoDesigner('', '0', '', Liferay.Util.getWindowName());";

	addMenuItems.add(new AddMenuItem(LanguageUtil.format(request, "add-new-x", "definition"), taglibHREF));
	%>

</c:if>

<c:if test="<%= !addMenuItems.isEmpty() && workflowDefinitionDisplayContext.canPublishWorkflowDefinition() %>">
	<liferay-frontend:add-menu
		addMenuItems="<%= addMenuItems %>"
	/>
</c:if>

<c:if test='<%= DeployManagerUtil.isDeployed("kaleo-designer-portlet") %>'>
	<aui:script>
		Liferay.provide(
			window,
			'<portlet:namespace />openKaleoDesigner',
			function(workflowDefinitionName, workflowDefinitionVersion, saveCallback, openerWindowName) {
				Liferay.Util.openKaleoDesignerPortlet(
					{
						availablePropertyModels: 'Liferay.KaleoDesigner.AVAILABLE_PROPERTY_MODELS.KALEO_FORMS_EDIT',
						name: workflowDefinitionName,
						openerWindowName: openerWindowName,
						portletResourceNamespace: '<%= renderResponse.getNamespace() %>',
						saveCallback: saveCallback,
						version: workflowDefinitionVersion,
						versionLabel: '<liferay-ui:message key="version" />'
					}
				);
			},
			['aui-base']
		);
	</aui:script>
</c:if>