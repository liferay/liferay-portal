<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CKEditor5SampleDisplayContext ckEditor5SampleDisplayContext = (CKEditor5SampleDisplayContext)request.getAttribute(CKEditor5SampleWebKeys.CKEDITOR_5_SAMPLE_DISPLAY_CONTEXT);
%>

<react:component
	module="{CKEditor5ReactClassicEditor} from frontend-editor-ckeditor5-sample-web"
	props='<%=
		HashMapBuilder.<String, Object>put(
			"editorConfig", ckEditor5SampleDisplayContext.getCKEditor5ClassicEditorConfig("sampleReactCKEditor5ClassicEditor")
		).build()
	%>'
/>