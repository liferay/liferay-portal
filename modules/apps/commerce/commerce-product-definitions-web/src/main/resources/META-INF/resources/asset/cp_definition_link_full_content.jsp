<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<div class="container-fluid container-fluid-max-xl">
	<liferay-ui:search-container
		id="cpDefinitionLinks"
	>
		<liferay-ui:search-container-results
			results="<%= (List<CPDefinitionLink>)request.getAttribute(CPWebKeys.CP_DEFINITION_LINK) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.commerce.product.model.CPDefinitionLink"
			escapedModel="<%= true %>"
			keyProperty="CPDefinitionLinkId"
			modelVar="cpDefinitionLink"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="name"
				value="<%= LanguageUtil.get(request, HtmlUtil.escape(cpDefinitionLink.getCProductName())) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="type"
				value="<%= LanguageUtil.get(request, cpDefinitionLink.getType()) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="order"
				property="priority"
			/>

			<liferay-ui:search-container-column-status
				cssClass="table-cell-expand"
				property="status"
			/>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand"
				name="create-date"
				property="createDate"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</div>