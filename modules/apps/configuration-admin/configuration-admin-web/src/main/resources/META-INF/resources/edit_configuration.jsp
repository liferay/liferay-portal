<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

PortletURL portletURL = renderResponse.createRenderURL();

if (Validator.isNull(redirect)) {
	redirect = portletURL.toString();
}

PortalUtil.addPortletBreadcrumbEntry(request, portletDisplay.getPortletDisplayName(), String.valueOf(renderResponse.createRenderURL()));

ConfigurationCategoryMenuDisplay configurationCategoryMenuDisplay = (ConfigurationCategoryMenuDisplay)request.getAttribute(ConfigurationAdminWebKeys.CONFIGURATION_CATEGORY_MENU_DISPLAY);

ConfigurationCategoryDisplay configurationCategoryDisplay = configurationCategoryMenuDisplay.getConfigurationCategoryDisplay();

String categoryDisplayName = configurationCategoryDisplay.getCategoryLabel(locale);

String viewCategoryHREF = ConfigurationCategoryUtil.getHREF(configurationCategoryMenuDisplay, liferayPortletResponse, renderRequest, renderResponse);

PortalUtil.addPortletBreadcrumbEntry(request, categoryDisplayName, viewCategoryHREF);

ConfigurationModel configurationModel = (ConfigurationModel)request.getAttribute(ConfigurationAdminWebKeys.CONFIGURATION_MODEL);

ResourceBundleLoader resourceBundleLoader = ResourceBundleLoaderProviderUtil.getResourceBundleLoader(configurationModel.getBundleSymbolicName());

ResourceBundle componentResourceBundle = resourceBundleLoader.loadResourceBundle(PortalUtil.getLocale(request));

String configurationModelName = (componentResourceBundle != null) ? LanguageUtil.get(componentResourceBundle, configurationModel.getName()) : configurationModel.getName();

PortletURL viewFactoryInstancesURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/configuration_admin/view_factory_instances"
).setParameter(
	"factoryPid", configurationModel.getFactoryPid()
).buildPortletURL();

if (configurationModel.isFactory()) {
	PortalUtil.addPortletBreadcrumbEntry(request, configurationModelName, viewFactoryInstancesURL.toString());
}

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(categoryDisplayName);
%>

<liferay-ui:error exception="<%= ConfigurationModelListenerException.class %>">

	<%
	ConfigurationModelListenerException cmle = (ConfigurationModelListenerException)errorException;
	%>

	<liferay-ui:message key="<%= HtmlUtil.escape(cmle.causeMessage) %>" localizeKey="<%= false %>" />
</liferay-ui:error>

<portlet:actionURL name="/configuration_admin/bind_configuration" var="bindConfigurationActionURL" />
<portlet:actionURL name="/configuration_admin/delete_configuration" var="deleteConfigurationActionURL" />

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
				<aui:form action="<%= bindConfigurationActionURL %>" method="post" name="fm">
					<aui:input name="factoryPid" type="hidden" value="<%= configurationModel.getFactoryPid() %>" />
					<aui:input name="pid" type="hidden" value="<%= configurationModel.getID() %>" />

					<%
					String configurationTitle = null;

					ConfigurationScopeDisplayContext configurationScopeDisplayContext = ConfigurationScopeDisplayContextFactory.create(renderRequest);

					if (configurationModel.isFactory()) {
						if (configurationModel.hasScopeConfiguration(configurationScopeDisplayContext.getScope())) {
							configurationTitle = configurationModel.getLabel();
						}
						else {
							configurationTitle = LanguageUtil.get(request, "add");
						}
					}
					else {
						configurationTitle = (componentResourceBundle != null) ? LanguageUtil.format(componentResourceBundle, configurationModel.getName(), configurationModel.getNameArguments()) : configurationModel.getName();
					}
					%>

					<clay:content-row
						cssClass="autofit-padded-no-gutters-x sheet-title"
					>
						<clay:content-col
							containerElement="h2"
							expand="<%= true %>"
						>
							<clay:content-row
								cssClass="autofit-padded-no-gutters-x"
							>
								<clay:content-col>
									<%= HtmlUtil.escape(configurationTitle) %>
								</clay:content-col>

								<c:if test="<%= configurationModel.isDeprecated() %>">
									<clay:content-col>
										<liferay-frontend:feature-indicator
											interactive="<%= true %>"
											type="deprecated"
										/>
									</clay:content-col>
								</c:if>
							</clay:content-row>
						</clay:content-col>

						<c:if test="<%= configurationModel.hasScopeConfiguration(configurationScopeDisplayContext.getScope()) %>">
							<clay:content-col>

								<%
								EditConfigurationDisplayContext editConfigurationDisplayContext = new EditConfigurationDisplayContext(request, renderRequest, renderResponse);
								%>

								<clay:dropdown-actions
									dropdownItems="<%= editConfigurationDisplayContext.getDropdownItems() %>"
									propsTransformer="{EditConfigurationActionDropdownPropsTransformer} from configuration-admin-web"
									title='<%= LanguageUtil.get(request, "actions") %>'
								/>
							</clay:content-col>
						</c:if>
					</clay:content-row>

					<c:if test="<%= configurationModel.hasScopeConfiguration(configurationScopeDisplayContext.getScope()) && configurationModel.isReadOnly() %>">
						<clay:alert
							message="this-configuration-is-read-only"
						/>
					</c:if>

					<c:if test="<%= !configurationModel.hasScopeConfiguration(configurationScopeDisplayContext.getScope()) %>">
						<clay:alert
							message="this-configuration-is-not-saved-yet.-the-values-shown-are-the-default"
						/>
					</c:if>

					<liferay-util:dynamic-include key='<%= "com.liferay.configuration.admin.web#/edit_configuration.jsp#" + configurationModel.getFactoryPid() + "#pre" %>' />

					<%
					String configurationModelDescription = (componentResourceBundle != null) ? LanguageUtil.format(componentResourceBundle, configurationModel.getDescription(), configurationModel.getDescriptionArguments()) : configurationModel.getDescription();
					%>

					<c:if test="<%= !Validator.isBlank(configurationModelDescription) %>">
						<p class="text-default">
							<strong><%= configurationModelDescription %></strong>
						</p>
					</c:if>

					<%
					ConfigurationFormRenderer configurationFormRenderer = (ConfigurationFormRenderer)request.getAttribute(ConfigurationAdminWebKeys.CONFIGURATION_FORM_RENDERER);

					configurationFormRenderer.render(request, PipingServletResponseFactory.createPipingServletResponse(pageContext));
					%>

					<liferay-util:dynamic-include key='<%= "com.liferay.configuration.admin.web#/edit_configuration.jsp#" + configurationModel.getFactoryPid() + "#post" %>' />

					<c:if test="<%= !configurationModel.isReadOnly() %>">
						<div class="align-items-center d-flex justify-content-between">
							<aui:button-row>
								<c:choose>
									<c:when test="<%= configurationModel.hasScopeConfiguration(configurationScopeDisplayContext.getScope()) %>">
										<aui:button data-qa-id="submitConfiguration" name="update" type="submit" value="update" />
									</c:when>
									<c:otherwise>
										<aui:button data-qa-id="submitConfiguration" name="save" type="submit" value="save" />
									</c:otherwise>
								</c:choose>

								<aui:button cssClass="ml-3" href="<%= redirect %>" name="cancel" type="cancel" />
							</aui:button-row>

							<c:if test="<%= Validator.isNotNull(configurationModel.getLiferayLearnMessageKey()) && Validator.isNotNull(configurationModel.getLiferayLearnMessageResource()) %>">
								<liferay-learn:message
									key="<%= configurationModel.getLiferayLearnMessageKey() %>"
									resource="<%= configurationModel.getLiferayLearnMessageResource() %>"
								/>
							</c:if>
						</div>
					</c:if>
				</aui:form>
			</clay:sheet>
		</clay:col>
	</clay:row>
</clay:container-fluid>