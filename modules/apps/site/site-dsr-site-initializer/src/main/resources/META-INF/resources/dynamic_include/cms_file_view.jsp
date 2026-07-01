<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<c:if test="<%= DSRRoomUtil.isReadOnly(themeDisplay.getScopeGroupId(), themeDisplay.getPermissionChecker()) %>">
	<aui:style>
		.portlet-document-library [data-qa-id="creationMenuNewButton"] {
			display: none !important;
		}
	</aui:style>
</c:if>

<aui:script type="module">
	import(
		'<%= FrontendESMUtil.buildURL(themeDisplay, "site-dsr-site-initializer") %>'
	).then(function (module) {
		module.CMSFileSelectorEventHandler();
	});
</aui:script>