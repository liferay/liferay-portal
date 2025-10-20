<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
PreviewFragmentCollectionDisplayContext previewFragmentCollectionDisplayContext = new PreviewFragmentCollectionDisplayContext(request);
%>

<liferay-util:html-top
	outputKey="com.liferay.style.book.web#/preview_fragment_collection.jsp"
>
	<aui:link hashedFile="<%= true %>" href="style-book-web/css/FragmentCollectionPreview.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<div>
	<react:component
		module="{FragmentCollectionPreview} from style-book-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"fragmentCollectionKey", previewFragmentCollectionDisplayContext.getFragmentCollectionKey()
			).put(
				"fragments", previewFragmentCollectionDisplayContext.getFragmentsJSONArray()
			).put(
				"namespace", previewFragmentCollectionDisplayContext.getStyleBookPortletNamespace()
			).build()
		%>'
	/>
</div>