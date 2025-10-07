<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
long patcherFixId = ParamUtil.getLong(request, "patcherFixId");

PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(patcherFixId);
%>

<aui:model-context bean="<%= patcherFix %>" model="<%= PatcherFix.class %>" />

<portlet:actionURL name="/patcher/update_comments_field_fixes" var="updatePatcherFixCommentsFieldURL" />

<liferay-frontend:edit-form
	action="<%= updatePatcherFixCommentsFieldURL %>"
	fluid="<%= true %>"
	method="post"
>
	<aui:input name="patcherFixId" type="hidden" value="<%= patcherFix.getPatcherFixId() %>" />

	<liferay-frontend:edit-form-body>
		<aui:input name="comments" type="textarea" />
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>