<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditContactInformationDisplayContext editContactInformationDisplayContext = new EditContactInformationDisplayContext("email-address", request, renderResponse);

editContactInformationDisplayContext.setPortletDisplay(portletDisplay);

EmailAddress emailAddress = null;

if (editContactInformationDisplayContext.getPrimaryKey() > 0) {
	emailAddress = EmailAddressServiceUtil.getEmailAddress(editContactInformationDisplayContext.getPrimaryKey());
}

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "contact-information"), editContactInformationDisplayContext.getRedirect());
PortalUtil.addPortletBreadcrumbEntry(request, editContactInformationDisplayContext.getSheetTitle(), null);
%>

<portlet:actionURL name="/account_admin/update_contact_information" var="actionURL" />

<aui:form action="<%= actionURL %>" method="post" name="fm">
	<aui:input name="errorMVCPath" type="hidden" value="/account_entries_admin/account_entry/account_contact/edit_email_address.jsp" />
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.EDIT %>" />
	<aui:input name="redirect" type="hidden" value="<%= editContactInformationDisplayContext.getRedirect() %>" />
	<aui:input name="className" type="hidden" value="<%= editContactInformationDisplayContext.getClassName() %>" />
	<aui:input name="classPK" type="hidden" value="<%= String.valueOf(editContactInformationDisplayContext.getClassPK()) %>" />
	<aui:input name="listType" type="hidden" value="<%= ListTypeConstants.EMAIL_ADDRESS %>" />
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
				<aui:model-context bean="<%= emailAddress %>" model="<%= EmailAddress.class %>" />

				<aui:input checked="<%= (emailAddress != null)? emailAddress.isPrimary() : false %>" id="emailAddressPrimary" label="make-primary" name="emailAddressPrimary" type="checkbox" />

				<liferay-ui:error key="<%= NoSuchListTypeException.class.getName() + editContactInformationDisplayContext.getClassName() + ListTypeConstants.EMAIL_ADDRESS %>" message="please-select-a-type" />

				<aui:select inlineField="<%= true %>" label="type" listType="<%= editContactInformationDisplayContext.getClassName() + ListTypeConstants.EMAIL_ADDRESS %>" listTypeFieldName="listTypeId" name="emailAddressTypeId" />

				<liferay-ui:error exception="<%= EmailAddressException.class %>" message="please-enter-a-valid-email-address" />

				<aui:input fieldParam="emailAddressAddress" id="emailAddressAddress" name="address" required="<%= true %>" />
			</clay:sheet-section>

			<clay:sheet-footer>
				<aui:button primary="<%= true %>" type="submit" />

				<aui:button href="<%= editContactInformationDisplayContext.getRedirect() %>" type="cancel" />
			</clay:sheet-footer>
		</clay:sheet>
	</clay:container-fluid>
</aui:form>