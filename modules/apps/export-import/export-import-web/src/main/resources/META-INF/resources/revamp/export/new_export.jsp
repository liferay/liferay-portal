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

ExportImportProcessDisplayContext exportImportProcessDisplayContext = (ExportImportProcessDisplayContext)request.getAttribute(ExportImportWebKeys.EXPORT_IMPORT_PROCESS_DISPLAY_CONTEXT);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(exportImportProcessDisplayContext.getBackURL());

renderResponse.setTitle(exportImportProcessDisplayContext.getExportTitle());
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
				"backURL", exportImportProcessDisplayContext.getBackURL()
			).put(
				"commentsAndRatingsEnabled", exportImportProcessDisplayContext.isCommentsAndRatingsEnabled()
			).put(
				"exportPreview", exportImportProcessDisplayContext.getExportPreviewJSONObject()
			).put(
				"exportPreviewAPIURL", exportImportProcessDisplayContext.getExportPreviewAPIURL()
			).put(
				"exportProcessAPIURL", exportImportProcessDisplayContext.getExportProcessAPIURL()
			).put(
				"lookAndFeelEnabled", exportImportProcessDisplayContext.isLookAndFeelEnabled()
			).put(
				"pageTreeModalConfiguration",
				HashMapBuilder.<String, Object>put(
					"groupId", liveGroupId
				).put(
					"pageSize", PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN
				).put(
					"privateLayoutsAvailable", liveGroup.isPrivateLayoutsEnabled() && liveGroup.hasPrivateLayouts()
				).build()
			).build()
		%>'
	/>
</clay:container-fluid>