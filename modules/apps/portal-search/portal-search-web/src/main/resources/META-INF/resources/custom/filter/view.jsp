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
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.custom.filter.configuration.CustomFilterPortletInstanceConfiguration" %><%@
page import="com.liferay.portal.search.web.internal.custom.filter.display.context.CustomFilterDisplayContext" %>

<%@ page import="java.util.ArrayList" %>

<portlet:defineObjects />

<%
CustomFilterDisplayContext customFilterDisplayContext = (CustomFilterDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));

CustomFilterPortletInstanceConfiguration customFilterPortletInstanceConfiguration = customFilterDisplayContext.getCustomFilterPortletInstanceConfiguration();
%>

<c:if test="<%= !customFilterDisplayContext.isRenderNothing() %>">
	<form action="<%= customFilterDisplayContext.getSearchURL() %>" id="<portlet:namespace />fm" method="get" name="<portlet:namespace />fm">
		<liferay-ddm:template-renderer
			className="<%= CustomFilterDisplayContext.class.getName() %>"
			contextObjects='<%=
				HashMapBuilder.<String, Object>put(
					"customFilterDisplayContext", customFilterDisplayContext
				).put(
					"namespace", liferayPortletResponse.getNamespace()
				).build()
			%>'
			displayStyle="<%= customFilterPortletInstanceConfiguration.displayStyle() %>"
			displayStyleGroupId="<%= customFilterDisplayContext.getDisplayStyleGroupId() %>"
			entries="<%= new ArrayList<CustomFilterDisplayContext>() %>"
		>
			<liferay-ui:panel-container
				extended="<%= true %>"
				id='<%= liferayPortletResponse.getNamespace() + "filterCustomPanelContainer" %>'
				markupView="lexicon"
				persistState="<%= true %>"
			>
				<liferay-ui:panel
					collapsible="<%= true %>"
					cssClass="search-facet"
					id='<%= liferayPortletResponse.getNamespace() + "filterCustomPanel" %>'
					markupView="lexicon"
					persistState="<%= true %>"
					title="<%= HtmlUtil.escapeAttribute(customFilterDisplayContext.getHeading()) %>"
				>
					<aui:input cssClass="custom-filter-value-input" data-qa-id="customFilterValueInput" disabled="<%= customFilterDisplayContext.isImmutable() %>" id="<%= liferayPortletResponse.getNamespace() + StringUtil.randomId() %>" label="" name="<%= HtmlUtil.escapeAttribute(customFilterDisplayContext.getParameterName()) %>" useNamespace="<%= false %>" value="<%= HtmlUtil.escapeAttribute(customFilterDisplayContext.getFilterValue()) %>" />

					<clay:button
						aria-label='<%= LanguageUtil.get(request, "apply") %>'
						cssClass="custom-filter-apply-button"
						disabled="<%= customFilterDisplayContext.isImmutable() %>"
						displayType="secondary"
						label="apply"
						small="<%= true %>"
						type="submit"
					/>
				</liferay-ui:panel>
			</liferay-ui:panel-container>
		</liferay-ddm:template-renderer>
	</form>

	<liferay-frontend:component
		context='<%=
			HashMapBuilder.<String, Object>put(
				"namespace", liferayPortletResponse.getNamespace()
			).build()
		%>'
		module="{CustomFilter} from portal-search-web"
	/>
</c:if>