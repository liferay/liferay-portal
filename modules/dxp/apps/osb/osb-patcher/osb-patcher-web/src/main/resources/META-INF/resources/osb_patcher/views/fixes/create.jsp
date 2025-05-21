<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="fixes" />
		<liferay-util:param name="patcherProductVersionId" value="${patcherProductVersionId}" />
	</liferay-util:include>
</c:if>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="create-fix" />
	<liferay-util:param name="controller" value="fixes" />
	<liferay-util:param name="action" value="index" />
	<liferay-util:param name="patcherProductVersionId" value="${patcherProductVersionId}" />
</liferay-util:include>

<aui:model-context bean="${patcherFix}" model="<%= PatcherFix.class %>" />

<portlet:actionURL var="addPatcherFixURL">
	<portlet:param name="controller" value="fixes" />
	<portlet:param name="action" value="add" />
</portlet:actionURL>

<aui:form action="${addPatcherFixURL}" method="post" name="fm">
	<portlet:renderURL var="viewPatcherFixesURL">
		<portlet:param name="controller" value="fixes" />
		<portlet:param name="action" value="index" />
		<portlet:param name="patcherProductVersionId" value="${patcherProductVersionId}" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="${viewPatcherFixesURL}" />
	<aui:input name="id" type="hidden" value="${patcherFix.patcherFixId}" />

	<aui:field-wrapper name="version">
		${patcherFix.keyVersion}
	</aui:field-wrapper>

	<aui:select label="product-version" name="patcherProductVersionId" onChange="${renderResponse.namespace}productVersionOnChange(this.value);" required="${true}" showEmptyOption="${true}">
		<c:forEach items="${patcherProductVersions}" var="patcherProductVersion">
			<aui:option label="${patcherProductVersion.getName()}" value="${patcherProductVersion.getPatcherProductVersionId()}" />
		</c:forEach>
	</aui:select>

	<aui:select label="project-version" name="patcherProjectVersionId" required="${true}" />

	<aui:input inputCssClass="osb-patcher-input-wide" label="content" name="patcherFixName" type="textarea" value="${patcherFix.name}" />

	<aui:input label="branch-name" name="committish" />

	<aui:input label="github-url" name="gitRemoteURL" />

	<aui:input name="workaround" type="checkbox" value="${patcherFix.type == PatcherFixConstants.TYPE_WORKAROUND}" />

	<aui:button-row>
		<aui:button type="submit" value="add" />

		<aui:button href="${(not empty redirect) ? redirect : viewPatcherFixesURL}" value="cancel" />
	</aui:button-row>
</aui:form>

<aui:script>
	var select = document.getElementById("<portlet:namespace />patcherProjectVersionId");

	Liferay.provide(
		window,
		'<portlet:namespace />productVersionOnChange',
		function(productVersionId) {
			Liferay.Patcher.populateProjectVersionField(productVersionId, select, ${patcherProjectVersionsJSON});
		},
		['aui-base']
	);

	AUI().ready(
		function() {
			var A = AUI();

			var productVersionId = A.one('#<portlet:namespace />patcherProductVersionId').val();

			Liferay.Patcher.populateProjectVersionField(productVersionId, select, ${patcherProjectVersionsJSON});
		}
	);

	YUI().ready(
		'aui-popover',
		function(Y) {
			var align_points = [Y.WidgetPositionAlign.LC, Y.WidgetPositionAlign.RC];
			var tickets = document.getElementById('_1_WAR_osbpatcherportlet_patcherFixName');
			var trigger = Y.one('#_1_WAR_osbpatcherportlet_patcherFixName');

			Liferay.Patcher.getTicketLinksPopover(Y, align_points, tickets, trigger)
		}
	);
</aui:script>