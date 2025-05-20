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
		<liferay-util:param name="tabs1" value="fixes" />
	</liferay-util:include>
</c:if>

<liferay-util:include page="/WEB-INF/jsp/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="edit-fix" />
	<liferay-util:param name="controller" value="fixes" />
	<liferay-util:param name="action" value="index" />
</liferay-util:include>

<aui:model-context bean="${patcherFix}" model="<%= PatcherFix.class %>" />

<portlet:actionURL var="updatePatcherFixURL">
	<portlet:param name="controller" value="fixes" />
	<portlet:param name="action" value="update" />
</portlet:actionURL>

<aui:form action="${updatePatcherFixURL}" method="post" name="fm">
	<portlet:renderURL var="viewPatcherFixesURL">
		<portlet:param name="controller" value="fixes" />
		<portlet:param name="action" value="index" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="${viewPatcherFixesURL}" />
	<aui:input name="id" type="hidden" value="${patcherFix.patcherFixId}" />

	<aui:field-wrapper name="modifiedDate">
		<fmt:formatDate
			type="both"
			value="${patcherFix.modifiedDate}"
		/>
	</aui:field-wrapper>

	<aui:field-wrapper name="createdBy">
		${patcherFix.userName}
	</aui:field-wrapper>

	<aui:field-wrapper name="fixId">
		${patcherFix.patcherFixId}
	</aui:field-wrapper>

	<aui:field-wrapper name="version">
		${patcherFix.keyVersion}
	</aui:field-wrapper>

	<aui:field-wrapper name="patcher-status">
		${patcherFixStatus}
	</aui:field-wrapper>

	<aui:select disabled="${true}" label="product-version" name="patcherProductVersionId" required="${true}">
		<c:forEach items="${patcherProductVersions}" var="patcherProductVersion">
			<aui:option label="${patcherProductVersion.getName()}" value="${patcherProductVersion.getPatcherProductVersionId()}" />
		</c:forEach>
	</aui:select>

	<aui:select disabled="${true}" label="project-version" name="patcherProjectVersionId" onChange="${renderResponse.namespace}portalVersionOnChange(this.value);" required="${true}" showEmptyOption="${true}">
		<c:forEach items="${patcherProjectVersions}" var="patcherProjectVersion">
			<aui:option label="${patcherProjectVersion.name}" value="${patcherProjectVersion.patcherProjectVersionId}" />
		</c:forEach>
	</aui:select>

	<aui:field-wrapper name="git-hash">
		<a href="${gitHubURL}" target="_blank">${patcherFix.gitHash}</a>
	</aui:field-wrapper>

	<aui:input inputCssClass="osb-patcher-input-wide osb-patcher-read-only" label="content" name="patcherFixName" readonly="${true}" type="textarea" value="${patcherFix.name}" />

	<aui:input label="branch-name" name="committish" />

	<aui:input label="github-url" name="gitRemoteURL" />

	<aui:input name="workaround" type="checkbox" value="${patcherFix.type == PatcherFixConstants.TYPE_WORKAROUND}" />

	<aui:button-row>
		<aui:button type="submit" value="update" />

		<aui:button href="${(not empty redirect) ? redirect : viewPatcherFixesURL}" value="cancel" />

		<c:if test='${PatcherPermission.contains(themeDisplay, patcherFix, "editFixPackFields")}'>
			<portlet:renderURL var="editPatcherFixFixPackFieldsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
				<portlet:param name="controller" value="fixes" />
				<portlet:param name="action" value="editFixPackFields" />
				<portlet:param name="id" value="${patcherFix.patcherFixId}" />
			</portlet:renderURL>

			<c:set value='${AlloyLanguageUtil.formatUnicode("edit-fix-packs")}' var="editPatcherFixFixPackFieldsURLTitle" />

			<c:set value="javascript:Liferay.Patcher.openWindow('${editPatcherFixFixPackFieldsURL}', '${editPatcherFixFixPackFieldsURLTitle}', true, 800)" var="editPatcherFixFixPackFieldsURL" />

			<aui:button onClick="${editPatcherFixFixPackFieldsURL}" value="edit-fix-packs" />
		</c:if>
	</aui:button-row>
</aui:form>