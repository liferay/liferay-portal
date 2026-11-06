<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SearchContainer<Country> countrySearchContainer = CountrySearchContainerFactory.create(liferayPortletRequest, liferayPortletResponse);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new CountriesManagementAdminManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, countrySearchContainer) %>"
	propsTransformer="{CountriesManagementToolbarPropsTransformer} from address-web"
/>

<clay:container-fluid>
	<aui:form method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="countryIds" type="hidden" />

		<liferay-ui:search-container
			searchContainer="<%= countrySearchContainer %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.portal.kernel.model.Country"
				keyPropertyName="countryId"
				modelVar="country"
			>

				<%
				List<String> availableActions = new ArrayList<>();

				if (CountryPermissionUtil.contains(permissionChecker, country, ActionKeys.DELETE)) {
					availableActions.add("deleteCountries");
				}

				if (CountryPermissionUtil.contains(permissionChecker, country, ActionKeys.UPDATE)) {
					if (country.getActive()) {
						availableActions.add("deactivateCountries");
					}
					else {
						availableActions.add("activateCountries");
					}
				}

				row.setData(
					HashMapBuilder.<String, Object>put(
						"actions", StringUtil.merge(availableActions)
					).build());
				%>

				<portlet:renderURL var="rowURL">
					<portlet:param name="mvcRenderCommandName" value="/address/edit_country" />
					<portlet:param name="countryId" value="<%= String.valueOf(country.getCountryId()) %>" />
				</portlet:renderURL>

				<liferay-ui:search-container-column-text
					cssClass="font-weight-bold important table-cell-expand-smallest"
					href="<%= rowURL %>"
					name="name"
					value="<%= HtmlUtil.escape(country.getTitle(locale)) %>"
				/>

				<liferay-ui:search-container-column-text
					name="billing-allowed"
				>
					<liferay-ui:icon
						cssClass='<%= country.isBillingAllowed() ? "text-success" : "text-danger" %>'
						icon='<%= country.isBillingAllowed() ? "check" : "times" %>'
						markupView="lexicon"
					/>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					name="shipping-allowed"
				>
					<liferay-ui:icon
						cssClass='<%= country.isShippingAllowed() ? "text-success" : "text-danger" %>'
						icon='<%= country.isShippingAllowed() ? "check" : "times" %>'
						markupView="lexicon"
					/>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					name="two-letter-iso-code"
					property="a2"
				/>

				<liferay-ui:search-container-column-text
					name="active"
				>
					<liferay-ui:icon
						cssClass='<%= country.isActive() ? "text-success" : "text-danger" %>'
						icon='<%= country.isActive() ? "check" : "times" %>'
						markupView="lexicon"
					/>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					name="priority"
					property="position"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-cell-ws-nowrap table-column-text-center"
					name="status"
				>
					<clay:label
						displayType="<%= WorkflowConstants.getStatusStyle(country.getStatus()) %>"
						label="<%= WorkflowConstants.getStatusLabel(country.getStatus()) %>"
					/>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-jsp
					cssClass="table-column-text-end"
					path="/country_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>