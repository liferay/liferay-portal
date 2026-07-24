<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceOrder commerceOrder = commerceOrderContentDisplayContext.getCommerceOrder();
%>

<liferay-portlet:renderURL var="editPaymentTermsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="mvcRenderCommandName" value="/commerce_order_content/view_commerce_order_payment_terms" />
	<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrderContentDisplayContext.getCommerceOrderId()) %>" />
</liferay-portlet:renderURL>

<liferay-portlet:renderURL var="editDeliveryTermsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="mvcRenderCommandName" value="/commerce_order_content/view_commerce_order_delivery_terms" />
	<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrderContentDisplayContext.getCommerceOrderId()) %>" />
</liferay-portlet:renderURL>

<div class="row">
	<div class="col-12">
		<commerce-ui:header
			actions="<%= commerceOrderContentDisplayContext.getHeaderActionModels() %>"
			bean="<%= commerceOrder %>"
			beanIdLabel="id"
			dropdownItems="<%= commerceOrderContentDisplayContext.getDropdownItems() %>"
			externalReferenceCode="<%= commerceOrder.getExternalReferenceCode() %>"
			model="<%= CommerceOrder.class %>"
			thumbnailURL="<%= commerceOrderContentDisplayContext.getCommerceAccountThumbnailURL() %>"
			title="<%= String.valueOf(commerceOrder.getCommerceOrderId()) %>"
			transitionPortletURL="<%= commerceOrderContentDisplayContext.getTransitionOrderPortletURL(commerceOrder) %>"
		/>
	</div>

	<c:if test="<%= !commerceOrder.isOpen() %>">
		<div class="col-12 mb-4">
			<commerce-ui:step-tracker
				spritemap="<%= themeDisplay.getPathThemeSpritemap() %>"
				steps="<%= commerceOrderContentDisplayContext.getOrderSteps() %>"
			/>
		</div>
	</c:if>

	<div class="col-12">
		<commerce-ui:panel
			elementClasses="flex-fill"
			title='<%= LanguageUtil.get(request, "details") %>'
		>
			<div class="row vertically-divided">
				<div class="col-xl-4">

					<%
					String commerceOrderName = commerceOrder.getName();
					%>

					<commerce-ui:info-box
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "name") %>'
					>
						<%= HtmlUtil.escape(commerceOrderName) %>
					</commerce-ui:info-box>

					<%
					AccountEntry accountEntry = commerceOrder.getAccountEntry();
					%>

					<commerce-ui:info-box
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "account-info") %>'
					>
						<c:choose>
							<c:when test="<%= Validator.isNull(accountEntry) %>">
								<span class="text-muted">
									<%= StringPool.BLANK %>
								</span>
							</c:when>
							<c:otherwise>
								<p class="mb-0"><%= HtmlUtil.escape(accountEntry.getName()) %></p>
								<p class="mb-0">#<%= accountEntry.getAccountEntryId() %></p>
							</c:otherwise>
						</c:choose>
					</commerce-ui:info-box>

					<%
					String purchaseOrderNumber = commerceOrder.getPurchaseOrderNumber();
					%>

					<commerce-ui:info-box
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "purchase-order-number") %>'
					>
						<%= HtmlUtil.escape(purchaseOrderNumber) %>
					</commerce-ui:info-box>

					<commerce-ui:info-box
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "channel") %>'
					>
						<%= HtmlUtil.escape(commerceOrderContentDisplayContext.fetchCommerceChannel().getName()) %>
					</commerce-ui:info-box>
				</div>

				<div class="col-xl-4">

					<%
					CommerceAddress billingCommerceAddress = commerceOrder.getBillingAddress();
					%>

					<c:if test="<%= commerceOrderContentDisplayContext.hasViewBillingAddressPermission(permissionChecker, accountEntry) && (billingCommerceAddress != null) %>">
						<commerce-ui:info-box
							elementClasses="py-3"
							title='<%= LanguageUtil.get(request, "billing-address") %>'
						>
							<p class="mb-0">
								<%= HtmlUtil.escape(billingCommerceAddress.getStreet1()) %>
							</p>

							<c:if test="<%= !Validator.isBlank(billingCommerceAddress.getStreet2()) %>">
								<p class="mb-0">
									<%= HtmlUtil.escape(billingCommerceAddress.getStreet2()) %>
								</p>
							</c:if>

							<c:if test="<%= !Validator.isBlank(billingCommerceAddress.getStreet3()) %>">
								<p class="mb-0">
									<%= HtmlUtil.escape(billingCommerceAddress.getStreet3()) %>
								</p>
							</c:if>

							<p class="mb-0">
								<%= HtmlUtil.escape(commerceOrderContentDisplayContext.getDescriptiveAddress(billingCommerceAddress)) %>
							</p>
						</commerce-ui:info-box>
					</c:if>

					<%
					CommerceAddress shippingCommerceAddress = commerceOrder.getShippingAddress();
					%>

					<c:if test="<%= shippingCommerceAddress != null %>">
						<commerce-ui:info-box
							elementClasses="py-3"
							title='<%= LanguageUtil.get(request, "shipping-address") %>'
						>
							<p class="mb-0">
								<%= HtmlUtil.escape(shippingCommerceAddress.getStreet1()) %>
							</p>

							<c:if test="<%= !Validator.isBlank(shippingCommerceAddress.getStreet2()) %>">
								<p class="mb-0">
									<%= HtmlUtil.escape(shippingCommerceAddress.getStreet2()) %>
								</p>
							</c:if>

							<c:if test="<%= !Validator.isBlank(shippingCommerceAddress.getStreet3()) %>">
								<p class="mb-0">
									<%= HtmlUtil.escape(shippingCommerceAddress.getStreet3()) %>
								</p>
							</c:if>

							<p class="mb-0">
								<%= HtmlUtil.escape(commerceOrderContentDisplayContext.getDescriptiveAddress(shippingCommerceAddress)) %>
							</p>
						</commerce-ui:info-box>
					</c:if>

					<commerce-ui:info-box
						actionContext='<%=
							HashMapBuilder.<String, Object>put(
								"containerCssClasses", "modal-height-md"
							).put(
								"namespace", liferayPortletResponse.getNamespace()
							).put(
								"refreshOnClose", true
							).put(
								"size", "md"
							).put(
								"title", (commerceOrder.getPaymentCommerceTermEntryId() == 0) ? LanguageUtil.get(request, "payment-terms") : LanguageUtil.get(request, "edit-payment-terms")
							).build()
						%>'
						actionLabel='<%= (commerceOrderContentDisplayContext.hasManageCommerceOrderPaymentTermsPermission() && (commerceOrder.getPaymentCommerceTermEntryId() > 0)) ? LanguageUtil.get(request, "view") : null %>'
						actionURL="<%= (commerceOrderContentDisplayContext.hasManageCommerceOrderPaymentTermsPermission() && (commerceOrder.getPaymentCommerceTermEntryId() > 0)) ? editPaymentTermsURL : null %>"
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "payment-terms") %>'
					>
						<c:if test="<%= commerceOrder.getPaymentCommerceTermEntryId() > 0 %>">
							<p class="mb-0">
								<%= commerceOrder.getPaymentCommerceTermEntryName() %>
							</p>
						</c:if>
					</commerce-ui:info-box>

					<commerce-ui:info-box
						actionContext='<%=
							HashMapBuilder.<String, Object>put(
								"containerCssClasses", "modal-height-md"
							).put(
								"namespace", liferayPortletResponse.getNamespace()
							).put(
								"refreshOnClose", true
							).put(
								"size", "md"
							).put(
								"title", (commerceOrder.getDeliveryCommerceTermEntryId() == 0) ? LanguageUtil.get(request, "delivery-terms") : LanguageUtil.get(request, "edit-delivery-terms")
							).build()
						%>'
						actionLabel='<%= (commerceOrderContentDisplayContext.hasManageCommerceOrderDeliveryTermsPermission() && (commerceOrder.getDeliveryCommerceTermEntryId() > 0)) ? LanguageUtil.get(request, "view") : null %>'
						actionURL="<%= (commerceOrderContentDisplayContext.hasManageCommerceOrderDeliveryTermsPermission() && (commerceOrder.getDeliveryCommerceTermEntryId() > 0)) ? editDeliveryTermsURL : null %>"
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "delivery-terms") %>'
					>
						<c:if test="<%= commerceOrder.getDeliveryCommerceTermEntryId() > 0 %>">
							<p class="mb-0">
								<%= commerceOrder.getDeliveryCommerceTermEntryName() %>
							</p>
						</c:if>
					</commerce-ui:info-box>
				</div>

				<div class="col-xl-4">
					<c:if test="<%= commerceOrder.getOrderDate() != null %>">
						<commerce-ui:info-box
							elementClasses="py-3"
							title='<%= LanguageUtil.get(request, "order-date") %>'
						>
							<%= commerceOrderContentDisplayContext.formatCommerceOrderDate(commerceOrder.getOrderDate()) %>
						</commerce-ui:info-box>
					</c:if>

					<%
					Date requestedDeliveryDate = commerceOrder.getRequestedDeliveryDate();
					%>

					<commerce-ui:info-box
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "requested-delivery-date") %>'
					>
						<%= commerceOrderContentDisplayContext.formatCommerceOrderDate(requestedDeliveryDate) %>
					</commerce-ui:info-box>

					<commerce-ui:info-box
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "order-type") %>'
					>
						<%= HtmlUtil.escape(commerceOrderContentDisplayContext.getCommerceOrderTypeName(LanguageUtil.getLanguageId(locale))) %>
					</commerce-ui:info-box>

					<portlet:renderURL var="viewCommerceOrderNotesURL">
						<portlet:param name="mvcRenderCommandName" value="/commerce_order_content/view_commerce_order_notes" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
						<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrder.getCommerceOrderId()) %>" />
					</portlet:renderURL>

					<%
					List<CommerceOrderNote> commerceOrderNotes = commerceOrderContentDisplayContext.getCommerceOrderNotes(commerceOrder);
					%>

					<commerce-ui:info-box
						actionLabel='<%= (commerceOrderNotes.size() > 0) ? LanguageUtil.get(request, "view") : null %>'
						actionURL="<%= (commerceOrderNotes.size() > 0) ? viewCommerceOrderNotesURL : null %>"
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "notes") %>'
					/>
				</div>
			</div>
		</commerce-ui:panel>
	</div>

	<div class="col-12">
		<commerce-ui:panel
			bodyClasses="p-0"
			title='<%= LanguageUtil.get(request, "items") %>'
		>
			<frontend-data-set:classic-display
				contextParams='<%=
					HashMapBuilder.<String, String>put(
						"commerceOrderId", String.valueOf(commerceOrder.getCommerceOrderId())
					).build()
				%>'
				dataProviderKey="<%= CommerceOrderFDSNames.PLACED_ORDER_ITEMS %>"
				id="<%= CommerceOrderFDSNames.PLACED_ORDER_ITEMS %>"
				nestedItemsKey="orderItemId"
				nestedItemsReferenceKey="orderItems"
			/>
		</commerce-ui:panel>
	</div>

	<div class="col-12">
		<commerce-ui:panel
			title='<%= LanguageUtil.get(request, "order-summary") %>'
		>
			<div id="summary-root"></div>

			<liferay-frontend:component
				context='<%=
					HashMapBuilder.<String, Object>put(
						"commerceOrderId", commerceOrderContentDisplayContext.getCommerceOrderId()
					).put(
						"placedOrderItems", CommerceOrderFDSNames.PLACED_ORDER_ITEMS
					).put(
						"portletId", portletDisplay.getRootPortletId()
					).build()
				%>'
				module="{newView} from commerce-order-content-web"
			/>
		</commerce-ui:panel>
	</div>
</div>

<liferay-frontend:component
	module="{view} from commerce-order-content-web"
/>