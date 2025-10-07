<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-ui:message key="use-the-username-and-password-below-when-authenticating-your-webdav-client" />

<br /><br />

<%
User selectedUser = userDisplayContext.getSelectedUser();
%>

<div>
	<react:component
		module="{WebdavInputCopyButton} from users-admin-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"fields",
				Arrays.asList(
					HashMapBuilder.<String, Object>put(
						"id", liferayPortletResponse.getNamespace() + "webDAVUusername"
					).put(
						"label", LanguageUtil.get(request, "web-dav-username")
					).put(
						"value", selectedUser.getUserId()
					).build(),
					HashMapBuilder.<String, Object>put(
						"id", liferayPortletResponse.getNamespace() + "webDAVPassword"
					).put(
						"label", LanguageUtil.get(request, "web-dav-password")
					).put(
						"value", renderRequest.getParameter("webDAVPassword")
					).build())
			).build()
		%>'
	/>
</div>