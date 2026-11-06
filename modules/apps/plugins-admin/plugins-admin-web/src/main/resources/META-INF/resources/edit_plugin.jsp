<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String moduleId = ParamUtil.getString(request, "moduleId");
String pluginId = ParamUtil.getString(request, "pluginId");

String pluginType = ParamUtil.getString(request, "pluginType");

String title = ParamUtil.getString(request, "title", pluginType);

PluginSetting pluginSetting = PluginSettingLocalServiceUtil.getPluginSetting(company.getCompanyId(), pluginId, pluginType);

boolean active = pluginSetting.isActive();
String[] rolesArray = pluginSetting.getRolesArray();

Portlet portlet = null;

if (pluginType.equals(Plugin.TYPE_PORTLET)) {
	String portletId = pluginId;

	portlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), portletId);

	active = portlet.isActive();
	rolesArray = portlet.getRolesArray();
}

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(title);
%>

<portlet:actionURL name="/plugins_admin/edit_plugin" var="editPluginURL" />

<clay:container-fluid>
	<aui:form action="<%= editPluginURL %>" method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="pluginId" type="hidden" value="<%= pluginId %>" />
		<aui:input name="pluginType" type="hidden" value="<%= pluginType %>" />

		<div class="sheet">
			<div class="panel-group panel-group-flush">
				<aui:fieldset>
					<aui:input name="moduleId" type="resource" value="<%= moduleId %>" />

					<aui:input name="pluginId" type="resource" value="<%= pluginId %>" />

					<aui:input name="active" type="checkbox" value="<%= active %>" />

					<c:choose>
						<c:when test="<%= pluginType.equals(Plugin.TYPE_PORTLET) %>">
							<aui:field-wrapper helpMessage="edit-plugin-permissions-help" label="permissions">

								<%
								List<String> resourceActions = ResourceActionsUtil.getResourceActions(portlet.getPortletId(), null);

								int maxNumberOfRolesChecked = 500;

								List<Role> roles = RoleLocalServiceUtil.search(company.getCompanyId(), null, null, (Integer[])null, 0, maxNumberOfRolesChecked, RoleRoleIdComparator.getInstance(true));

								int rolesCount = RoleLocalServiceUtil.searchCount(company.getCompanyId(), null, null, (Integer[])null);

								List<Role> addToPageRoles = null;
								List<Role> accessInControlPanelRoles = null;

								if (resourceActions.contains(ActionKeys.ADD_TO_PAGE)) {
									addToPageRoles = _filterRoles(roles, portlet.getPortletId(), ActionKeys.ADD_TO_PAGE);
								}
								else {
									addToPageRoles = new ArrayList<Role>();
								}

								if (resourceActions.contains(ActionKeys.ACCESS_IN_CONTROL_PANEL)) {
									accessInControlPanelRoles = _filterRoles(roles, portlet.getPortletId(), ActionKeys.ACCESS_IN_CONTROL_PANEL);
								}
								else {
									accessInControlPanelRoles = new ArrayList<Role>();
								}
								%>

								<c:if test="<%= rolesCount > maxNumberOfRolesChecked %>">
									<div class="alert alert-warning">
										<liferay-ui:message arguments="<%= maxNumberOfRolesChecked %>" key="the-portal-has-more-roles-than-the-maximum-that-can-be-checked-x" translateArguments="<%= false %>" />
									</div>
								</c:if>

								<c:if test="<%= !addToPageRoles.isEmpty() %>">
									<div class="permission-group">
										<b><liferay-ui:message key="action.ADD_TO_PAGE" /></b>: <liferay-ui:message key="the-users-with-the-following-roles-can-add-this-portlet-to-the-pages-they-manage" />

										<liferay-ui:search-container>
											<liferay-ui:search-container-results
												results="<%= addToPageRoles %>"
											/>

											<liferay-ui:search-container-row
												className="com.liferay.portal.kernel.model.Role"
												keyPropertyName="name"
												modelVar="role"
											>
												<liferay-ui:search-container-column-text
													name="role"
												>
													<liferay-ui:icon
														icon="<%= role.getIconCssClass() %>"
														label="<%= true %>"
														markupView="lexicon"
														message="<%= HtmlUtil.escape(role.getTitle(locale)) %>"
													/>
												</liferay-ui:search-container-column-text>

												<liferay-ui:search-container-column-text
													align="right"
												>
													<liferay-ui:icon
														icon="pencil"
														label="<%= true %>"
														markupView="lexicon"
														message="change"
														url='<%=
															PortletURLBuilder.create(
																PortletProviderUtil.getPortletURL(request, Role.class.getName(), PortletProvider.Action.MANAGE)
															).setCMD(
																"edit"
															).setRedirect(
																currentURL
															).setPortletResource(
																portlet.getPortletId()
															).setTabs1(
																"roles"
															).setParameter(
																"roleId", role.getRoleId()
															).buildString()
														%>'
													/>
												</liferay-ui:search-container-column-text>
											</liferay-ui:search-container-row>

											<liferay-ui:search-iterator
												type="more"
											/>
										</liferay-ui:search-container>
									</div>
								</c:if>

								<c:if test="<%= !accessInControlPanelRoles.isEmpty() %>">
									<div class="permission-group">
										<strong><liferay-ui:message key="action.ACCESS_IN_CONTROL_PANEL" /></strong>: <liferay-ui:message key="the-users-with-the-following-roles-can-access-this-portlet-in-the-control-panel" />

										<liferay-ui:search-container>
											<liferay-ui:search-container-results
												results="<%= accessInControlPanelRoles %>"
											/>

											<liferay-ui:search-container-row
												className="com.liferay.portal.kernel.model.Role"
												keyPropertyName="name"
												modelVar="role"
											>
												<liferay-ui:search-container-column-text
													name="role"
												>
													<liferay-ui:icon
														icon="<%= role.getIconCssClass() %>"
														label="<%= true %>"
														markupView="lexicon"
														message="<%= HtmlUtil.escape(role.getTitle(locale)) %>"
													/>
												</liferay-ui:search-container-column-text>

												<liferay-ui:search-container-column-text
													align="right"
												>
													<liferay-ui:icon
														icon="pencil"
														label="<%= true %>"
														markupView="lexicon"
														message="change"
														url='<%=
															PortletURLBuilder.create(
																PortletProviderUtil.getPortletURL(request, Role.class.getName(), PortletProvider.Action.MANAGE)
															).setCMD(
																"edit"
															).setPortletResource(
																portlet.getPortletId()
															).setTabs1(
																"roles"
															).setParameter(
																"roleId", role.getRoleId()
															).buildString()
														%>'
													/>
												</liferay-ui:search-container-column-text>
											</liferay-ui:search-container-row>

											<liferay-ui:search-iterator
												markupView="deprecated"
												type="more"
											/>
										</liferay-ui:search-container>
									</div>
								</c:if>

								<c:if test="<%= addToPageRoles.isEmpty() && accessInControlPanelRoles.isEmpty() %>">
									<liferay-ui:message key="only-administrators-can-use-this-portlet" />
								</c:if>
							</aui:field-wrapper>
						</c:when>
						<c:otherwise>
							<aui:input cssClass="lfr-textarea-container" helpMessage="enter-one-role-name-per-line-a-user-must-belong-to-one-of-these-roles-in-order-to-add-this-plugin-to-a-page" name="roles" type="textarea" value='<%= StringUtil.merge(rolesArray, "\n") %>' />
						</c:otherwise>
					</c:choose>
				</aui:fieldset>
			</div>
		</div>

		<aui:button-row>
			<aui:button type="submit" />

			<aui:button href="<%= redirect %>" type="cancel" />
		</aui:button-row>
	</aui:form>
</clay:container-fluid>

<%!
private List<Role> _filterRoles(List<Role> roles, String portletId, String actionId) throws Exception {
	List<Role> filteredRoles = new ArrayList<Role>(0);

	for (Role role : roles) {
		if ((role.getType() == RoleConstants.TYPE_REGULAR) && (_hasPermission(role, actionId, portletId, ResourceConstants.SCOPE_COMPANY) || _hasPermission(role, actionId, portletId, ResourceConstants.SCOPE_GROUP))) {
			filteredRoles.add(role);
		}
		else if (_hasPermission(role, actionId, portletId, ResourceConstants.SCOPE_GROUP_TEMPLATE)) {
			filteredRoles.add(role);
		}
	}

	return filteredRoles;
}

private boolean _hasPermission(Role role, String actionId, String resourceName, Integer scope) throws Exception {
	return ResourcePermissionLocalServiceUtil.hasScopeResourcePermission(role.getCompanyId(), resourceName, scope, role.getRoleId(), actionId);
}
%>