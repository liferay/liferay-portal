<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/revamp/init.jsp" %>

<liferay-staging:defineObjects />

<%
if (liveGroup == null) {
	liveGroup = group;
	liveGroupId = groupId;
}

ExportImportPreviewDisplayContext exportImportPreviewDisplayContext = new ExportImportPreviewDisplayContext("/export_import/view_import_layouts", liveGroup, groupId, request, liferayPortletResponse, liveGroupId, privateLayout);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(exportImportPreviewDisplayContext.getBackURL());

renderResponse.setTitle(exportImportPreviewDisplayContext.getImportTitle());
%>

<clay:container-fluid
	cssClass="container-form-lg"
>
	<div class="sheet">
		<span aria-hidden="true" class="loading-animation mb-9 mt-8"></span>
	</div>

	<react:component
		module="{NewImport} from exportimport-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"backURL", exportImportPreviewDisplayContext.getBackURL()
			).put(
				"commentsAndRatingsEnabled", exportImportPreviewDisplayContext.isCommentsAndRatingsEnabled()
			).put(
				"importPreviewAPIURL", exportImportPreviewDisplayContext.getImportPreviewAPIURL()
			).put(
				"importProcessAPIURL", exportImportPreviewDisplayContext.getImportProcessAPIURL()
			).put(
				"lookAndFeelEnabled", exportImportPreviewDisplayContext.isLookAndFeelEnabled()
			).put(
				"scope", exportImportPreviewDisplayContext.getScope()
			).build()
		%>'
	/>
</clay:container-fluid>