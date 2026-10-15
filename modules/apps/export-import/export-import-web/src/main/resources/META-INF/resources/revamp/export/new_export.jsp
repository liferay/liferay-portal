<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/revamp/init.jsp" %>

<%
ExportImportProcessDisplayContext exportImportProcessDisplayContext = (ExportImportProcessDisplayContext)request.getAttribute(ExportImportWebKeys.EXPORT_IMPORT_PROCESS_DISPLAY_CONTEXT);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(exportImportProcessDisplayContext.getBackURL());

renderResponse.setTitle(exportImportProcessDisplayContext.getExportTitle());
%>

<clay:container-fluid
	cssClass="container-form-lg"
	size="lg"
>
	<div class="sheet">
		<span aria-hidden="true" class="loading-animation mb-9 mt-8"></span>
	</div>

	<c:choose>
		<c:when test="<%= exportImportProcessDisplayContext.isStaticSite() %>">
			<react:component
				module="{NewStaticSiteExport} from exportimport-web"
				props='<%=
					HashMapBuilder.<String, Object>put(
						"availableLanguages", exportImportProcessDisplayContext.getAvailableLanguagesJSONArray()
					).put(
						"backURL", exportImportProcessDisplayContext.getBackURL()
					).put(
						"defaultLanguageId", exportImportProcessDisplayContext.getDefaultLanguageId()
					).put(
						"exportProcessAPIURL", exportImportProcessDisplayContext.getExportProcessAPIURL()
					).put(
						"pageTreeModalConfiguration", exportImportProcessDisplayContext.getPageTreeModalConfiguration()
					).build()
				%>'
			/>
		</c:when>
		<c:otherwise>
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
						"exportPreviewSitesAPIURL", exportImportProcessDisplayContext::getExportPreviewSitesAPIURL
					).put(
						"exportProcessAPIURL", exportImportProcessDisplayContext.getExportProcessAPIURL()
					).put(
						"pageTreeModalConfiguration", exportImportProcessDisplayContext.getPageTreeModalConfiguration()
					).put(
						"siteSelectionEnabled", exportImportProcessDisplayContext.isSiteSelectionEnabled()
					).put(
						"timeZoneId", timeZone.getID()
					).build()
				%>'
			/>
		</c:otherwise>
	</c:choose>
</clay:container-fluid>