/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.display.context;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.rule.type.CommerceDiscountRuleTypeRegistry;
import com.liferay.commerce.discount.service.CommerceDiscountAccountRelService;
import com.liferay.commerce.discount.service.CommerceDiscountCommerceAccountGroupRelService;
import com.liferay.commerce.discount.service.CommerceDiscountOrderTypeRelService;
import com.liferay.commerce.discount.service.CommerceDiscountRuleService;
import com.liferay.commerce.discount.service.CommerceDiscountService;
import com.liferay.commerce.discount.target.CommerceDiscountTargetRegistry;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.percentage.PercentageFormatter;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceDiscountQualifiersDisplayContext
	extends CommerceDiscountDisplayContext {

	public CommerceDiscountQualifiersDisplayContext(
		CommerceChannelRelService commerceChannelRelService,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		ModelResourcePermission<CommerceDiscount>
			commerceDiscountModelResourcePermission,
		CommerceDiscountAccountRelService commerceDiscountAccountRelService,
		CommerceDiscountCommerceAccountGroupRelService
			commerceDiscountCommerceAccountGroupRelService,
		CommerceDiscountOrderTypeRelService commerceDiscountOrderTypeRelService,
		CommerceDiscountService commerceDiscountService,
		CommerceDiscountRuleService commerceDiscountRuleService,
		CommerceDiscountRuleTypeRegistry commerceDiscountRuleTypeRegistry,
		CommerceDiscountTargetRegistry commerceDiscountTargetRegistry,
		CommercePriceFormatter commercePriceFormatter,
		PercentageFormatter percentageFormatter,
		HttpServletRequest httpServletRequest, Portal portal) {

		super(
			commerceCurrencyLocalService,
			commerceDiscountModelResourcePermission, commerceDiscountService,
			commerceDiscountRuleService, commerceDiscountRuleTypeRegistry,
			commerceDiscountTargetRegistry, commercePriceFormatter,
			percentageFormatter, httpServletRequest, portal);

		_commerceChannelRelService = commerceChannelRelService;
		_commerceDiscountAccountRelService = commerceDiscountAccountRelService;
		_commerceDiscountCommerceAccountGroupRelService =
			commerceDiscountCommerceAccountGroupRelService;
		_commerceDiscountOrderTypeRelService =
			commerceDiscountOrderTypeRelService;
	}

	public List<FDSActionDropdownItem> getAccountFDSActionDropdownItems()
		throws PortalException {

		return getFDSActionTemplates(
			PortletURLBuilder.create(
				portal.getControlPanelPortletURL(
					httpServletRequest,
					AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/account_admin/edit_account_entry"
			).setRedirect(
				commercePricingRequestHelper.getCurrentURL()
			).setParameter(
				"accountEntryId", "{account.id}"
			).buildString(),
			false);
	}

	public List<FDSActionDropdownItem> getAccountGroupFDSActionDropdownItems()
		throws PortalException {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				null, "trash", "remove",
				LanguageUtil.get(httpServletRequest, "remove"), "delete",
				"delete", "headless"));
	}

	public String getActiveAccountEligibility() throws PortalException {
		long commerceDiscountId = getCommerceDiscountId();

		long commerceDiscountAccountRelsCount =
			_commerceDiscountAccountRelService.
				getCommerceDiscountAccountRelsCount(commerceDiscountId);

		if (commerceDiscountAccountRelsCount > 0) {
			return "accounts";
		}

		long commerceDiscountAccountGroupRelsCount =
			_commerceDiscountCommerceAccountGroupRelService.
				getCommerceDiscountCommerceAccountGroupRelsCount(
					commerceDiscountId);

		if (commerceDiscountAccountGroupRelsCount > 0) {
			return "accountGroups";
		}

		return "all";
	}

	public String getActiveChannelEligibility() throws PortalException {
		long commerceChannelRelsCount =
			_commerceChannelRelService.getCommerceChannelRelsCount(
				CommerceDiscount.class.getName(), getCommerceDiscountId());

		if (commerceChannelRelsCount > 0) {
			return "channels";
		}

		return "all";
	}

	public String getActiveOrderTypeEligibility() throws PortalException {
		int commerceDiscountOrderTypeRelsCount =
			_commerceDiscountOrderTypeRelService.
				getCommerceDiscountOrderTypeRelsCount(
					getCommerceDiscountId(), StringPool.BLANK);

		if (commerceDiscountOrderTypeRelsCount > 0) {
			return "orderTypes";
		}

		return "all";
	}

	public String getDiscountAccountGroupsAPIURL() throws PortalException {
		return "/o/headless-commerce-admin-pricing/v2.0/discounts/" +
			getCommerceDiscountId() +
				"/discount-account-groups?nestedFields=accountGroup";
	}

	public String getDiscountAccountsAPIURL() throws PortalException {
		return "/o/headless-commerce-admin-pricing/v2.0/discounts/" +
			getCommerceDiscountId() + "/discount-accounts?nestedFields=account";
	}

	public List<FDSActionDropdownItem>
			getDiscountChannelFDSActionDropdownItems()
		throws PortalException {

		return getFDSActionTemplates(
			PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest, CommerceChannel.class.getName(),
					PortletProvider.Action.MANAGE)
			).setMVCRenderCommandName(
				"/commerce_channels/edit_commerce_channel"
			).setRedirect(
				commercePricingRequestHelper.getCurrentURL()
			).setParameter(
				"commerceChannelId", "{channel.id}"
			).buildString(),
			false);
	}

	public String getDiscountChannelsAPIURL() throws PortalException {
		return "/o/headless-commerce-admin-pricing/v2.0/discounts/" +
			getCommerceDiscountId() + "/discount-channels?nestedFields=channel";
	}

	public List<FDSActionDropdownItem>
			getDiscountOrderTypeFDSActionDropdownItems()
		throws PortalException {

		return getFDSActionTemplates(
			PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest, CommerceOrderType.class.getName(),
					PortletProvider.Action.MANAGE)
			).setMVCRenderCommandName(
				"/commerce_order_type/edit_commerce_order_type"
			).setRedirect(
				commercePricingRequestHelper.getCurrentURL()
			).setParameter(
				"commerceOrderTypeId", "{orderType.id}"
			).buildString(),
			false);
	}

	public String getDiscountOrderTypesAPIURL() throws PortalException {
		return "/o/headless-commerce-admin-pricing/v2.0/discounts/" +
			getCommerceDiscountId() +
				"/discount-order-types?nestedFields=orderType";
	}

	private final CommerceChannelRelService _commerceChannelRelService;
	private final CommerceDiscountAccountRelService
		_commerceDiscountAccountRelService;
	private final CommerceDiscountCommerceAccountGroupRelService
		_commerceDiscountCommerceAccountGroupRelService;
	private final CommerceDiscountOrderTypeRelService
		_commerceDiscountOrderTypeRelService;

}