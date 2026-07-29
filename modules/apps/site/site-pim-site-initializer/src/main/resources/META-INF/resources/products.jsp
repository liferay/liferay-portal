<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ProductsSectionDisplayContext productsSectionDisplayContext = (ProductsSectionDisplayContext)request.getAttribute(ProductsSectionDisplayContext.class.getName());
%>

<div class="pim-products">
	<div>
		<section aria-label="<liferay-ui:message key="products" />" class="autofit-row autofit-row-center cms-breadcrumb px-4">
			<div class="autofit-col">
				<div class="c-gap-2 d-flex">
					<h2 class="font-weight-semi-bold mb-0 text-7 text-dark">
						<liferay-ui:message key="products" />
					</h2>
				</div>
			</div>
		</section>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			apiURL="<%= productsSectionDisplayContext.getAPIURL() %>"
			bulkActionDropdownItems="<%= productsSectionDisplayContext.getBulkActionDropdownItems() %>"
			creationMenu="<%= productsSectionDisplayContext.getCreationMenu() %>"
			emptyState="<%= productsSectionDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= productsSectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= PIMFDSNames.PRODUCTS %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{ProductsFDSPropsTransformer} from site-pim-site-initializer"
			selectedItemsKey="embedded.id"
			selectionType="multiple"
			showSelectAll="<%= true %>"
		/>
	</div>
</div>