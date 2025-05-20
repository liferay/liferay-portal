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

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/WEB-INF/jsp/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="builds" />
	</liferay-util:include>
</c:if>

<liferay-util:include page="/WEB-INF/jsp/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="edit-build" />
	<liferay-util:param name="controller" value="builds" />
	<liferay-util:param name="action" value="index" />
</liferay-util:include>

<aui:model-context bean="${patcherBuild}" model="<%= PatcherBuild.class %>" />

<c:if test="${not empty overlappingProjectVersionFixedIssues}">
	<aui:field-wrapper>
		<liferay-ui:icon
			image="../api/exception"
			message=""
		/>

		<liferay-ui:message key='${AlloyLanguageUtil.format("the-tickets-x-will-be-removed-from-the-list-since-they-are-included-in-the-project-version", overlappingProjectVersionFixedIssues)}' />
	</aui:field-wrapper>
</c:if>

<portlet:actionURL var="updatePatcherBuildURL">
	<portlet:param name="controller" value="builds" />
	<portlet:param name="action" value="update" />
</portlet:actionURL>

<aui:form action="${updatePatcherBuildURL}" method="post">
	<aui:input name="redirect" type="hidden" value="${redirect}" />
	<aui:input name="id" type="hidden" value="${patcherBuild.patcherBuildId}" />

	<aui:field-wrapper name="modifiedDate">
		<fmt:formatDate
			type="both"
			value="${patcherBuild.modifiedDate}"
		/>
	</aui:field-wrapper>

	<aui:field-wrapper name="createdBy">
		${patcherBuild.userName}
	</aui:field-wrapper>

	<aui:field-wrapper name="buildId">
		${patcherBuild.patcherBuildId}
	</aui:field-wrapper>

	<aui:field-wrapper name="version">
		${patcherBuild.keyVersion}
	</aui:field-wrapper>

	<aui:select disabled="${true}" label="product-version" name="patcherProductVersionId" required="${true}">
		<c:forEach items="${patcherProductVersions}" var="patcherProductVersion">
			<aui:option label="${patcherProductVersion.getName()}" value="${patcherProductVersion.getPatcherProductVersionId()}" />
		</c:forEach>
	</aui:select>

	<aui:select disabled="${true}" label="project-version" name="patcherProjectVersionId" required="${true}" showEmptyOption="${true}">
		<c:forEach items="${patcherProjectVersions}" var="patcherProjectVersion">
			<aui:option label="${patcherProjectVersion.name}" value="${patcherProjectVersion.patcherProjectVersionId}" />
		</c:forEach>
	</aui:select>

	<aui:input inputCssClass="osb-patcher-input-wide osb-patcher-read-only" label="tickets-list" name="patcherBuildName" readonly="${true}" type="textarea" value="${patcherBuild.name}" />

	<aui:input inputCssClass="osb-patcher-input-wide osb-patcher-read-only" label="account-code" name="patcherBuildAccountEntryCode" readonly="${true}" type="text" value="${patcherBuildAccountEntryCode}" />

	<aui:input inputCssClass="osb-patcher-input-wide" name="supportTicket" type="text" />

	<aui:select name="type">
		<aui:option label="${PatcherBuildConstants.LABEL_OFFICIAL}" value="${PatcherBuildConstants.TYPE_OFFICIAL}" />
		<aui:option label="${PatcherBuildConstants.LABEL_DEBUG}" value="${PatcherBuildConstants.TYPE_DEBUG}" />
		<aui:option label="${PatcherBuildConstants.LABEL_IGNORE}" value="${PatcherBuildConstants.TYPE_IGNORE}" />
	</aui:select>

	<aui:input name="mergeOnly" type="checkbox" value="${patcherBuildMergeOnly}" />

	<aui:input name="smokeTestOnly" type="checkbox" wrapperCssClass="osb-patcher-display-none" />

	<aui:button-row>
		<aui:button type="submit" value="update" />

		<portlet:renderURL var="viewPatcherBuildsURL">
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="index" />
		</portlet:renderURL>

		<aui:button href="${(not empty redirect) ? redirect : viewPatcherBuildsURL}" value="cancel" />
	</aui:button-row>
</aui:form>