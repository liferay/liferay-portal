<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/ui/panel/init.jsp" %>

<%
boolean collapsed = false;

if (accordion) {
	if ((panelCount != null) && (panelCount.getValue() > 1)) {
		collapsed = true;
	}
}
else if ((extended != null) && !extended) {
	collapsed = true;
}

if (persistState) {
	collapsed = GetterUtil.getBoolean(SessionClicks.get(request, id, null), collapsed);
}
%>

<div class="panel <%= cssClass %>" id="<%= id %>">
	<c:choose>
		<c:when test="<%= collapsible %>">
			<a aria-controls="<%= id %>Content" aria-expanded="<%= !collapsed %>" class="collapse-icon panel-header panel-header-link <%= collapsed ? "collapsed" : StringPool.BLANK %>" data-parent="#<%= id %>" data-toggle="liferay-collapse" href="#<%= id %>Content" role="button">
				<span class="panel-title" id="<%= id %>Header">
					<c:if test="<%= Validator.isNotNull(iconCssClass) %>">
						<i class="<%= iconCssClass %>"></i>
					</c:if>

					<liferay-ui:message key="<%= title %>" />

					<c:if test="<%= Validator.isNotNull(helpMessage) %>">
						<liferay-ui:icon-help message="<%= helpMessage %>" />
					</c:if>
				</span>

				<aui:icon cssClass="collapse-icon-closed" image="angle-right" markupView="lexicon" />

				<aui:icon cssClass="collapse-icon-open" image="angle-down" markupView="lexicon" />
			</a>
		</c:when>
		<c:otherwise>
			<span class="panel-title" id="<%= id %>Header">
				<c:if test="<%= Validator.isNotNull(iconCssClass) %>">
					<i class="<%= iconCssClass %>"></i>
				</c:if>

				<liferay-ui:message key="<%= title %>" />

				<c:if test="<%= Validator.isNotNull(helpMessage) %>">
					<liferay-ui:icon-help message="<%= helpMessage %>" />
				</c:if>
			</span>
		</c:otherwise>
	</c:choose>

	<div aria-labelledby="<%= id %>Header" class="<%= collapsible ? "collapse panel-collapse" : StringPool.BLANK %> <%= !collapsed ? "show" : StringPool.BLANK %>" <%= accordion ? "data-parent='#" + parentId + "'" : StringPool.BLANK %> id="<%= id %>Content" role="region">
		<div class="panel-body">