<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="project-versions" />
</liferay-util:include>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="${patcherProjectVersion.name}" />
	<liferay-util:param name="controller" value="project_versions" />
	<liferay-util:param name="action" value="index" />
</liferay-util:include>

<aui:model-context bean="${patcherProjectVersion}" model="<%= PatcherProjectVersion.class %>" />

<aui:field-wrapper name="name">
	${patcherProjectVersion.name}
</aui:field-wrapper>

<c:if test="${permissionChecker.isCompanyAdmin()}">
	<aui:field-wrapper name="combined-branch">
		${patcherProjectVersion.combinedBranch}
	</aui:field-wrapper>
</c:if>

<aui:field-wrapper name="tag-name">
	${patcherProjectVersion.committish}
</aui:field-wrapper>

<aui:field-wrapper name="repositoryName">
	${patcherProjectVersion.repositoryName}
</aui:field-wrapper>