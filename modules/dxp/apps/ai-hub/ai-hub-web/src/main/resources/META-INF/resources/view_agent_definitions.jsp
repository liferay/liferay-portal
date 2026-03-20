<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewAgentDefinitionsDisplayContext viewAgentDefinitionsDisplayContext = (ViewAgentDefinitionsDisplayContext)request.getAttribute(ViewAgentDefinitionsDisplayContext.class.getName());
%>

<div class="ai-hub-section-header align-items-center container-fluid d-flex">
	<div class="ml-2 p-3">
		<h2><liferay-ui:message key="agents" /></h2>
	</div>
</div>

<frontend-data-set:headless-display
	apiURL="<%= viewAgentDefinitionsDisplayContext.getAPIURL() %>"
	creationMenu="<%= viewAgentDefinitionsDisplayContext.getCreationMenu() %>"
	fdsActionDropdownItems="<%= viewAgentDefinitionsDisplayContext.getFDSActionDropdownItems() %>"
	id="<%= AIHubFDSNames.AGENT_DEFINITIONS %>"
	itemsPerPage="<%= 20 %>"
	propsTransformer="{AgentDefinitionItemTitlePropsTransformer} from ai-hub-web"
	style="fluid"
/>