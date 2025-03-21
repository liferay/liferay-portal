/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.cart.content.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.cart.content.web.internal.display.context.helper.CommerceCartContentRequestHelper;
import com.liferay.commerce.cart.content.web.internal.portlet.configuration.CommerceCartContentPortletInstanceConfiguration;
import com.liferay.commerce.configuration.CommerceOrderFieldsConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.CommerceOrderValidatorRegistry;
import com.liferay.commerce.order.CommerceOrderValidatorResult;
import com.liferay.commerce.price.CommerceOrderPrice;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CommerceCartContentDisplayContext {

	public CommerceCartContentDisplayContext(
			CommerceChannelLocalService commerceChannelLocalService,
			CommerceOrderItemService commerceOrderItemService,
			ModelResourcePermission<CommerceOrder>
				commerceOrderModelResourcePermission,
			CommerceOrderPriceCalculation commerceOrderPriceCalculation,
			CommerceOrderValidatorRegistry commerceOrderValidatorRegistry,
			PortletResourcePermission commerceProductPortletResourcePermission,
			ConfigurationProvider configurationProvider,
			CPDefinitionHelper cpDefinitionHelper,
			CPInstanceHelper cpInstanceHelper,
			GroupLocalService groupLocalService,
			HttpServletRequest httpServletRequest, Portal portal)
		throws PortalException {

		_commerceChannelLocalService = commerceChannelLocalService;
		_commerceOrderItemService = commerceOrderItemService;
		this.commerceOrderModelResourcePermission =
			commerceOrderModelResourcePermission;
		_commerceOrderPriceCalculation = commerceOrderPriceCalculation;
		_commerceOrderValidatorRegistry = commerceOrderValidatorRegistry;
		_commerceProductPortletResourcePermission =
			commerceProductPortletResourcePermission;

		this.cpDefinitionHelper = cpDefinitionHelper;
		this.cpInstanceHelper = cpInstanceHelper;

		commerceCartContentRequestHelper = new CommerceCartContentRequestHelper(
			httpServletRequest);

		commerceContext = commerceCartContentRequestHelper.getCommerceContext();

		_commerceCartContentPortletInstanceConfiguration =
			configurationProvider.getPortletInstanceConfiguration(
				CommerceCartContentPortletInstanceConfiguration.class,
				commerceCartContentRequestHelper.getThemeDisplay());

		_configurationProvider = configurationProvider;
		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;
		_portal = portal;
	}

	public CommerceOrder getCommerceOrder() {
		if ((_commerceOrder != null) || (commerceContext == null)) {
			return _commerceOrder;
		}

		_commerceOrder = commerceContext.getCommerceOrder();

		return _commerceOrder;
	}

	public long getCommerceOrderId() throws PortalException {
		CommerceOrder commerceOrder = getCommerceOrder();

		if (commerceOrder == null) {
			return 0;
		}

		return commerceOrder.getCommerceOrderId();
	}

	public CommerceOrderPrice getCommerceOrderPrice() throws PortalException {
		return _commerceOrderPriceCalculation.getCommerceOrderPrice(
			getCommerceOrder(), commerceContext);
	}

	public Map<Long, List<CommerceOrderValidatorResult>>
			getCommerceOrderValidatorResults()
		throws PortalException {

		return _commerceOrderValidatorRegistry.getCommerceOrderValidatorResults(
			commerceCartContentRequestHelper.getLocale(), getCommerceOrder());
	}

	public String getCommercePriceDisplayType() throws PortalException {
		if (commerceContext == null) {
			return CommercePricingConstants.TAX_EXCLUDED_FROM_PRICE;
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannel(
				commerceContext.getCommerceChannelId());

		if (commerceChannel == null) {
			return CommercePricingConstants.TAX_EXCLUDED_FROM_PRICE;
		}

		return commerceChannel.getPriceDisplayType();
	}

	public String getCPDefinitionURL(
			long cpDefinitionId, ThemeDisplay themeDisplay)
		throws PortalException {

		return cpDefinitionHelper.getFriendlyURL(cpDefinitionId, themeDisplay);
	}

	public String getCPInstanceCDNURL(CommerceOrderItem commerceOrderItem)
		throws Exception {

		return cpInstanceHelper.getCPInstanceCDNURL(
			CommerceUtil.getCommerceAccountId(commerceContext),
			commerceOrderItem.getCPInstanceId());
	}

	public FileVersion getCPInstanceImageFileVersion(
			CommerceOrderItem commerceOrderItem)
		throws Exception {

		return cpInstanceHelper.getCPInstanceImageFileVersion(
			CommerceUtil.getCommerceAccountId(commerceContext),
			_portal.getCompanyId(_httpServletRequest),
			commerceOrderItem.getCPInstanceId());
	}

	public String getDeleteURL(CommerceOrderItem commerceOrderItem) {
		return PortletURLBuilder.createActionURL(
			commerceCartContentRequestHelper.getLiferayPortletResponse()
		).setActionName(
			"/commerce_cart_content/edit_commerce_order_item"
		).setCMD(
			Constants.DELETE
		).setRedirect(
			commerceCartContentRequestHelper.getCurrentURL()
		).setParameter(
			"commerceOrderItemId", commerceOrderItem.getCommerceOrderItemId()
		).buildString();
	}

	public CommerceMoney getDiscountAmountCommerceMoney(
			CommerceOrderItem commerceOrderItem)
		throws PortalException {

		if (Objects.equals(
				getCommercePriceDisplayType(),
				CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {

			return commerceOrderItem.getDiscountWithTaxAmountMoney();
		}

		return commerceOrderItem.getDiscountAmountMoney();
	}

	public String getDisplayStyle() {
		return _commerceCartContentPortletInstanceConfiguration.displayStyle();
	}

	public long getDisplayStyleGroupId() {
		if (_displayStyleGroupId != null) {
			return _displayStyleGroupId;
		}

		String displayStyleGroupExternalReferenceCode =
			_commerceCartContentPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		ThemeDisplay themeDisplay =
			commerceCartContentRequestHelper.getThemeDisplay();

		Group group = themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = _groupLocalService.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				themeDisplay.getCompanyId());
		}

		if (group != null) {
			_displayStyleGroupId = group.getGroupId();
		}
		else {
			_displayStyleGroupId = themeDisplay.getScopeGroupId();
		}

		return _displayStyleGroupId;
	}

	public String getDisplayStyleGroupKey() {
		if (Validator.isNotNull(_displayStyleGroupKey)) {
			return _displayStyleGroupKey;
		}

		ThemeDisplay themeDisplay =
			commerceCartContentRequestHelper.getThemeDisplay();

		Group group = themeDisplay.getScopeGroup();

		String displayStyleGroupExternalReferenceCode =
			_commerceCartContentPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = _groupLocalService.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				themeDisplay.getCompanyId());
		}

		if (group != null) {
			_displayStyleGroupKey = group.getGroupKey();
		}
		else {
			_displayStyleGroupKey = StringPool.BLANK;
		}

		return _displayStyleGroupKey;
	}

	public CommerceMoney getFinalPriceCommerceMoney(
			CommerceOrderItem commerceOrderItem)
		throws PortalException {

		if (Objects.equals(
				getCommercePriceDisplayType(),
				CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {

			return commerceOrderItem.getFinalPriceWithTaxAmountMoney();
		}

		return commerceOrderItem.getFinalPriceMoney();
	}

	public List<KeyValuePair> getKeyValuePairs(
			long cpDefinitionId, String json, Locale locale)
		throws PortalException {

		return cpInstanceHelper.getKeyValuePairs(cpDefinitionId, json, locale);
	}

	public PortletURL getPortletURL() {
		LiferayPortletResponse liferayPortletResponse =
			commerceCartContentRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String delta = ParamUtil.getString(
			commerceCartContentRequestHelper.getRequest(), "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String deltaEntry = ParamUtil.getString(
			commerceCartContentRequestHelper.getRequest(), "deltaEntry");

		if (Validator.isNotNull(deltaEntry)) {
			portletURL.setParameter("deltaEntry", deltaEntry);
		}

		return portletURL;
	}

	public SearchContainer<CommerceOrderItem> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			commerceCartContentRequestHelper.getLiferayPortletRequest(),
			getPortletURL(), null, "no-items-were-found");

		long commerceOrderId = getCommerceOrderId();

		if (commerceOrderId == 0) {
			return _searchContainer;
		}

		_searchContainer.setResultsAndTotal(
			() -> _commerceOrderItemService.getCommerceOrderItems(
				commerceOrderId, _searchContainer.getStart(),
				_searchContainer.getEnd()),
			_commerceOrderItemService.getCommerceOrderItemsCount(
				commerceOrderId));

		return _searchContainer;
	}

	public CommerceMoney getUnitPriceCommerceMoney(
			CommerceOrderItem commerceOrderItem)
		throws PortalException {

		if (Objects.equals(
				getCommercePriceDisplayType(),
				CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {

			return commerceOrderItem.getUnitPriceWithTaxAmountMoney();
		}

		return commerceOrderItem.getUnitPriceMoney();
	}

	public CommerceMoney getUnitPromoPriceCommerceMoney(
			CommerceOrderItem commerceOrderItem)
		throws PortalException {

		if (Objects.equals(
				getCommercePriceDisplayType(),
				CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {

			return commerceOrderItem.getPromoPriceWithTaxAmountMoney();
		}

		return commerceOrderItem.getPromoPriceMoney();
	}

	public boolean hasPermission(String actionId) throws PortalException {
		if (_commerceOrder == null) {
			return false;
		}

		return commerceOrderModelResourcePermission.contains(
			commerceCartContentRequestHelper.getPermissionChecker(),
			_commerceOrder, actionId);
	}

	public boolean hasViewPricePermission() throws PortalException {
		AccountEntry accountEntry = commerceContext.getAccountEntry();

		if ((accountEntry != null) && accountEntry.isBusinessAccount()) {
			return _commerceProductPortletResourcePermission.contains(
				commerceCartContentRequestHelper.getPermissionChecker(),
				accountEntry.getAccountEntryGroupId(), CPActionKeys.VIEW_PRICE);
		}

		return _commerceProductPortletResourcePermission.contains(
			commerceCartContentRequestHelper.getPermissionChecker(),
			commerceCartContentRequestHelper.getScopeGroupId(),
			CPActionKeys.VIEW_PRICE);
	}

	public boolean isRequestQuoteEnabled() throws PortalException {
		CommerceOrderFieldsConfiguration commerceOrderFieldsConfiguration =
			_getCommerceOrderFieldsConfiguration();

		if (commerceOrderFieldsConfiguration == null) {
			return false;
		}

		return commerceOrderFieldsConfiguration.requestQuoteEnabled();
	}

	public boolean isUnitPromoPriceActive(CommerceOrderItem commerceOrderItem)
		throws PortalException {

		CommerceMoney unitPriceCommerceMoney = getUnitPriceCommerceMoney(
			commerceOrderItem);
		CommerceMoney unitPromoPriceCommerceMoney =
			getUnitPromoPriceCommerceMoney(commerceOrderItem);

		if (!unitPromoPriceCommerceMoney.isEmpty() &&
			BigDecimalUtil.gt(
				unitPromoPriceCommerceMoney.getPrice(), BigDecimal.ZERO) &&
			BigDecimalUtil.lt(
				unitPromoPriceCommerceMoney.getPrice(),
				unitPriceCommerceMoney.getPrice())) {

			return true;
		}

		return false;
	}

	public boolean isValidCommerceOrder() throws PortalException {
		CommerceOrder commerceOrder = getCommerceOrder();

		if (commerceOrder == null) {
			return false;
		}

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		if (commerceOrderItems.isEmpty()) {
			return false;
		}

		return _commerceOrderValidatorRegistry.isValid(
			commerceCartContentRequestHelper.getLocale(), commerceOrder);
	}

	protected final CommerceCartContentRequestHelper
		commerceCartContentRequestHelper;
	protected final CommerceContext commerceContext;
	protected final ModelResourcePermission<CommerceOrder>
		commerceOrderModelResourcePermission;
	protected final CPDefinitionHelper cpDefinitionHelper;
	protected final CPInstanceHelper cpInstanceHelper;

	private CommerceOrderFieldsConfiguration
			_getCommerceOrderFieldsConfiguration()
		throws PortalException {

		if ((_commerceOrderFieldsConfiguration != null) ||
			(commerceContext == null)) {

			return _commerceOrderFieldsConfiguration;
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannel(
				commerceContext.getCommerceChannelId());

		if (commerceChannel == null) {
			return null;
		}

		_commerceOrderFieldsConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderFieldsConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER_FIELDS));

		return _commerceOrderFieldsConfiguration;
	}

	private final CommerceCartContentPortletInstanceConfiguration
		_commerceCartContentPortletInstanceConfiguration;
	private final CommerceChannelLocalService _commerceChannelLocalService;
	private CommerceOrder _commerceOrder;
	private CommerceOrderFieldsConfiguration _commerceOrderFieldsConfiguration;
	private final CommerceOrderItemService _commerceOrderItemService;
	private final CommerceOrderPriceCalculation _commerceOrderPriceCalculation;
	private final CommerceOrderValidatorRegistry
		_commerceOrderValidatorRegistry;
	private final PortletResourcePermission
		_commerceProductPortletResourcePermission;
	private final ConfigurationProvider _configurationProvider;
	private Long _displayStyleGroupId;
	private String _displayStyleGroupKey;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final Portal _portal;
	private SearchContainer<CommerceOrderItem> _searchContainer;

}