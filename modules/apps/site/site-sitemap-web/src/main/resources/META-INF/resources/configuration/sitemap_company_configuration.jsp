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

<c:if test="<%= sitemapCompanyConfigurationDisplayContext.isCachedGenerationEnabled() %>">
	<p class="c-mb-0 small text-secondary"><liferay-ui:message key="last-updated" />: <%= sitemapCompanyConfigurationDisplayContext.getLastRegenerateSitemapDateString() %></p>

	<p class="c-mb-3 small text-secondary"><liferay-ui:message key="next-scheduled" />: <%= sitemapCompanyConfigurationDisplayContext.isRegenerateSitemapInProgress() ? LanguageUtil.get(request, "generating-now") : sitemapCompanyConfigurationDisplayContext.getNextRegenerateSitemapDateString() %></p>
</c:if>

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
				checked="<%= sitemapCompanyConfigurationDisplayContext.isXMLSitemapIndexEnabled() %>"
				id='<%= liferayPortletResponse.getNamespace() + "xmlSitemapIndexEnabled" %>'
				label='<%= LanguageUtil.get(request, "xml-sitemap-index-enabled") %>'
				name='<%= liferayPortletResponse.getNamespace() + "xmlSitemapIndexEnabled" %>'
			/>

			<p class="c-mb-0 c-mt-2 small text-secondary"><liferay-ui:message arguments="https://www.sitemaps.org/protocol.html" key="when-this-configuration-is-enabled,-a-sitemap-index-is-created" /></p>
		</clay:content-col>
	</clay:content-row>
</clay:sheet-section>

<clay:sheet-section
	aria-labelledby='<%= liferayPortletResponse.getNamespace() + "xmlSitemapIndexModeTitle" %>'
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
			<span class="heading-text text-secondary" id="<portlet:namespace />xmlSitemapIndexModeTitle"><liferay-ui:message key="xml-sitemap-index-mode" /></span>
		</clay:content-col>
	</clay:content-row>

	<clay:content-row
		cssClass="c-mb-2"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<clay:select
				aria-label='<%= LanguageUtil.get(request, "xml-sitemap-index-mode") %>'
				data-qa-id="xmlSitemapIndexModeSelector"
				disabled="<%= !sitemapCompanyConfigurationDisplayContext.isXMLSitemapIndexEnabled() %>"
				id='<%= liferayPortletResponse.getNamespace() + "xmlSitemapIndexMode" %>'
				label='<%= LanguageUtil.get(request, "xml-sitemap-index-mode") %>'
				name="xmlSitemapIndexMode"
				options="<%= sitemapCompanyConfigurationDisplayContext.getSitemapIndexModeSelectOptions() %>"
			/>
		</clay:content-col>
	</clay:content-row>
</clay:sheet-section>

<c:if test="<%= sitemapCompanyConfigurationDisplayContext.isIndexModeAssetTypeEnabled() %>">

	<%
	boolean cachedGenerationEnabled = sitemapCompanyConfigurationDisplayContext.isCachedGenerationEnabled();
	%>

	<clay:sheet-section
		aria-labelledby='<%= liferayPortletResponse.getNamespace() + "xmlSitemapGenerationModeTitle" %>'
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
				<span class="heading-text text-secondary" id="<portlet:namespace />xmlSitemapGenerationModeTitle"><liferay-ui:message key="xml-sitemap-generation-mode" /></span>
			</clay:content-col>
		</clay:content-row>

		<clay:content-row
			cssClass="c-mt-2"
		>
			<clay:content-col
				expand="<%= true %>"
			>
				<clay:radio
					checked="<%= !cachedGenerationEnabled %>"
					data-qa-id="onDemandRadioButton"
					id='<%= liferayPortletResponse.getNamespace() + "cachedGenerationEnabledOnDemand" %>'
					label='<%= LanguageUtil.get(request, "on-demand") %>'
					name='<%= liferayPortletResponse.getNamespace() + "cachedGenerationEnabled" %>'
					value="false"
				/>

				<p class="c-ml-4 small text-secondary"><liferay-ui:message key="xml-sitemap-on-demand-generation-mode-help" /></p>

				<clay:radio
					checked="<%= cachedGenerationEnabled %>"
					data-qa-id="scheduledCachedRadioButton"
					id='<%= liferayPortletResponse.getNamespace() + "cachedGenerationEnabledScheduledCached" %>'
					label='<%= LanguageUtil.get(request, "scheduled-cached") %>'
					name='<%= liferayPortletResponse.getNamespace() + "cachedGenerationEnabled" %>'
					value="true"
				/>

				<p class="c-ml-4 small text-secondary"><liferay-ui:message key="xml-sitemap-cached-generation-mode-help" /></p>
			</clay:content-col>
		</clay:content-row>

		<input id="<portlet:namespace />saveAndGenerate" name="<portlet:namespace />saveAndGenerate" type="hidden" value="false" />
	</clay:sheet-section>
</c:if>

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
	aria-labelledby='<%= liferayPortletResponse.getNamespace() + "includeContentTitle" %>'
	cssClass="c-mb-4"
	role="group"
>
	<clay:content-row
		containerElement="h3"
		cssClass="c-mb-3 sheet-subtitle"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<span class="heading-text text-secondary" id="<portlet:namespace />includeContentTitle"><liferay-ui:message key="content-included-in-the-xml-sitemap" /></span>
		</clay:content-col>
	</clay:content-row>

	<p class="c-mb-3 c-mt-2 small text-secondary"><liferay-ui:message key="select-what-to-be-included-in-the-sitemap" /></p>

	<clay:content-row
		cssClass="c-mt-2"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<clay:checkbox
				checked="<%= sitemapCompanyConfigurationDisplayContext.includePages() %>"
				id='<%= liferayPortletResponse.getNamespace() + "includePages" %>'
				label='<%= LanguageUtil.get(request, "page-urls-including-widget-pages-content-pages-panel-pages-and-embedded-pages") %>'
				name='<%= liferayPortletResponse.getNamespace() + "includePages" %>'
			/>
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
				label='<%= LanguageUtil.get(request, "web-content-urls") %>'
				name='<%= liferayPortletResponse.getNamespace() + "includeWebContent" %>'
			/>
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
				label='<%= LanguageUtil.get(request, "category-urls") %>'
				name='<%= liferayPortletResponse.getNamespace() + "includeCategories" %>'
			/>

			<p class="c-mb-0 c-mt-2 small text-secondary"><liferay-ui:message key="search-engines-will-be-notified-that-selected-URLs-are-available-for-crawling" /></p>
		</clay:content-col>
	</clay:content-row>
</clay:sheet-section>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"groupSelectorURL", sitemapCompanyConfigurationDisplayContext.getGroupSelectorURL()
		).put(
			"isRegenerateSitemapInProgress", sitemapCompanyConfigurationDisplayContext.isRegenerateSitemapInProgress()
		).put(
			"objectDefinitionSelectorURL", sitemapCompanyConfigurationDisplayContext.getObjectDefinitionSelectorURL()
		).put(
			"regenerateSitemapInProgressURL", sitemapCompanyConfigurationDisplayContext.getRegenerateSitemapInProgressURL()
		).put(
			"selectGroupEventName", sitemapCompanyConfigurationDisplayContext.getSelectGroupEventName()
		).put(
			"selectObjectDefinitionEventName", sitemapCompanyConfigurationDisplayContext.getSelectObjectDefinitionEventName()
		).build()
	%>'
	module="{SitemapCompanyConfiguration} from site-sitemap-web"
/>