<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long organizationId = ParamUtil.getLong(request, "organizationId");

Organization organization = OrganizationServiceUtil.fetchOrganization(organizationId);

String[] organizationsTypes = OrganizationLocalServiceUtil.getTypes();

String type = BeanParamUtil.getString(organization, request, "type", organizationsTypes[0]);

long regionId = BeanParamUtil.getLong(organization, request, "regionId");
long countryId = BeanParamUtil.getLong(organization, request, "countryId");

long groupId = 0;

if (organization != null) {
	groupId = organization.getGroupId();
}
%>

<aui:model-context bean="<%= organization %>" model="<%= Organization.class %>" />

<liferay-ui:error exception="<%= DuplicateOrganizationException.class %>" message="the-organization-name-is-already-taken" />

<liferay-ui:error exception="<%= OrganizationNameException.class %>">
	<liferay-ui:message arguments="<%= new String[] {OrganizationConstants.NAME_LABEL, OrganizationConstants.NAME_GENERAL_RESTRICTIONS, OrganizationConstants.NAME_RESERVED_WORDS} %>" key="the-x-cannot-be-x-or-a-reserved-word-such-as-x" />
</liferay-ui:error>

<liferay-frontend:logo-selector
	currentLogoURL='<%= (organization != null) ? organization.getLogoURL() : themeDisplay.getPathImage() + "/organization_logo?img_id=0" %>'
	defaultLogoURL='<%= themeDisplay.getPathImage() + "/organization_logo?img_id=0" %>'
	label='<%= LanguageUtil.get(request, "image") %>'
	type="organization_portrait"
/>

<aui:input name="name" />

<c:choose>
	<c:when test="<%= PropsValues.FIELD_ENABLE_COM_LIFERAY_PORTAL_KERNEL_MODEL_ORGANIZATION_STATUS %>">
		<liferay-ui:error key="<%= NoSuchListTypeException.class.getName() + Organization.class.getName() + ListTypeConstants.ORGANIZATION_STATUS %>" message="please-select-a-type" />

		<aui:select label="status" listType="<%= ListTypeConstants.ORGANIZATION_STATUS %>" listTypeFieldName="statusListTypeId" name="statusId" showEmptyOption="<%= false %>" />
	</c:when>
	<c:otherwise>
		<aui:input name="statusId" type="hidden" value="<%= (organization != null) ? organization.getStatusListTypeId() : ListTypeServiceUtil.getListTypeId(themeDisplay.getCompanyId(), ListTypeConstants.ORGANIZATION_STATUS_DEFAULT, ListTypeConstants.ORGANIZATION_STATUS) %>" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="<%= (organization == null) && (organizationsTypes.length > 1) %>">
		<aui:select name="type">

			<%
			for (String curType : organizationsTypes) {
			%>

				<aui:option label="<%= curType %>" selected="<%= type.equals(curType) %>" />

			<%
			}
			%>

		</aui:select>
	</c:when>
	<c:when test="<%= organization == null %>">
		<aui:input name="type" type="hidden" value="<%= organizationsTypes[0] %>" />
	</c:when>
	<c:otherwise>
		<aui:input name="typeLabel" type="resource" value="<%= LanguageUtil.get(request, organization.getType()) %>" />

		<aui:input name="type" type="hidden" value="<%= organization.getType() %>" />
	</c:otherwise>
</c:choose>

<liferay-ui:error exception="<%= NoSuchCountryException.class %>" message="please-select-a-country" />

<div class="<%= OrganizationLocalServiceUtil.isCountryEnabled(type) ? StringPool.BLANK : "hide" %>" id="<portlet:namespace />countryDiv">
	<aui:select label="country" name="countryId" required="<%= OrganizationLocalServiceUtil.isCountryRequired(type) %>" />

	<aui:select label="region" name="regionId" />
</div>

<liferay-frontend:component
	componentId="CountryRegionDynamicSelect"
	context='<%=
		HashMapBuilder.<String, Object>put(
			"countrySelect", portletDisplay.getNamespace() + "countryId"
		).put(
			"countrySelectVal", countryId
		).put(
			"regionSelect", portletDisplay.getNamespace() + "regionId"
		).put(
			"regionSelectVal", regionId
		).build()
	%>'
	module="{CountryRegionDynamicSelect} from users-admin-web"
/>

<c:if test="<%= organization == null %>">
	<aui:script sandbox="<%= true %>">
		var typeSelect = document.getElementById('<portlet:namespace />type');

		if (typeSelect) {
			typeSelect.addEventListener('change', (event) => {
				var countryDiv = document.getElementById(
					'<portlet:namespace />countryDiv'
				);

				if (countryDiv) {

					<%
					for (String curType : organizationsTypes) {
					%>

						if (event.currentTarget.value === '<%= curType %>') {
							if (
								!<%= OrganizationLocalServiceUtil.isCountryEnabled(curType) %>
							) {
								countryDiv.classList.add('hide');
							}
							else {
								countryDiv.classList.remove('hide');
							}
						}

					<%
					}
					%>

				}
			});
		}
	</aui:script>
</c:if>