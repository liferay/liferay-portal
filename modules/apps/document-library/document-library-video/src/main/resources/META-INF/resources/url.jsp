<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String eventName = (String)request.getAttribute(DLVideoWebKeys.EVENT_NAME);
%>

<liferay-util:html-top
	outputKey="com.liferay.document.library.video#/url.jsp"
>
	<aui:link hashedFile="<%= true %>" href="document-library-video/css/main.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<div class="lfr-form-content">
	<clay:sheet>
		<div class="panel-group panel-group-flush">
			<aui:input disabled="<%= true %>" label="video-url" name="dlVideoExternalShortcutURL" wrapperCssClass="mb-0" />

			<p class="form-text"><liferay-ui:message key="video-url-help" /></p>

			<aui:button disabled="<%= true %>" primary="<%= true %>" value="add" />

			<div class="mt-4 video-preview">
				<div class="video-preview-aspect-ratio">
					<div class="video-preview-placeholder">
						<clay:icon
							symbol="video"
						/>
					</div>
				</div>
			</div>

			<liferay-portlet:resourceURL id="/document_library_video/get_dl_video_external_shortcut_fields" portletName="<%= DLVideoPortletKeys.DL_VIDEO %>" var="getDLVideoExternalShortcutFieldsURL" />

			<react:component
				module="{DLVideoExternalShortcutURLItemSelectorView} from document-library-video"
				props='<%=
					HashMapBuilder.<String, Object>put(
						"eventName", eventName
					).put(
						"getDLVideoExternalShortcutFieldsURL", getDLVideoExternalShortcutFieldsURL
					).put(
						"namespace", PortalUtil.getPortletNamespace(DLVideoPortletKeys.DL_VIDEO)
					).put(
						"returnType", VideoEmbeddableHTMLItemSelectorReturnType.class.getName()
					).build()
				%>'
			/>
		</div>
	</clay:sheet>
</div>