<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceReturnEditDisplayContext commerceReturnEditDisplayContext = (CommerceReturnEditDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceReturn commerceReturn = commerceReturnEditDisplayContext.getCommerceReturn();

CommerceOrder commerceOrder = commerceReturnEditDisplayContext.getCommerceReturnCommerceOrder();
%>

<portlet:actionURL name="/commerce_return/edit_commerce_return" var="editCommerceReturnActionURL" />

<aui:form action="<%= editCommerceReturnActionURL %>" cssClass="pt-4" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

	<aui:model-context bean="<%= commerceReturn.getObjectEntry() %>" model="<%= ObjectEntry.class %>" />

	<aui:input name="primaryKey" type="hidden" />

	<div class="row">
		<div class="col-12">
			<commerce-ui:panel
				elementClasses="flex-fill"
				title='<%= LanguageUtil.get(request, "details") %>'
			>
				<div class="row vertically-divided">
					<div class="col-xl-4">
						<commerce-ui:info-box
							elementClasses="py-3"
							title='<%= LanguageUtil.get(request, "account-info") %>'
						>

							<%
							AccountEntry accountEntry = commerceReturnEditDisplayContext.getCommerceReturnAccountEntry();
							%>

							<c:choose>
								<c:when test="<%= Validator.isNull(accountEntry) %>">
									<span class="text-muted">
										<%= StringPool.BLANK %>
									</span>
								</c:when>
								<c:otherwise>
									<p class="mb-0" data-qa-id="commerceReturnAccountEntryName"><%= HtmlUtil.escape(accountEntry.getName()) %></p>
									<p class="mb-0">#<%= accountEntry.getAccountEntryId() %></p>
								</c:otherwise>
							</c:choose>
						</commerce-ui:info-box>

						<commerce-ui:info-box
							elementClasses="py-3"
							title='<%= LanguageUtil.get(request, "shipping-address") %>'
						>

							<%
							CommerceAddress shippingCommerceAddress = commerceOrder.getShippingAddress();
							%>

							<c:if test="<%= shippingCommerceAddress != null %>">
								<p class="mb-0">
									<%= HtmlUtil.escape(shippingCommerceAddress.getStreet1()) %>
								</p>

								<c:if test="<%= !Validator.isBlank(shippingCommerceAddress.getStreet2()) %>">
									<p class="mb-0">
										<%= HtmlUtil.escape(shippingCommerceAddress.getStreet2()) %>
									</p>

									<p class="mb-0">
										<%= HtmlUtil.escape(shippingCommerceAddress.getStreet3()) %>
									</p>
								</c:if>

								<p class="mb-0">
									<%= commerceReturnEditDisplayContext.getDescriptiveAddress(shippingCommerceAddress) %>
								</p>
							</c:if>
						</commerce-ui:info-box>
					</div>

					<div class="col-xl-4">
						<commerce-ui:info-box
							elementClasses="py-3"
							title='<%= LanguageUtil.get(request, "order-id") %>'
						>
							<p class="mb-0" data-qa-id="commerceReturnOrderId">#<%= commerceOrder.getCommerceOrderId() %></p>
						</commerce-ui:info-box>

						<commerce-ui:info-box
							elementClasses="py-3"
							title='<%= LanguageUtil.get(request, "order-date") %>'
						>
							<p class="mb-0" data-qa-id="commerceReturnOrderDate">
								<%= commerceReturnEditDisplayContext.getDateTimeFormatted(commerceOrder.getOrderDate()) %>
							</p>
						</commerce-ui:info-box>

						<commerce-ui:info-box
							elementClasses="py-3"
							title='<%= LanguageUtil.get(request, "channel") %>'
						>
							<p class="mb-0" data-qa-id="commerceReturnChannelName"><%= HtmlUtil.escape(commerceReturn.getChannelName()) %></p>
						</commerce-ui:info-box>
					</div>

					<div class="col-xl-4">
						<commerce-ui:info-box
							elementClasses="py-3"
							title='<%= LanguageUtil.get(request, "payment-method") %>'
						>
							<p class="mb-0" data-qa-id="commerceReturnPaymentMethod"><%= HtmlUtil.escape(LanguageUtil.get(request, commerceOrder.getCommercePaymentMethodKey())) %></p>
						</commerce-ui:info-box>

						<commerce-ui:info-box
							elementClasses="py-3"
							title='<%= LanguageUtil.get(request, "return-date") %>'
						>
							<p class="mb-0" data-qa-id="commerceReturnDate">
								<%= commerceReturnEditDisplayContext.getDateTimeFormatted(commerceReturn.getCreateDate()) %>
							</p>
						</commerce-ui:info-box>

						<liferay-portlet:renderURL var="editCommerceReturnNoteURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
							<portlet:param name="mvcRenderCommandName" value="/commerce_return/edit_commerce_return_note" />
							<portlet:param name="commerceReturnId" value="<%= String.valueOf(commerceReturn.getId()) %>" />
						</liferay-portlet:renderURL>

						<commerce-ui:modal
							id="commerce-return-note-modal"
							refreshPageOnClose="<%= true %>"
							size="lg"
							title='<%= LanguageUtil.get(request, "comment") %>'
							url="<%= editCommerceReturnNoteURL %>"
						/>

						<%
						String note = commerceReturn.getNote();
						%>

						<commerce-ui:info-box
							actionLabel='<%= LanguageUtil.get(request, Validator.isNull(note) ? "add" : "edit") %>'
							actionTargetId="commerce-return-note-modal"
							actionUrl="<%= editCommerceReturnNoteURL %>"
							elementClasses="py-3"
							title='<%= LanguageUtil.get(request, "comment") %>'
						>
							<c:choose>
								<c:when test="<%= Validator.isNull(note) %>">
									<span class="text-muted">
										<liferay-ui:message key="click-add-to-insert" />
									</span>
								</c:when>
								<c:otherwise>
									<%= HtmlUtil.escape(note) %>
								</c:otherwise>
							</c:choose>
						</commerce-ui:info-box>
					</div>
				</div>
			</commerce-ui:panel>
		</div>

		<div class="col-12">
			<commerce-ui:panel
				bodyClasses="p-0"
				title='<%= LanguageUtil.get(request, "items") %>'
			>
				<frontend-data-set:headless-display
					additionalProps='<%=
						HashMapBuilder.<String, Object>put(
							"namespace", liferayPortletResponse.getNamespace()
						).build()
					%>'
					apiURL='<%= "/o/commerce/return-items?filter=r_commerceReturnToCommerceReturnItems_l_commerceReturnId eq '" + commerceReturn.getId() + "'&nestedFields=commerceOrderItem" %>'
					fdsActionDropdownItems="<%= commerceReturnEditDisplayContext.getCommerceReturnItemFDSActionDropdownItems() %>"
					id="<%= CommerceReturnFDSNames.RETURN_ITEMS %>"
					propsTransformer="{commerceReturnItemsPropsTransformer} from commerce-order-web"
					style="fluid"
				/>
			</commerce-ui:panel>
		</div>

		<div class="col-12">
			<commerce-ui:panel
				title='<%= LanguageUtil.get(request, "return-summary") %>'
			>
				<div class="row summary-table text-right">
					<div class="col-6 col-md-9">
						<span class="summary-table-item">
							<liferay-ui:message key="number-of-items" />
						</span>
					</div>

					<div class="col-6 col-md-3">
						<span class="summary-table-item"><%= commerceReturn.getRequestedItems() %></span>
					</div>

					<div class="col-6 col-md-9">
						<span class="summary-table-item"><liferay-ui:message key="delivery" /></span>
					</div>

					<div class="col-6 col-md-3">
						<span class="summary-table-item"><%= commerceReturnEditDisplayContext.getCommerceOrderShippingAmountFormatted() %></span>
					</div>

					<div class="col-6 col-md-9">
						<span class="summary-table-item"><liferay-ui:message key="return-subtotal" /></span>
					</div>

					<div class="col-6 col-md-3">
						<span class="summary-table-item"><%= commerceReturnEditDisplayContext.getAmountFormatted(commerceReturn.getTotalAmount()) %></span>
					</div>

					<div class="col-12">
						<hr />
					</div>

					<div class="col-6 col-md-9">
						<div class="h4 my-2 summary-table-item-big"><liferay-ui:message key="total-estimated-return" /></div>
					</div>

					<div class="col-6 col-md-3">
						<div class="h4 my-2 summary-table-item-big"><%= commerceReturnEditDisplayContext.getAmountFormatted(commerceReturn.getTotalAmount()) %></div>
					</div>
				</div>
			</commerce-ui:panel>
		</div>
	</div>
</aui:form>