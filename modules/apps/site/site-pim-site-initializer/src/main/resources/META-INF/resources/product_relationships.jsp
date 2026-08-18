<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ProductRelationshipsDisplayContext productRelationshipsDisplayContext = (ProductRelationshipsDisplayContext)request.getAttribute(ProductRelationshipsDisplayContext.class.getName());
%>

<div class="pim-product-relationships">
	<div>
		<section aria-label="<liferay-ui:message key="product-relationships" />" class="autofit-row autofit-row-center cms-breadcrumb px-4">
			<div class="autofit-col">
				<div class="c-gap-2 d-flex">
					<h2 class="font-weight-semi-bold mb-0 text-7 text-dark">
						<liferay-ui:message key="product-relationships" />
					</h2>
				</div>
			</div>
		</section>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			apiURL="<%= productRelationshipsDisplayContext.getAPIURL() %>"
			creationMenu="<%= productRelationshipsDisplayContext.getCreationMenu() %>"
			emptyState="<%= productRelationshipsDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= productRelationshipsDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= PIMFDSNames.PRODUCT_RELATIONSHIPS %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{ProductRelationshipsFDSPropsTransformer} from site-pim-site-initializer"
		/>
	</div>
</div>