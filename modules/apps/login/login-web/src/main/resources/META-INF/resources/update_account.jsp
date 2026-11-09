<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

User selUser = null;

Contact selContact = null;

long userId = ParamUtil.getLong(request, "userId");

if (userId > 0) {
	selUser = UserLocalServiceUtil.getUser(userId);

	if (selUser.getStatus() != WorkflowConstants.STATUS_INCOMPLETE) {
		throw new PrincipalException.MustBeAuthenticated(userId);
	}

	selContact = selUser.getContact();
}

String screenName = BeanParamUtil.getString(selUser, request, "screenName", User::getScreenName);
String emailAddress = BeanParamUtil.getString(selUser, request, "emailAddress", User::getEmailAddress);
String firstName = BeanParamUtil.getString(selUser, request, "firstName", User::getFirstName);
String middleName = BeanParamUtil.getString(selUser, request, "middleName", User::getMiddleName);
String lastName = BeanParamUtil.getString(selUser, request, "lastName", User::getLastName);
long prefixListTypeId = BeanParamUtil.getLong(selContact, request, "prefixListTypeId", Contact::getPrefixListTypeId);
long suffixListTypeId = BeanParamUtil.getLong(selContact, request, "suffixListTypeId", Contact::getSuffixListTypeId);

Calendar calendar = CalendarFactoryUtil.getCalendar();

int birthdayDay = ParamUtil.getInteger(request, "birthdayDay");
int birthdayMonth = ParamUtil.getInteger(request, "birthdayMonth");
int birthdayYear = ParamUtil.getInteger(request, "birthdayYear");

Date birthdayDate = PortalUtil.getDate(birthdayMonth, birthdayDay, birthdayYear);

if (birthdayDate != null) {
	calendar.setTime(birthdayDate);
}
else if (selUser != null) {
	calendar.setTime(selContact.getBirthday());
}

boolean male = BeanParamUtil.getBoolean(selContact, request, "male", Contact::isMale, true);
%>

<div class="anonymous-account">
	<portlet:actionURL name="/login/create_account" var="createAccountURL">
		<portlet:param name="mvcRenderCommandName" value="/login/create_account" />
	</portlet:actionURL>

	<aui:form action="<%= createAccountURL %>" method="post" name="fm">
		<aui:input name="saveLastPath" type="hidden" value="<%= false %>" />
		<aui:input name="<%= Constants.CMD %>" type="hidden" />

		<c:if test="<%= Validator.isNotNull(redirect) %>">
			<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		</c:if>

		<aui:input name="screenName" type="hidden" value="<%= screenName %>" />
		<aui:input name="emailAddress" type="hidden" value="<%= emailAddress %>" />
		<aui:input name="firstName" type="hidden" value="<%= firstName %>" />
		<aui:input name="middleName" type="hidden" value="<%= middleName %>" />
		<aui:input name="lastName" type="hidden" value="<%= lastName %>" />
		<aui:input name="prefixListTypeId" type="hidden" value="<%= String.valueOf(prefixListTypeId) %>" />
		<aui:input name="suffixListTypeId" type="hidden" value="<%= String.valueOf(suffixListTypeId) %>" />
		<aui:input name="male" type="hidden" value="<%= String.valueOf(male) %>" />
		<aui:input name="birthdayDay" type="hidden" value="<%= String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) %>" />
		<aui:input name="birthdayMonth" type="hidden" value="<%= String.valueOf(calendar.get(Calendar.MONTH)) %>" />
		<aui:input name="birthdayYear" type="hidden" value="<%= String.valueOf(calendar.get(Calendar.YEAR)) %>" />
		<aui:input name="jobTitle" type="hidden" value='<%= BeanParamUtil.getString(selUser, request, "jobTitle", User::getJobTitle) %>' />
	</aui:form>

	<div class="alert alert-warning">
		<liferay-ui:message arguments="<%= HtmlUtil.escape(emailAddress) %>" key="an-account-with-x-as-the-email-address-already-exists-in-the-portal.-do-you-want-to-associate-this-activity-with-that-account" translateArguments="<%= false %>" />
	</div>

	<aui:button name="updateUser" onClick='<%= liferayPortletResponse.getNamespace() + "updateUser();" %>' value="associate-account" />

	<aui:button name="resetUser" onClick='<%= liferayPortletResponse.getNamespace() + "resetUser();" %>' value="create-new-account" />
</div>

<aui:script>
	window.<portlet:namespace />resetUser = function () {
		var form = document.getElementById('<portlet:namespace />fm');

		if (form) {
			var cmd = form.querySelector(
				'#<portlet:namespace /><%= Constants.CMD %>'
			);

			if (cmd) {
				cmd.setAttribute('value', '<%= Constants.RESET %>');

				submitForm(form);
			}
		}
	};

	window.<portlet:namespace />updateUser = function () {
		var form = document.getElementById('<portlet:namespace />fm');

		if (form) {
			var cmd = form.querySelector(
				'#<portlet:namespace /><%= Constants.CMD %>'
			);

			if (cmd) {
				cmd.setAttribute('value', '<%= Constants.UPDATE %>');

				submitForm(form);
			}
		}
	};
</aui:script>