<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SitemapGroupConfigurationDisplayContext sitemapGroupConfigurationDisplayContext = (SitemapGroupConfigurationDisplayContext)request.getAttribute(SitemapGroupConfigurationDisplayContext.class.getName());
%>

<clay:content-row
	cssClass="c-mb-3"
>
	<clay:content-col>
		<span>
			<liferay-ui:message key="the-sitemap-protocol-notifies-search-engines-of-the-structure-of-the-website" />
		</span>
		<span>
			<clay:link
				href="http://www.sitemaps.org"
				label='<%= LanguageUtil.format(request, "for-more-information,-visit-x", "www.sitemaps.org") %>'
				target="_blank"
			/>
		</span>
	</clay:content-col>
</clay:content-row>

<clay:sheet-section
	aria-labelledby='<%= liferayPortletResponse.getNamespace() + "pagesTitle" %>'
	role="group"
>
	<clay:content-row
		containerElement="h3"
		cssClass="c-mb-3 sheet-subtitle"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<span class="heading-text text-secondary" id="<portlet:namespace />pagesTitle"><liferay-ui:message key="pages" /></span>
		</clay:content-col>
	</clay:content-row>

	<clay:content-row
		cssClass="c-mt-2"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<c:if test="<%= sitemapGroupConfigurationDisplayContext.isIncludePagesDisabled() %>">
				<clay:alert
					message="enabling-the-inclusion-of-page-urls-in-the-xml-sitemap-must-first-be-enabled-from-instance-settings"
				/>
			</c:if>

			<clay:checkbox
				checked="<%= sitemapGroupConfigurationDisplayContext.includePages() %>"
				disabled="<%= sitemapGroupConfigurationDisplayContext.isIncludePagesDisabled() %>"
				id='<%= liferayPortletResponse.getNamespace() + "includePages" %>'
				label='<%= LanguageUtil.get(request, "include-page-urls-in-the-xml-sitemap") %>'
				name='<%= liferayPortletResponse.getNamespace() + "includePages" %>'
			/>

			<p class="c-mb-0 c-mt-2 small text-secondary"><liferay-ui:message key="when-this-configuration-is-enabled,-search-engines-will-be-notified-that-page-URLs-are-available-for-crawling" /></p>
		</clay:content-col>
	</clay:content-row>
</clay:sheet-section>

<clay:sheet-section
	aria-labelledby='<%= liferayPortletResponse.getNamespace() + "webContentTitle" %>'
	role="group"
>
	<clay:content-row
		containerElement="h3"
		cssClass="c-mb-3 sheet-subtitle"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<span class="heading-text text-secondary" id="<portlet:namespace />webContentTitle"><liferay-ui:message key="web-content" /></span>
		</clay:content-col>
	</clay:content-row>

	<clay:content-row
		cssClass="c-mt-2"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<c:if test="<%= sitemapGroupConfigurationDisplayContext.isIncludeWebContentDisabled() %>">
				<clay:alert
					message="enabling-the-inclusion-of-web-content-urls-in-the-xml-sitemap-must-first-be-enabled-from-instance-settings"
				/>
			</c:if>

			<clay:checkbox
				checked="<%= sitemapGroupConfigurationDisplayContext.includeWebContent() %>"
				disabled="<%= sitemapGroupConfigurationDisplayContext.isIncludeWebContentDisabled() %>"
				id='<%= liferayPortletResponse.getNamespace() + "includeWebContent" %>'
				label='<%= LanguageUtil.get(request, "include-web-content-urls-in-the-xml-sitemap") %>'
				name='<%= liferayPortletResponse.getNamespace() + "includeWebContent" %>'
			/>

			<p class="c-mb-0 c-mt-2 small text-secondary"><liferay-ui:message key="when-this-configuration-is-enabled,-search-engines-will-be-notified-that-web-content-URLs-are-available-for-crawling" /></p>
		</clay:content-col>
	</clay:content-row>
</clay:sheet-section>

<clay:sheet-section
	aria-labelledby='<%= liferayPortletResponse.getNamespace() + "commerceProductsTitle" %>'
	role="group"
>
	<clay:content-row
		containerElement="h3"
		cssClass="c-mb-3 sheet-subtitle"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<span class="heading-text text-secondary" id="<portlet:namespace />commerceProductsTitle"><liferay-ui:message key="products" /></span>
		</clay:content-col>
	</clay:content-row>

	<clay:content-row
		cssClass="c-mt-2"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<c:if test="<%= sitemapGroupConfigurationDisplayContext.isIncludeCommerceProductsDisabled() %>">
				<clay:alert
					message="enabling-the-inclusion-of-commerce-product-urls-in-the-xml-sitemap-must-first-be-enabled-from-instance-settings"
				/>
			</c:if>

			<clay:checkbox
				checked="<%= sitemapGroupConfigurationDisplayContext.includeCommerceProducts() %>"
				disabled="<%= sitemapGroupConfigurationDisplayContext.isIncludeCommerceProductsDisabled() %>"
				id='<%= liferayPortletResponse.getNamespace() + "includeCommerceProducts" %>'
				label='<%= LanguageUtil.get(request, "include-commerce-product-urls-in-the-xml-sitemap") %>'
				name='<%= liferayPortletResponse.getNamespace() + "includeCommerceProducts" %>'
			/>

			<p class="c-mb-0 c-mt-2 small text-secondary"><liferay-ui:message key="when-this-configuration-is-enabled,-search-engines-will-be-notified-that-commerce-product-URLs-are-available-for-crawling" /></p>
		</clay:content-col>
	</clay:content-row>
</clay:sheet-section>

<clay:sheet-section
	aria-labelledby='<%= liferayPortletResponse.getNamespace() + "categoriesTitle" %>'
	role="group"
>
	<clay:content-row
		containerElement="h3"
		cssClass="c-mb-3 sheet-subtitle"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<span class="heading-text text-secondary" id="<portlet:namespace />categoriesTitle"><liferay-ui:message key="categories" /></span>
		</clay:content-col>
	</clay:content-row>

	<clay:content-row
		cssClass="c-mt-2"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<c:if test="<%= sitemapGroupConfigurationDisplayContext.isIncludeCategoriesDisabled() %>">
				<clay:alert
					message="enabling-the-inclusion-of-category-urls-in-the-xml-sitemap-must-first-be-enabled-from-instance-settings"
				/>
			</c:if>

			<clay:checkbox
				checked="<%= sitemapGroupConfigurationDisplayContext.includeCategories() %>"
				disabled="<%= sitemapGroupConfigurationDisplayContext.isIncludeCategoriesDisabled() %>"
				id='<%= liferayPortletResponse.getNamespace() + "includeCategories" %>'
				label='<%= LanguageUtil.get(request, "include-category-urls-in-the-xml-sitemap") %>'
				name='<%= liferayPortletResponse.getNamespace() + "includeCategories" %>'
			/>

			<p class="c-mb-0 c-mt-2 small text-secondary"><liferay-ui:message key="when-this-configuration-is-enabled,-search-engines-will-be-notified-that-category-URLs-are-available-for-crawling" /></p>
		</clay:content-col>
	</clay:content-row>
</clay:sheet-section>