<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceVirtualOrderItemContentDisplayContext commerceVirtualOrderItemContentDisplayContext = (CommerceVirtualOrderItemContentDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<c:choose>
	<c:when test="<%= !commerceVirtualOrderItemContentDisplayContext.hasCommerceChannel() %>">
		<div class="alert alert-info mx-auto">
			<liferay-ui:message key="this-site-does-not-have-a-channel" />
		</div>
	</c:when>
	<c:otherwise>

		<%
		Map<String, Object> contextObjects = HashMapBuilder.<String, Object>put(
			"commerceVirtualOrderItemContentDisplayContext", commerceVirtualOrderItemContentDisplayContext
		).build();

		SearchContainer<CommerceVirtualOrderItem> commerceVirtualOrderItemContentDisplayContextSearchContainer = commerceVirtualOrderItemContentDisplayContext.getSearchContainer();
		%>

		<liferay-ddm:template-renderer
			className="<%= CommerceVirtualOrderItemContentPortlet.class.getName() %>"
			contextObjects="<%= contextObjects %>"
			displayStyle="<%= commerceVirtualOrderItemContentDisplayContext.getDisplayStyle() %>"
			displayStyleGroupId="<%= commerceVirtualOrderItemContentDisplayContext.getDisplayStyleGroupId() %>"
			entries="<%= commerceVirtualOrderItemContentDisplayContextSearchContainer.getResults() %>"
		>
			<div class="container-fluid container-fluid-max-xl" id="<portlet:namespace />virtualOrderItemsContainer">
				<div class="commerce-virtual-order-items-container" id="<portlet:namespace />entriesContainer">
					<liferay-ui:search-container
						id="commerceVirtualOrderItems"
						iteratorURL="<%= currentURLObj %>"
						searchContainer="<%= commerceVirtualOrderItemContentDisplayContextSearchContainer %>"
					>
						<liferay-ui:search-container-row
							className="com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem"
							keyPropertyName="commerceVirtualOrderItemId"
							modelVar="commerceVirtualOrderItem"
						>

							<%
							CommerceOrderItem commerceOrderItem = commerceVirtualOrderItem.getCommerceOrderItem();

							String thumbnailSrc = commerceVirtualOrderItemContentDisplayContext.getCommerceOrderItemThumbnailSrc(commerceOrderItem);

							StringJoiner stringJoiner = new StringJoiner(StringPool.COMMA);

							for (KeyValuePair keyValuePair : commerceVirtualOrderItemContentDisplayContext.getKeyValuePairs(commerceOrderItem.getCPDefinitionId(), commerceOrderItem.getJson(), locale)) {
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
								<%= HtmlUtil.escape(commerceOrderItem.getName(languageId)) %>

								<div class="h6 text-default">
									<%= HtmlUtil.escape(stringJoiner.toString()) %>
								</div>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text
								cssClass="entry-action-column font-weight-bold important"
							>

								<%
								CPDefinitionVirtualSetting cpDefinitionVirtualSetting = commerceVirtualOrderItemContentDisplayContext.getCPDefinitionVirtualSetting(commerceOrderItem);

								for (CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry : commerceVirtualOrderItem.getCommerceVirtualOrderItemFileEntries()) {
									String downloadURL = commerceVirtualOrderItemContentDisplayContext.getDownloadURL(commerceVirtualOrderItem, commerceVirtualOrderItemFileEntry.getCommerceVirtualOrderItemFileEntryId());
								%>

									<c:if test="<%= commerceVirtualOrderItemContentDisplayContext.hasPermission(permissionChecker, commerceVirtualOrderItemFileEntry, CommerceVirtualOrderActionKeys.DOWNLOAD_COMMERCE_VIRTUAL_ORDER_ITEM) && (cpDefinitionVirtualSetting != null) %>">
										<c:choose>
											<c:when test="<%= (cpDefinitionVirtualSetting != null) && cpDefinitionVirtualSetting.isTermsOfUseRequired() %>">
												<aui:form action="<%= String.valueOf(commerceVirtualOrderItemContentDisplayContext.getDownloadResourceURL(commerceVirtualOrderItem.getCommerceVirtualOrderItemId(), commerceVirtualOrderItemFileEntry.getCommerceVirtualOrderItemFileEntryId())) %>" method="post" name='<%= commerceVirtualOrderItem.getCommerceVirtualOrderItemId() + "-" + commerceVirtualOrderItemFileEntry.getCommerceVirtualOrderItemFileEntryId() + "Fm" %>' />

												<clay:button
													additionalProps='<%=
														HashMapBuilder.<String, Object>put(
															"commerceVirtualOrderItemFileEntryId", commerceVirtualOrderItemFileEntry.getCommerceVirtualOrderItemFileEntryId()
														).put(
															"commerceVirtualOrderItemId", commerceVirtualOrderItemFileEntry.getCommerceVirtualOrderItemId()
														).put(
															"dialogId", HtmlUtil.escape(portletDisplay.getNamespace()) + "viewTermsOfUseDialog"
														).put(
															"downloadURL", downloadURL
														).put(
															"title", HtmlUtil.escape(LanguageUtil.get(request, "terms-of-use"))
														).build()
													%>'
													borderless="<%= true %>"
													displayType="secondary"
													icon="download"
													label='<%= LanguageUtil.format(request, "download-x", commerceVirtualOrderItemFileEntry.getVersion(), false) %>'
													propsTransformer="{OpenTermsOfUseModalPropsTransformer} from commerce-product-type-virtual-order-content-web"
												/>
											</c:when>
											<c:otherwise>
												<div class="lfr-tooltip-scope">
													<clay:link
														cssClass="btn btn-outline-borderless btn-outline-secondary"
														href="<%= downloadURL %>"
														icon="download"
														label='<%= LanguageUtil.format(request, "download-x", commerceVirtualOrderItemFileEntry.getVersion(), false) %>'
													/>
												</div>
											</c:otherwise>
										</c:choose>
									</c:if>

								<%
								}
								%>

							</liferay-ui:search-container-column-text>
						</liferay-ui:search-container-row>

						<liferay-ui:search-iterator
							displayStyle="list"
							markupView="lexicon"
						/>
					</liferay-ui:search-container>
				</div>
			</div>
		</liferay-ddm:template-renderer>
	</c:otherwise>
</c:choose>