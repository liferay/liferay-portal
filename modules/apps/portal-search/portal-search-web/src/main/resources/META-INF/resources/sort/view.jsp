<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/ddm" prefix="liferay-ddm" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.sort.configuration.SortPortletInstanceConfiguration" %><%@
page import="com.liferay.portal.search.web.internal.sort.display.context.SortDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.sort.display.context.SortTermDisplayContext" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
SortDisplayContext sortDisplayContext = (SortDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));

if (sortDisplayContext.isRenderNothing()) {
	return;
}

SortPortletInstanceConfiguration sortPortletInstanceConfiguration = sortDisplayContext.getSortPortletInstanceConfiguration();
%>

<c:choose>
	<c:when test="<%= sortDisplayContext.isRenderNothing() %>">
		<div class="alert alert-info text-center">
			<liferay-ui:message key="this-widget-is-not-visible-to-users-yet" />

			<clay:link
				href="javascript:void(0);"
				label='<%= LanguageUtil.get(request, "complete-its-configuration-to-make-it-visible") %>'
				onClick="<%= portletDisplay.getURLConfigurationJS() %>"
			/>
		</div>
	</c:when>
	<c:otherwise>
		<aui:form action="#" method="post" name="fm">
			<aui:input cssClass="sort-parameter-name" name="sort-parameter-name" type="hidden" value="<%= sortDisplayContext.getParameterName() %>" />

			<liferay-ddm:template-renderer
				className="<%= SortDisplayContext.class.getName() %>"
				contextObjects='<%=
					HashMapBuilder.<String, Object>put(
						"sortDisplayContext", sortDisplayContext
					).build()
				%>'
				displayStyle="<%= sortPortletInstanceConfiguration.displayStyle() %>"
				displayStyleGroupId="<%= sortDisplayContext.getDisplayStyleGroupId() %>"
				entries="<%= sortDisplayContext.getSortTermDisplayContexts() %>"
			>
				<aui:fieldset>
					<aui:select class="sort-term" label="sort-by" name="sortSelection">
						<c:if test="<%= !sortDisplayContext.isAnySelected() %>">
							<aui:option disabled="<%= true %>" label="relevance" selected="<%= true %>" />
						</c:if>

						<%
						for (SortTermDisplayContext sortTermDisplayContext : sortDisplayContext.getSortTermDisplayContexts()) {
						%>

							<aui:option label="<%= HtmlUtil.escapeAttribute(sortTermDisplayContext.getLabel()) %>" selected="<%= sortTermDisplayContext.isSelected() %>" value="<%= HtmlUtil.escapeAttribute(sortTermDisplayContext.getField()) %>" />

						<%
						}
						%>

					</aui:select>
				</aui:fieldset>
			</liferay-ddm:template-renderer>
		</aui:form>
	</c:otherwise>
</c:choose>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"namespace", liferayPortletResponse.getNamespace()
		).build()
	%>'
	module="{Sort} from portal-search-web"
/>