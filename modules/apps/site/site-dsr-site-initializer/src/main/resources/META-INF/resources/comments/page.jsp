<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/comments/init.jsp" %>

<c:if test="<%= roomId != 0 %>">
	<react:component
		module="{RoomComments} from site-dsr-site-initializer"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"addCommentURL", addCommentURL
			).put(
				"deleteCommentURL", deleteCommentURL
			).put(
				"editCommentURL", editCommentURL
			).put(
				"editorConfig", editorConfig
			).put(
				"getCommentsURL", getCommentsURL
			).put(
				"readOnly", readOnly
			).build()
		%>'
	/>
</c:if>