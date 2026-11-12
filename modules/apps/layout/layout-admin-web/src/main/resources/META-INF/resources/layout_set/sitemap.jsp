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

String sitemapURLInputId = liferayPortletResponse.getNamespace() + "sitemapURLInput";
%>

<liferay-ui:error-marker
	key="<%= WebKeys.ERROR_SECTION %>"
	value="siteMap"
/>

<p class="text-secondary">
	<liferay-ui:message key="sitemap-help" />

	<clay:link
		decoration="underline"
		href="https://www.sitemaps.org"
		label='<%= LanguageUtil.format(request, "for-more-information,-visit-x", "www.sitemaps.org") %>'
		target="_blank"
	/>
</p>

<div class="form-group">
	<label for="<%= sitemapURLInputId %>"><liferay-ui:message key="sitemap-url" /></label>

	<div class="input-group">
		<div class="input-group-item input-group-prepend">
			<input class="form-control" id="<%= sitemapURLInputId %>" readonly value="<%= HtmlUtil.escapeAttribute(sitemapUrl) %>" />
		</div>

		<span class="input-group-append input-group-item input-group-item-shrink">
			<clay:button
				cssClass="lfr-portal-tooltip"
				data-clipboard-target='<%= "#" + sitemapURLInputId %>'
				displayType="secondary"
				icon="paste"
				id='<%= liferayPortletResponse.getNamespace() + "copySitemapURLButton" %>'
				title="copy-link"
			/>
		</span>
	</div>
</div>

<p class="font-weight-semi-bold mb-2 text-3"><liferay-ui:message key="search-engine-tools" /></p>

<ul class="list-unstyled">
	<li class="mb-2">
		<clay:link
			decoration="underline"
			href="https://search.google.com/search-console"
			iconAfter="shortcut"
			label="google-search-console"
			target="_blank"
		/>
	</li>
	<li>
		<clay:link
			decoration="underline"
			href="https://www.bing.com/webmasters"
			iconAfter="shortcut"
			label="bing-webmaster-tools"
			target="_blank"
		/>
	</li>
</ul>

<liferay-frontend:component
	module="{CopySitemapURL} from layout-admin-web"
/>