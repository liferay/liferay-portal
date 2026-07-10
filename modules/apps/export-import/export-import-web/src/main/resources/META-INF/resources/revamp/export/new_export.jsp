<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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

ExportImportPreviewDisplayContext exportImportPreviewDisplayContext = (ExportImportPreviewDisplayContext)request.getAttribute(ExportImportWebKeys.EXPORT_IMPORT_PREVIEW_DISPLAY_CONTEXT);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(exportImportPreviewDisplayContext.getBackURL());

renderResponse.setTitle(exportImportPreviewDisplayContext.getExportTitle());
%>

<clay:container-fluid
	cssClass="container-form-lg"
>
	<div class="sheet">
		<span aria-hidden="true" class="loading-animation mb-9 mt-8"></span>
	</div>

	<react:component
		module="{NewExport} from exportimport-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"backURL", exportImportPreviewDisplayContext.getBackURL()
			).put(
				"commentsAndRatingsEnabled", exportImportPreviewDisplayContext.isCommentsAndRatingsEnabled()
			).put(
				"exportPreview", exportImportPreviewDisplayContext.getExportPreviewJSONObject()
			).put(
				"exportPreviewAPIURL", exportImportPreviewDisplayContext.getExportPreviewAPIURL()
			).put(
				"exportProcessAPIURL", exportImportPreviewDisplayContext.getExportProcessAPIURL()
			).put(
				"lookAndFeelEnabled", exportImportPreviewDisplayContext.isLookAndFeelEnabled()
			).put(
				"pageTreeModalConfiguration",
				HashMapBuilder.<String, Object>put(
					"liveGroupId", liveGroupId
				).put(
					"pageSize", PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN
				).put(
					"privateLayoutsAvailable", liveGroup.isPrivateLayoutsEnabled() && liveGroup.hasPrivateLayouts()
				).build()
			).build()
		%>'
	/>
</clay:container-fluid>