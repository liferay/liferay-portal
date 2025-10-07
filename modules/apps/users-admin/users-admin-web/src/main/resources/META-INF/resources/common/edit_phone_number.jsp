<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditContactInformationDisplayContext editContactInformationDisplayContext = new EditContactInformationDisplayContext("phone-number", request, renderResponse);

editContactInformationDisplayContext.setPortletDisplay(portletDisplay, portletName);

Phone phone = null;

if (editContactInformationDisplayContext.getPrimaryKey() > 0) {
	phone = PhoneServiceUtil.getPhone(editContactInformationDisplayContext.getPrimaryKey());
}

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "contact-information"), editContactInformationDisplayContext.getRedirect());
PortalUtil.addPortletBreadcrumbEntry(request, editContactInformationDisplayContext.getSheetTitle(), null);
%>

<portlet:actionURL name="/users_admin/update_contact_information" var="actionURL" />

<aui:form action="<%= actionURL %>" method="post" name="fm">
	<aui:input name="errorMVCPath" type="hidden" value="/common/edit_phone_number.jsp" />
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.EDIT %>" />
	<aui:input name="redirect" type="hidden" value="<%= editContactInformationDisplayContext.getRedirect() %>" />
	<aui:input name="className" type="hidden" value="<%= editContactInformationDisplayContext.getClassName() %>" />
	<aui:input name="classPK" type="hidden" value="<%= String.valueOf(editContactInformationDisplayContext.getClassPK()) %>" />
	<aui:input name="listType" type="hidden" value="<%= ListTypeConstants.PHONE %>" />
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
				<clay:alert
					message="extension-must-be-numeric"
				/>

				<aui:model-context bean="<%= phone %>" model="<%= Phone.class %>" />

				<aui:input checked="<%= (phone != null)? phone.isPrimary() : false %>" id="phonePrimary" label="make-primary" name="phonePrimary" type="checkbox" />

				<liferay-ui:error key="<%= NoSuchListTypeException.class.getName() + editContactInformationDisplayContext.getClassName() + ListTypeConstants.PHONE %>" message="please-select-a-type" />

				<aui:select inlineField="<%= true %>" label="type" listType="<%= editContactInformationDisplayContext.getClassName() + ListTypeConstants.PHONE %>" listTypeFieldName="listTypeId" name="phoneListTypeId" />

				<liferay-ui:error exception="<%= PhoneNumberException.class %>" message="please-enter-a-valid-phone-number" />

				<aui:input fieldParam="phoneNumber" id="phoneNumber" label="phone-number" name="number" required="<%= true %>" />

				<liferay-ui:error exception="<%= PhoneNumberExtensionException.class %>" message="please-enter-a-valid-phone-number-extension" />

				<aui:input fieldParam="phoneExtension" id="phoneExtension" label="phone-extension" name="extension">
					<aui:validator name="digits" />
				</aui:input>
			</clay:sheet-section>

			<clay:sheet-footer>
				<aui:button primary="<%= true %>" type="submit" />

				<aui:button href="<%= editContactInformationDisplayContext.getRedirect() %>" type="cancel" />
			</clay:sheet-footer>
		</clay:sheet>
	</clay:container-fluid>
</aui:form>