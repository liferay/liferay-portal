<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<h3>portal-properties</h3>

<p>
	<%= _testProperty("terms.of.use.required", false) %><br />
</p>

<p>
	application.startup.events=<%= _assertTrue(TestHookUtil.getStartupActionFile().exists()) %>
</p>

<p>
	<%= _testProperty("field.enable.com.liferay.portal.kernel.model.Contact.male", false) %><br />
	<%= _testProperty("field.enable.com.liferay.portal.kernel.model.Contact.birthday", false) %><br />
	<%= _testProperty("field.enable.com.liferay.portal.kernel.model.Organization.status", true) %>
</p>

<h3>language-properties</h3>

<p>
	jakarta.portlet.title.33=<%= _assertEquals("Blogger", LanguageUtil.get(request, "jakarta.portlet.title.33")) %>
</p>

<h3>custom-jsp-dir</h3>

<liferay-util:buffer
	var="setupWizardJsp"
>
	<liferay-util:include page="/html/portal/setup_wizard.jsp" />
</liferay-util:buffer>

<p>
	/META-INF/custom_jsps=<%= _assertTrue(setupWizardJsp.contains("Custom Setup Wizard Header")) %>
</p>

<h3>service</h3>

<p>

	<%
	Class<?> clazz = UserLocalServiceUtil.getUserByEmailAddress(
		themeDisplay.getCompanyId(), "test@liferay.com"
	).getClass();
	%>

	com.liferay.portal.kernel.service.UserLocalService=<%= _assertEquals(TestHookUserImpl.class.getName(), clazz.getName()) %>
</p>

<%!
private static String _assertEquals(Object expected, Object actual) {
	return _assertTrue(Objects.equals(expected, actual));
}

private static String _assertFalse(boolean value) {
	return _assertTrue(!value);
}

private static String _assertFalse(String value) {
	return _assertFalse(GetterUtil.getBoolean(value));
}

private static String _assertTrue(boolean value) {
	if (value) {
		return "PASSED";
	}
	else {
		return "FAILED";
	}
}

private static String _assertTrue(String value) {
	return _assertTrue(GetterUtil.getBoolean(value));
}

private static String _testProperty(String key, boolean expected) throws Exception {
	StringBuilder sb = new StringBuilder();

	sb.append(key);
	sb.append(StringPool.EQUAL);

	String actual = PropsUtil.get(key);

	if (expected) {
		sb.append(_assertTrue(actual));
	}
	else {
		sb.append(_assertFalse(actual));
	}

	return sb.toString();
}
%>