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
	<liferay-util:param name="title" value="create-fix-pack" />
	<liferay-util:param name="controller" value="fix_packs" />
	<liferay-util:param name="action" value="index" />
</liferay-util:include>

<aui:model-context bean="${patcherFixPack}" model="<%= PatcherFixPack.class %>" />

<portlet:actionURL var="addPatcherFixPackURL">
	<portlet:param name="controller" value="fix_packs" />
	<portlet:param name="action" value="add" />
</portlet:actionURL>

<aui:form action="${addPatcherFixPackURL}" method="post">
	<portlet:renderURL var="viewPatcherFixPacksURL">
		<portlet:param name="controller" value="fix_packs" />
		<portlet:param name="action" value="index" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="${viewPatcherFixPacksURL}" />

	<aui:select label="project-version" name="patcherProjectVersionId" onChange="${renderResponse.namespace}patcherFixPackFieldsOnChange();" required="${true}" showEmptyOption="${true}">
		<c:forEach items="${patcherProjectVersions}" var="patcherProjectVersion">
			<aui:option label="${patcherProjectVersion.name}" value="${patcherProjectVersion.patcherProjectVersionId}" />
		</c:forEach>
	</aui:select>

	<aui:select label="component" name="patcherFixComponentId" onChange="${renderResponse.namespace}patcherFixPackFieldsOnChange();" required="${true}" showEmptyOption="${true}">
		<c:forEach items="${patcherFixComponents}" var="patcherFixComponent">
			<aui:option value="${patcherFixComponent.patcherFixComponentId}">${patcherFixComponent.name}</aui:option>
		</c:forEach>
	</aui:select>

	<span class="aui-helper-hidden displaying-version" id="${renderResponse.namespace}displayingVersion">
		<aui:input label="initial-version" name="patcherFixPackVersion" type="text" value="" />
	</span>

	<aui:button-row>
		<aui:button type="submit" value="add" />

		<aui:button href="${(not empty redirect) ? redirect : viewPatcherFixPacksURL}" value="cancel" />
	</aui:button-row>
</aui:form>

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />patcherFixPackFieldsOnChange',
		function() {
			var A = AUI();

			var patcherFixComponentId = A.one('#<portlet:namespace />patcherFixComponentId').val();

			if ((patcherFixComponentId == null) || (patcherFixComponentId <= 0)) {
				return;
			}

			var patcherProjectVersionId = A.one('#<portlet:namespace />patcherProjectVersionId').val();

			if ((patcherProjectVersionId == null) || (patcherProjectVersionId <= 0)) {
				return;
			}

			var filteredPatcherFixPacks = JSON.parse('${filteredPatcherFixPacksJSON}');

			for (var i in filteredPatcherFixPacks) {
				var filteredPatcherFixPack = filteredPatcherFixPacks[i];

				if ((filteredPatcherFixPack.patcherFixComponentId == patcherFixComponentId) && (filteredPatcherFixPack.patcherProjectVersionId == patcherProjectVersionId)) {
					A.one('#<portlet:namespace />patcherFixPackVersion').val('');

					A.one('#<portlet:namespace />displayingVersion').hide();

					return;
				}
			}

			A.one('#<portlet:namespace />displayingVersion').show();
		},
		['aui-base']
	);
</aui:script>