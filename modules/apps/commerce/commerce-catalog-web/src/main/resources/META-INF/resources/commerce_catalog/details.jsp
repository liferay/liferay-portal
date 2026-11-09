<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceCatalogDisplayContext commerceCatalogDisplayContext = (CommerceCatalogDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceCatalog commerceCatalog = commerceCatalogDisplayContext.getCommerceCatalog();
List<CommerceCurrency> commerceCurrencies = commerceCatalogDisplayContext.getCommerceCurrencies();
FileEntry fileEntry = commerceCatalogDisplayContext.getDefaultFileEntry();

CommercePriceList baseCommercePriceList = commerceCatalogDisplayContext.getBaseCommercePriceList(CommercePriceListConstants.TYPE_PRICE_LIST);
CommercePriceList basePromotionCommercePriceList = commerceCatalogDisplayContext.getBaseCommercePriceList(CommercePriceListConstants.TYPE_PROMOTION);

long fileEntryId = BeanParamUtil.getLong(fileEntry, request, "fileEntryId");

boolean viewOnly = !commerceCatalogDisplayContext.hasModelResourcePermission(commerceCatalog.getCommerceCatalogId(), ActionKeys.UPDATE);
%>

<portlet:actionURL name="/commerce_catalogs/edit_commerce_catalog" var="editCommerceCatalogActionURL" />

<aui:form action="<%= editCommerceCatalogActionURL %>" cssClass="mt-4" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (commerceCatalog == null) ? Constants.ADD : Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="baseCommercePriceListId" type="hidden" value="<%= commerceCatalogDisplayContext.getBaseCommercePriceListId(CommercePriceListConstants.TYPE_PRICE_LIST) %>" />
	<aui:input name="basePromotionCommercePriceListId" type="hidden" value="<%= commerceCatalogDisplayContext.getBaseCommercePriceListId(CommercePriceListConstants.TYPE_PROMOTION) %>" />
	<aui:input name="commerceCatalogId" type="hidden" value="<%= (commerceCatalog == null) ? 0 : commerceCatalog.getCommerceCatalogId() %>" />

	<liferay-ui:error exception="<%= AccountEntryStatusException.class %>" message="please-select-a-valid-supplier" />
	<liferay-ui:error exception="<%= AccountEntryTypeException.class %>" message="please-select-a-valid-supplier" />
	<liferay-ui:error exception="<%= NoSuchFileEntryException.class %>" message="please-select-an-existing-file" />
	<liferay-ui:error exception="<%= NoSuchPriceListException.class %>" message="please-select-an-existing-price-list-or-promotion" />

	<div class="row">
		<div class="col-8">
			<commerce-ui:panel
				elementClasses="flex-fill"
				title='<%= LanguageUtil.get(request, "details") %>'
			>
				<div class="col-12 lfr-form-content">
					<aui:input bean="<%= commerceCatalog %>" disabled="<%= viewOnly %>" model="<%= CommerceCatalog.class %>" name="name" required="<%= true %>" />

					<aui:select disabled="<%= viewOnly %>" helpMessage="the-default-language-for-the-content-within-this-catalog" label="default-catalog-language" name="catalogDefaultLanguageId" required="<%= true %>" title="language">

						<%
						String catalogDefaultLanguageId = themeDisplay.getLanguageId();

						if (commerceCatalog != null) {
							catalogDefaultLanguageId = commerceCatalog.getCatalogDefaultLanguageId();
						}

						Set<Locale> siteAvailableLocales = LanguageUtil.getAvailableLocales(themeDisplay.getScopeGroupId());

						for (Locale siteAvailableLocale : siteAvailableLocales) {
						%>

							<aui:option label="<%= siteAvailableLocale.getDisplayName(locale) %>" lang="<%= LocaleUtil.toW3cLanguageId(siteAvailableLocale) %>" selected="<%= catalogDefaultLanguageId.equals(LanguageUtil.getLanguageId(siteAvailableLocale)) %>" value="<%= LocaleUtil.toLanguageId(siteAvailableLocale) %>" />

						<%
						}
						%>

					</aui:select>

					<aui:select disabled="<%= viewOnly %>" label="currency" name="commerceCurrencyCode" required="<%= true %>" title="currency">

						<%
						for (CommerceCurrency commerceCurrency : commerceCurrencies) {
							String commerceCurrencyCode = commerceCurrency.getCode();
						%>

							<aui:option label="<%= HtmlUtil.escape(commerceCurrency.getName(locale)) %>" selected="<%= (commerceCatalog == null) ? commerceCurrency.isPrimary() : commerceCurrencyCode.equals(commerceCatalog.getCommerceCurrencyCode()) %>" value="<%= HtmlUtil.escape(commerceCurrencyCode) %>" />

						<%
						}
						%>

					</aui:select>

					<aui:select label="inventory-method-key" name="inventorySettings--inventoryMethodKey--" required="<%= true %>">

						<%
						for (CommerceInventoryMethod commerceInventoryMethod : commerceCatalogDisplayContext.getCommerceInventoryMethods()) {
						%>

							<aui:option label="<%= commerceInventoryMethod.getLabel(locale) %>" selected="<%= commerceCatalogDisplayContext.isCommerceInventoryMethodSelected(commerceCatalog.getGroupId(), commerceInventoryMethod.getKey()) %>" value="<%= commerceInventoryMethod.getKey() %>" />

						<%
						}
						%>

					</aui:select>

					<c:if test="<%= commerceCatalogDisplayContext.showBasePriceListInputs() %>">
						<label class="control-label" for="baseCommercePriceListId"><liferay-ui:message key="base-price-list" /></label>

						<div class="mb-4" id="base-price-list-autocomplete-root"></div>

						<liferay-frontend:component
							context='<%=
								HashMapBuilder.<String, Object>put(
									"apiUrl", commerceCatalogDisplayContext.getPriceListsAPIURL(CommercePriceListConstants.TYPE_PRICE_LIST)
								).put(
									"initialLabel", (baseCommercePriceList == null) ? StringPool.BLANK : baseCommercePriceList.getName()
								).put(
									"initialValue", (baseCommercePriceList == null) ? 0 : baseCommercePriceList.getCommercePriceListId()
								).put(
									"inputId", "baseCommercePriceListId"
								).put(
									"inputName", liferayPortletResponse.getNamespace() + "baseCommercePriceListId"
								).put(
									"itemsKey", "id"
								).put(
									"itemsLabel", "name"
								).put(
									"namespace", liferayPortletResponse.getNamespace()
								).build()
							%>'
							module="{detailsAutocompleteBasePrice} from commerce-catalog-web"
						/>

						<label class="control-label" for="basePromotionCommercePriceListId"><liferay-ui:message key="base-promotion" /></label>

						<div class="mb-4" id="base-promotion-autocomplete-root"></div>

						<liferay-frontend:component
							context='<%=
								HashMapBuilder.<String, Object>put(
									"apiUrl", commerceCatalogDisplayContext.getPriceListsAPIURL(CommercePriceListConstants.TYPE_PROMOTION)
								).put(
									"initialLabel", (basePromotionCommercePriceList == null) ? StringPool.BLANK : basePromotionCommercePriceList.getName()
								).put(
									"initialValue", (basePromotionCommercePriceList == null) ? 0 : basePromotionCommercePriceList.getCommercePriceListId()
								).put(
									"inputId", "basePromotionCommercePriceListId"
								).put(
									"inputName", liferayPortletResponse.getNamespace() + "basePromotionCommercePriceListId"
								).put(
									"itemsKey", "id"
								).put(
									"itemsLabel", "name"
								).put(
									"namespace", liferayPortletResponse.getNamespace()
								).build()
							%>'
							module="{detailsAutocompleteBasePromotion} from commerce-catalog-web"
						/>
					</c:if>

					<%
					AccountEntry accountEntry = commerceCatalogDisplayContext.getAccountEntry();
					%>

					<c:choose>
						<c:when test="<%= commerceCatalogDisplayContext.hasManageLinkSupplierPermission(Constants.UPDATE) && !viewOnly %>">
							<label class="control-label" for="accountEntryId"><liferay-ui:message key="link-catalog-to-a-supplier" /></label>

							<div class="mb-4" id="link-account-entry-autocomplete-root"></div>

							<liferay-frontend:component
								context='<%=
									HashMapBuilder.<String, Object>put(
										"apiUrl", commerceCatalogDisplayContext.getAccountEntriesAPIURL()
									).put(
										"initialLabel", (accountEntry == null) ? StringPool.BLANK : accountEntry.getName()
									).put(
										"initialValue", (accountEntry == null) ? 0 : accountEntry.getAccountEntryId()
									).put(
										"inputId", liferayPortletResponse.getNamespace() + "accountEntryId"
									).put(
										"inputName", liferayPortletResponse.getNamespace() + "accountEntryId"
									).put(
										"itemsKey", "id"
									).put(
										"itemsLabel", "name"
									).build()
								%>'
								module="{detailsAutocompleteAccountLink} from commerce-catalog-web"
							/>
						</c:when>
						<c:otherwise>
							<aui:input disabled="<%= true %>" label="link-catalog-to-a-supplier" name="" value="<%= (accountEntry != null) ? accountEntry.getName() : StringPool.BLANK %>" />
						</c:otherwise>
					</c:choose>
				</div>
			</commerce-ui:panel>
		</div>

		<div class="col-4">
			<commerce-ui:panel
				elementClasses="flex-fill"
				title='<%= LanguageUtil.get(request, "default-catalog-image") %>'
			>
				<div class="row">
					<div class="col-12 h-100">
						<aui:model-context bean="<%= fileEntry %>" model="<%= FileEntry.class %>" />

						<div class="lfr-attachment-cover-image-selector">
							<portlet:actionURL name="/commerce_catalogs/upload_commerce_media_default_image" var="uploadCommerceMediaDefaultImageActionURL" />

							<liferay-item-selector:image-selector
								draggableImage="vertical"
								fileEntryId="<%= fileEntryId %>"
								itemSelectorEventName="addFileEntry"
								itemSelectorURL="<%= commerceCatalogDisplayContext.getImageItemSelectorURL() %>"
								maxFileSize="<%= commerceCatalogDisplayContext.getImageMaxSize() %>"
								paramName="fileEntry"
								uploadURL="<%= uploadCommerceMediaDefaultImageActionURL %>"
								validExtensions="<%= StringUtil.merge(commerceCatalogDisplayContext.getImageExtensions(), StringPool.COMMA_AND_SPACE) %>"
							/>
						</div>
					</div>
				</div>
			</commerce-ui:panel>
		</div>
	</div>
</aui:form>