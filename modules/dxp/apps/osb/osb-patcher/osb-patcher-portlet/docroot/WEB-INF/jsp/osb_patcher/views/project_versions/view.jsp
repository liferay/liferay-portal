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

<%@ include file="/WEB-INF/jsp/osb_patcher/views/init.jsp" %>

<liferay-util:include page="/WEB-INF/jsp/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="project-versions" />
</liferay-util:include>

<liferay-util:include page="/WEB-INF/jsp/osb_patcher/views/header.jsp" servletContext="<%= application %>">
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