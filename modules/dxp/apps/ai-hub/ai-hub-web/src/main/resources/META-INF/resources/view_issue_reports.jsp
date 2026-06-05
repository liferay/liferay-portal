<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewIssueReportsDisplayContext viewIssueReportsDisplayContext = (ViewIssueReportsDisplayContext)request.getAttribute(ViewIssueReportsDisplayContext.class.getName());
%>

<div class="ai-hub-issue-reports-content">
	<div>
		<react:component
			module="{IssueReportsCards} from ai-hub-web"
			props="<%= viewIssueReportsDisplayContext.getCardsReactData() %>"
		/>
	</div>

	<div class="ai-hub-issue-reports-table">
		<frontend-data-set:headless-display
			apiURL="<%= viewIssueReportsDisplayContext.getAPIURL() %>"
			id="<%= AIHubFDSNames.ISSUE_REPORTS %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{IssueReportsPropsTransformer} from ai-hub-web"
			style="fluid"
		/>
	</div>
</div>