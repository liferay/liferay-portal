/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.cart.content.web.internal.display.context;

import com.liferay.commerce.cart.content.web.internal.portlet.configuration.CommerceCartContentMiniPortletInstanceConfiguration;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.order.CommerceOrderValidatorRegistry;
import com.liferay.commerce.percentage.PercentageFormatter;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.Locale;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CommerceCartContentMiniDisplayContext
	extends CommerceCartContentDisplayContext {

	public CommerceCartContentMiniDisplayContext(
			CommerceChannelLocalService commerceChannelLocalService,
			CommerceOrderHttpHelper commerceOrderHttpHelper,
			CommerceOrderItemService commerceOrderItemService,
			CommerceOrderPriceCalculation commerceOrderPriceCalculation,
			CommerceOrderValidatorRegistry commerceOrderValidatorRegistry,
			ConfigurationProvider configurationProvider,
			CPDefinitionHelper cpDefinitionHelper,
			CPInstanceHelper cpInstanceHelper,
			ModelResourcePermission<CommerceOrder>
				commerceOrderModelResourcePermission,
			PortletResourcePermission commerceProductPortletResourcePermission,
			PercentageFormatter percentageFormatter,
			GroupLocalService groupLocalService,
			HttpServletRequest httpServletRequest, Portal portal)
		throws PortalException {

		super(
			commerceChannelLocalService, commerceOrderItemService,
			commerceOrderModelResourcePermission, commerceOrderPriceCalculation,
			commerceOrderValidatorRegistry,
			commerceProductPortletResourcePermission, configurationProvider,
			cpDefinitionHelper, cpInstanceHelper, groupLocalService,
			httpServletRequest, portal);

		_commerceCartContentMiniPortletInstanceConfiguration =
			configurationProvider.getPortletInstanceConfiguration(
				CommerceCartContentMiniPortletInstanceConfiguration.class,
				commerceCartContentRequestHelper.getThemeDisplay());

		_commerceOrderHttpHelper = commerceOrderHttpHelper;
		_percentageFormatter = percentageFormatter;
		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;
	}

	public String getCommerceCartPortletURL() throws PortalException {
		return _commerceOrderHttpHelper.getCommerceCartPortletURL(
			commerceCartContentRequestHelper.getRequest());
	}

	public int getCommerceOrderItemsQuantity() throws PortalException {
		BigDecimal quantity =
			_commerceOrderHttpHelper.getCommerceOrderItemsQuantity(
				commerceCartContentRequestHelper.getRequest());

		return quantity.intValue();
	}

	@Override
	public String getDisplayStyle() {
		return _commerceCartContentMiniPortletInstanceConfiguration.
			displayStyle();
	}

	@Override
	public long getDisplayStyleGroupId() {
		if (_displayStyleGroupId != null) {
			return _displayStyleGroupId;
		}

		String displayStyleGroupExternalReferenceCode =
			_commerceCartContentMiniPortletInstanceConfiguration.
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
			_commerceCartContentMiniPortletInstanceConfiguration.
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

	public String getLocalizedPercentage(BigDecimal percentage, Locale locale)
		throws PortalException {

		CommerceOrder commerceOrder = getCommerceOrder();

		if (commerceOrder == null) {
			return StringPool.BLANK;
		}

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		return _percentageFormatter.getLocalizedPercentage(
			locale, commerceCurrency.getMaxFractionDigits(),
			commerceCurrency.getMinFractionDigits(), percentage);
	}

	private final CommerceCartContentMiniPortletInstanceConfiguration
		_commerceCartContentMiniPortletInstanceConfiguration;
	private final CommerceOrderHttpHelper _commerceOrderHttpHelper;
	private Long _displayStyleGroupId;
	private String _displayStyleGroupKey;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final PercentageFormatter _percentageFormatter;

}