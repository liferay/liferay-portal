<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/ui/empty_result_message/init.jsp" %>

<%
String message = GetterUtil.getString((String)request.getAttribute("liferay-ui:empty-result-message:message"));
boolean search = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:empty-result-message:search"));
%>

<c:choose>
	<c:when test="<%= compact %>">
		<div class="c-empty-state">
			<p class="c-empty-state-text">
				<liferay-ui:message key="<%= message %>" />
			</p>
		</div>
	</c:when>
	<c:otherwise>
		<div class="c-empty-state c-empty-state-animation">
			<div class="c-empty-state-image <%= (String)request.getAttribute("liferay-ui:empty-result-message:cssClass") %>">
				<div class="c-empty-state-illustration">
					<svg aria-hidden="true" class="c-empty-state-icon c-empty-state-icon-<%= search ? "search-state" : "empty-state" %>" focusable="false" viewBox="0 0 256 256">
						<use xlink:href="<%= themeDisplay.getPathThemeEmptyStatesSpritemap() %>#<%= search ? "search-state" : "empty-state" %>"></use>
					</svg>
				</div>
			</div>

			<c:if test="<%= Validator.isNotNull(message) %>">
				<div class="c-empty-state-text">
					<liferay-ui:message key="<%= message %>" />
				</div>
			</c:if>
	</c:otherwise>
</c:choose>