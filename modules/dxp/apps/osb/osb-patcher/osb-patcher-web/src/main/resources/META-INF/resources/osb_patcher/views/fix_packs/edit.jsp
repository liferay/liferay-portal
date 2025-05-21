<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="fix-packs" />
	</liferay-util:include>
</c:if>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="edit-fix-pack" />
	<liferay-util:param name="controller" value="fix_packs" />
	<liferay-util:param name="action" value="index" />
</liferay-util:include>

<aui:model-context bean="${patcherFixPack}" model="<%= PatcherFixPack.class %>" />

<portlet:actionURL var="updatePatcherFixPackURL">
	<portlet:param name="controller" value="fix_packs" />
	<portlet:param name="action" value="update" />
</portlet:actionURL>

<aui:form action="${updatePatcherFixPackURL}" method="post">
	<portlet:renderURL var="viewPatcherFixPacksURL">
		<portlet:param name="controller" value="fix_packs" />
		<portlet:param name="action" value="index" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="${viewPatcherFixPacksURL}" />
	<aui:input name="id" type="hidden" value="${patcherFixPack.patcherFixPackId}" />

	<c:set value="${patcherFixPack.patcherFixPackId > 0}" var="disabled" />

	<aui:field-wrapper name="git-hash">
		<a href="${gitHubURL}" target="_blank">${patcherFix.gitHash}</a>
	</aui:field-wrapper>

	<aui:select disabled="${true}" label="project-version" name="patcherProjectVersionId" required="${true}" showEmptyOption="${true}">
		<c:forEach items="${patcherProjectVersions}" var="patcherProjectVersion">
			<aui:option label="${patcherProjectVersion.name}" value="${patcherProjectVersion.patcherProjectVersionId}" />
		</c:forEach>
	</aui:select>

	<aui:select disabled="${true}" label="component" name="patcherFixComponentId" required="${true}" showEmptyOption="${true}">
		<c:forEach items="${patcherFixComponents}" var="patcherFixComponent">
			<aui:option value="${patcherFixComponent.patcherFixComponentId}">${patcherFixComponent.name}</aui:option>
		</c:forEach>
	</aui:select>

	<aui:input name="patcherFixPackVersion" type="hidden" value="${patcherFixPack.version}" />

	<aui:field-wrapper name="version">
		${patcherFixPack.version}
	</aui:field-wrapper>

	<c:set value="${patcherFixPack.status == WorkflowConstants.STATUS_FIX_PACK_RELEASED}" var="released" />

	<aui:select disabled="${released}" name="status" showEmptyOption="${false}">
		<c:if test="${patcherFixPack.status != WorkflowConstants.STATUS_FIX_PACK_RELEASED}">
			<aui:option label="${WorkflowConstants.LABEL_FIX_PACK_UNDER_DEVELOPMENT}" value="${WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT}" />
			<aui:option label="${WorkflowConstants.LABEL_FIX_PACK_FROZEN}" value="${WorkflowConstants.STATUS_FIX_PACK_FROZEN}" />
		</c:if>

		<c:if test="${patcherFixPack.status != WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT}">
			<aui:option label="${WorkflowConstants.LABEL_FIX_PACK_RELEASED}" value="${WorkflowConstants.STATUS_FIX_PACK_RELEASED}" />
		</c:if>
	</aui:select>

	<c:if test="${patcherFixPack.releasedDate != null}">
		<aui:field-wrapper name="released-date">
			<fmt:formatDate
				value="${patcherFixPack.releasedDate}"
			/>
		</aui:field-wrapper>
	</c:if>

	<aui:input disabled="${released}" name="requirements" />

	<aui:button-row>
		<aui:button disabled="${released}" type="submit" value="update" />

		<aui:button href="${(not empty redirect) ? redirect : viewPatcherFixPacksURL}" value="cancel" />

		<c:if test="${not empty patcherFix.gitHash}">
			<portlet:actionURL var="buildPatcherBuildURL">
				<portlet:param name="controller" value="fix_packs" />
				<portlet:param name="action" value="build" />
				<portlet:param name="id" value="${patcherFixPack.patcherFixPackId}" />
				<portlet:param name="redirect" value="${viewPatcherFixPacksURL}" />
			</portlet:actionURL>

			<aui:button href="${buildPatcherBuildURL}" value="build" />
		</c:if>

		<c:if test="${patcherFixPack.patcherBuildId > 0}">
			<portlet:renderURL var="viewPatcherBuildURL">
				<portlet:param name="controller" value="builds" />
				<portlet:param name="action" value="view" />
				<portlet:param name="id" value="${patcherFixPack.patcherBuildId}" />
			</portlet:renderURL>

			<aui:button href="${viewPatcherBuildURL}" value="view-build" />
		</c:if>

		<c:if test="${(patcherFixPack.status == WorkflowConstants.STATUS_FIX_PACK_FROZEN) && (empty patcherFix.gitHash)}">
			<portlet:actionURL var="mergePatcherFixPackURL">
				<portlet:param name="controller" value="fix_packs" />
				<portlet:param name="action" value="setBuild" />
				<portlet:param name="id" value="${patcherFixPack.patcherFixPackId}" />
				<portlet:param name="redirect" value="${(not empty redirect) ? redirect : viewPatcherFixPacksURL}" />
			</portlet:actionURL>

			<aui:button href="${mergePatcherFixPackURL}" value="merge" />
		</c:if>
	</aui:button-row>
</aui:form>

<c:if test="${not empty jenkinsRequestParameters}">
	<aui:layout>
		<aui:column columnWidth="20">
			<aui:field-wrapper name="jenkins-request-parameters" />
		</aui:column>

		<aui:column columnWidth="80">
			<aui:field-wrapper name="value" />
		</aui:column>
	</aui:layout>

	<c:forEach items="${jenkinsRequestParameters}" var="jenkinsRequestParameter">
		<aui:layout>
			<aui:column columnWidth="20">
				${jenkinsRequestParameter.key}
			</aui:column>

			<aui:column columnWidth="80">
				${jenkinsRequestParameter.value}
			</aui:column>
		</aui:layout>
	</c:forEach>
</c:if>