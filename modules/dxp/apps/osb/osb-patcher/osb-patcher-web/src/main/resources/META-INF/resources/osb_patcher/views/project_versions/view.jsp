<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
long patcherProjectVersionId = ParamUtil.getLong(request, "patcherProjectVersionId");

PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherProjectVersionId);
%>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="project-versions" />
</liferay-util:include>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="<%= patcherProjectVersion.getName() %>" />
	<liferay-util:param name="mvcRenderCommandName" value="/patcher/index_project_versions" />
</liferay-util:include>

<aui:field-wrapper label="name">
	<%= patcherProjectVersion.getName() %>
</aui:field-wrapper>

<c:if test="<%= permissionChecker.isCompanyAdmin() %>">
	<aui:field-wrapper label="combined-branch">
		<%= patcherProjectVersion.getCombinedBranch() %>
	</aui:field-wrapper>
</c:if>

<aui:field-wrapper label="tag-name">
	<%= patcherProjectVersion.getCommittish() %>
</aui:field-wrapper>

<aui:field-wrapper label="repository-name">
	<%= patcherProjectVersion.getRepositoryName() %>
</aui:field-wrapper>