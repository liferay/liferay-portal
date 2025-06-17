<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPCompareContentHelper cpCompareContentHelper = (CPCompareContentHelper)request.getAttribute(CPContentWebKeys.CP_COMPARE_CONTENT_HELPER);

CPContentHelper cpContentHelper = (CPContentHelper)request.getAttribute(CPContentWebKeys.CP_CONTENT_HELPER);

CPCatalogEntry cpCatalogEntry = cpContentHelper.getCPCatalogEntry(request);

boolean hasMultipleCPSkus = cpContentHelper.hasMultipleCPSkus(cpCatalogEntry);
%>

<div class="cp-renderer">
	<liferay-util:dynamic-include key="com.liferay.commerce.product.content.web#/add_to_cart#pre" />

	<div class="card d-flex flex-column product-card">
		<div class="card-item-first position-relative">

			<%
			String productDetailURL = cpContentHelper.getFriendlyURL(cpCatalogEntry, themeDisplay);
			%>

			<a href="<%= productDetailURL %>">

				<%
				String cpDefinitionCDNURL = cpContentHelper.getCPDefinitionCDNURL(cpCatalogEntry.getCPDefinitionId(), request);
				%>

				<c:choose>
					<c:when test="<%= Validator.isNotNull(cpDefinitionCDNURL) %>">
						<img alt="thumbnail" class="img-fluid product-card-picture" src="<%= cpDefinitionCDNURL %>" />
					</c:when>
					<c:otherwise>
						<liferay-adaptive-media:img
							alt="thumbnail"
							class="img-fluid product-card-picture"
							fileVersion="<%= cpContentHelper.getCPDefinitionImageFileVersion(cpCatalogEntry.getCPDefinitionId(), request) %>"
						/>
					</c:otherwise>
				</c:choose>

				<div class="aspect-ratio-item-bottom-left">
					<c:if test="<%= !hasMultipleCPSkus %>">
						<commerce-ui:availability-label
							CPCatalogEntry="<%= cpCatalogEntry %>"
						/>

						<commerce-ui:discontinued-label
							CPCatalogEntry="<%= cpCatalogEntry %>"
						/>
					</c:if>
				</div>
			</a>
		</div>

		<div class="card-body d-flex flex-column justify-content-between py-2">
			<div class="cp-information">

				<%
				String sku = StringPool.BLANK;

				CPSku cpSku = cpContentHelper.getDefaultCPSku(cpCatalogEntry);

				if (!hasMultipleCPSkus && (cpSku != null)) {
					sku = cpSku.getSku();
				}
				%>

				<p class="card-subtitle" title="<%= sku %>">
					<span class="text-truncate-inline">
						<span class="text-truncate"><%= sku %></span>
					</span>
				</p>

				<p class="card-title" title="<%= cpCatalogEntry.getName() %>">
					<a href="<%= productDetailURL %>">
						<span class="text-truncate-inline">
							<span class="text-truncate"><%= cpCatalogEntry.getName() %></span>
						</span>
					</a>
				</p>

				<p class="card-text">
					<span class="text-truncate-inline">
						<span class="d-flex flex-row text-truncate">
							<commerce-ui:price
								compact="<%= true %>"
								CPCatalogEntry="<%= cpCatalogEntry %>"
							/>
						</span>
					</span>
				</p>
			</div>

			<div>
				<c:choose>
					<c:when test="<%= !hasMultipleCPSkus && (cpSku != null) && !cpContentHelper.hasRequiredCPDefinitionOptionRels(cpCatalogEntry.getCPDefinitionId()) %>">
						<div class="mt-2">
							<commerce-ui:add-to-cart
								alignment="full-width"
								CPCatalogEntry="<%= cpCatalogEntry %>"
								inline="<%= false %>"
								namespace="<%= portletDisplay.getNamespace() %>"
								size="md"
								skuOptions="[]"
							/>
						</div>
					</c:when>
					<c:otherwise>
						<div class="add-to-cart d-flex my-2 pt-5" id="<%= PortalUtil.generateRandomKey(request, "taglib") + StringPool.UNDERLINE %>add_to_cart">
							<a class="btn btn-block btn-secondary mt-2" href="<%= productDetailURL %>" role="button">
								<liferay-ui:message key="view-all-variants" />
							</a>
						</div>
					</c:otherwise>
				</c:choose>

				<div class="autofit-float autofit-row autofit-row-center compare-wishlist">
					<div class="autofit-col autofit-col-expand compare-checkbox">
						<div class="autofit-section">
							<div class="custom-checkbox custom-control custom-control-primary">
								<div class="custom-checkbox custom-control">
									<commerce-ui:compare-checkbox
										CPCatalogEntry="<%= cpCatalogEntry %>"
										label='<%= LanguageUtil.get(request, "compare") %>'
										refreshOnRemove="<%= (cpCompareContentHelper != null) && CPPortletKeys.CP_COMPARE_CONTENT_WEB.equals(portletDisplay.getPortletName()) %>"
									/>
								</div>
							</div>
						</div>
					</div>

					<div class="autofit-col">
						<div class="autofit-section">
							<commerce-ui:add-to-wish-list
								CPCatalogEntry="<%= cpCatalogEntry %>"
							/>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<liferay-util:dynamic-include key="com.liferay.commerce.product.content.web#/add_to_cart#post" />
</div>