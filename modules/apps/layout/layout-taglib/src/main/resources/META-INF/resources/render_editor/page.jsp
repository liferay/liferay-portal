<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/render_editor/init.jsp" %>

<%
String content = (String)request.getAttribute("liferay-layout:render-editor:content");
String label = (String)request.getAttribute("liferay-layout:render-editor:label");
String layoutMode = (String)request.getAttribute("liferay-layout:render-editor:layoutMode");
String name = (String)request.getAttribute("liferay-layout:render-editor:name");
boolean required = GetterUtil.getBoolean(request.getAttribute("liferay-layout:render-editor:required"));
%>

<c:choose>
	<c:when test='<%= FeatureFlagManagerUtil.isEnabled("LPD-11235") %>'>
		<liferay-editor:editor
			contents="<%= content %>"
			editorName="ckeditor5_classic"
			name="<%= name %>"
			placeholder="<%= label %>"
			required="<%= required %>"
		/>
	</c:when>
	<c:otherwise>
		<liferay-editor:editor
			contents="<%= content %>"
			editorName="ckeditor"
			name="<%= name %>"
			placeholder="<%= label %>"
			required="<%= required %>"
		/>
	</c:otherwise>
</c:choose>