<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
FaroProject faroProject = (FaroProject)request.getAttribute(FaroProject.class.getName());
%>

<c:choose>
	<c:when test="<%= faroProject != null %>">
		<div class="sidebar-header">
			<div class="h4"><%= HtmlUtil.escape(faroProject.getName()) %></div>
		</div>

		<div class="sidebar-body">
			<div class="h5"><liferay-ui:message key="group-id" /></div>

			<p>
				<%= faroProject.getGroupId() %>
			</p>

			<div class="h5"><liferay-ui:message key="corp-project-uuid" /></div>

			<p>
				<%= faroProject.getCorpProjectUuid() %>
			</p>

			<div class="h5"><liferay-ui:message key="corp-project-name" /></div>

			<p>
				<%= faroProject.getCorpProjectName() %>
			</p>

			<div class="h5"><liferay-ui:message key="wedeploy-key" /></div>

			<p>
				<%= faroProject.getWeDeployKey() %>
			</p>

			<div class="h5"><liferay-ui:message key="last-access-date" /></div>

			<p>
				<%= faroProject.getLastAccessDate() %>
			</p>

			<div class="h5"><liferay-ui:message key="individuals-usage" /></div>

			<p>
				<%= StringBundler.concat(faroProject.getIndividualsCountSinceLastAnniversary(), " / ", faroProject.getIndividualsLimit(), " (", faroProject.getIndividualsUsage(), "%)") %>
			</p>

			<div class="h5"><liferay-ui:message key="page-views-usage" /></div>

			<p>
				<%= StringBundler.concat(faroProject.getPageViewsCountSinceLastAnniversary(), " / ", faroProject.getPageViewsLimit(), " (", faroProject.getPageViewsUsage(), "%)") %>
			</p>

			<div class="h5"><liferay-ui:message key="subscription" /></div>

			<p>
				<%= faroProject.getSubscription() %>
			</p>

			<div class="h5"><liferay-ui:message key="serverLocation" /></div>

			<p>
				<%= faroProject.getServerLocation() %>
			</p>
		</div>
	</c:when>
	<c:otherwise>
		<div class="sidebar-header">

			<%
			long[] rowIds = ParamUtil.getLongValues(request, "rowIds");
			%>

			<div class="h4"><liferay-ui:message arguments="<%= rowIds.length %>" key="x-items-are-selected" /></div>
		</div>
	</c:otherwise>
</c:choose>