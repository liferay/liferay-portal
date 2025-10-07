<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String sitemapUrl = PortalUtil.getPortalURL(request) + themeDisplay.getPathContext() + "/sitemap.xml";

LayoutSet layoutSet = layoutsAdminDisplayContext.getSelLayoutSet();

NavigableMap<String, String> virtualHostnames = layoutSet.getVirtualHostnames();

if (!virtualHostnames.containsKey(PortalUtil.getHost(request))) {
	sitemapUrl += "?groupId=" + layoutsAdminDisplayContext.getLiveGroupId() + "&privateLayout=" + layoutsAdminDisplayContext.isPrivateLayout();
}
%>

<liferay-ui:error-marker
	key="<%= WebKeys.ERROR_SECTION %>"
	value="siteMap"
/>

<liferay-util:buffer
	var="linkContent"
>
	<a href="http://www.sitemaps.org" target="_blank">
		http://www.sitemaps.org
	</a>
</liferay-util:buffer>

<div class="text-secondary">
	<p>
		<liferay-ui:message key="the-sitemap-protocol-notifies-search-engines-of-the-structure-of-the-website" /> <liferay-ui:message arguments="<%= linkContent %>" key="see-x-for-more-information" translateArguments="<%= false %>" />
	</p>

	<%
	String sitemapUrlLink = "<a target=\"_blank\" href=\"" + HtmlUtil.escapeAttribute(sitemapUrl) + "\">";
	%>

	<p>
		<liferay-ui:message arguments='<%= new Object[] {sitemapUrlLink, "</a>"} %>' key="send-sitemap-information-to-preview" translateArguments="<%= false %>" />
	</p>

	<ul class="list-unstyled">
		<li>
			<clay:link
				href='<%= "http://www.google.com/webmasters/sitemaps/ping?sitemap=" + HtmlUtil.escapeURL(sitemapUrl) %>'
				label="Google"
				target="_blank"
			/>
		</li>
		<li>
			<div class="d-flex">
				<clay:link
					cssClass="c-mr-2"
					href='<%= "https://siteexplorer.search.yahoo.com/submit/ping?sitemap=" + HtmlUtil.escapeURL(sitemapUrl) %>'
					label="Yahoo!"
					target="_blank"
				/>

				<liferay-ui:message key="requires-log-in" />
			</div>
		</li>
	</ul>
</div>