<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");
%>

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="fixes" />
		<liferay-util:param name="patcherProductVersionId" value="<%= String.valueOf(patcherProductVersionId) %>" />
	</liferay-util:include>
</c:if>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="create-fix" />
	<liferay-util:param name="mvcRenderCommandName" value="/patcher/index_fixes" />
	<liferay-util:param name="patcherProductVersionId" value="<%= String.valueOf(patcherProductVersionId) %>" />
</liferay-util:include>

<aui:model-context bean="<%= null %>" model="<%= PatcherFix.class %>" />

<portlet:actionURL name="/patcher/add_fixes" var="addPatcherFixURL" />

<aui:form action="<%= addPatcherFixURL %>" method="post" name="fm">
	<portlet:renderURL var="viewPatcherFixesURL">
		<portlet:param name="mvcRenderCommandName" value="/patcher/index_fixes" />
		<portlet:param name="patcherProductVersionId" value="<%= String.valueOf(patcherProductVersionId) %>" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="<%= viewPatcherFixesURL %>" />

	<aui:select label="product-version" name="patcherProductVersionId" onChange='<%= liferayPortletResponse.getNamespace() + "productVersionOnChange(this.value);" %>' required="<%= true %>" showEmptyOption="<%= true %>">

		<%
		for (PatcherProductVersion patcherProductVersion : PatcherProductVersionUtil.getPatcherProductVersions()) {
		%>

			<aui:option label="<%= patcherProductVersion.getName() %>" value="<%= patcherProductVersion.getPatcherProductVersionId() %>" />

		<%
		}
		%>

	</aui:select>

	<aui:select label="project-version" name="patcherProjectVersionId" required="<%= true %>" />

	<aui:input label="content" name="name" type="textarea" />

	<aui:input label="branch-name" name="committish" />

	<aui:input label="github-url" name="gitRemoteURL" />

	<aui:input name="workaround" type="checkbox" />

	<aui:button-row>
		<aui:button type="submit" value="add" />

		<aui:button href="<%= Validator.isNotNull(redirect) ? redirect : viewPatcherFixesURL %>" value="cancel" />
	</aui:button-row>
</aui:form>

<%
Map<Long, List<PatcherProjectVersion>> patcherProjectVersions = PatcherProjectVersionUtil.getPatcherProductVersionIdPatcherProjectVersions();

JSONObject patcherProjectVersionsJSONObject = JSONFactoryUtil.createJSONObject(JSONFactoryUtil.looseSerializeDeep(patcherProjectVersions));
%>

<aui:script>
	var select = document.getElementById("<portlet:namespace />patcherProjectVersionId");

	Liferay.provide(
		window,
		'<portlet:namespace />productVersionOnChange',
		function(productVersionId) {
			Liferay.Patcher.populateProjectVersionField(productVersionId, select, <%= patcherProjectVersionsJSONObject %>);
		},
		['aui-base']
	);

	AUI().ready(
		function() {
			var A = AUI();

			var productVersionId = A.one('#<portlet:namespace />patcherProductVersionId').val();

			Liferay.Patcher.populateProjectVersionField(productVersionId, select, <%= patcherProjectVersionsJSONObject %>);
		}
	);

	YUI().ready(
		'aui-popover',
		function(Y) {
			var align_points = [Y.WidgetPositionAlign.LC, Y.WidgetPositionAlign.RC];
			var tickets = document.getElementById('<portlet:namespace />name');
			var trigger = Y.one('#<portlet:namespace />name');

			Liferay.Patcher.getTicketLinksPopover(Y, align_points, tickets, trigger)
		}
	);
</aui:script>