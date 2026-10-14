<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
RoleTypeContributorProvider roleTypeContributorProvider = (RoleTypeContributorProvider)request.getAttribute(RolesAdminWebKeys.ROLE_TYPE_CONTRIBUTOR_PROVIDER);

PortletConfigurationPermissionsDisplayContext portletConfigurationPermissionsDisplayContext = new PortletConfigurationPermissionsDisplayContext(request, renderRequest, roleTypeContributorProvider);

List<Resource> resources = portletConfigurationPermissionsDisplayContext.getResources();

Resource resource = resources.get(0);

String resourceName = resource.getName();

SearchContainer<Role> roleSearchContainer = portletConfigurationPermissionsDisplayContext.getRoleSearchContainer();

if (Validator.isNotNull(portletConfigurationPermissionsDisplayContext.getModelResource())) {
	PortalUtil.addPortletBreadcrumbEntry(request, HtmlUtil.unescape(portletConfigurationPermissionsDisplayContext.getSelResourceDescription()), null);
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "permissions"), currentURL);
}
%>

<div class="cadmin edit-permissions portlet-configuration-edit-permissions">
	<div class="portlet-configuration-body-content">
		<clay:management-toolbar
			clearResultsURL="<%= portletConfigurationPermissionsDisplayContext.getClearResultsURL() %>"
			itemsTotal="<%= roleSearchContainer.getTotal() %>"
			searchActionURL="<%= portletConfigurationPermissionsDisplayContext.getSearchActionURL() %>"
			searchFormName="searchFm"
			selectable="<%= false %>"
		/>

		<aui:form action="<%= portletConfigurationPermissionsDisplayContext.getUpdateRolePermissionsURL() %>" cssClass="container-fluid container-fluid-max-xxxl" method="post" name="fm">
			<liferay-ui:search-container
				searchContainer="<%= roleSearchContainer %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.portal.kernel.model.Role"
					cssClass="table-title"
					escapedModel="<%= true %>"
					keyProperty="roleId"
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
								<liferay-ui:icon
									icon='<%= (roleTypeContributor != null) ? roleTypeContributor.getIcon() : "users" %>'
									label="<%= false %>"
									markupView="lexicon"
									message='<%= LanguageUtil.get(request, (roleTypeContributor != null) ? roleTypeContributor.getTitle(locale) : "team") %>'
								/>
							</span>
							<span class="lfr-portal-tooltip text-truncate" title="<%= role.getTitle(locale) %>">
								<%= role.getTitle(locale) %>
							</span>

							<c:if test="<%= layout.isPrivateLayout() && name.equals(RoleConstants.GUEST) && PropsValues.PERMISSIONS_CHECK_GUEST_ENABLED %>">
								<span class="inline-item-after">
									<liferay-ui:icon-help message="under-the-current-configuration-all-users-automatically-inherit-permissions-from-the-guest-role" />
								</span>
							</c:if>
						</span>
					</liferay-ui:search-container-column-text>

					<%

					// Actions

					List<String> currentIndividualActions = new ArrayList<String>();
					List<String> currentGroupActions = new ArrayList<String>();
					List<String> currentGroupTemplateActions = new ArrayList<String>();
					List<String> currentCompanyActions = new ArrayList<String>();

					for (Resource curResource : resources) {
						ResourcePermissionUtil.populateResourcePermissionActionIds(portletConfigurationPermissionsDisplayContext.getGroupId(), role, curResource, portletConfigurationPermissionsDisplayContext.getActions(), currentIndividualActions, currentGroupActions, currentGroupTemplateActions, currentCompanyActions);
					}

					Map<String, List<String>> actionIdResourcePrimKeysMap = portletConfigurationPermissionsDisplayContext.getActionIdResourcePrimKeysMap(role);

					for (String action : portletConfigurationPermissionsDisplayContext.getActions()) {
						if (action.equals(ActionKeys.ACCESS_IN_CONTROL_PANEL)) {
							continue;
						}

						boolean checked = false;

						if (currentIndividualActions.contains(action) || currentGroupActions.contains(action) || currentGroupTemplateActions.contains(action) || currentCompanyActions.contains(action)) {
							checked = true;
						}

						String preselectedMsg = StringPool.BLANK;

						if (currentGroupActions.contains(action) || currentGroupTemplateActions.contains(action)) {
							preselectedMsg = "x-is-allowed-to-do-action-x-in-all-items-of-type-x-in-x";
						}
						else if (currentCompanyActions.contains(action)) {
							preselectedMsg = "x-is-allowed-to-do-action-x-in-all-items-of-type-x-in-this-portal-instance";
						}

						List<String> guestUnsupportedActions = portletConfigurationPermissionsDisplayContext.getGuestUnsupportedActions();

						boolean disabled = false;

						if (name.equals(RoleConstants.GUEST) && guestUnsupportedActions.contains(action)) {
							disabled = true;
						}

						String dataMessage = StringPool.BLANK;

						if (Validator.isNotNull(preselectedMsg)) {
							String type = portletConfigurationPermissionsDisplayContext.getSelResourceDescription();

							if (Validator.isNull(type)) {
								type = ResourceActionsUtil.getModelResource(locale, resourceName);
							}

							dataMessage = LanguageUtil.format(request, preselectedMsg, new Object[] {role.getTitle(locale), _getActionLabel(request, resourceName, action), type, portletConfigurationPermissionsDisplayContext.getGroupDescriptiveName()}, false);

							disabled = true;
						}

						String actionSeparator = Validator.isNotNull(preselectedMsg) ? ActionUtil.PRESELECTED : ActionUtil.ACTION;

						String inputName = StringBundler.concat(liferayPortletResponse.getNamespace(), role.getRoleId(), actionSeparator, action);
						String inputId = StringBundler.concat(FriendlyURLNormalizerUtil.normalize(role.getName()), actionSeparator, action);
					%>

						<liferay-ui:search-container-column-text
							cssClass="table-column-text-center"
							name="<%= _getActionLabel(request, resourceName, action) %>"
						>
							<c:if test="<%= disabled && checked %>">
								<input name="<%= inputName %>" type="hidden" value="<%= true %>" />
							</c:if>

							<%
							List<String> resourcePrimKeys = actionIdResourcePrimKeysMap.getOrDefault(action, Collections.emptyList());

							if (!disabled && (resourcePrimKeys.size() < resources.size())) {
								checked = false;
							}

							boolean indeterminate = false;

							if (!checked && ListUtil.isNotEmpty(resourcePrimKeys)) {
								indeterminate = true;
							}
							%>

							<div>
								<div class="custom-checkbox custom-control custom-control-inline">
									<label>
										<input
											<%= (checked || indeterminate) ? "checked" : StringPool.BLANK %>
											<%= disabled ? "disabled" : StringPool.BLANK %>
											<%= indeterminate ? "value=\"indeterminate\"" : StringPool.BLANK %>
											class="custom-control-input <%= Validator.isNotNull(preselectedMsg) ? "lfr-portal-tooltip" : StringPool.BLANK %>"
											id="<%= inputId %>"
											name="<%= inputName %>"
											type="checkbox"
											title="<%= HtmlUtil.escapeAttribute(dataMessage) %>"
										/><span class="custom-control-label"></span
									>
									</label>
								</div>

								<react:component
									module="{PermissionsCheckbox} from portlet-configuration-web"
									props='<%=
										HashMapBuilder.<String, Object>put(
											"actionId", _getActionLabel(request, resourceName, action)
										).put(
											"checked", checked
										).put(
											"disabled", disabled
										).put(
											"id", inputId
										).put(
											"indeterminate", indeterminate
										).put(
											"name", inputName
										).put(
											"roleName", role.getName()
										).put(
											"title", dataMessage
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

<aui:script>
	var <portlet:namespace />saveButton = document.getElementById(
		'<portlet:namespace />saveButton'
	);

	if (<portlet:namespace />saveButton) {
		<portlet:namespace />saveButton.addEventListener('click', (event) => {
			event.preventDefault();

			if (
				<%= portletConfigurationPermissionsDisplayContext.getRoleSearchContainer().getTotal() != 0 %>
			) {
				var form = document.getElementById('<portlet:namespace />fm');

				if (form) {
					submitForm(form);
				}
			}
		});
	}
</aui:script>