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

String screenName = ParamUtil.getString(request, "screenName", GetterUtil.get(selUser, User::getScreenName, StringPool.BLANK));
String emailAddress = ParamUtil.getString(request, "emailAddress", GetterUtil.get(selUser, User::getEmailAddress, StringPool.BLANK));
String firstName = ParamUtil.getString(request, "firstName", GetterUtil.get(selUser, User::getFirstName, StringPool.BLANK));
String middleName = ParamUtil.getString(request, "middleName", GetterUtil.get(selUser, User::getMiddleName, StringPool.BLANK));
String lastName = ParamUtil.getString(request, "lastName", GetterUtil.get(selUser, User::getLastName, StringPool.BLANK));
long prefixListTypeId = ParamUtil.getLong(request, "prefixListTypeId", GetterUtil.get(selContact, Contact::getPrefixListTypeId, 0L));
long suffixListTypeId = ParamUtil.getLong(request, "suffixListTypeId", GetterUtil.get(selContact, Contact::getSuffixListTypeId, 0L));

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

boolean male = ParamUtil.getBoolean(request, "male", GetterUtil.get(selContact, Contact::isMale, true));
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
		<aui:input name="jobTitle" type="hidden" value='<%= ParamUtil.getString(request, "jobTitle", GetterUtil.get(selUser, User::getJobTitle, StringPool.BLANK)) %>' />
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