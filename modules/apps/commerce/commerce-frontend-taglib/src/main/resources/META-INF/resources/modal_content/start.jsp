<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/modal_content/init.jsp" %>

<div class="modal-iframe-wrapper">
	<c:if test="<%= Validator.isNotNull(title) %>">
		<header class="bg-white modal-header modal-iframe-header position-fixed w-100">
			<h2 class="modal-title"><%= HtmlUtil.escape(title) %></h2>

			<button aria-label="close" class="btn btn-unstyled close modal-closer" type="button">
				<clay:icon
					symbol="times"
				/>
			</button>
		</header>
	</c:if>

	<div class="liferay-modal-body modal-body modal-iframe-content<%= Validator.isNotNull(contentCssClasses) ? StringPool.SPACE + HtmlUtil.escapeAttribute(contentCssClasses) : StringPool.BLANK %> pt-6">