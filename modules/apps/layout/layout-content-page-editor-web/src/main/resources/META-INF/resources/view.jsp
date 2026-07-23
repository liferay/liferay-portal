<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ContentPageEditorDisplayContext contentPageEditorDisplayContext = (ContentPageEditorDisplayContext)request.getAttribute(ContentPageEditorWebKeys.LIFERAY_SHARED_CONTENT_PAGE_EDITOR_DISPLAY_CONTEXT);

Group group = themeDisplay.getScopeGroup();
%>

<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-11235") %>'>
	<liferay-editor:resources
		editorName="alloyeditor"
	/>
</c:if>

<liferay-util:html-top>
	<aui:link hashedFile="<%= true %>" href="layout-content-page-editor-web/page_editor/app/components/App.css" rel="stylesheet" />

	<c:if test="<%= group.isCMS() %>">
		<aui:style type="text/css">
			.control-menu {
				display: none;
			}
		</aui:style>
	</c:if>
</liferay-util:html-top>

<div id="<portlet:namespace />pageEditor">
	<div class="inline-item my-5 p-5 w-100">
		<span aria-hidden="true" class="loading-animation"></span>
	</div>

	<react:component
		module="{App} from layout-content-page-editor-web"
		props="<%= contentPageEditorDisplayContext.getEditorContext() %>"
	/>
</div>