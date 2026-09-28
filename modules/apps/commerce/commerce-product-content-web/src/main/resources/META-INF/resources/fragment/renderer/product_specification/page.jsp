<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/fragment/renderer/product_specification/init.jsp" %>

<c:if test="<%= visible %>">
	<div class="<%= namespace %>product-specification" id="<%= elementId %>">
		<c:if test="<%= showLabel && !Validator.isBlank(label) %>">
			<<%= labelElementType %> class="node-label"><%= HtmlUtil.escape(label) %></<%= labelElementType %>>
		</c:if>

		<<%= valueElementType %> class="node-value"><%= HtmlUtil.escape(value) %></<%= valueElementType %>>
	</div>
</c:if>