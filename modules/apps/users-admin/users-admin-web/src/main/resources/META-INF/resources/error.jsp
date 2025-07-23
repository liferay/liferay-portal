<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-ui:error-header />

<liferay-ui:error exception="<%= ContactNameException.MustHaveFirstName.class %>" message="please-enter-a-valid-first-name" />
<liferay-ui:error exception="<%= ContactNameException.MustHaveLastName.class %>" message="please-enter-a-valid-last-name" />
<liferay-ui:error exception="<%= ContactNameException.MustHaveMiddleName.class %>" message="please-enter-a-valid-middle-name" />
<liferay-ui:error exception="<%= ContactNameException.MustHaveValidFullName.class %>" message="please-enter-a-valid-first-middle-and-last-name" />
<liferay-ui:error exception="<%= DataLimitExceededException.class %>" message="unable-to-create-organization-because-the-maximum-number-of-organizations-has-been-reached" />
<liferay-ui:error exception="<%= NoSuchOrganizationException.class %>" message="the-organization-could-not-be-found" />
<liferay-ui:error exception="<%= NoSuchRoleException.class %>" message="the-role-could-not-be-found" />
<liferay-ui:error exception="<%= NoSuchUserException.class %>" message="the-user-could-not-be-found" />
<liferay-ui:error exception="<%= RequiredRoleException.MustNotRemoveLastAdministator.class %>" message="at-least-one-administrator-is-required" />
<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBeDuplicate.class %>" message="the-email-address-you-requested-is-already-taken" />
<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBeNull.class %>" message="please-enter-an-email-address" />
<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBePOP3User.class %>" message="the-email-address-you-requested-is-reserved" />
<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBeReserved.class %>" message="the-email-address-you-requested-is-reserved" />
<liferay-ui:error exception="<%= UserEmailAddressException.MustNotUseCompanyMx.class %>" message="the-email-address-you-requested-is-not-valid-because-its-domain-is-reserved" />
<liferay-ui:error exception="<%= UserEmailAddressException.MustValidate.class %>" message="please-enter-a-valid-email-address" />
<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeDuplicate.class %>" message="the-screen-name-you-requested-is-already-taken" />
<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeNull.class %>" message="the-screen-name-cannot-be-blank" />
<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeNumeric.class %>" message="the-screen-name-cannot-contain-only-numeric-values" />
<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeReserved.class %>" message="the-screen-name-you-requested-is-reserved" />
<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeReservedForAnonymous.class %>" message="the-screen-name-you-requested-is-reserved-for-the-anonymous-user" />
<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeUsedByGroup.class %>" message="the-screen-name-you-requested-is-already-taken-by-a-site" />

<liferay-ui:error exception="<%= UserScreenNameException.MustNotExceedMaximumLength.class %>">

	<%
	int screenNameMaxLength = ModelHintsUtil.getMaxLength(User.class.getName(), "screenName");
	%>

	<liferay-ui:message arguments="<%= String.valueOf(screenNameMaxLength) %>" key="please-enter-a-screen-name-with-fewer-than-x-characters" />
</liferay-ui:error>

<liferay-ui:error exception="<%= UserScreenNameException.MustProduceValidFriendlyURL.class %>" message="the-screen-name-you-requested-must-produce-a-valid-friendly-url" />
<liferay-ui:error exception="<%= UserScreenNameException.MustValidate.class %>" message="please-enter-a-valid-screen-name" />

<liferay-ui:error-principal />