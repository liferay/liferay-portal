<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/input_localized/init.jsp" %>

<%
List<String> activeLanguageIds = (List<String>)request.getAttribute("liferay-editor:input-localized:activeLanguageIds");
boolean adminMode = (boolean)request.getAttribute("liferay-editor:input-localized:adminMode");
boolean autofillFromDefault = (boolean)request.getAttribute("liferay-editor:input-localized:autofillFromDefault");
String componentId = (String)request.getAttribute("liferay-editor:input-localized:componentId");
JSONArray availableLocalesJSONArray = (JSONArray)request.getAttribute("liferay-editor:input-localized:availableLocales");
String defaultLanguageId = (String)request.getAttribute("liferay-editor:input-localized:defaultLanguageId");
Map<String, Object> editorConfigurationData = (Map<String, Object>)request.getAttribute("liferay-editor:input-localized:editorConfigurationData");
boolean languagesDropdownVisible = (boolean)request.getAttribute("liferay-editor:input-localized:languagesDropdownVisible");
String name = (String)request.getAttribute("liferay-editor:input-localized:name");
String selectedLanguageId = (String)request.getAttribute("liferay-editor:input-localized:selectedLanguageId");
String onBlurMethod = (String)request.getAttribute("liferay-editor:input-localized:onBlurMethod");
String onChangeMethod = (String)request.getAttribute("liferay-editor:input-localized:onChangeMethod");
String onFocusMethod = (String)request.getAttribute("liferay-editor:input-localized:onFocusMethod");
JSONObject translationsJSONObject = (JSONObject)request.getAttribute("liferay-editor:input-localized:translations");

JSONObject editorConfigJSONObject = null;

if (editorConfigurationData != null) {
	editorConfigJSONObject = (JSONObject)editorConfigurationData.get("editorConfig");
}

if (defaultLanguageId == null) {
	defaultLanguageId = LocaleUtil.toLanguageId(themeDisplay.getSiteDefaultLocale());
}
%>

<div>
	<react:component
		componentId="<%= componentId %>"
		module="{InputLocalized} from frontend-editor-ckeditor-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"activeLanguageIds", activeLanguageIds
			).put(
				"adminMode", adminMode
			).put(
				"autofillFromDefault", autofillFromDefault
			).put(
				"availableLocales", availableLocalesJSONArray
			).put(
				"defaultLanguageId", defaultLanguageId
			).put(
				"editorConfig", editorConfigJSONObject
			).put(
				"languagesDropdownVisible", languagesDropdownVisible
			).put(
				"name", name
			).put(
				"onBlurMethod", onBlurMethod
			).put(
				"onChangeMethod", onChangeMethod
			).put(
				"onFocusMethod", onFocusMethod
			).put(
				"selectedLanguageId", selectedLanguageId
			).put(
				"translations", translationsJSONObject
			).build()
		%>'
	/>
</div>