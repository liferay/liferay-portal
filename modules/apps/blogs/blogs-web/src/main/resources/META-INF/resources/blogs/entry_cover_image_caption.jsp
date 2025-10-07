<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/blogs/init.jsp" %>

<%
String coverImageCaption = ParamUtil.getString(request, "coverImageCaption");
String coverImageURL = ParamUtil.getString(request, "coverImageURL");

String viewEntryURL = ParamUtil.getString(request, "viewEntryURL");

boolean validViewEntryURL = Validator.isNotNull(viewEntryURL) && Validator.isUrl(viewEntryURL);
%>

<c:if test="<%= Validator.isNotNull(coverImageURL) %>">
	<c:if test="<%= validViewEntryURL %>">
		<a href="<%= HtmlUtil.escapeHREF(viewEntryURL) %>">
	</c:if>

	<liferay-ui:csp>
		<div <c:if test="<%= Validator.isNotNull(coverImageCaption) %>">aria-label="<%= HtmlUtil.escapeAttribute(HtmlUtil.stripHtml(coverImageCaption)) %>" role="img"</c:if> class="aspect-ratio aspect-ratio-8-to-3 aspect-ratio-bg-cover cover-image" style="background-image: url(<%= HtmlUtil.escapeAttribute(coverImageURL) %>);"></div>
	</liferay-ui:csp>

	<c:if test="<%= validViewEntryURL %>">
		</a>
	</c:if>
</c:if>