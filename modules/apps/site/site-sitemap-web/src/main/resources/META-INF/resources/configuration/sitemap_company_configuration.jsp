<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SitemapCompanyConfigurationDisplayContext sitemapCompanyConfigurationDisplayContext = (SitemapCompanyConfigurationDisplayContext)request.getAttribute(SitemapCompanyConfigurationDisplayContext.class.getName());
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
	aria-labelledby='<%= liferayPortletResponse.getNamespace() + "xmlSitemapIndexTitle" %>'
	role="group"
>
	<clay:content-row
		containerElement="h3"
		cssClass="c-mb-3 sheet-subtitle"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<span class="heading-text text-secondary" id="<portlet:namespace />xmlSitemapIndexTitle"><liferay-ui:message key="xml-sitemap-index" /></span>
		</clay:content-col>
	</clay:content-row>

	<clay:content-row
		cssClass="c-mt-2"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<clay:checkbox
				checked="<%= sitemapCompanyConfigurationDisplayContext.xmlSitemapIndexEnabled() %>"
				id='<%= liferayPortletResponse.getNamespace() + "xmlSitemapIndexEnabled" %>'
				label='<%= LanguageUtil.get(request, "xml-sitemap-index-enabled") %>'
				name='<%= liferayPortletResponse.getNamespace() + "xmlSitemapIndexEnabled" %>'
			/>

			<p class="c-mb-0 c-mt-2 small text-secondary"><liferay-ui:message arguments="https://www.sitemaps.org/protocol.html" key="when-this-configuration-is-enabled,-a-sitemap-index-is-created" /></p>
		</clay:content-col>
	</clay:content-row>
</clay:sheet-section>

<clay:sheet-section
	aria-labelledby='<%= liferayPortletResponse.getNamespace() + "sitesIncludedTitle" %>'
	cssClass="c-mb-0"
	role="group"
>
	<clay:content-row
		containerElement="h3"
		cssClass="c-mb-3 sheet-subtitle"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<span class="heading-text text-secondary" id="<portlet:namespace />sitesIncludedTitle"><liferay-ui:message key="company-sitemap-group-ids" /></span>
		</clay:content-col>

		<clay:content-col>
			<clay:button
				aria-label='<%= LanguageUtil.format(request, "select-x", "company-sitemap-group-ids") %>'
				displayType="secondary"
				id='<%= liferayPortletResponse.getNamespace() + "selectSiteLink" %>'
				label="select"
				small="<%= true %>"
				title="select"
			/>
		</clay:content-col>
	</clay:content-row>

	<clay:content-row>
		<clay:content-col
			expand="<%= true %>"
		>
			<clay:alert
				cssClass="c-mb-0 c-mt-2"
				message="the-companys-xml-sitemap-only-includes-sites-without-virtual-hosts"
			/>
		</clay:content-col>
	</clay:content-row>

	<clay:content-row>
		<clay:content-col
			expand="<%= true %>"
		>
			<liferay-ui:search-container
				compactEmptyResultsMessage="<%= true %>"
				id="groupsSearchContainer"
				searchContainer="<%= sitemapCompanyConfigurationDisplayContext.getGroupSearchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.portal.kernel.model.Group"
					escapedModel="<%= true %>"
					keyProperty="groupId"
					modelVar="group"
					rowIdProperty="groupId"
				>
					<liferay-ui:search-container-column-text>
						<clay:icon
							cssClass="c-ml-2 text-4 text-secondary"
							symbol="sites"
						/>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						name="site-name"
						truncate="<%= true %>"
					>
						<%= HtmlUtil.escape(group.getDescriptiveName()) %>

						<c:if test="<%= sitemapCompanyConfigurationDisplayContext.hasVirtualHost(group) %>">
							<clay:icon
								aria-label='<%= LanguageUtil.get(request, "this-site-is-not-included-in-the-companys-xml-sitemap-because-it-already-has-a-virtual-host") %>'
								cssClass="text-warning"
								symbol="warning-full"
							/>
						</c:if>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text>
						<c:if test="<%= !group.isGuest() %>">
							<clay:button
								aria-label='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escape(group.getDescriptiveName())) %>'
								borderless="<%= true %>"
								cssClass="lfr-portal-tooltip remove-button"
								data-rowId="<%= group.getGroupId() %>"
								displayType=""
								icon="times-circle"
								monospaced="<%= true %>"
								small="<%= true %>"
								title='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escape(group.getDescriptiveName())) %>'
								type="button"
							/>
						</c:if>
					</liferay-ui:search-container-column-text>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
					paginate="<%= false %>"
				/>
			</liferay-ui:search-container>
		</clay:content-col>
	</clay:content-row>
</clay:sheet-section>

<clay:sheet-section
	aria-labelledby='<%= liferayPortletResponse.getNamespace() + "objectsIncludedTitle" %>'
	cssClass="c-mb-0"
	role="group"
>
	<clay:content-row
		containerElement="h3"
		cssClass="c-mb-3 sheet-subtitle"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<span class="heading-text text-secondary" id="<portlet:namespace />objectsIncludedTitle"><liferay-ui:message key="company-sitemap-object-definition-ids" /></span>
		</clay:content-col>

		<clay:content-col>
			<clay:button
				aria-label='<%= LanguageUtil.format(request, "select-x", "company-sitemap-object-definition-ids") %>'
				displayType="secondary"
				id='<%= liferayPortletResponse.getNamespace() + "selectObjectDefinitionLink" %>'
				label="select"
				small="<%= true %>"
				title="select"
			/>
		</clay:content-col>
	</clay:content-row>

	<p class="c-mb-0 c-mt-2 small text-secondary"><liferay-ui:message key="when-an-object-or-a-cms-structure-is-added-to-this-list,-search-engines-will-be-notified-that-it-is-available-for-crawling" /></p>

	<clay:content-row>
		<clay:content-col
			expand="<%= true %>"
		>
			<liferay-ui:search-container
				compactEmptyResultsMessage="<%= true %>"
				id="objectDefinitionsSearchContainer"
				searchContainer="<%= sitemapCompanyConfigurationDisplayContext.getObjectDefinitionSearchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.object.model.ObjectDefinition"
					escapedModel="<%= true %>"
					keyProperty="objectDefinitionId"
					modelVar="objectDefinition"
					rowIdProperty="objectDefinitionId"
				>
					<liferay-ui:search-container-column-text
						name="object-label"
						truncate="<%= true %>"
					>
						<%= HtmlUtil.escape(objectDefinition.getLabel(locale)) %>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text>
						<clay:button
							aria-label='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escape(objectDefinition.getLabel(locale))) %>'
							borderless="<%= true %>"
							cssClass="lfr-portal-tooltip remove-button"
							data-rowId="<%= objectDefinition.getObjectDefinitionId() %>"
							displayType=""
							icon="times-circle"
							monospaced="<%= true %>"
							small="<%= true %>"
							title='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escape(objectDefinition.getLabel(locale))) %>'
							type="button"
						/>
					</liferay-ui:search-container-column-text>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
					paginate="<%= false %>"
				/>
			</liferay-ui:search-container>
		</clay:content-col>
	</clay:content-row>
</clay:sheet-section>

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
			<clay:checkbox
				checked="<%= sitemapCompanyConfigurationDisplayContext.includePages() %>"
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
			<clay:checkbox
				checked="<%= sitemapCompanyConfigurationDisplayContext.includeWebContent() %>"
				id='<%= liferayPortletResponse.getNamespace() + "includeWebContent" %>'
				label='<%= LanguageUtil.get(request, "include-web-content-urls-in-the-xml-sitemap") %>'
				name='<%= liferayPortletResponse.getNamespace() + "includeWebContent" %>'
			/>

			<p class="c-mb-0 c-mt-2 small text-secondary"><liferay-ui:message key="when-this-configuration-is-enabled,-search-engines-will-be-notified-that-web-content-URLs-are-available-for-crawling" /></p>
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
			<clay:checkbox
				checked="<%= sitemapCompanyConfigurationDisplayContext.includeCategories() %>"
				id='<%= liferayPortletResponse.getNamespace() + "includeCategories" %>'
				label='<%= LanguageUtil.get(request, "include-category-urls-in-the-xml-sitemap") %>'
				name='<%= liferayPortletResponse.getNamespace() + "includeCategories" %>'
			/>

			<p class="c-mb-0 c-mt-2 small text-secondary"><liferay-ui:message key="when-this-configuration-is-enabled,-search-engines-will-be-notified-that-category-URLs-are-available-for-crawling" /></p>
		</clay:content-col>
	</clay:content-row>

	<liferay-frontend:component
		context='<%=
			HashMapBuilder.<String, Object>put(
				"groupSelectorURL", sitemapCompanyConfigurationDisplayContext.getGroupSelectorURL()
			).put(
				"objectDefinitionSelectorURL", sitemapCompanyConfigurationDisplayContext.getObjectDefinitionSelectorURL()
			).put(
				"selectGroupEventName", sitemapCompanyConfigurationDisplayContext.getSelectGroupEventName()
			).put(
				"selectObjectDefinitionEventName", sitemapCompanyConfigurationDisplayContext.getSelectObjectDefinitionEventName()
			).build()
		%>'
		module="{SitemapCompanyConfiguration} from site-sitemap-web"
	/>
</clay:sheet-section>