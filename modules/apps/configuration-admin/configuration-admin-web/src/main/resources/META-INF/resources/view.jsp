<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
List<ConfigurationCategorySectionDisplay> configurationCategorySectionDisplays = (List<ConfigurationCategorySectionDisplay>)request.getAttribute(ConfigurationAdminWebKeys.CONFIGURATION_CATEGORY_SECTION_DISPLAYS);
ConfigurationEntryRetriever configurationEntryRetriever = (ConfigurationEntryRetriever)request.getAttribute(ConfigurationAdminWebKeys.CONFIGURATION_ENTRY_RETRIEVER);

ConfigurationScopeDisplayContext configurationScopeDisplayContext = ConfigurationScopeDisplayContextFactory.create(renderRequest);

ExtendedObjectClassDefinition.Scope scope = configurationScopeDisplayContext.getScope();
%>

<aui:style type="text/css">
	.configuration-admin--main {
		top: var(--control-menu-container-height);
	}
</aui:style>

<div class="configuration-admin--main sticky-top">
	<clay:management-toolbar
		managementToolbarDisplayContext="<%= new ConfigurationScopeManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, 0) %>"
	/>
</div>

<liferay-ui:success key='<%= ConfigurationAdminPortletKeys.SITE_SETTINGS + "requestProcessed" %>'>
	<liferay-ui:message key="site-was-successfully-added" />
</liferay-ui:success>

<clay:container-fluid
	cssClass="container-view"
	size="xxxl"
>
	<c:if test="<%= scope.equals(ExtendedObjectClassDefinition.Scope.COMPANY) || scope.equals(ExtendedObjectClassDefinition.Scope.SYSTEM) %>">

		<%
		String[] installedPatchNames = PatcherValues.INSTALLED_PATCH_NAMES;
		%>

		<div class="alert alert-info">
			<strong><liferay-ui:message key="info" /></strong>: <%= ReleaseInfo.getReleaseInfo() %>

			<c:if test="<%= (installedPatchNames != null) && (installedPatchNames.length > 0) %>">
				<strong><liferay-ui:message key="patch" /></strong>: <%= StringUtil.merge(installedPatchNames, StringPool.COMMA_AND_SPACE) %>
			</c:if>
		</div>
	</c:if>

	<c:if test="<%= configurationCategorySectionDisplays.isEmpty() %>">
		<liferay-frontend:empty-result-message
			animationType="<%= EmptyResultMessageKeys.AnimationType.SEARCH %>"
			title='<%= LanguageUtil.get(resourceBundle, "no-configurations-were-found") %>'
		/>
	</c:if>

	<ul class="list-group <%= configurationCategorySectionDisplays.isEmpty() ? "hide" : StringPool.BLANK %>">

		<%
		Map<String, ConfigurationCategoryMenuDisplay> configurationCategoryMenuDisplays = configurationEntryRetriever.getConfigurationCategoryMenuDisplays(themeDisplay.getLanguageId(), scope, configurationScopeDisplayContext.getScopePK());

		for (ConfigurationCategorySectionDisplay configurationCategorySectionDisplay : configurationCategorySectionDisplays) {
		%>

			<li class="list-group-header">
				<p class="list-group-header-title text-uppercase">
					<%= HtmlUtil.escape(configurationCategorySectionDisplay.getConfigurationCategorySectionLabel(locale)) %>
				</p>
			</li>
			<li class="list-group-card">
				<ul class="list-group">

					<%
					for (ConfigurationCategoryDisplay configurationCategoryDisplay : configurationCategorySectionDisplay.getConfigurationCategoryDisplays()) {
						ConfigurationCategoryMenuDisplay configurationCategoryMenuDisplay = configurationCategoryMenuDisplays.get(configurationCategoryDisplay.getCategoryKey());

						if (configurationCategoryMenuDisplay.isEmpty()) {
							continue;
						}
					%>

						<li class="list-group-card-item">
							<a href="<%= ConfigurationCategoryUtil.getHREF(configurationCategoryMenuDisplay, liferayPortletResponse, renderRequest, renderResponse) %>">
								<clay:icon
									symbol="<%= configurationCategoryDisplay.getCategoryIcon() %>"
								/>

								<span class="list-group-card-item-text">
									<%= HtmlUtil.escape(configurationCategoryDisplay.getCategoryLabel(locale)) %>
								</span>
							</a>
						</li>

					<%
					}
					%>

				</ul>
			</li>

		<%
		}
		%>

	</ul>
</clay:container-fluid>