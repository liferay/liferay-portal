<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = PortalUtil.escapeRedirect(renderRequest.getParameter("redirect"));

ConfigurationEntryIterator configurationEntryIterator = (ConfigurationEntryIterator)request.getAttribute(ConfigurationAdminWebKeys.CONFIGURATION_ENTRY_ITERATOR);
ConfigurationEntryRetriever configurationEntryRetriever = (ConfigurationEntryRetriever)request.getAttribute(ConfigurationAdminWebKeys.CONFIGURATION_ENTRY_RETRIEVER);

ConfigurationScopeDisplayContext configurationScopeDisplayContext = ConfigurationScopeDisplayContextFactory.create(renderRequest);

if (Validator.isNull(redirect)) {
	redirect = String.valueOf(renderResponse.createRenderURL());
}

PortletURL searchURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/configuration_admin/search_results"
).setRedirect(
	redirect
).buildPortletURL();

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(LanguageUtil.get(request, "search-results"));
%>

<aui:style type="text/css">
	.configuration-admin--search-results {
		top: var(--control-menu-container-height);
	}
</aui:style>

<div class="configuration-admin--search-results">
	<clay:management-toolbar
		managementToolbarDisplayContext="<%= new ConfigurationScopeManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, configurationEntryIterator.getTotal()) %>"
	/>
</div>

<clay:container-fluid
	cssClass="container-view"
>
	<liferay-ui:search-container
		emptyResultsMessage="no-configurations-were-found"
		iteratorURL="<%= searchURL %>"
		total="<%= configurationEntryIterator.getTotal() %>"
	>
		<liferay-ui:search-container-results
			results="<%= configurationEntryIterator.getResults(searchContainer.getStart(), searchContainer.getEnd()) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.configuration.admin.web.internal.display.ConfigurationEntry"
			keyProperty="key"
			modelVar="configurationEntry"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="name"
			>
				<a href="<%= configurationEntry.getEditURL(renderRequest, renderResponse) %>"><strong><%= configurationEntry.getName(locale) %></strong></a>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="category"
			>

				<%
				ConfigurationCategory configurationCategory = configurationEntryRetriever.getConfigurationCategory(configurationEntry.getCategory());

				String categorySection = null;
				String category = null;

				if (configurationCategory != null) {
					ConfigurationCategoryMenuDisplay configurationCategoryMenuDisplay = configurationEntryRetriever.getConfigurationCategoryMenuDisplay(configurationCategory.getCategoryKey(), themeDisplay.getLanguageId(), configurationScopeDisplayContext.getScope(), configurationScopeDisplayContext.getScopePK());

					ConfigurationCategoryDisplay configurationCategoryDisplay = configurationCategoryMenuDisplay.getConfigurationCategoryDisplay();

					category = HtmlUtil.escape(configurationCategoryDisplay.getCategoryLabel(locale));

					categorySection = configurationCategoryDisplay.getSectionLabel(locale);
				}
				else {
					categorySection = LanguageUtil.get(request, "other");
					category = configurationEntry.getCategory();
				}
				%>

				<liferay-ui:message key="<%= categorySection %>" /> &gt; <liferay-ui:message key="<%= category %>" />
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="scope"
			>
				<c:choose>
					<c:when test="<%= ExtendedObjectClassDefinition.Scope.COMPANY.equals(configurationEntry.getScope()) %>">
						<liferay-ui:message key="default-settings-for-all-instances" />
					</c:when>
					<c:when test="<%= ExtendedObjectClassDefinition.Scope.GROUP.equals(configurationEntry.getScope()) %>">
						<liferay-ui:message key="default-configuration-for-all-sites" />
					</c:when>
					<c:when test="<%= ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.equals(configurationEntry.getScope()) %>">
						<liferay-ui:message key="default-configuration-for-widget" />
					</c:when>
					<c:when test="<%= ExtendedObjectClassDefinition.Scope.SYSTEM.equals(configurationEntry.getScope()) %>">
						<liferay-ui:message key="system" />
					</c:when>
					<c:otherwise>
						-
					</c:otherwise>
				</c:choose>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				name=""
			>
				<c:if test="<%= configurationEntry instanceof ConfigurationModelConfigurationEntry %>">
					<liferay-ui:icon-menu
						direction="right"
						markupView="lexicon"
						showWhenSingleIcon="<%= true %>"
					>

						<%
						ConfigurationModelConfigurationEntry configurationModelConfigurationEntry = (ConfigurationModelConfigurationEntry)configurationEntry;

						ConfigurationModel configurationModel = configurationModelConfigurationEntry.getConfigurationModel();
						%>

						<liferay-ui:icon
							message="edit"
							method="post"
							url="<%= configurationEntry.getEditURL(renderRequest, renderResponse) %>"
						/>

						<c:if test="<%= configurationModel.hasScopeConfiguration(configurationScopeDisplayContext.getScope()) %>">
							<portlet:actionURL name="/configuration_admin/delete_configuration" var="deleteConfigActionURL">
								<portlet:param name="redirect" value="<%= currentURL %>" />
								<portlet:param name="factoryPid" value="<%= configurationModel.getFactoryPid() %>" />
								<portlet:param name="pid" value="<%= configurationModel.getID() %>" />
							</portlet:actionURL>

							<liferay-ui:icon
								message="reset-default-values"
								method="post"
								url="<%= deleteConfigActionURL %>"
							/>

							<c:if test="<%= ExtendedObjectClassDefinition.Scope.SYSTEM.equals(configurationScopeDisplayContext.getScope()) %>">
								<portlet:resourceURL id="/configuration_admin/export_configuration" var="exportURL">
									<portlet:param name="factoryPid" value="<%= configurationModel.getFactoryPid() %>" />
									<portlet:param name="pid" value="<%= configurationModel.getID() %>" />
								</portlet:resourceURL>

								<liferay-ui:icon
									message="export"
									method="get"
									url="<%= exportURL %>"
								/>
							</c:if>
						</c:if>
					</liferay-ui:icon-menu>
				</c:if>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>