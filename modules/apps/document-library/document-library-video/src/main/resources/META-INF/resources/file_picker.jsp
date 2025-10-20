<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DLVideoExternalShortcut dlVideoExternalShortcut = (DLVideoExternalShortcut)request.getAttribute(DLVideoExternalShortcut.class.getName());
String onFilePickCallback = (String)request.getAttribute(DLVideoWebKeys.ON_FILE_PICK_CALLBACK);
%>

<liferay-util:html-top
	outputKey="com.liferay.document.library.video#/file_picker.jsp"
>
	<aui:link hashedFile="<%= true %>" href="document-library-video/css/main.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<aui:input name="contentType" type="hidden" value="<%= ContentTypes.APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML %>" />

<div class="form-group">
	<aui:input disabled="<%= true %>" label="video-url" name="dlVideoExternalShortcutURL" value="<%= (dlVideoExternalShortcut != null) ? dlVideoExternalShortcut.getURL() : null %>" wrapperCssClass="mb-0" />

	<p class="form-text"><liferay-ui:message key="video-url-help" /></p>

	<div class="mt-4 video-preview video-preview-framed video-preview-sm">
		<div class="video-preview-aspect-ratio">
			<c:choose>
				<c:when test="<%= dlVideoExternalShortcut != null %>">
					<%= dlVideoExternalShortcut.renderHTML(request) %>
				</c:when>
				<c:otherwise>
					<div class="video-preview-placeholder">
						<clay:icon
							symbol="video"
						/>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<liferay-portlet:resourceURL id="/document_library_video/get_dl_video_external_shortcut_fields" portletName="<%= DLVideoPortletKeys.DL_VIDEO %>" var="getDLVideoExternalShortcutFieldsURL" />

	<react:component
		module="{DLVideoExternalShortcutDLFilePicker} from document-library-video"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"dlVideoExternalShortcutHTML", (dlVideoExternalShortcut != null) ? dlVideoExternalShortcut.renderHTML(request) : ""
			).put(
				"dlVideoExternalShortcutURL", (dlVideoExternalShortcut != null) ? dlVideoExternalShortcut.getURL() : ""
			).put(
				"getDLVideoExternalShortcutFieldsURL", getDLVideoExternalShortcutFieldsURL
			).put(
				"namespace", PortalUtil.getPortletNamespace(DLVideoPortletKeys.DL_VIDEO)
			).put(
				"onFilePickCallback", onFilePickCallback
			).build()
		%>'
	/>
</div>