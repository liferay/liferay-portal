<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
PortalDefaultPermissionsConfigurationManager portalDefaultPermissionsConfigurationManager = (PortalDefaultPermissionsConfigurationManager)request.getAttribute(PortalDefaultPermissionsWebKeys.PORTAL_DEFAULT_PERMISSIONS_CONFIGURATION_MANAGER);
RoleTypeContributorProvider roleTypeContributorProvider = (RoleTypeContributorProvider)request.getAttribute(RolesAdminWebKeys.ROLE_TYPE_CONTRIBUTOR_PROVIDER);

EditPortalDefaultPermissionsConfigurationDisplayContext editPortalDefaultPermissionsConfigurationDisplayContext = new EditPortalDefaultPermissionsConfigurationDisplayContext(request, portalDefaultPermissionsConfigurationManager, renderRequest, roleTypeContributorProvider);

SearchContainer<Role> roleSearchContainer = editPortalDefaultPermissionsConfigurationDisplayContext.getRoleSearchContainer();
%>

<div class="cadmin edit-permissions portlet-configuration-edit-permissions">
	<div class="portlet-configuration-body-content">
		<clay:management-toolbar
			clearResultsURL="<%= editPortalDefaultPermissionsConfigurationDisplayContext.getClearResultsURL() %>"
			itemsTotal="<%= roleSearchContainer.getTotal() %>"
			searchActionURL="<%= editPortalDefaultPermissionsConfigurationDisplayContext.getSearchActionURL() %>"
			searchFormName="searchFm"
			selectable="<%= false %>"
		/>

		<aui:form action="<%= editPortalDefaultPermissionsConfigurationDisplayContext.getUpdateRolePermissionsURL() %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
			<liferay-ui:search-container
				searchContainer="<%= roleSearchContainer %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.portal.kernel.model.Role"
					cssClass="table-title"
					escapedModel="<%= true %>"
					keyPropertyName="roleId"
					modelVar="role"
				>

					<%
					String name = role.getName();
					%>

					<liferay-ui:search-container-column-text
						name="role"
					>

						<%
						RoleTypeContributor roleTypeContributor = roleTypeContributorProvider.getRoleTypeContributor(role.getType());
						%>

						<span class="text-truncate-inline">
							<span class="inline-item-before">
								<clay:icon
									symbol='<%= (roleTypeContributor != null) ? roleTypeContributor.getIcon() : "users" %>'
									title='<%= LanguageUtil.get(request, (roleTypeContributor != null) ? roleTypeContributor.getTitle(locale) : "team") %>'
								/>
							</span>
							<span class="lfr-portal-tooltip text-truncate" title="<%= role.getTitle(locale) %>">
								<%= role.getTitle(locale) %>
							</span>
						</span>
					</liferay-ui:search-container-column-text>

					<%
					List<String> currentActions = editPortalDefaultPermissionsConfigurationDisplayContext.getCurrentActions(role);

					for (String action : editPortalDefaultPermissionsConfigurationDisplayContext.getActions()) {
						if (action.equals(ActionKeys.ACCESS_IN_CONTROL_PANEL)) {
							continue;
						}

						boolean checked = false;

						if (currentActions.contains(action)) {
							checked = true;
						}

						boolean disabled = false;

						List<String> groupDisabledActions = editPortalDefaultPermissionsConfigurationDisplayContext.getGroupDisabledActions();
						List<String> guestDisabledActions = editPortalDefaultPermissionsConfigurationDisplayContext.getGuestDisabledActions();
						List<String> ownerDisabledActions = editPortalDefaultPermissionsConfigurationDisplayContext.getOwnerDisabledActions();

						if ((RoleConstants.GUEST.equals(name) && guestDisabledActions.contains(action)) || (RoleConstants.OWNER.equals(name) && ownerDisabledActions.contains(action)) || (RoleConstants.SITE_MEMBER.equals(name) && groupDisabledActions.contains(action))) {
							disabled = true;
						}

						String inputName = StringBundler.concat(liferayPortletResponse.getNamespace(), role.getRoleId(), EditPortalDefaultPermissionsConfigurationMVCActionCommand.ACTION_SEPARATOR, action);
						String inputId = StringBundler.concat(FriendlyURLNormalizerUtil.normalize(role.getName()), EditPortalDefaultPermissionsConfigurationMVCActionCommand.ACTION_SEPARATOR, action);
					%>

						<liferay-ui:search-container-column-text
							cssClass="table-column-text-center"
							name="<%= editPortalDefaultPermissionsConfigurationDisplayContext.getActionLabel(request, editPortalDefaultPermissionsConfigurationDisplayContext.getModelResource(), action) %>"
						>
							<c:if test="<%= disabled && checked %>">
								<input name="<%= inputName %>" type="hidden" value="<%= true %>" />
							</c:if>

							<div data-qa-id="<%= inputId %>">
								<div class="custom-checkbox custom-control custom-control-inline">
									<label>
										<input
											<%= checked ? "checked" : StringPool.BLANK %>
											<%= disabled ? "disabled" : StringPool.BLANK %>
											class="custom-control-input"
											id="<%= inputId %>"
											name="<%= inputName %>"
											type="checkbox"
										/><span class="custom-control-label"></span
									>
									</label>
								</div>

								<react:component
									module="{PermissionsCheckbox} from portlet-configuration-web"
									props='<%=
										HashMapBuilder.<String, Object>put(
											"actionId", ResourceActionsUtil.getAction(request, action)
										).put(
											"checked", checked
										).put(
											"disabled", disabled
										).put(
											"id", inputId
										).put(
											"name", inputName
										).put(
											"roleName", role.getName()
										).build()
									%>'
								/>
							</div>
						</liferay-ui:search-container-column-text>

					<%
					}
					%>

				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					fixedHeader="<%= true %>"
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</aui:form>
	</div>

	<aui:button-row>
		<clay:button
			id='<%= liferayPortletResponse.getNamespace() + "saveButton" %>'
			label="save"
			type="submit"
		/>

		<clay:button
			cssClass="btn-cancel"
			displayType="secondary"
			label="cancel"
		/>
	</aui:button-row>
</div>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"namespace", liferayPortletResponse.getNamespace()
		).put(
			"updateRolePermissionsURL", editPortalDefaultPermissionsConfigurationDisplayContext.getUpdateRolePermissionsURL()
		).build()
	%>'
	module="{editPortalDefaultPermissionsConfiguration} from portal-defaultpermissions-web"
/>