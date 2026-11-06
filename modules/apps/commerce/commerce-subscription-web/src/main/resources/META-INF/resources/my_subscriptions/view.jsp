<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceSubscriptionContentDisplayContext commerceSubscriptionContentDisplayContext = (CommerceSubscriptionContentDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<c:choose>
	<c:when test="<%= !commerceSubscriptionContentDisplayContext.hasCommerceChannel() %>">
		<div class="alert alert-info mx-auto">
			<liferay-ui:message key="this-site-does-not-have-a-channel" />
		</div>
	</c:when>
	<c:otherwise>
		<div class="container-fluid container-fluid-max-xl" id="<portlet:namespace />subscriptionEntriesContainer">
			<div class="commerce-product-subscription-entries-container" id="<portlet:namespace />entriesContainer">
				<liferay-ui:search-container
					id="commerceSubscriptionEntries"
					iteratorURL="<%= commerceSubscriptionContentDisplayContext.getPortletURL() %>"
					searchContainer="<%= commerceSubscriptionContentDisplayContext.getSearchContainer() %>"
				>
					<liferay-ui:search-container-row
						className="com.liferay.commerce.model.CommerceSubscriptionEntry"
						keyPropertyName="commerceSubscriptionEntryId"
						modelVar="commerceSubscriptionEntry"
					>

						<%
						CommerceOrderItem commerceOrderItem = commerceSubscriptionEntry.fetchCommerceOrderItem();

						String thumbnailSrc = commerceSubscriptionContentDisplayContext.getCommerceSubscriptionEntryThumbnailSrc(commerceSubscriptionEntry);

						StringJoiner stringJoiner = new StringJoiner(StringPool.COMMA);

						for (KeyValuePair keyValuePair : commerceSubscriptionContentDisplayContext.getKeyValuePairs(commerceSubscriptionEntry)) {
							stringJoiner.add(keyValuePair.getValue());
						}
						%>

						<liferay-ui:search-container-column-image
							name="product"
							src="<%= thumbnailSrc %>"
						/>

						<liferay-ui:search-container-column-text
							name="description"
						>
							<a class="font-weight-bold" href="<%= HtmlUtil.escapeHREF(commerceSubscriptionContentDisplayContext.getCPDefinitionURL(commerceSubscriptionEntry, themeDisplay)) %>">
								<%= (commerceOrderItem == null) ? StringPool.BLANK : HtmlUtil.escape(commerceOrderItem.getName(languageId)) %>
							</a>

							<div class="h6 text-default">
								<%= HtmlUtil.escape(stringJoiner.toString()) %>
							</div>
						</liferay-ui:search-container-column-text>

						<liferay-ui:search-container-column-text
							name="status"
						>
							<div class="h6 text-default">
								<%= HtmlUtil.escape(LanguageUtil.get(request, CommerceSubscriptionEntryConstants.getSubscriptionStatusLabel(commerceSubscriptionEntry.getSubscriptionStatus()))) %>
							</div>
						</liferay-ui:search-container-column-text>

						<liferay-ui:search-container-column-date
							name="start-date"
							property="startDate"
						/>

						<liferay-ui:search-container-column-date
							name="next-iteration-date"
							property="nextIterationDate"
						/>

						<%
						CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();
						%>

						<c:if test="<%= commerceSubscriptionContentDisplayContext.isPaymentMethodActive(commerceOrder.getCommercePaymentMethodKey()) %>">
							<liferay-ui:search-container-column-text>

								<%
								DropdownItemList commerceSubscriptionEntryActionItemList = commerceSubscriptionContentDisplayContext.getCommerceSubscriptionEntryActionItemList(commerceSubscriptionEntry, renderRequest, renderResponse);
								%>

								<c:if test="<%= commerceSubscriptionEntryActionItemList != null %>">
									<c:choose>
										<c:when test="<%= commerceSubscriptionEntryActionItemList.isEmpty() %>">
										</c:when>
										<c:when test="<%= commerceSubscriptionEntryActionItemList.size() > 1 %>">
											<clay:dropdown-menu
												dropdownItems="<%= commerceSubscriptionEntryActionItemList %>"
											/>
										</c:when>
										<c:otherwise>

											<%
											DropdownItem dropdownItem = commerceSubscriptionEntryActionItemList.get(0);
											%>

											<clay:link
												displayType="secondary"
												href='<%= String.valueOf(dropdownItem.get("href")) %>'
												label='<%= String.valueOf(dropdownItem.get("label")) %>'
											/>
										</c:otherwise>
									</c:choose>
								</c:if>
							</liferay-ui:search-container-column-text>
						</c:if>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						displayStyle="list"
						markupView="lexicon"
					/>
				</liferay-ui:search-container>
			</div>
		</div>
	</c:otherwise>
</c:choose>