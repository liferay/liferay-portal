<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<c:choose>
	<c:when test="<%= themeDisplay.isSignedIn() %>">
		<c:if test="<%= layout != null %>">
			<liferay-util:html-top>
				<aui:link hashedFile="<%= true %>" href="product-navigation-control-menu-web/css/App.css" rel="stylesheet" />
			</liferay-util:html-top>

			<%
			AddContentPanelDisplayContext addContentPanelDisplayContext = new AddContentPanelDisplayContext(request, liferayPortletRequest, liferayPortletResponse);
			%>

			<c:if test="<%= addContentPanelDisplayContext.showAddPanel() %>">
				<div class="add-content-menu" data-qa-id="addPanelBody" id="<portlet:namespace />addPanelContainer">
					<react:component
						module="{AddPanel} from product-navigation-control-menu-web"
						props="<%= addContentPanelDisplayContext.getAddContentPanelData() %>"
					/>
				</div>
			</c:if>
		</c:if>
	</c:when>
	<c:otherwise>
		<liferay-ui:message key="please-sign-in-to-continue" />
	</c:otherwise>
</c:choose>