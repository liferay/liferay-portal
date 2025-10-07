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

<liferay-ui:header
	title="create-fix"
/>

<aui:model-context bean="<%= null %>" model="<%= PatcherFix.class %>" />

<portlet:actionURL name="/patcher/add_fixes" var="addPatcherFixURL" />

<liferay-frontend:edit-form
	action="<%= addPatcherFixURL %>"
	fluid="<%= true %>"
	method="post"
	name="fm"
>
	<liferay-frontend:edit-form-body>
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

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

		<div>
			<aui:input label="content" name="patcherFixName" type="textarea" />

			<react:component
				module="{PopoverTooltip} from osb-patcher-web"
				props='<%=
					HashMapBuilder.<String, Object>put(
						"label", LanguageUtil.get(request, "content")
					).put(
						"name", "patcherFixName"
					).build()
				%>'
			/>
		</div>

		<aui:input label="branch-name" name="committish" />

		<aui:input label="github-url" name="gitRemoteURL" />

		<aui:input name="workaround" type="checkbox" />
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= redirect %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script>
	var select = document.getElementById(
		'<portlet:namespace />patcherProjectVersionId'
	);

	Liferay.provide(
		window,
		'<portlet:namespace />productVersionOnChange',
		function (productVersionId) {
			Liferay.Patcher.populateProjectVersionField(
				productVersionId,
				select,
				<%= PatcherProjectVersionUtil.getPatcherProjectVersionsJSONObject(themeDisplay.getCompanyId()) %>
			);
		},
		['aui-base']
	);

	AUI().ready(function () {
		var A = AUI();

		var productVersionId = A.one(
			'#<portlet:namespace />patcherProductVersionId'
		).val();

		Liferay.Patcher.populateProjectVersionField(
			productVersionId,
			select,
			<%= PatcherProjectVersionUtil.getPatcherProjectVersionsJSONObject(themeDisplay.getCompanyId()) %>
		);
	});
</aui:script>