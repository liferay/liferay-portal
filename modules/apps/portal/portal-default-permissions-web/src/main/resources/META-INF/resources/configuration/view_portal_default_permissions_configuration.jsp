<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-util:html-top>
	<aui:link hashedFile="<%= true %>" href="portal-defaultpermissions-web/css/configuration.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<clay:container-fluid
	cssClass="mt-4"
>
	<clay:management-toolbar
		managementToolbarDisplayContext="<%= new PortalDefaultPermissionsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, baseViewPortalDefaultPermissionsConfigurationDisplayContext.getSearchContainer()) %>"
	/>

	<div data-qa-id="portal-default-permissions-search-container">
		<liferay-ui:search-container
			id="portalDefaultPermissionsSearchContainer"
			searchContainer="<%= baseViewPortalDefaultPermissionsConfigurationDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.portal.defaultpermissions.web.internal.search.PortalDefaultPermissionsSearchEntry"
				modelVar="portalDefaultPermissionsSearchEntry"
			>
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-200 table-title"
					name="asset-type"
					value="<%= portalDefaultPermissionsSearchEntry.getLabel() %>"
				/>

				<liferay-ui:search-container-column-text>

					<%
					List<DropdownItem> dropdownItems = baseViewPortalDefaultPermissionsConfigurationDisplayContext.getActionDropdownItems(portalDefaultPermissionsSearchEntry);
					%>

					<c:choose>
						<c:when test="<%= dropdownItems.size() > 1 %>">
							<div data-qa-id="actions-<%= portalDefaultPermissionsSearchEntry.getLabel() %>">
								<clay:dropdown-actions
									aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
									dropdownItems="<%= dropdownItems %>"
									propsTransformer="{viewPortalDefaultPermissionsDropdownPropsTransformer} from portal-defaultpermissions-web"
								/>
							</div>
						</c:when>
						<c:otherwise>

							<%
							DropdownItem dropdownItem = dropdownItems.get(0);

							Map<String, Object> data = (HashMap<String, Object>)dropdownItem.get("data");
							%>

							<clay:button
								additionalProps='<%=
									HashMapBuilder.<String, Object>put(
										"url", String.valueOf(data.get("editDefaultPermissionsURL"))
									).build()
								%>'
								aria-label='<%= String.valueOf(dropdownItem.get("label")) %>'
								data-qa-id='<%= "edit-" + portalDefaultPermissionsSearchEntry.getLabel() %>'
								displayType="secondary"
								label='<%= String.valueOf(dropdownItem.get("label")) %>'
								propsTransformer="{viewPortalDefaultPermissionsButtonPropsTransformer} from portal-defaultpermissions-web"
								small="<%= true %>"
							/>
						</c:otherwise>
					</c:choose>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="list"
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</div>
</clay:container-fluid>