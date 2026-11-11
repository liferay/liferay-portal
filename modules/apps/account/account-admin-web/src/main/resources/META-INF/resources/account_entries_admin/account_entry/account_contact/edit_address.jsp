<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditContactInformationDisplayContext editContactInformationDisplayContext = new EditContactInformationDisplayContext("address", request, renderResponse);

editContactInformationDisplayContext.setPortletDisplay(portletDisplay);

Address address = null;

long countryId = 0L;
long regionId = 0L;

if (editContactInformationDisplayContext.getPrimaryKey() > 0) {
	address = AddressServiceUtil.getAddress(editContactInformationDisplayContext.getPrimaryKey());

	countryId = address.getCountryId();
	regionId = address.getRegionId();
}

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "addresses"), editContactInformationDisplayContext.getRedirect());
PortalUtil.addPortletBreadcrumbEntry(request, editContactInformationDisplayContext.getSheetTitle(), null);
%>

<portlet:actionURL name="/account_admin/update_contact_information" var="actionURL" />

<aui:form action="<%= actionURL %>" method="post" name="fm">
	<aui:input name="errorMVCPath" type="hidden" value="/account_entries_admin/account_entry/account_contact/edit_address.jsp" />
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.EDIT %>" />
	<aui:input name="redirect" type="hidden" value="<%= editContactInformationDisplayContext.getRedirect() %>" />
	<aui:input name="className" type="hidden" value="<%= editContactInformationDisplayContext.getClassName() %>" />
	<aui:input name="classPK" type="hidden" value="<%= String.valueOf(editContactInformationDisplayContext.getClassPK()) %>" />
	<aui:input name="listType" type="hidden" value="<%= ListTypeConstants.ADDRESS %>" />
	<aui:input name="primaryKey" type="hidden" value="<%= String.valueOf(editContactInformationDisplayContext.getPrimaryKey()) %>" />

	<clay:container-fluid>
		<div class="sheet-lg" id="breadcrumb">
			<liferay-site-navigation:breadcrumb
				breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, true, true) %>"
			/>
		</div>

		<clay:sheet>
			<clay:sheet-header>
				<h2 class="sheet-title"><%= editContactInformationDisplayContext.getSheetTitle() %></h2>
			</clay:sheet-header>

			<clay:sheet-section>
				<aui:model-context bean="<%= address %>" model="<%= Address.class %>" />

				<aui:input checked="<%= (address != null)? address.isPrimary() : false %>" id="primary" label="make-primary" name="primary" type="checkbox" />

				<liferay-ui:error key='<%= NoSuchListTypeException.class.getName() + editContactInformationDisplayContext.getClassName() + ".contact" + ListTypeConstants.ADDRESS %>' message="please-select-a-type" />

				<aui:select label="type" listType='<%= editContactInformationDisplayContext.getClassName() + ".contact" + ListTypeConstants.ADDRESS %>' listTypeFieldName="listTypeId" name="listTypeId" />

				<liferay-ui:error exception="<%= AddressStreetException.class %>" message="please-enter-a-valid-street" />

				<aui:input name="street1" required="<%= true %>" />

				<aui:input name="street2" />

				<aui:input name="street3" />

				<liferay-ui:error exception="<%= AddressCityException.class %>" message="please-enter-a-valid-city" />

				<aui:input name="city" required="<%= true %>" />

				<liferay-ui:error exception="<%= NoSuchCountryException.class %>" message="please-select-a-country" />

				<aui:select label="country" name="countryId" required="<%= true %>">
					<aui:validator errorMessage='<%= LanguageUtil.get(request, "this-field-is-required") %>' name="custom">
						function(val) {
							if (Number(val) !== 0) {
								return true;
							}

							return false;
						}
					</aui:validator>
				</aui:select>

				<liferay-ui:error exception="<%= NoSuchRegionException.class %>" message="please-select-a-region" />

				<label class="control-label" for="<portlet:namespace />regionId">
					<liferay-ui:message key="region" />

					<span hidden id="<portlet:namespace />regionRequiredWrapper">
						<clay:icon
							cssClass="reference-mark text-warning"
							symbol="asterisk"
						/>

						<span class="hide-accessible sr-only"><liferay-ui:message key="required" /></span>
					</span>
				</label>

				<aui:select label="" name="regionId">
					<aui:validator errorMessage='<%= LanguageUtil.get(request, "this-field-is-required") %>' name="custom">
						function(val, fieldNode) {
							if (fieldNode.length === 1) {
								return true;
							}

							if (Number(val) !== 0) {
								return true;
							}

							return false;
						}
					</aui:validator>
				</aui:select>

				<liferay-ui:error exception="<%= AddressZipException.class %>" message="please-enter-a-valid-postal-code" />

				<div class="form-group">
					<label class="control-label" for="<portlet:namespace />zip">
						<liferay-ui:message key="postal-code" />

						<span hidden id="<portlet:namespace />zipRequiredWrapper">
							<clay:icon
								cssClass="reference-mark text-warning"
								symbol="asterisk"
							/>

							<span class="hide-accessible sr-only"><liferay-ui:message key="required" /></span>
						</span>
					</label>

					<aui:input label="" name="zip" />
				</div>

				<aui:input cssClass="mailing-ctrl" name="mailing" />
			</clay:sheet-section>

			<clay:sheet-footer
				cssClass="sheet-footer-btn-block-sm-down"
			>
				<div class="btn-group">
					<div class="btn-group-item">
						<clay:button
							displayType="primary"
							label='<%= LanguageUtil.get(resourceBundle, "save") %>'
							type="submit"
						/>
					</div>

					<div class="btn-group-item">
						<clay:link
							cssClass="btn btn-secondary"
							displayType="null"
							href="<%= editContactInformationDisplayContext.getRedirect() %>"
							label='<%= LanguageUtil.get(resourceBundle, "cancel") %>'
							role="button"
						/>
					</div>
				</div>
			</clay:sheet-footer>
		</clay:sheet>
	</clay:container-fluid>

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
		module="{CountryRegionDynamicSelect} from account-admin-web"
	/>
</aui:form>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"countryId", countryId
		).build()
	%>'
	module="{AccountContactAddressZipValidator} from account-admin-web"
/>