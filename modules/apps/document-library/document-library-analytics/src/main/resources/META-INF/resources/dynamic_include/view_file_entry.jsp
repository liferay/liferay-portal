<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<%
FileEntry fileEntry = (FileEntry)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_ENTRY);
%>

<aui:script senna="temporary" type="text/javascript">
	if (window.Analytics) {
		window.<%= DocumentLibraryAnalyticsConstants.JS_PREFIX %>isViewFileEntry = true;
	}
</aui:script>

<aui:script>
	function <portlet:namespace />sendDocumentPreviewedAnalyticsEvent() {
		if (window.Analytics) {
			Analytics.send('documentPreviewed', 'Document', {
				externalReferenceCode:
					'<%= fileEntry.getExternalReferenceCode() %>',
				fileEntryId: '<%= fileEntry.getFileEntryId() %>',
				groupId: '<%= fileEntry.getGroupId() %>',
				fileEntryUUID: '<%= fileEntry.getUuid() %>',
				title: '<%= HtmlUtil.escapeJS(fileEntry.getTitle()) %>',
				version: '<%= fileEntry.getVersion() %>',
			});
		}
	}

	if (Liferay.SPA && document.readyState === 'complete') {
		<portlet:namespace />sendDocumentPreviewedAnalyticsEvent();
	}

	window.addEventListener(
		'load',
		<portlet:namespace />sendDocumentPreviewedAnalyticsEvent
	);
</aui:script>