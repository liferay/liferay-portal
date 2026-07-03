<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/publications/init.jsp" %>

<%
ViewChangesDisplayContext viewChangesDisplayContext = (ViewChangesDisplayContext)request.getAttribute(CTWebKeys.VIEW_CHANGES_DISPLAY_CONTEXT);

String backURL = ParamUtil.getString(
	request, "backURL",
	PortletURLBuilder.createRenderURL(
		renderResponse
	).setMVCRenderCommandName(
		"/change_tracking/view_changes"
	).setParameter(
		"ctCollectionId", viewChangesDisplayContext.getCtCollectionId()
	).buildString());

portletDisplay.setURLBack(backURL);

portletDisplay.setShowBackIcon(true);

renderResponse.setTitle(LanguageUtil.get(request, "review-change"));
%>

<div class="publications-view-changes-wrapper">
	<div>
		<react:component
			module="{ChangeTrackingChangesToolbar} from change-tracking-web"
			props="<%= viewChangesDisplayContext.getToolbarReactData() %>"
		/>
	</div>

	<div class="sidenav-content">
		<react:component
			module="{ChangeTrackingChangeView} from change-tracking-web"
			props="<%= viewChangesDisplayContext.getReactData() %>"
		/>
	</div>
</div>

<aui:script>
	function <%= viewChangesDisplayContext.getMyWorkflowTaskPortletNamespace() %>refreshPortlet() {
		Liferay.fire('<portlet:namespace />workflowTaskUpdated');
	}
</aui:script>