<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceWishListDisplayContext commerceWishListDisplayContext = (CommerceWishListDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<c:choose>
	<c:when test="<%= !commerceWishListDisplayContext.hasCommerceChannel() %>">
		<div class="alert alert-info mx-auto">
			<liferay-ui:message key="this-site-does-not-have-a-channel" />
		</div>
	</c:when>
	<c:otherwise>

		<%
		CommerceWishList commerceWishList = commerceWishListDisplayContext.getCommerceWishList();
		long commerceWishListId = commerceWishListDisplayContext.getCommerceWishListId();
		%>

		<c:choose>
			<c:when test="<%= commerceWishList == null %>">
				<div class="alert alert-info mx-auto">
					<liferay-ui:message key="please-select-a-valid-wish-list" />
				</div>
			</c:when>
			<c:otherwise>
				<c:if test="<%= !commerceWishList.isGuestWishList() %>">
					<div class="container-fluid container-fluid-max-xl">
						<h3 class="d-inline"><%= HtmlUtil.escape(commerceWishList.getName()) %></h3>

						<portlet:renderURL var="editCommerceWishListURL">
							<portlet:param name="mvcRenderCommandName" value="/commerce_wish_list_content/edit_commerce_wish_list" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
							<portlet:param name="commerceWishListId" value="<%= String.valueOf(commerceWishListId) %>" />
						</portlet:renderURL>

						<aui:button cssClass="d-inline float-right" href="<%= editCommerceWishListURL %>" value="edit" />
					</div>
				</c:if>

				<portlet:actionURL name="/commerce_wish_list_content/edit_commerce_wish_list_item" var="editCommerceWishListItemActionURL" />

				<div class="container-fluid container-fluid-max-xl">
					<aui:form action="<%= editCommerceWishListItemActionURL %>" method="post" name="fm">
						<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.DELETE %>" />
						<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
						<aui:input name="deleteCommerceWishListItemIds" type="hidden" />

						<liferay-ui:search-container
							id="commerceWishListItems"
							searchContainer="<%= commerceWishListDisplayContext.getCommerceWishListItemsSearchContainer() %>"
						>
							<liferay-ui:search-container-row
								className="com.liferay.commerce.wish.list.model.CommerceWishListItem"
								keyProperty="commerceWishListItemId"
								modelVar="commerceWishListItem"
							>

								<%
								CPDefinition cpDefinition = commerceWishListItem.getCPDefinition();
								%>

								<liferay-ui:search-container-column-image
									name="product"
									src="<%= cpDefinition.getDefaultImageThumbnailSrc(commerceWishListDisplayContext.getCommerceAccountId()) %>"
								/>

								<liferay-ui:search-container-column-text
									cssClass="table-cell-expand"
									name="description"
								>
									<c:choose>
										<c:when test="<%= commerceWishListDisplayContext.isProductVisibleToAccount(cpDefinition.getCPDefinitionId()) %>">
											<a class="font-weight-bold" href="<%= commerceWishListDisplayContext.getCPDefinitionURL(cpDefinition.getCPDefinitionId(), themeDisplay) %>">
												<%= HtmlUtil.escape(cpDefinition.getName(themeDisplay.getLanguageId())) %>
											</a>
										</c:when>
										<c:otherwise>
											<span class="font-weight-bold">
												<%= HtmlUtil.escape(cpDefinition.getName(themeDisplay.getLanguageId())) %>
											</span>
										</c:otherwise>
									</c:choose>

									<div class="h6 text-default">
										<%= HtmlUtil.escape(commerceWishListDisplayContext.getCommerceWishListItemDescription(commerceWishListItem)) %>
									</div>
								</liferay-ui:search-container-column-text>

								<liferay-ui:search-container-column-text
									name="price"
									value="<%= commerceWishListDisplayContext.getCommerceWishListItemPrice(commerceWishListItem) %>"
								/>

								<liferay-ui:search-container-column-text>
									<portlet:actionURL name="/commerce_wish_list_content/edit_commerce_wish_list_item" var="deleteURL">
										<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.DELETE %>" />
										<portlet:param name="redirect" value="<%= currentURL %>" />
										<portlet:param name="commerceWishListItemId" value="<%= String.valueOf(commerceWishListItem.getCommerceWishListItemId()) %>" />
									</portlet:actionURL>

									<clay:link
										href="<%= deleteURL.toString() %>"
										label="delete"
									/>
								</liferay-ui:search-container-column-text>

								<liferay-ui:search-container-column-jsp
									cssClass="lfr-description-column"
									path="/wish_list_item_action.jsp"
								/>
							</liferay-ui:search-container-row>

							<liferay-ui:search-iterator
								markupView="lexicon"
							/>
						</liferay-ui:search-container>
					</aui:form>
				</div>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>