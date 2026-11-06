<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect", String.valueOf(renderResponse.createRenderURL()));

ConfigurationModel configurationModel = (ConfigurationModel)request.getAttribute(ConfigurationAdminWebKeys.FACTORY_CONFIGURATION_MODEL);
ConfigurationModelIterator configurationModelIterator = (ConfigurationModelIterator)request.getAttribute(ConfigurationAdminWebKeys.CONFIGURATION_MODEL_ITERATOR);

PortalUtil.addPortletBreadcrumbEntry(request, portletDisplay.getPortletDisplayName(), String.valueOf(renderResponse.createRenderURL()));

ConfigurationCategoryMenuDisplay configurationCategoryMenuDisplay = (ConfigurationCategoryMenuDisplay)request.getAttribute(ConfigurationAdminWebKeys.CONFIGURATION_CATEGORY_MENU_DISPLAY);

ConfigurationCategoryDisplay configurationCategoryDisplay = configurationCategoryMenuDisplay.getConfigurationCategoryDisplay();

String categoryDisplayName = configurationCategoryDisplay.getCategoryLabel(locale);

String viewCategoryHREF = ConfigurationCategoryUtil.getHREF(configurationCategoryMenuDisplay, liferayPortletResponse, renderRequest, renderResponse);

PortalUtil.addPortletBreadcrumbEntry(request, categoryDisplayName, viewCategoryHREF);

ResourceBundleLoader resourceBundleLoader = ResourceBundleLoaderProviderUtil.getResourceBundleLoader(configurationModel.getBundleSymbolicName());

ResourceBundle componentResourceBundle = resourceBundleLoader.loadResourceBundle(PortalUtil.getLocale(request));

String factoryConfigurationModelName = (componentResourceBundle != null) ? LanguageUtil.get(componentResourceBundle, configurationModel.getName()) : configurationModel.getName();

PortalUtil.addPortletBreadcrumbEntry(request, factoryConfigurationModelName, null);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(categoryDisplayName);
%>

<clay:container-fluid>
	<clay:col
		size="12"
	>
		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, false, true) %>"
		/>
	</clay:col>
</clay:container-fluid>

<clay:container-fluid>
	<clay:row>
		<clay:col
			md="3"
		>
			<liferay-util:include page="/configuration_category_menu.jsp" servletContext="<%= application %>" />
		</clay:col>

		<clay:col
			md="9"
		>
			<clay:sheet
				size="full"
			>
				<clay:content-row>
					<clay:content-col>
						<h2><%= factoryConfigurationModelName %></h2>
					</clay:content-col>

					<c:if test="<%= configurationModelIterator.getTotal() > 0 %>">
						<clay:content-col>
							<liferay-ui:icon-menu
								cssClass="float-right"
								direction="right"
								markupView="lexicon"
								showWhenSingleIcon="<%= true %>"
							>
								<portlet:resourceURL id="/configuration_admin/export_configuration" var="exportEntriesURL">
									<portlet:param name="redirect" value="<%= currentURL %>" />
									<portlet:param name="factoryPid" value="<%= configurationModel.getFactoryPid() %>" />
								</portlet:resourceURL>

								<liferay-ui:icon
									message="export-entries"
									method="get"
									url="<%= exportEntriesURL %>"
								/>
							</liferay-ui:icon-menu>
						</clay:content-col>
					</c:if>
				</clay:content-row>

				<clay:content-row
					containerElement="h3"
					cssClass="sheet-subtitle"
				>
					<clay:content-col
						containerElement="span"
						expand="<%= true %>"
					>
						<span class="heading-text">
							<liferay-ui:message key="configuration-entries" />
						</span>
					</clay:content-col>

					<clay:content-col
						containerElement="span"
					>
						<span class="heading-end">
							<portlet:renderURL var="createFactoryConfigURL">
								<portlet:param name="mvcRenderCommandName" value="/configuration_admin/edit_configuration" />
								<portlet:param name="redirect" value="<%= currentURL %>" />
								<portlet:param name="factoryPid" value="<%= configurationModel.getFactoryPid() %>" />
							</portlet:renderURL>

							<a class="btn btn-secondary btn-sm" href="<%= createFactoryConfigURL %>"><liferay-ui:message key="add" /></a>
						</span>
					</clay:content-col>
				</clay:content-row>

				<liferay-ui:search-container
					emptyResultsMessage='<%= LanguageUtil.format(request, "no-entries-for-x-have-been-added-yet.-use-the-add-button-above-to-add-the-first", factoryConfigurationModelName) %>'
					iteratorURL='<%=
						PortletURLBuilder.createRenderURL(
							renderResponse
						).setMVCRenderCommandName(
							"/configuration_admin/view_factory_instances"
						).setParameter(
							"factoryPid", configurationModel.getFactoryPid()
						).buildPortletURL()
					%>'
					total="<%= configurationModelIterator.getTotal() %>"
				>
					<liferay-ui:search-container-results
						results="<%= configurationModelIterator.getResults(searchContainer.getStart(), searchContainer.getEnd()) %>"
					/>

					<liferay-ui:search-container-row
						className="com.liferay.configuration.admin.web.internal.model.ConfigurationModel"
						keyPropertyName="ID"
						modelVar="curConfigurationModel"
					>
						<portlet:renderURL var="editFactoryInstanceURL">
							<portlet:param name="mvcRenderCommandName" value="/configuration_admin/edit_configuration" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
							<portlet:param name="factoryPid" value="<%= curConfigurationModel.getFactoryPid() %>" />
							<portlet:param name="pid" value="<%= curConfigurationModel.getID() %>" />
						</portlet:renderURL>

						<%
						String columnLabel = "entry";

						String labelAttribute = curConfigurationModel.getLabelAttribute();

						if (labelAttribute != null) {
							AttributeDefinition attributeDefinition = curConfigurationModel.getExtendedAttributeDefinition(labelAttribute);

							if (attributeDefinition != null) {
								columnLabel = attributeDefinition.getName();
							}
						}

						if (componentResourceBundle != null) {
							columnLabel = LanguageUtil.get(componentResourceBundle, columnLabel);
						}
						%>

						<liferay-ui:search-container-column-text
							name="<%= columnLabel %>"
						>
							<aui:a href="<%= editFactoryInstanceURL %>"><strong><%= HtmlUtil.escape(curConfigurationModel.getLabel()) %></strong></aui:a>
						</liferay-ui:search-container-column-text>

						<liferay-ui:search-container-column-text
							align="right"
							cssClass="entry-action"
							name=""
						>
							<liferay-ui:icon-menu
								direction="down"
								markupView="lexicon"
								showWhenSingleIcon="<%= true %>"
							>
								<liferay-ui:icon
									message="edit"
									method="post"
									url="<%= editFactoryInstanceURL %>"
								/>

								<c:if test="<%= curConfigurationModel.hasConfiguration() %>">
									<portlet:actionURL name="/configuration_admin/delete_configuration" var="deleteConfigActionURL">
										<portlet:param name="redirect" value="<%= currentURL %>" />
										<portlet:param name="factoryPid" value="<%= curConfigurationModel.getFactoryPid() %>" />
										<portlet:param name="pid" value="<%= curConfigurationModel.getID() %>" />
									</portlet:actionURL>

									<liferay-ui:icon
										message="delete"
										method="post"
										url="<%= deleteConfigActionURL %>"
									/>

									<portlet:resourceURL id="/configuration_admin/export_configuration" var="exportURL">
										<portlet:param name="factoryPid" value="<%= curConfigurationModel.getFactoryPid() %>" />
										<portlet:param name="pid" value="<%= curConfigurationModel.getID() %>" />
									</portlet:resourceURL>

									<liferay-ui:icon
										message="export"
										method="get"
										url="<%= exportURL %>"
									/>
								</c:if>
							</liferay-ui:icon-menu>
						</liferay-ui:search-container-column-text>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						markupView="lexicon"
						searchResultCssClass="show-quick-actions-on-hover table table-autofit"
					/>
				</liferay-ui:search-container>
			</clay:sheet>
		</clay:col>
	</clay:row>
</clay:container-fluid>