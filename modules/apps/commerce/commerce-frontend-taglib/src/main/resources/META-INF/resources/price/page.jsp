<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/price/init.jsp" %>

<c:if test="<%= priceModel != null %>">

	<%
	String containerId = StringPool.BLANK;

	if (Validator.isNotNull(namespace) || (Validator.isNull(namespace) && !compact)) {
		containerId = namespace + "price";
	}
	%>

	<span class="<%= Validator.isNotNull(namespace) ? namespace + "price price" : "price" %><%= compact ? " compact" : StringPool.BLANK %>" id="<%= containerId %>">
		<liferay-util:include page="/price/default.jsp" servletContext="<%= application %>" />

		<c:if test="<%= !priceModel.isPriceOnApplication() %>">
			<c:choose>
				<c:when test="<%= compact %>">
					<c:choose>
						<c:when test="<%= Validator.isNull(priceModel.getDiscount()) %>">
							<c:if test="<%= Validator.isNotNull(priceModel.getPromoPrice()) %>">
								<liferay-util:include page="/price/promo.jsp" servletContext="<%= application %>" />
							</c:if>
						</c:when>
						<c:otherwise>
							<c:if test="<%= Validator.isNotNull(priceModel.getFinalPrice()) %>">
								<liferay-util:include page="/price/discount.jsp" servletContext="<%= application %>" />
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:if test="<%= Validator.isNotNull(priceModel.getPromoPrice()) %>">
						<liferay-util:include page="/price/promo.jsp" servletContext="<%= application %>" />
					</c:if>

					<c:if test="<%= Validator.isNotNull(priceModel.getFinalPrice()) %>">
						<liferay-util:include page="/price/discount.jsp" servletContext="<%= application %>" />
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:if>
	</span>

	<c:if test="<%= Validator.isNotNull(containerId) %>">
		<liferay-frontend:component
			context='<%=
				HashMapBuilder.<String, Object>put(
					"containerId", containerId
				).put(
					"displayDiscountLevels", displayDiscountLevels
				).put(
					"namespace", namespace
				).put(
					"netPrice", netPrice
				).put(
					"price", priceModel
				).put(
					"standalone", true
				).build()
			%>'
			module="{price} from commerce-frontend-taglib"
		/>
	</c:if>
</c:if>