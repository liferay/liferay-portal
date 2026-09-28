<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/fragment/renderer/dynamic_field/init.jsp" %>

<div class="<%= namespace %>dynamic-field" id="<%= elementId %>">
	<c:if test="<%= !Validator.isBlank(label) %>">
		<<%= labelElementType %> class="node-label"><%= HtmlUtil.escape(label) %>:</<%= labelElementType %>>
	</c:if>
	<<%= valueElementType %> class="node-value"><%= HtmlUtil.escape(fieldValue) %></<%= valueElementType %>>
</div>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"elementId", elementId
		).put(
			"field", field
		).put(
			"namespace", namespace
		).build()
	%>'
	module="{CPInstanceChangeHandler} from commerce-product-content-web"
/>