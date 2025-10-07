<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/fragment/renderer/orders_data_set/init.jsp" %>

<c:choose>
	<c:when test="<%= name.equals(CommerceOrderFragmentFDSNames.PENDING_ORDERS) %>">
		<frontend-data-set:headless-display
			additionalProps="<%= additionalProps %>"
			apiURL="<%= apiURL %>"
			bulkActionDropdownItems="<%= bulkActionDropdownItems %>"
			creationMenu="<%= fdsCreationMenu %>"
			fdsActionDropdownItems="<%= fdsActionDropdownItems %>"
			id="<%= name %>"
			propsTransformer="<%= propsTransformer %>"
			selectedItemsKey="id"
			selectionType="multiple"
			style="<%= displayStyle %>"
		/>

		<liferay-frontend:component
			context='<%=
				HashMapBuilder.<String, Object>put(
					"additionalProps", additionalProps
				).build()
			%>'
			module="{createCommerceCart} from commerce-frontend-js"
		/>
	</c:when>
	<c:otherwise>
		<frontend-data-set:headless-display
			additionalProps="<%= additionalProps %>"
			apiURL="<%= apiURL %>"
			creationMenu="<%= fdsCreationMenu %>"
			fdsActionDropdownItems="<%= fdsActionDropdownItems %>"
			id="<%= name %>"
			propsTransformer="<%= propsTransformer %>"
			style="<%= displayStyle %>"
		/>

		<c:if test='<%= FeatureFlagManagerUtil.isEnabled(company.getCompanyId(), "LPD-10562") %>'>
			<liferay-frontend:component
				context='<%=
					HashMapBuilder.<String, Object>put(
						"namespace", namespace
					).put(
						"returnableOrderItemsContextParams", returnableOrderItemsContextParams
					).put(
						"viewReturnableOrderItemsURL", viewReturnableOrderItemsURL
					).build()
				%>'
				module="{viewCommerceOrderDetailsCTAs} from commerce-order-content-web"
			/>
		</c:if>
	</c:otherwise>
</c:choose>