<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String contents = (String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":contents");
Map<String, Object> data = (Map<String, Object>)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":data");
String name = namespace + GetterUtil.getString((String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":name"));
String onChangeMethod = (String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":onChangeMethod");
String placeholder = GetterUtil.getString((String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":placeholder"));
String toolbarSet = (String)request.getAttribute(CKEditorConstants.ATTRIBUTE_NAMESPACE + ":toolbarSet");

if (Validator.isNotNull(onChangeMethod)) {
	onChangeMethod = namespace + onChangeMethod;
}

JSONObject editorConfigJSONObject = null;

if (data != null) {
	editorConfigJSONObject = (JSONObject)data.get("editorConfig");
}
%>

<div>
	<react:component
		module="{ClassicEditor} from frontend-editor-ckeditor-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"ariaLabel", LanguageUtil.get(request, placeholder)
			).put(
				"contents", contents
			).put(
				"editorConfig", editorConfigJSONObject
			).put(
				"initialToolbarSet", toolbarSet
			).put(
				"name", HtmlUtil.escapeAttribute(name)
			).put(
				"onChangeMethodName", HtmlUtil.escapeJS(onChangeMethod)
			).put(
				"title", LanguageUtil.get(request, placeholder)
			).build()
		%>'
	/>
</div>