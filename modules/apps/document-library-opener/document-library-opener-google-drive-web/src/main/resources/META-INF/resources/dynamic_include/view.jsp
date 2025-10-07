<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<%
DLOpenerGoogleDriveFileReference dlOpenerGoogleDriveFileReference = (DLOpenerGoogleDriveFileReference)request.getAttribute(DLOpenerGoogleDriveWebKeys.DL_OPENER_GOOGLE_DRIVE_FILE_REFERENCE);
%>

<c:if test="<%= dlOpenerGoogleDriveFileReference != null %>">
	<portlet:resourceURL id="/document_library/google_drive_background_task_status" var="googleDriveBackgroundTaskStatusURL">
		<portlet:param name="backgroundTaskId" value="<%= String.valueOf(dlOpenerGoogleDriveFileReference.getBackgroundTaskId()) %>" />
		<portlet:param name="fileEntryId" value="<%= String.valueOf(dlOpenerGoogleDriveFileReference.getFileEntryId()) %>" />
		<portlet:param name="googleDocsRedirect" value='<%= ParamUtil.getString(request, "googleDocsRedirect") %>' />
	</portlet:resourceURL>

	<aui:script>
		(function () {
			var TIME_POLLING = 500;
			var TIME_SHOW_MSG = 2000;

			var defaultError =
				'<liferay-ui:message key="an-unexpected-error-occurred" />';
			var dialogId = '<portlet:namespace />LoadingDialog';

			var isTimeConsumed = false;

			var url;

			showStatusMessage = Liferay.lazyLoad(
				'frontend-js-web',
				({openToast}, data) => {
					openToast(data);
				}
			);

			function navigate() {
				if (url && isTimeConsumed) {
					window.location.href = url;
				}
			}

			function polling() {
				Liferay.Util.fetch('<%= googleDriveBackgroundTaskStatusURL %>', {
					method: 'POST',
				})
					.then((response) => {
						if (!response.ok) {
							throw defaultError;
						}

						return response.json();
					})
					.then((response) => {
						if (response.complete) {
							url = response.googleDocsEditURL;

							navigate();
						}
						else if (response.error) {
							throw defaultError;
						}
						else {
							setTimeout(polling, TIME_POLLING);
						}
					})
					.catch((error) => {
						showError(error);

						Liferay.Util.getWindow(dialogId).hide();
					});
			}

			function showError(message) {
				showStatusMessage({
					message: message,
					type: 'danger',
				});
			}

			<%
			String messageKey = "you-are-being-redirected-to-an-external-editor-to-edit-this-document";

			String cmd = ParamUtil.getString(request, Constants.CMD);

			if (cmd.equals(Constants.ADD)) {
				messageKey = "you-are-being-redirected-to-an-external-editor-to-create-this-document";
			}
			%>

			Liferay.Util.openModal({
				bodyHTML:
					'<p><liferay-ui:message key="<%= messageKey %>" /></p><div aria-hidden="true" class="loading-animation"></div>',
				className: 'google-docs-redirect-modal',
				containerProps: {},
				id: dialogId,
				onOpen: () => {
					setTimeout(polling, TIME_POLLING);

					setTimeout(() => {
						isTimeConsumed = true;

						navigate();
					}, TIME_SHOW_MSG);
				},
			});
		})();
	</aui:script>
</c:if>

<liferay-util:html-top>
	<aui:link href='<%= HtmlUtil.escapeAttribute(PortalUtil.getStaticResourceURL(request, StringBundler.concat(themeDisplay.getCDNBaseURL(), PortalUtil.getPathProxy(), application.getContextPath(), "/css/document_library.css"))) %>' rel="stylesheet" type="text/css" />
</liferay-util:html-top>