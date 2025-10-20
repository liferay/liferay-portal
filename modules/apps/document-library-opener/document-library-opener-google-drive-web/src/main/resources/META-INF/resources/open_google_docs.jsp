<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DLOpenerGoogleDriveFileReference dlOpenerGoogleDriveFileReference = (DLOpenerGoogleDriveFileReference)request.getAttribute(DLOpenerGoogleDriveWebKeys.DL_OPENER_GOOGLE_DRIVE_FILE_REFERENCE);

String googleDocsEditURL = ParamUtil.getString(request, "googleDocsEditURL");
String googleDocsRedirect = ParamUtil.getString(request, "googleDocsRedirect");
%>

<!DOCTYPE html>
<html>
	<head>
		<meta content="initial-scale=1.0, width=device-width" name="viewport" />

		<aui:link cssClass="lfr-css-file" href="<%= HtmlUtil.escapeAttribute(themeDisplay.getClayCSSURL()) %>" id="liferayAUICSS" rel="stylesheet" type="text/css" />
		<aui:link hashedFile="<%= true %>" href="document-library-opener-google-drive-web/css/google_docs.css" id="liferayGoogleDriveCSS" rel="stylesheet" type="text/css" />
	</head>

	<body>
		<clay:content-row
			cssClass="google-docs-toolbar"
			padded="<%= true %>"
			verticalAlign="center"
		>
			<clay:content-col
				expand="<%= true %>"
			>
				<div class="autofit-section">
					<portlet:actionURL name="/document_library/edit_in_google_docs" var="checkInURL">
						<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.CHECKIN %>" />
						<portlet:param name="redirect" value="<%= googleDocsRedirect %>" />
						<portlet:param name="fileEntryId" value="<%= String.valueOf(dlOpenerGoogleDriveFileReference.getFileEntryId()) %>" />
					</portlet:actionURL>

					<form action="<%= checkInURL %>" method="post">
						<clay:button
							icon="angle-left"
							id="closeAndCheckinBtn"
							label='<%= LanguageUtil.format(resourceBundle, "save-and-return-to-x", themeDisplay.getSiteGroupName()) %>'
							small="<%= true %>"
							type="submit"
						/>
					</form>
				</div>
			</clay:content-col>

			<clay:content-col>
				<portlet:actionURL name="/document_library/edit_in_google_docs" var="cancelCheckoutURL">
					<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.CANCEL_CHECKOUT %>" />
					<portlet:param name="redirect" value="<%= googleDocsRedirect %>" />
					<portlet:param name="fileEntryId" value="<%= String.valueOf(dlOpenerGoogleDriveFileReference.getFileEntryId()) %>" />
				</portlet:actionURL>

				<form action="<%= cancelCheckoutURL %>" method="post">
					<clay:button
						id="discardChangesBtn"
						label="discard-changes"
						small="<%= true %>"
						type="submit"
					/>
				</form>
			</clay:content-col>
		</clay:content-row>

		<iframe class="google-docs-iframe" frameborder="0" id="<portlet:namespace />gDocsIFrame" src="<%= GoogleDriveBackgroundTaskConstants.DOCS_GOOGLE_COM_URL + HtmlUtil.escapeAttribute(googleDocsEditURL) %>"></iframe>
	</body>
</html>