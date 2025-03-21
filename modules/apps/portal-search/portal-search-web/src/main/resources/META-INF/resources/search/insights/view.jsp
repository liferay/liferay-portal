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
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.search.insights.display.context.SearchInsightsDisplayContext" %>

<portlet:defineObjects />

<%
SearchInsightsDisplayContext searchInsightsDisplayContext = (SearchInsightsDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));

String insightsRequestId = liferayPortletResponse.getNamespace() + "insightsRequest";
String insightsResponseId = liferayPortletResponse.getNamespace() + "insightsResponse";
%>

<aui:style>
	/* .full-query {
		font-size: x-small;
	} */
</aui:style>

<c:choose>
	<c:when test="<%= !Validator.isBlank(searchInsightsDisplayContext.getHelpMessage()) %>">
		<div class="alert alert-info">
			<%= HtmlUtil.escape(searchInsightsDisplayContext.getHelpMessage()) %>
		</div>
	</c:when>
	<c:otherwise>
		<div class="full-query">
			<liferay-frontend:component
				context='<%=
					HashMapBuilder.<String, Object>put(
						"selector", ".search-insights-copy-to-clipboard"
					).build()
				%>'
				module="{InitializeClipboard} from portal-search-web"
			/>

			<liferay-ui:panel-container
				extended="<%= true %>"
				id='<%= liferayPortletResponse.getNamespace() + "insightsPanelContainer" %>'
				markupView="lexicon"
				persistState="<%= true %>"
			>
				<liferay-ui:panel
					collapsible="<%= true %>"
					id='<%= liferayPortletResponse.getNamespace() + "insightsRequestPanel" %>'
					markupView="lexicon"
					persistState="<%= true %>"
					title="request-string"
				>
					<clay:button
						cssClass="search-insights-copy-to-clipboard"
						data-clipboard-text="<%= searchInsightsDisplayContext.getRequestString() %>"
						displayType="secondary"
						icon="copy"
						label="copy-to-clipboard"
						small="<%= true %>"
					/>

					<div class="codemirror-editor-wrapper">
						<textarea readonly id="<%= insightsRequestId %>"><%= HtmlUtil.escape(searchInsightsDisplayContext.getRequestString()) %></textarea>
					</div>

					<liferay-frontend:component
						context='<%=
							HashMapBuilder.<String, Object>put(
								"id", insightsRequestId
							).build()
						%>'
						module="{CodeMirrorTextArea} from portal-search-web"
					/>
				</liferay-ui:panel>

				<liferay-ui:panel
					collapsible="<%= true %>"
					id='<%= liferayPortletResponse.getNamespace() + "insightsResponsePanel" %>'
					markupView="lexicon"
					persistState="<%= true %>"
					title="response-string"
				>
					<clay:button
						cssClass="search-insights-copy-to-clipboard"
						data-clipboard-text="<%= searchInsightsDisplayContext.getResponseString() %>"
						displayType="secondary"
						icon="copy"
						label="copy-to-clipboard"
						small="<%= true %>"
					/>

					<div class="codemirror-editor-wrapper">
						<textarea readonly id="<%= insightsResponseId %>"><%= HtmlUtil.escape(searchInsightsDisplayContext.getResponseString()) %></textarea>
					</div>

					<liferay-frontend:component
						context='<%=
							HashMapBuilder.<String, Object>put(
								"id", insightsResponseId
							).build()
						%>'
						module="{CodeMirrorTextArea} from portal-search-web"
					/>
				</liferay-ui:panel>
			</liferay-ui:panel-container>
		</div>
	</c:otherwise>
</c:choose>