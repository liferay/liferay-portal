<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/user_name_fields/init.jsp" %>

<aui:select label="language" name="languageId">

	<%
	for (Locale curLocale : LanguageUtil.getAvailableLocales()) {
	%>

		<aui:option label="<%= curLocale.getDisplayName(curLocale) %>" lang="<%= LocaleUtil.toW3cLanguageId(curLocale) %>" selected="<%= userLocale.getLanguage().equals(curLocale.getLanguage()) && userLocale.getCountry().equals(curLocale.getCountry()) %>" value="<%= LocaleUtil.toLanguageId(curLocale) %>" />

	<%
	}
	%>

</aui:select>

<liferay-frontend:component
	componentId="UserNameFields"
	context='<%=
		HashMapBuilder.<String, Object>put(
			"baseURL", themeDisplay.getURLCurrent()
		).put(
			"formNode", "#" + liferayPortletResponse.getNamespace() + "fm"
		).put(
			"languageIdSelectNode", "#" + liferayPortletResponse.getNamespace() + "languageId"
		).put(
			"portletNamespace", liferayPortletResponse.getNamespace()
		).put(
			"userNameFieldsNode", "#" + liferayPortletResponse.getNamespace() + "userNameFields"
		).build()
	%>'
	module="{UserNameFields} from users-admin-web"
/>

<%
FullNameDefinition fullNameDefinition = FullNameDefinitionFactory.getInstance(userLocale);
%>

<liferay-ui:error exception="<%= ContactNameException.MustHaveFirstName.class %>" message="please-enter-a-valid-first-name" />
<liferay-ui:error exception="<%= ContactNameException.MustHaveValidFullName.class %>" message="please-enter-a-valid-first-middle-and-last-name" />

<div id="<portlet:namespace />userNameFields">

	<%
	for (FullNameField fullNameField : fullNameDefinition.getFullNameFields()) {
		String fieldName = CamelCaseUtil.toCamelCase(fullNameField.getName());
	%>

		<c:choose>
			<c:when test="<%= fullNameField.isFreeText() %>">
				<aui:input bean="<%= bean %>" disabled="<%= !UsersAdminUtil.hasUpdateFieldPermission(permissionChecker, user, selUser, fieldName) %>" model="<%= User.class %>" name="<%= fieldName %>" required="<%= fullNameField.isRequired() %>" />
			</c:when>
			<c:otherwise>
				<aui:select disabled="<%= !UsersAdminUtil.hasUpdateFieldPermission(permissionChecker, user, selUser, fieldName) %>" label="<%= fieldName %>" name='<%= fieldName.concat("ListTypeValue") %>' showEmptyOption="<%= true %>">

					<%
					String listTypeName = StringPool.BLANK;

					if (selContact != null) {
						ToLongFunction<Contact> toLongFunction = Contact::getSuffixListTypeId;

						if (fieldName.equals(LanguageConstants.VALUE_PREFIX)) {
							toLongFunction = Contact::getPrefixListTypeId;
						}

						long listTypeId = BeanParamUtil.getLong(selContact, request, fieldName.concat("ListTypeId"), toLongFunction);

						try {
							ListType listType = ListTypeServiceUtil.getListType(listTypeId);

							listTypeName = listType.getName();
						}
						catch (Exception e) {
						}
					}

					for (String value : fullNameField.getValues()) {
					%>

						<aui:option label="<%= value %>" selected="<%= StringUtil.equalsIgnoreCase(listTypeName, value) %>" value="<%= value %>" />

					<%
					}
					%>

				</aui:select>
			</c:otherwise>
		</c:choose>

	<%
	}
	%>

</div>