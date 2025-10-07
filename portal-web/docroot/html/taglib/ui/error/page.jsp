<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/init.jsp" %>

<%
String alertIcon = (String)request.getAttribute("liferay-ui:error:alertIcon");
String alertMessage = (String)request.getAttribute("liferay-ui:error:alertMessage");
String alertStyle = (String)request.getAttribute("liferay-ui:error:alertStyle");
String alertTitle = (String)request.getAttribute("liferay-ui:error:alertTitle");
%>

<c:choose>
	<c:when test='<%= GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:error:embed")) %>'>
		<div class="alert alert-dismissible alert-<%= alertStyle %>" role="alert">
			<liferay-ui:csp>
				<button aria-label="<%= LanguageUtil.get(request, "close") %>" class="close" onclick="event.preventDefault();const container = event.target.closest('.alert');if (container) {container.parentNode.removeChild(container);}" type="button">
					<aui:icon image="times" markupView="lexicon" />

					<span class="sr-only"><liferay-ui:message key="close" /></span>
				</button>
			</liferay-ui:csp>

			<span class="alert-indicator">
				<svg aria-hidden="true" class="lexicon-icon lexicon-icon-<%= alertIcon %>">
					<use xlink:href="<%= themeDisplay.getPathThemeSpritemap() %>#<%= alertIcon %>"></use>
				</svg>
			</span>

			<strong class="lead"><%= alertTitle %></strong><%= alertMessage %>
		</div>

		<%= (String)request.getAttribute("liferay-ui:error:rowBreak") %>
	</c:when>
	<c:otherwise>
		<aui:script>
			Liferay.Util.openToast({
			   message: '<%= HtmlUtil.escapeJS(alertMessage) %>',
			   title: '<%= alertTitle %>',
			   type: '<%= alertStyle %>'
			});
		</aui:script>
	</c:otherwise>
</c:choose>