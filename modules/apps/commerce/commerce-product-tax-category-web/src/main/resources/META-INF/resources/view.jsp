<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPTaxCategoryDisplayContext cpTaxCategoryDisplayContext = (CPTaxCategoryDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<c:if test="<%= cpTaxCategoryDisplayContext.hasPortletResourcePermission(CPActionKeys.VIEW_COMMERCE_PRODUCT_TAX_CATEGORIES) %>">
	<clay:management-toolbar
		managementToolbarDisplayContext="<%= new CPTaxCategoryManagementToolbarDisplayContext(cpTaxCategoryDisplayContext, request, liferayPortletRequest, liferayPortletResponse) %>"
		propsTransformer="{CPTaxCategoryManagementToolbarPropsTransformer} from commerce-product-tax-category-web"
	/>

	<div class="container-fluid container-fluid-max-xl">
		<portlet:actionURL name="/cp_tax_category/edit_cp_tax_category" var="editCPTaxCategoryActionURL" />

		<aui:form action="<%= editCPTaxCategoryActionURL %>" method="post" name="fm">
			<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.DELETE %>" />
			<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

			<liferay-ui:search-container
				id="cpTaxCategories"
				searchContainer="<%= cpTaxCategoryDisplayContext.getSearchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.commerce.product.model.CPTaxCategory"
					keyPropertyName="CPTaxCategoryId"
					modelVar="cpTaxCategory"
				>

					<%
					String availableActions = StringPool.BLANK;

					if (cpTaxCategoryDisplayContext.hasModelResourcePermission(cpTaxCategory, ActionKeys.DELETE)) {
						availableActions = "deleteEntries";
					}

					row.setData(
						HashMapBuilder.<String, Object>put(
							"actions", availableActions
						).build());
					%>

					<liferay-ui:search-container-column-text
						cssClass="font-weight-bold important table-cell-expand"
						href="<%= cpTaxCategoryDisplayContext.getRowURL(cpTaxCategory, currentURL) %>"
						name="name"
						value="<%= HtmlUtil.escape(cpTaxCategory.getName(languageId)) %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						name="description"
						value="<%= HtmlUtil.escape(cpTaxCategory.getDescription(languageId)) %>"
					/>

					<liferay-ui:search-container-column-date
						cssClass="table-cell-expand"
						name="create-date"
						property="createDate"
					/>

					<liferay-ui:search-container-column-jsp
						cssClass="entry-action-column"
						path="/tax_category_action.jsp"
					/>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</aui:form>
	</div>
</c:if>