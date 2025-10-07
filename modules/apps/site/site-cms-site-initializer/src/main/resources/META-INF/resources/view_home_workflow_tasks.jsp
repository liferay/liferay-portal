<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewHomeWorkflowTasksDisplayContext viewHomeWorkflowTasksDisplayContext = (ViewHomeWorkflowTasksDisplayContext)request.getAttribute(ViewHomeWorkflowTasksDisplayContext.class.getName());
%>

<div class="cms-section">
	<react:component
		module="{ViewWorkflowTasks} from site-cms-site-initializer"
		props="<%= viewHomeWorkflowTasksDisplayContext.getReactData() %>"
	/>
</div>