<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/document_library/init.jsp" %>

<%
DLAccessFromDesktopDisplayContext dlAccessFromDesktopDisplayContext = new DLAccessFromDesktopDisplayContext(request);
%>

<div class="hide" id="<%= dlAccessFromDesktopDisplayContext.getRandomNamespace() %>webDav">
	<div class="portlet-document-library">
		<liferay-ui:message key="<%= dlAccessFromDesktopDisplayContext.getWebDAVHelpMessage() %>" />

		<liferay-learn:message
			key="webdav"
			resource="document-library-web"
		/>

		<br /><br />

		<aui:input cssClass="webdav-url-resource" id='<%= dlAccessFromDesktopDisplayContext.getRandomNamespace() + "webDavURL" %>' name="webDavURL" type="resource" value="<%= dlAccessFromDesktopDisplayContext.getWebDAVURL() %>" />

		<div class="alert alert-info">
			<liferay-ui:message arguments='<%= "<a href=" + PersonalApplicationURLUtil.getPersonalApplicationURL(request, PortletKeys.MY_ACCOUNT) + ">" + LanguageUtil.get(resourceBundle, "my-account") + "</a>" %>' key="webdav-access-requires-generation-of-a-webdav-specific-password-at-x" />
		</div>
	</div>
</div>

<aui:script>
	Liferay.Util.setPortletConfigurationIconAction(
		'<portlet:namespace />accessFromDesktop',
		() => {
			var webdavContentContainer = document.getElementById(
				'<%= dlAccessFromDesktopDisplayContext.getRandomNamespace() %>webDav'
			);

			var html = '';

			if (webdavContentContainer) {
				html = webdavContentContainer.innerHTML;

				Liferay.Util.openModal({
					bodyHTML: html,
					onOpen: function (event) {
						var webdavURLInput = document.getElementById(
							'<portlet:namespace /><%= dlAccessFromDesktopDisplayContext.getRandomNamespace() %>webDavURL'
						);

						if (webdavURLInput) {
							webdavURLInput.focus();
						}
					},
					title: '<%= UnicodeLanguageUtil.get(request, "access-from-desktop") %>',
				});
			}
		}
	);
</aui:script>