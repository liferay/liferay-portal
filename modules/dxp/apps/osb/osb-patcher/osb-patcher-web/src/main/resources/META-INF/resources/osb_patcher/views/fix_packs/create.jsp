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
	title="create-fix-pack"
/>

<aui:model-context bean="<%= null %>" model="<%= PatcherFixPack.class %>" />

<portlet:actionURL name="/patcher/add_fix_packs" var="addPatcherFixPackURL" />

<liferay-frontend:edit-form
	action="<%= addPatcherFixPackURL %>"
	fluid="<%= true %>"
	method="post"
	name="fm"
>
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<liferay-frontend:edit-form-body>
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
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= redirect %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />patcherFixPackFieldsOnChange',
		function () {
			var A = AUI();

			var patcherFixComponentId = A.one(
				'#<portlet:namespace />patcherFixComponentId'
			).val();

			if (patcherFixComponentId == null || patcherFixComponentId <= 0) {
				return;
			}

			var patcherProjectVersionId = A.one(
				'#<portlet:namespace />patcherProjectVersionId'
			).val();

			if (patcherProjectVersionId == null || patcherProjectVersionId <= 0) {
				return;
			}

			var filteredPatcherFixPacks = JSON.parse(
				'<%= HtmlUtil.escapeJS(JSONFactoryUtil.looseSerialize(PatcherFixPackUtil.getFilteredPatcherFixPacksByComponentAndProjectVersion())) %>'
			);

			for (var i in filteredPatcherFixPacks) {
				var filteredPatcherFixPack = filteredPatcherFixPacks[i];

				if (
					filteredPatcherFixPack.patcherFixComponentId ==
						patcherFixComponentId &&
					filteredPatcherFixPack.patcherProjectVersionId ==
						patcherProjectVersionId
				) {
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