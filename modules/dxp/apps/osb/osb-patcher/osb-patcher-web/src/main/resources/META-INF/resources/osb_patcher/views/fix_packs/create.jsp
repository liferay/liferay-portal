<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");
%>

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="fix-packs" />
	</liferay-util:include>
</c:if>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="create-fix-pack" />
	<liferay-util:param name="mvcRenderCommandName" value="/patcher/index_fix_packs" />
</liferay-util:include>

<aui:model-context bean="<%= null %>" model="<%= PatcherFixPack.class %>" />

<portlet:actionURL name="/patcher/add_fix_packs" var="addPatcherFixPackURL" />

<aui:form action="<%= addPatcherFixPackURL %>" method="post">
	<portlet:renderURL var="viewPatcherFixPacksURL">
		<portlet:param name="mvcRenderCommandName" value="/patcher/index_fix_packs" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="<%= viewPatcherFixPacksURL %>" />

	<aui:select label="project-version" name="patcherProjectVersionId" onChange='<%= liferayPortletResponse.getNamespace() + "patcherFixPackFieldsOnChange();" %>' required="<%= true %>" showEmptyOption="<%= true %>">

		<%
		for (PatcherProjectVersion patcherProjectVersion : PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersions()) {
		%>

			<aui:option label="<%= patcherProjectVersion.getName() %>" value="<%= patcherProjectVersion.getPatcherProjectVersionId() %>" />

		<%
		}
		%>

	</aui:select>

	<aui:select label="component" name="patcherFixComponentId" onChange='<%= liferayPortletResponse.getNamespace() + "patcherFixPackFieldsOnChange();" %>' required="<%= true %>" showEmptyOption="<%= true %>">

		<%
		for (PatcherFixComponent patcherFixComponent : PatcherFixComponentLocalServiceUtil.getPatcherFixComponents()) {
		%>

			<aui:option label="<%= patcherFixComponent.getName() %>" value="<%= patcherFixComponent.getPatcherFixComponentId() %>" />

		<%
		}
		%>

	</aui:select>

	<span class="aui-helper-hidden displaying-version" id="<portlet:namespace />displayingVersion">
		<aui:input label="initial-version" name="version" type="text" value="" />
	</span>

	<aui:button-row>
		<aui:button type="submit" value="add" />

		<aui:button href="<%= Validator.isNotNull(redirect) ? redirect : viewPatcherFixPacksURL %>" value="cancel" />
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

			var filteredPatcherFixPacks = JSON.parse('<%= HtmlUtil.escapeJS(JSONFactoryUtil.looseSerialize(PatcherFixPackUtil.getFilteredPatcherFixPacksByComponentAndProjectVersion())) %>');

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